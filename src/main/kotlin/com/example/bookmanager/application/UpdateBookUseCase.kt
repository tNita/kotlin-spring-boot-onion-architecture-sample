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
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/**
 * 書籍の更新のユースケース。
 */
@Component
class UpdateBookUseCase(
    private val bookCommandRepository: BookCommandRepository,
    private val authorDomainService: AuthorDomainService,
) {

    /** IDを元に書籍を全項目上書きする。 */
    @Transactional
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
        val title = Title.of(command.title)
        val price = Price.of(command.price)
        val authorIds = command.authorIds

        authorDomainService.ensureAllExist(authorIds)

        return book
            .withTitle(title)
            .withPrice(price)
            .withPublishStatus(command.publishStatus)
            .withAuthors(authorIds)
    }

    data class Command(
        val bookId: UUID,
        val title: String,
        val price: BigDecimal,
        val publishStatus: PublishStatus,
        val authorIds: List<AuthorId>
    ) {
        companion object {
            fun from(
                bookId: UUID,
                title: String,
                price: BigDecimal,
                publishStatus: PublishStatus,
                authorIds: List<AuthorId>
            ): Command = Command(
                bookId = bookId,
                title = title,
                price = price,
                publishStatus = publishStatus,
                authorIds = authorIds,
            )
        }
    }
}
