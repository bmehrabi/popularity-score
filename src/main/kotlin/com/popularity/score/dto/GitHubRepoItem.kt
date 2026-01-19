package com.popularity.score.dto

import java.time.Instant

data class GitHubRepoItem (
    val name: String,
    val owner: GitHubOwner,
    val stargazers_count: Int,
    val forks_count: Int,
    val updated_at: Instant,
    val created_at: Instant,
    val language: String?,
    var popularityScore: Double = 0.0,
)