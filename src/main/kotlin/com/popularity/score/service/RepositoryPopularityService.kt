package com.popularity.score.service

import com.popularity.score.client.GitHubSearchClient
import com.popularity.score.dto.GitHubRepoItem
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ln

@Service
class RepositoryPopularityService(private val client: GitHubSearchClient) {
    companion object {
        private const val STAR_IMPORTANCE_FACTOR = 0.7
    }

    fun getPopularRepositories(
        createdAfter: LocalDate,
        language: String,
        page: Int = 1,
        size: Int = 30
    ): List<GitHubRepoItem> {
        val repos = client.search(createdAfter, language, page, size)
        val now = Instant.now()

        return repos.asSequence()
            .map { repo ->
                repo.copy(popularityScore = calculatePopularityScore(repo, now))
            }
            .sortedByDescending { it.popularityScore }
            .take(50)
            .toList()
    }

    // The score function here is private to the service
    private fun calculatePopularityScore(repo: GitHubRepoItem, now: Instant = Instant.now()): Double {
        val starsLog = ln((repo.stargazers_count + 1).toDouble()) / ln(2.0)
        val forksLog = ln((repo.forks_count + 1).toDouble()) / ln(2.0)

        val daysSinceUpdate = ChronoUnit.DAYS.between(repo.updated_at, now)
        val recencyFactor = when {
            daysSinceUpdate <= 7 -> 1.5
            daysSinceUpdate <= 30 -> 1.2
            else -> 1.0
        }

        return (
                starsLog * STAR_IMPORTANCE_FACTOR + forksLog * (1 - STAR_IMPORTANCE_FACTOR)
            ) * recencyFactor
    }
}