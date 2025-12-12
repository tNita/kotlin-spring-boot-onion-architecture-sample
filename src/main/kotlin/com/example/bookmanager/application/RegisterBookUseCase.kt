package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.BookRepository
import com.example.bookmanager.domain.Price
import com.example.bookmanager.domain.PublishStatus
import com.example.bookmanager.domain.Title
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class RegisterBookUseCase(
    private val bookRepository: BookRepository,
    private val authorDomainService: AuthorDomainService
) {

    /**
     * 著者の存在を検証しつつ書籍を登録する。
     */
    fun exec(command: Command): BookOutput {
        val title = Title.of(command.title)
        val price = Price.of(command.price)
        authorDomainService.ensureAllExist(command.authorIds)

        val book = Book.create(
            title = title,
            price = price,
            publishStatus = command.publishStatus,
            authorIds = command.authorIds
        )

        val saved = bookRepository.save(book)
        return BookOutput(saved)
    }

    data class Command(
        val title: String,
        val price: BigDecimal,
        val publishStatus: PublishStatus = PublishStatus.UNPUBLISHED,
        val authorIds: List<AuthorId>
    )
}
