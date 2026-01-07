package com.wfarat.recruitment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class RestClientConfig {

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @Bean
    RestClient restClient() {
        return RestClient.builder().baseUrl(wireMockUrl).build();
    }
}
