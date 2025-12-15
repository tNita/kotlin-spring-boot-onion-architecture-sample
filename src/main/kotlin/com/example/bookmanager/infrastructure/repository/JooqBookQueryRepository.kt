package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.query.AuthorView
import com.example.bookmanager.domain.query.BookQueryRepository
import com.example.bookmanager.domain.query.BookView
import com.example.bookmanager.jooq.tables.Authors.AUTHORS
import com.example.bookmanager.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.example.bookmanager.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class JooqBookQueryRepository(
    private val dsl: DSLContext,
) : BookQueryRepository {
    override fun findById(id: BookId): BookView? {
        val records = dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS,
            AUTHORS.ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE,
        )
            .from(BOOKS)
            .leftJoin(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .leftJoin(AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOKS.ID.eq(id.value))
            .orderBy(BOOKS.ID, AUTHORS.ID)
            .fetch()
        if (records.isEmpty()) return null

        val first = records.first()
        val authors = records.mapNotNull { record ->
            val authorId = record.get(AUTHORS.ID) ?: return@mapNotNull null
            val name = record.get(AUTHORS.NAME) ?: return@mapNotNull null
            val birthDate = record.get(AUTHORS.BIRTH_DATE) ?: return@mapNotNull null
            AuthorView(authorId, name, birthDate)
        }

        return BookView(
            id = first.get(BOOKS.ID)!!,
            title = first.get(BOOKS.TITLE)!!,
            price = first.get(BOOKS.PRICE) ?: BigDecimal.ZERO,
            publishStatus = first.get(BOOKS.PUBLISH_STATUS)!!,
            authors = authors
        )
    }

    override fun findByAuthorIds(authorIds: Collection<AuthorId>): List<BookView> {
        if (authorIds.isEmpty()) return emptyList()

        val filterAuthors = BOOK_AUTHORS.`as`("filter_authors")
        val allAuthors = BOOK_AUTHORS.`as`("all_authors")
        val authorTable = AUTHORS.`as`("authors")

        val records = dsl.select(
            BOOKS.ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS,
            authorTable.ID,
            authorTable.NAME,
            authorTable.BIRTH_DATE,
        )
            .from(BOOKS)
            .leftJoin(allAuthors).on(allAuthors.BOOK_ID.eq(BOOKS.ID))
            .leftJoin(authorTable).on(authorTable.ID.eq(allAuthors.AUTHOR_ID))
            .whereExists(
                dsl.selectOne()
                    .from(filterAuthors)
                    .where(
                        filterAuthors.BOOK_ID.eq(BOOKS.ID)
                            .and(filterAuthors.AUTHOR_ID.`in`(authorIds.map { it.value }))
                    )
            )
            .orderBy(BOOKS.ID, authorTable.ID)
            .fetch()
        if (records.isEmpty()) return emptyList()

        return records
            .groupBy { it.get(BOOKS.ID)!! }
            .map { (bookId, rows) ->
                val first = rows.first()
                val authorList = rows.mapNotNull { row ->
                    val authorId = row.get(authorTable.ID) ?: return@mapNotNull null
                    val name = row.get(authorTable.NAME) ?: return@mapNotNull null
                    val birthDate = row.get(authorTable.BIRTH_DATE) ?: return@mapNotNull null
                    AuthorView(authorId, name, birthDate)
                }
                BookView(
                    id = bookId,
                    title = first.get(BOOKS.TITLE)!!,
                    price = first.get(BOOKS.PRICE) ?: BigDecimal.ZERO,
                    publishStatus = first.get(BOOKS.PUBLISH_STATUS)!!,
                    authors = authorList,
                )
            }
    }
}
