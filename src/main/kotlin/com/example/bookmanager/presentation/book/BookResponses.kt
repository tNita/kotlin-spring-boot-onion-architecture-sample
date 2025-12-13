package com.example.bookmanager.presentation.book

import com.example.bookmanager.application.BookOutput
import com.example.bookmanager.domain.PublishStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "書籍レスポンス")
data class BookResponse(
    @Schema(description = "書籍ID", example = "1")
    val id: Long,
    @Schema(description = "タイトル", example = "ノルウェイの森")
    val title: String,
    @Schema(description = "価格", example = "1980")
    val price: BigDecimal,
    @Schema(description = "出版状況", example = "PUBLISHED")
    val publishStatus: PublishStatus,
    @Schema(description = "著者IDの配列", example = "[\"018d1a2e-3b34-780a-a516-8a3f8a4f9a11\"]")
    val authorIds: List<UUID>,
)

fun BookOutput.toResponse(): BookResponse = BookResponse(
    id = id,
    title = title,
    price = price,
    publishStatus = PublishStatus.valueOf(publishStatus),
    authorIds = authorIds,
)
