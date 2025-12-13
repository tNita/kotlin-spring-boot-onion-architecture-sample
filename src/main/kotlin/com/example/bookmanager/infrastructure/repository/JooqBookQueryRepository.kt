package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.query.BookQueryRepository
import com.example.bookmanager.domain.query.BookView
import com.example.bookmanager.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.example.bookmanager.jooq.tables.Books.BOOKS
import com.example.bookmanager.jooq.tables.records.BooksRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class JooqBookQueryRepository(
    private val dsl: DSLContext,
) : BookQueryRepository {
    override fun findById(id: BookId): BookView? {
        val record = dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id.value))
            .fetchOne()
            ?: return null

        val authorIds = loadAuthorIds(listOf(id.value))[id.value] ?: emptyList()
        return record.toView(authorIds)
    }

    override fun findByAuthorIds(authorIds: Collection<AuthorId>): List<BookView> {
        if (authorIds.isEmpty()) return emptyList()

        val authorUuidList = authorIds.map { it.value }
        val bookIds = dsl.select(BOOK_AUTHORS.BOOK_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.AUTHOR_ID.`in`(authorUuidList))
            .fetchInto(Long::class.java)
            .toSet()

        if (bookIds.isEmpty()) return emptyList()

        val authorMap = loadAuthorIds(bookIds)

        return dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.`in`(bookIds))
            .fetch()
            .map { record ->
                val authors = authorMap[record.id!!] ?: emptyList()
                record.toView(authors)
            }
    }

    private fun loadAuthorIds(bookIds: Collection<Long>): Map<Long, List<UUID>> =
        dsl.select(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.`in`(bookIds))
            .fetchGroups(
                BOOK_AUTHORS.BOOK_ID,
                BOOK_AUTHORS.AUTHOR_ID,
            )
            .mapValues { (_, values) -> values.toList() }

    private fun BooksRecord.toView(authorIds: List<UUID>): BookView =
        BookView(
            id = this.id!!,
            title = this.title!!,
            price = this.price ?: BigDecimal.ZERO,
            publishStatus = this.publishStatus!!,
            authorIds = authorIds
        )
}
