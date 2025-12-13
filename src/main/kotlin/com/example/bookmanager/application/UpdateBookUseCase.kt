package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.BookCommandRepository
import com.example.bookmanager.domain.Price
import com.example.bookmanager.domain.PublishStatus
import com.example.bookmanager.domain.Title
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID

/**
 * API 層からの PUT（冪等）更新に応えるユースケース。
 * - フィールドが指定されていない場合は変更しない。
 * - 非 null な項目に対して null が明示された場合はエラーにする。
 */
@Component
class UpdateBookUseCase(
    private val bookCommandRepository: BookCommandRepository,
    private val authorDomainService: AuthorDomainService
) {

    /**
     * IDを元に書籍を部分更新する。入力がある項目のみを検証して上書きする。
     */
    fun exec(command: Command): BookOutput {
        return runUseCase {
            val bookId = BookId.of(command.bookId)
            val existing = bookCommandRepository.findById(bookId)
                ?: throw ApplicationException(
                    ApplicationErrorCode.BOOK_NOT_FOUND,
                    "Book not found: ${bookId.value}"
                )

            val updated = applyUpdates(existing, command)
            val saved = bookCommandRepository.save(updated)
            saved.toOutput()
        }
    }

    private fun applyUpdates(book: Book, command: Command): Book {
        var updated = book

        if (command.title.isPresent) {
            val title = command.title.value ?: invalidNull("title")
            val titleVo = Title.of(title)
            updated = updated.withTitle(titleVo)
        }

        if (command.price.isPresent) {
            val price = command.price.value ?: invalidNull("price")
            val priceVo = Price.of(price)
            updated = updated.withPrice(priceVo)
        }

        if (command.publishStatus.isPresent) {
            val status = command.publishStatus.value ?: invalidNull("publishStatus")
            updated = updated.withPublishStatus(status)
        }

        if (command.authorIds.isPresent) {
            val authorIds = command.authorIds.value ?: invalidNull("authorIds")
            authorDomainService.ensureAllExist(authorIds)
            updated = updated.withAuthors(authorIds)
        }

        return updated
    }

    private fun invalidNull(fieldName: String): Nothing =
        throw ApplicationException(
            ApplicationErrorCode.INVALID_UPDATE_REQUEST,
            "Field '$fieldName' does not allow null for PUT update"
        )

    data class Command(
        val bookId: UUID,
        val title: UpdateField<String> = UpdateField.absent(),
        val price: UpdateField<BigDecimal> = UpdateField.absent(),
        val publishStatus: UpdateField<PublishStatus> = UpdateField.absent(),
        val authorIds: UpdateField<List<AuthorId>> = UpdateField.absent()
    ) {
        companion object {
            /**
             * コントローラ層などから nullable 値をそのまま受け取り、
             * null は「未指定」として扱うための補助コンストラクタ。
             */
            fun fromNullable(
                bookId: UUID,
                title: String? = null,
                price: BigDecimal? = null,
                publishStatus: PublishStatus? = null,
                authorIds: List<AuthorId>? = null
            ): Command = Command(
                bookId = bookId,
                title = UpdateField.fromNullable(title),
                price = UpdateField.fromNullable(price),
                publishStatus = UpdateField.fromNullable(publishStatus),
                authorIds = UpdateField.fromNullable(authorIds)
            )
        }
    }

    /**
     * 更新入力の有無を表す薄いラッパー。
     * - absent: 項目未指定（変更なし）
     * - value あり: 指定された値で更新
     * - value == null: 非 null 項目で使われた場合はエラーにする
     */
    data class UpdateField<T> private constructor(
        val value: T?,
        val isPresent: Boolean
    ) {
        companion object {
            fun <T> absent(): UpdateField<T> = UpdateField(value = null, isPresent = false)
            fun <T> of(value: T): UpdateField<T> = UpdateField(value = value, isPresent = true)
            fun <T> explicitNull(): UpdateField<T> = UpdateField(value = null, isPresent = true)
            fun <T> fromNullable(value: T?): UpdateField<T> =
                if (value == null) absent() else of(value)
        }
    }
}
