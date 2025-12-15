package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.BookRepository
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
    private val bookRepository: BookRepository,
    private val authorDomainService: AuthorDomainService,
) {

    /** IDを元に書籍を全項目上書きする。 */
    @Transactional
    fun exec(parameter: Parameter): CommandBookOutput {
        return runUseCase {
            val bookId = BookId.of(parameter.bookId)
            val existing = bookRepository.findById(bookId)
                ?: throw ApplicationException(
                    ApplicationErrorCode.BOOK_NOT_FOUND,
                    "Book not found: ${bookId.value}"
                )

            val updated = applyUpdates(existing, parameter)
            val saved = bookRepository.save(updated)
            saved.toCommandOutput()
        }
    }

    private fun applyUpdates(book: Book, parameter: Parameter): Book {
        val title = Title.of(parameter.title)
        val price = Price.of(parameter.price)
        val authorIds = parameter.authorIds

        authorDomainService.ensureAllExist(authorIds)

        return book
            .withTitle(title)
            .withPrice(price)
            .withPublishStatus(parameter.publishStatus)
            .withAuthors(authorIds)
    }

    data class Parameter(
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
            ): Parameter = Parameter(
                bookId = bookId,
                title = title,
                price = price,
                publishStatus = publishStatus,
                authorIds = authorIds,
            )
        }
    }
}
