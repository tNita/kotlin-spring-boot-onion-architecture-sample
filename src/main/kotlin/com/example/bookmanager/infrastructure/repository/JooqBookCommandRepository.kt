package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.BookCommandRepository
import com.example.bookmanager.domain.Id
import com.example.bookmanager.domain.Price
import com.example.bookmanager.domain.PublishStatus
import com.example.bookmanager.domain.Title
import com.example.bookmanager.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.example.bookmanager.jooq.tables.Books.BOOKS
import com.example.bookmanager.jooq.tables.records.BooksRecord
import org.jooq.DSLContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
@Primary
class JooqBookCommandRepository(
    private val dsl: DSLContext,
) : BookCommandRepository {
    override fun save(book: Book): Book {
        return dsl.transactionResult { config ->
            val tx = config.dsl()
            val bookId = upsertBook(tx, book)
            replaceBookAuthors(tx, bookId, book.authorIds)
            book.copy(id = bookId)
        }
    }

    override fun findById(id: BookId): Book? {
        val record = dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id.value))
            .fetchOne() ?: return null

        val authorIds = dsl.select(BOOK_AUTHORS.AUTHOR_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(id.value))
            .fetchInto(UUID::class.java)
            .map { uuid -> Id.generate { uuid } }
            .toSet()

        return record.toDomain(authorIds)
    }

    private fun upsertBook(tx: DSLContext, book: Book): BookId =
        if (book.id == null) {
            val insertedId = tx.insertInto(BOOKS)
                .columns(BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISH_STATUS)
                .values(book.title.value, book.price.amount, book.publishStatus.name)
                .returning(BOOKS.ID)
                .fetchOne()
                ?.id
                ?: error("書籍IDの採番に失敗しました")
            BookId.of(insertedId)
        } else {
            tx.update(BOOKS)
                .set(BOOKS.TITLE, book.title.value)
                .set(BOOKS.PRICE, book.price.amount)
                .set(BOOKS.PUBLISH_STATUS, book.publishStatus.name)
                .where(BOOKS.ID.eq(book.id.value))
                .execute()
            book.id
        }

    private fun replaceBookAuthors(tx: DSLContext, bookId: BookId, authorIds: Set<Id>) {
        tx.deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId.value))
            .execute()

        if (authorIds.isEmpty()) return

        authorIds.forEach { authorId ->
            tx.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId.value)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId.value)
                .execute()
        }
    }

    private fun BooksRecord.toDomain(authorIds: Set<Id>): Book =
        Book.ofExisting(
            id = BookId.of(this.id!!),
            title = Title.of(this.title!!),
            price = Price.of(this.price ?: BigDecimal.ZERO),
            publishStatus = PublishStatus.valueOf(this.publishStatus!!),
            authorIds = authorIds,
        )
}
