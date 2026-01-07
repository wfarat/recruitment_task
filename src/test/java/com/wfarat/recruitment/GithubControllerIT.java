package com.wfarat.recruitment;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@AutoConfigureRestTestClient
@EnableWireMock
public class GithubControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @BeforeEach
    void setup() {
        WireMock.reset();
    }

    @Test
    void getRepositories_success() throws Exception {
        stubFor(WireMock.get("/users/wfarat/repos").willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                            [
                              {
                                "name": "budgetSpring",
                                "owner": { "login": "wfarat" },
                                "fork": false
                              }
                            ]
                        """)));

        stubFor(WireMock.get("/repos/wfarat/budgetSpring/branches").willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                            [
                              {
                                "name": "main",
                                "commit": { "sha": "xxx" }
                              }
                            ]
                        """)));

        restTestClient.get()
                .uri("/api/v1/repositories/wfarat")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("budgetSpring")
                .jsonPath("$[0].ownerLogin").isEqualTo("wfarat")
                .jsonPath("$[0].branches.length()").isEqualTo(1)
                .jsonPath("$[0].branches[0].lastCommitSha").isEqualTo("xxx");
        verify(1, getRequestedFor(urlEqualTo("/users/wfarat/repos")));
        verify(1, getRequestedFor(urlEqualTo("/repos/wfarat/budgetSpring/branches")));
    }

    @Test
    void getRepositories_userNotFound() throws Exception {
        stubFor(WireMock.get("/users/unknown/repos").willReturn(aResponse()
                .withStatus(404)));

        restTestClient.get().uri("/api/v1/repositories/unknown")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User: unknown not found")
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getRepositories_withoutFork() throws Exception {
        stubFor(WireMock.get("/users/wfarat/repos").willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                            [
                              {
                                "name": "budgetSpring",
                                "owner": { "login": "wfarat" },
                                "fork": false
                              },
                              {
                                "name": "someForkedRepo",
                                "owner": { "login": "wfarat" },
                                "fork": true
                              }
                            ]
                        """)));

        stubFor(WireMock.get("/repos/wfarat/budgetSpring/branches").willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                            [
                              {
                                "name": "main",
                                "commit": { "sha": "xxx" }
                              }
                            ]
                        """)));

        restTestClient.get()
                .uri("/api/v1/repositories/wfarat")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1);
        verify(1, getRequestedFor(urlEqualTo("/users/wfarat/repos")));
        verify(1, getRequestedFor(urlEqualTo("/repos/wfarat/budgetSpring/branches")));
        verify(0, getRequestedFor(urlEqualTo("/repos/wfarat/someForkedRepo/branches")));

    }

}
