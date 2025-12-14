package com.example.bookmanager.presentation.integration.author

import com.example.bookmanager.support.author.AuthorFixture
import com.example.bookmanager.support.author.insert
import com.example.bookmanager.support.db.deleteAllTables
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthorSearchApiIntegrationTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var dsl: DSLContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        dsl.deleteAllTables()
    }

    @Test
    fun `GET 著者IDで検索できる`() {
        val author = AuthorFixture.natsume()
        dsl.insert(author)

        mockMvc.perform(get("/api/authors/search").param("id", author.id.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$[0].id").value(author.id.toString()))
            .andExpect(jsonPath("$[0].name").value(author.name))
            .andExpect(jsonPath("$[0].birthDate").value(author.birthDate.toString()))
    }
}
