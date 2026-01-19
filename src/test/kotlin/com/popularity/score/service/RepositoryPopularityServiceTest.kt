package com.popularity.score.service

import com.popularity.score.client.GitHubSearchClient
import com.popularity.score.dto.GitHubOwner
import com.popularity.score.dto.GitHubRepoItem
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

class RepositoryPopularityServiceTest {

    private val client: GitHubSearchClient = mockk()
    private val service = RepositoryPopularityService(client)

    @Test
    fun `getPopularRepositories calculates popularity score and sorts descending`() {
        val now = Instant.now()

        val repo1 = GitHubRepoItem(
            name = "repo1",
            owner = GitHubOwner("user1"),
            stargazers_count = 100,
            forks_count = 50,
            created_at = Instant.now(),
            updated_at = now.minusSeconds(2 * 24 * 3600), // updated 2 days ago
            language = "Kotlin"
        )

        val repo2 = GitHubRepoItem(
            name = "repo2",
            owner = GitHubOwner("user2"),
            stargazers_count = 50,
            forks_count = 100,
            created_at = Instant.now(),
            updated_at = now.minusSeconds(40 * 24 * 3600), // updated 40 days ago
            language = "Kotlin"
        )

        every { client.search(any(), any(), any(), any()) } returns listOf(repo1, repo2)

        val result = service.getPopularRepositories(LocalDate.now().minusMonths(1), "Kotlin")

        assert(result[0].popularityScore >= result[1].popularityScore)
        assert(result.all { it.popularityScore > 0.0 })
        assert(result.map { it.name }.containsAll(listOf("repo1", "repo2")))

        val scoreRepo1 = result.find { it.name == "repo1" }!!.popularityScore
        val scoreRepo2 = result.find { it.name == "repo2" }!!.popularityScore

        assert(scoreRepo1 > scoreRepo2)
    }

    @Test
    fun `getPopularRepositories handles empty list`() {
        every { client.search(any(), any(), any(), any()) } returns emptyList()

        val result = service.getPopularRepositories(LocalDate.now().minusMonths(1), "Kotlin")

        assertEquals(0, result.size)
    }
}
