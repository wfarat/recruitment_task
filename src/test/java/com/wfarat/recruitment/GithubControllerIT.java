package com.wfarat.recruitment;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock
public class GithubControllerIT {

    @Autowired
    private MockMvc mockMvc;

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
                            "owner": { "login": "wfarat" }
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

        mockMvc.perform(get("/api/v1/repositories/wfarat")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("budgetSpring"))
                .andExpect(jsonPath("$[0].ownerLogin").value("wfarat"))
                .andExpect(jsonPath("$[0].branches", hasSize(1)))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("xxx"));

        verify(1, getRequestedFor(urlEqualTo("/users/wfarat/repos")));
        verify(1, getRequestedFor(urlEqualTo("/repos/wfarat/budgetSpring/branches")));
    }

    @Test
    void getRepositories_userNotFound() throws Exception {
        stubFor(WireMock.get("/users/unknown/repos").willReturn(aResponse()
                .withStatus(404)));

        mockMvc.perform(get("/api/v1/repositories/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User: unknown not found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
