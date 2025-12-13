package com.example.bookmanager.application

import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.BookRepository
import org.springframework.stereotype.Component

@Component
class GetBookUseCase(
    private val bookRepository: BookRepository,
) {

    /**
     * 書籍IDをキーに取得する。存在しない場合は業務エラーとする。
     */
    fun exec(bookId: Long): BookOutput = runUseCase {
        val id = BookId.of(bookId)
        val book = bookRepository.findById(id)
            ?: throw ApplicationException(
                ApplicationErrorCode.BOOK_NOT_FOUND,
                "指定された書籍が見つかりません (id=$bookId)"
            )
        BookOutput(book)
    }
}
