package com.example.bookmanager.application

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.Book
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

/**
 * アプリケーション層からプレゼンテーション層へ返却するシンプルなレスポンスモデル。
 * ドメインの値オブジェクト／エンティティはそのまま漏らさない。
 */
data class AuthorOutput(
    val id: UUID,
    val name: String,
    val birthDate: LocalDate
)

data class BookOutput(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val publishStatus: String,
    val authorIds: List<UUID>
)

fun AuthorOutput(author: Author): AuthorOutput =
    AuthorOutput(
        id = author.id.value,
        name = author.name.value,
        birthDate = author.birthDate.value
    )

fun BookOutput(book: Book): BookOutput {
    val bookId = book.id ?: throw ApplicationException(ApplicationErrorCode.BOOK_ID_MISSING, "Book id is missing")
    return BookOutput(
        id = bookId.value,
        title = book.title.value,
        price = book.price.amount,
        publishStatus = book.publishStatus.name,
        authorIds = book.authorIds.map { it.value }
    )
}
