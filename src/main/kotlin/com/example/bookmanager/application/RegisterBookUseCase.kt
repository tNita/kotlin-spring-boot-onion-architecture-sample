package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Book
import com.example.bookmanager.domain.BookRepository
import com.example.bookmanager.domain.Price
import com.example.bookmanager.domain.PublishStatus
import com.example.bookmanager.domain.Title
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class RegisterBookUseCase(
    private val bookRepository: BookRepository,
    private val authorDomainService: AuthorDomainService,
) {

    /**
     * 著者の存在を検証しつつ書籍を登録する。
     */
    @Transactional
    fun exec(parameter: Parameter): CommandBookOutput {
        return runUseCase {
            val title = Title.of(parameter.title)
            val price = Price.of(parameter.price)
            authorDomainService.ensureAllExist(parameter.authorIds)

            val book = Book.create(
                title = title,
                price = price,
                publishStatus = parameter.publishStatus,
                authorIds = parameter.authorIds
            )

            val saved = bookRepository.save(book)
            saved.toCommandOutput()
        }
    }

    data class Parameter(
        val title: String,
        val price: BigDecimal,
        val publishStatus: PublishStatus = PublishStatus.UNPUBLISHED,
        val authorIds: List<AuthorId>
    )
}
