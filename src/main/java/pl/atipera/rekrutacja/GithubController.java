package pl.atipera.rekrutacja;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
class GithubController {

    private final GithubService githubService;

    GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public List<RepositoryResponse> getUserRepositories(
            @PathVariable String username,
            @RequestHeader(value = "Accept") String acceptHeader) {

        return githubService.getRepositoriesForUser(username);
    }
}