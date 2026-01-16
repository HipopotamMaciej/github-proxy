
package pl.atipera.rekrutacja;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
@TestPropertySource(properties = "github.api.url=http://localhost:8081")
class GithubProxyApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder builder;

    private RestClient restClient() {
        return builder.baseUrl("http://localhost:" + port).build();
    }

    @Test
    void powinienZwrocic404DlaNieistniejacegoUzytkownika() {
        String username = "nonexistent";
        stubFor(get(urlPathEqualTo("/users/"+ username + "/repos"))
                .willReturn(notFound()));

        RestClient client = restClient();

        ResponseEntity<ErrorResponse> response = client.get()
                .uri("/api/repositories/nonexistent")
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {  })
                .toEntity(ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).contains("User " + username + " not found on GitHub");
    }

    @Test
    void powinienZwracacRepozytoriaBezForkowZBranchami() {
        stubFor(get(urlPathEqualTo("/users/john/repos"))
                .willReturn(okJson("""
                [
                    {"name": "repo1", "owner": {"login": "john"}, "fork": false},
                    {"name": "forked-repo", "owner": {"login": "john"}, "fork": true}
                ]
                """)));

        stubFor(get(urlPathEqualTo("/repos/john/repo1/branches"))
                .willReturn(okJson("""
                [{"name": "main", "commit": {"sha": "abcd1234"}}]
                """)));

        RestClient client = restClient();

        ResponseEntity<RepositoryResponse[]> response = client.get()
                .uri("/api/repositories/john")
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .toEntity(RepositoryResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);

        RepositoryResponse repository = response.getBody()[0];
        assertThat(repository.repositoryName()).isEqualTo("repo1");
        assertThat(repository.ownerLogin()).isEqualTo("john");
        assertThat(repository.branches()).hasSize(1);
        assertThat(repository.branches().get(0).name()).isEqualTo("main");
        assertThat(repository.branches().get(0).lastCommitSha()).isEqualTo("abcd1234");
    }
    @Test
    void powinienZwrocic406GdyKlientZadaFormatXml() {
        RestClient client = restClient();

        ResponseEntity<ErrorResponse> response = client.get()
                .uri("/api/repositories/john")
                .header(HttpHeaders.ACCEPT, "application/xml")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> { })
                .toEntity(ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(406);
        assertThat(response.getBody().message()).contains("Unsupported Media Type");
    }
}


