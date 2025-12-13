package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.AuthorRepository
import com.example.bookmanager.domain.BirthDate
import com.example.bookmanager.domain.Id
import com.example.bookmanager.jooq.tables.Authors.AUTHORS
import com.example.bookmanager.jooq.tables.records.AuthorsRecord
import org.jooq.DSLContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class JooqAuthorRepository(
    private val dsl: DSLContext,
) : AuthorRepository {
    override fun save(author: Author): Author {
        dsl.insertInto(AUTHORS)
            .set(AUTHORS.ID, author.id.value)
            .set(AUTHORS.NAME, author.name.value)
            .set(AUTHORS.BIRTH_DATE, author.birthDate.value)
            .onConflict(AUTHORS.ID)
            .doUpdate()
            .set(AUTHORS.NAME, author.name.value)
            .set(AUTHORS.BIRTH_DATE, author.birthDate.value)
            .execute()
        return author
    }

    override fun findById(id: AuthorId): Author? =
        dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id.value))
            .fetchOne()
            ?.toAuthor()

    override fun findByName(name: AuthorName): List<Author> =
        dsl.selectFrom(AUTHORS)
            .where(AUTHORS.NAME.eq(name.value))
            .fetch()
            .mapNotNull { it.toAuthor() }

    private fun AuthorsRecord.toAuthor(): Author? =
        Author.ofExisting(
            id = Id.generate { this.id },
            name = AuthorName.of(this.name),
            birthDate = BirthDate.of(this.birthDate),
        )
}
