package com.wfarat.recruitment;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class GithubService {

    private final RestClient restClient;

    public GithubService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Repository> getRepositoriesWithBranches(String username) {
        return getRepositories(username).stream()
                .peek(repo -> repo.setBranches(getBranches(repo.getOwnerLogin(), repo.getName())))
                .toList();
    }
    public List<Repository> getRepositories(String username) {
        ParameterizedTypeReference<List<Repository>> typeRef =
                new ParameterizedTypeReference<>() {};
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (_, _) -> {
                            throw new UserNotFoundException(username);
                        }
                )
                .body(typeRef);
    }

    public List<Branch> getBranches(String owner, String repo) {
        ParameterizedTypeReference<List<Branch>> typeRef =
                new ParameterizedTypeReference<>() {};
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(typeRef);
    }
}
