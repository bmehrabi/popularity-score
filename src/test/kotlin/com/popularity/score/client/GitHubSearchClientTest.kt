package com.popularity.score.client

import com.popularity.score.dto.GitHubOwner
import com.popularity.score.dto.GitHubRepoItem
import com.popularity.score.dto.GitHubSearchResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class GitHubSearchClientTest {

    @MockK
    lateinit var webClient: WebClient

    @MockK
    lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @MockK
    lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @MockK
    lateinit var responseSpec: WebClient.ResponseSpec

    private lateinit var client: GitHubSearchClient

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        client = GitHubSearchClient(webClient)

        every { webClient.get() } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.uri(any<java.util.function.Function<UriBuilder, URI>>()) } returns requestHeadersSpec as WebClient.RequestHeadersSpec<Any>
        every { requestHeadersSpec.retrieve() } returns responseSpec
    }

    @Test
    fun `search returns items from GitHub`() {
        val items = listOf(
            GitHubRepoItem(
                name = "repo1",
                owner = GitHubOwner("user1"),
                stargazers_count = 10,
                forks_count = 5,
                created_at = Instant.now(),
                updated_at = Instant.now(),
                language = "Kotlin"
            )
        )

        every { responseSpec.bodyToMono(GitHubSearchResponse::class.java).block() } returns GitHubSearchResponse(items)

        val result = client.search(LocalDate.of(2022,1,1), "Kotlin", 1, 10)

        assertEquals(1, result.size)
        assertEquals("repo1", result[0].name)
        assertEquals("user1", result[0].owner.login)
    }
}
