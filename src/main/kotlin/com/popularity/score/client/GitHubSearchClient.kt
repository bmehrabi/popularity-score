package com.popularity.score.client

import com.popularity.score.dto.GitHubRepoItem
import com.popularity.score.dto.GitHubSearchResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate


@Component
class GitHubSearchClient(
    private val githubWebClient: WebClient
) {

    fun search(
        createdAfter: LocalDate,
        language: String,
        page: Int,
        size: Int
    ): List<GitHubRepoItem> {

        val query = "language:$language created:>=${createdAfter}"

        return githubWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search/repositories")
                    .queryParam("q", query)
                    .queryParam("sort", "stars")
                    .queryParam("order", "desc")
                    .queryParam("page", page)
                    .queryParam("per_page", size)
                    .build()
            }
            .retrieve()
            .bodyToMono(GitHubSearchResponse::class.java)
            .block()
            ?.items
            ?: emptyList()
    }
}
