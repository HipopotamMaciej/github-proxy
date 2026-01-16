package pl.atipera.rekrutacja;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
class GithubService {

    private final GithubClient githubClient;

    GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getRepositoriesForUser(String username) {

        return githubClient.fetchRepositories(username).stream()

                .filter(repo -> !repo.fork())
                .map(repo -> {

                    List<BranchResponse> branches = githubClient.fetchBranches(repo.owner().login(), repo.name())
                            .stream()
                            .map(b -> new BranchResponse(b.name(), b.commit().sha()))
                            .toList();

                    return new RepositoryResponse(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }
}