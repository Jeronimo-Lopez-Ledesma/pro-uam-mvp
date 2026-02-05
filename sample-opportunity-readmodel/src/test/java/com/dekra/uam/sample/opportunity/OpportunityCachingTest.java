package com.dekra.uam.sample.opportunity;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OpportunityCachingTest {

    static MockWebServer mockWebServer;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("accessgrants.base-url", () -> mockWebServer.url("/").toString());
        registry.add("enforcement.cache.ttl-seconds", () -> 3600);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCacheByConsumer() {
        int before = mockWebServer.getRequestCount();
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", "application/json")
                .setBody("{\"mode\":\"IDS\",\"ids\":[\"opp-1\"],\"predicate\":null,\"ttlSeconds\":600}"));

        webTestClient.get().uri(uri -> uri.path("/opportunities").queryParam("consumer", "consumer-a").queryParam("subject", "subject-a").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("opp-1");

        webTestClient.get().uri(uri -> uri.path("/opportunities").queryParam("consumer", "consumer-a").queryParam("subject", "subject-a").build())
                .exchange()
                .expectStatus().isOk();

        assertThat(mockWebServer.getRequestCount() - before).isEqualTo(1);
    }

    @Test
    void differentConsumerUsesDifferentCacheEntry() {
        int before = mockWebServer.getRequestCount();
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", "application/json")
                .setBody("{\"mode\":\"IDS\",\"ids\":[\"opp-1\"],\"predicate\":null,\"ttlSeconds\":600}"));
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", "application/json")
                .setBody("{\"mode\":\"IDS\",\"ids\":[\"opp-2\"],\"predicate\":null,\"ttlSeconds\":600}"));

        webTestClient.get().uri(uri -> uri.path("/opportunities").queryParam("consumer", "consumer-a").queryParam("subject", "subject-a").build())
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri(uri -> uri.path("/opportunities").queryParam("consumer", "consumer-b").queryParam("subject", "subject-a").build())
                .exchange()
                .expectStatus().isOk();

        assertThat(mockWebServer.getRequestCount() - before).isEqualTo(2);
    }
}
