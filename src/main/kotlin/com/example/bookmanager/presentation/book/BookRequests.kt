package com.example.bookmanager.presentation.book

import com.example.bookmanager.application.RegisterBookUseCase
import com.example.bookmanager.application.UpdateBookUseCase
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Id
import com.example.bookmanager.domain.PublishStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "書籍登録リクエスト")
data class RegisterBookRequest(
    @field:NotBlank
    @field:Size(max = 255)
    @Schema(description = "タイトル", example = "ノルウェイの森")
    val title: String,
    @field:DecimalMin(value = "0.0", inclusive = true)
    @field:Digits(integer = 10, fraction = 2)
    @Schema(description = "価格", example = "1980")
    val price: BigDecimal,
    @Schema(description = "出版状況", example = "UNPUBLISHED", defaultValue = "UNPUBLISHED")
    val publishStatus: PublishStatus = PublishStatus.UNPUBLISHED,
    @field:NotEmpty
    @Schema(description = "著者IDの配列", example = "[\"018d1a2e-3b34-780a-a516-8a3f8a4f9a11\"]")
    val authorIds: List<UUID>,
) {
    fun toCommand(): RegisterBookUseCase.Command = RegisterBookUseCase.Command(
        title = title,
        price = price,
        publishStatus = publishStatus,
        authorIds = authorIds.toAuthorIds(),
    )
}

@Schema(description = "書籍更新リクエスト（指定された項目のみ更新）")
data class UpdateBookRequest(
    @field:Size(max = 255)
    @Schema(description = "タイトル", example = "新版 ノルウェイの森", required = false)
    val title: String? = null,
    @field:DecimalMin(value = "0.0", inclusive = true)
    @field:Digits(integer = 10, fraction = 2)
    @Schema(description = "価格", example = "2200", required = false)
    val price: BigDecimal? = null,
    @Schema(description = "出版状況", example = "PUBLISHED", required = false)
    val publishStatus: PublishStatus? = null,
    @Schema(description = "著者IDの配列", example = "[\"018d1a2e-3b34-780a-a516-8a3f8a4f9a11\"]", required = false)
    val authorIds: List<UUID>? = null,
) {
    fun toCommand(bookId: Long): UpdateBookUseCase.Command = UpdateBookUseCase.Command.fromNullable(
        bookId = bookId,
        title = title,
        price = price,
        publishStatus = publishStatus,
        authorIds = authorIds?.toAuthorIds(),
    )
}

private fun List<UUID>.toAuthorIds(): List<AuthorId> =
    map { uuid -> Id.generate { uuid } }
