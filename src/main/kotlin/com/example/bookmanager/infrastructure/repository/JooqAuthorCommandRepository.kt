package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.AuthorCommandRepository
import com.example.bookmanager.domain.DomainErrorCode
import com.example.bookmanager.domain.DomainException
import com.example.bookmanager.jooq.tables.Authors.AUTHORS
import org.jooq.DSLContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class JooqAuthorCommandRepository(
    private val dsl: DSLContext,
) : AuthorCommandRepository {
    override fun save(author: Author): Author {
        dsl.insertInto(AUTHORS)
            .set(AUTHORS.ID, author.id.value)
            .set(AUTHORS.NAME, author.name.value)
            .set(AUTHORS.BIRTH_DATE, author.birthDate.value)
            .execute()
        return author
    }
}
