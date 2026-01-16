package pl.atipera.rekrutacja;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;

@Component
class GithubClient {
    private final RestClient restClient;

    GithubClient(RestClient.Builder builder, @Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    List<GithubRepo> fetchRepositories(String username) {
        return restClient.get()
                .uri("/users/{user}/repos", username)
                .retrieve()

                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new UserNotFoundException("User " + username + " not found on GitHub");
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    List<Branch> fetchBranches(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}