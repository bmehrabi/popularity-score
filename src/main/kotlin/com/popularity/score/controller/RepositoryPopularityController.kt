package com.popularity.score.controller

import com.popularity.score.dto.GitHubRepoItem
import com.popularity.score.service.RepositoryPopularityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Tag(name = "RepositoryPopularity", description = "Endpoints for fetching repositories and calculating popularity")
@RequestMapping("/api/v1/repositories")
class RepositoryPopularityController(
    private val repositoryPopularityService: RepositoryPopularityService
) {

    @GetMapping
    @Operation(
        summary = "Get popular repositories",
        description = "Fetch repositories created after a specific date and return their popularity score"
    )
    fun searchRepositories(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "ISO date to filter repositories created after this date", example = "2023-01-01")
        createdAfter: LocalDate,

        @Parameter(description = "Programming language of the repository", example = "Kotlin")
        @RequestParam language: String,

        @Parameter(description = "Page number", example = "1")
        @RequestParam(defaultValue = "1") page: Int,

        @Parameter(description = "Number of items per page", example = "30")
        @RequestParam(defaultValue = "30") size: Int
    ): List<GitHubRepoItem> =
        repositoryPopularityService.getPopularRepositories(createdAfter, language, page, size)
}
