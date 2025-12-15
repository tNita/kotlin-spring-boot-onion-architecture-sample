package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.query.BookQueryRepository
import org.springframework.stereotype.Component

@Component
class SearchBookUseCase(
    private val bookQueryRepository: BookQueryRepository
) {

    /**
     * 著者名から書籍を検索し、重複を排除して返す。
     */
    fun exec(authorName: String): List<QueryBookOutput> {
        return runUseCase {
            val name = AuthorName.of(authorName)
            bookQueryRepository.findByAuthorName(name)
                .map { book -> book.toQueryOutput() }
        }
    }
}
