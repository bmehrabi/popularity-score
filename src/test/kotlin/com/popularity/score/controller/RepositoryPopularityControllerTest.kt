package com.popularity.score.controller

import com.ninjasquad.springmockk.MockkBean
import com.popularity.score.dto.GitHubOwner
import com.popularity.score.dto.GitHubRepoItem
import com.popularity.score.service.RepositoryPopularityService
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant
import java.time.LocalDate

@WebMvcTest(RepositoryPopularityController::class)
class RepositoryPopularityControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var repositoryPopularityService: RepositoryPopularityService

    private val testItems = listOf(
        GitHubRepoItem(
            name = "repo1",
            owner = GitHubOwner("user1"),
            stargazers_count = 10,
            forks_count = 5,
            created_at = Instant.parse("2022-01-01T00:00:00Z"),
            updated_at = Instant.parse("2022-02-01T00:00:00Z"),
            language = "Kotlin"
        )
    )

    @BeforeEach
    fun setup() {
        every { repositoryPopularityService.getPopularRepositories(
            LocalDate.of(2022,1,1),
            "Kotlin",
            1,
            30)
        } returns testItems
    }

    @Test
    fun `GET repositories returns JSON list`() {
        mockMvc.get("/api/v1/repositories") {
            param("createdAfter", "2022-01-01")
            param("language", "Kotlin")
            param("page", "1")
            param("size", "30")
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].name") { value("repo1") }
            jsonPath("$[0].owner.login") { value("user1") }
        }

        // Verify that the controller called the service correctly
        verify { repositoryPopularityService.getPopularRepositories(
            LocalDate.of(2022,1,1), "Kotlin", 1, 30)
        }
    }
}
