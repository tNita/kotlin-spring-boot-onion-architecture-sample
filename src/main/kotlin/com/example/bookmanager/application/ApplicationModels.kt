package com.example.bookmanager.application

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.query.AuthorView
import com.example.bookmanager.domain.query.BookView
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

/**
 * アプリケーション層からプレゼンテーション層へ返却するシンプルなレスポンスモデル。
 * ドメインの値オブジェクト／エンティティはそのまま漏らさない。
 */
data class AuthorResult(
    val id: UUID,
    val name: String,
    val birthDate: LocalDate
)

data class BookOutput(
    val id: UUID,
    val title: String,
    val price: BigDecimal,
    val publishStatus: String,
    val authorIds: List<UUID>,
    val authors: List<AuthorResult> = emptyList(),
)

fun Author.toResult(): AuthorResult =
    AuthorResult(
        id = this.id.value,
        name = this.name.value,
        birthDate = this.birthDate.value
    )

fun AuthorView.toResult(): AuthorResult =
    AuthorResult(
        id = this.id,
        name = this.name,
        birthDate = this.birthDate
    )

fun Book.toOutput(authors: List<AuthorResult> = emptyList()): BookOutput =
    BookOutput(
        id = this.id.value,
        title = this.title.value,
        price = this.price.amount,
        publishStatus = this.publishStatus.name,
        authorIds = this.authorIds.map { it.value },
        authors = authors,
    )

fun BookView.toOutput(): BookOutput =
    BookOutput(
        id = this.id,
        title = this.title,
        price = this.price,
        publishStatus = this.publishStatus,
        authorIds = this.authors.map { it.id },
        authors = this.authors.map { it.toResult() },
    )
