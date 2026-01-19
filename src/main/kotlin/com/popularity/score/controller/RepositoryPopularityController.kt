package com.popularity.score.controller

import com.popularity.score.client.GitHubSearchClient
import com.popularity.score.dto.GitHubRepoItem
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/repositories")
class RepositoryPopularityController(
    private val gitHubSearchClient: GitHubSearchClient
) {

    @GetMapping
    fun searchRepositories(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        createdAfter: LocalDate,

        @RequestParam language: String,

        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "30") size: Int
    ): List<GitHubRepoItem> =
        gitHubSearchClient.search(createdAfter, language, page, size)
}
