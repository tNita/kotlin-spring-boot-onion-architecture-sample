package com.example.bookmanager.application

import com.example.bookmanager.domain.BookId
import com.example.bookmanager.domain.query.BookQueryRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GetBookUseCase(
    private val bookQueryRepository: BookQueryRepository,
) {

    /**
     * 書籍IDをキーに取得する。存在しない場合は業務エラーとする。
     */
    fun exec(bookId: UUID): QueryBookOutput = runUseCase {
        val id = BookId.of(bookId)
        val book = bookQueryRepository.findById(id)
            ?: throw ApplicationException(
                ApplicationErrorCode.BOOK_NOT_FOUND,
                "指定された書籍が見つかりません (id=$bookId)"
            )
        book.toQueryOutput()
    }
}
