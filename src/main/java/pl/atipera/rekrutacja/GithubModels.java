package pl.atipera.rekrutacja;

import java.util.List;


record GithubRepo(String name, Owner owner, boolean fork) {}
record Owner(String login) {}
record Branch(String name, Commit commit) {}
record Commit(String sha) {}

record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {}

record BranchResponse(String name, String lastCommitSha) {}

record ErrorResponse(int status, String message) {}