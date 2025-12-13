package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.Id
import com.example.bookmanager.domain.query.AuthorQueryRepository
import com.example.bookmanager.domain.query.BookQueryRepository
import org.springframework.stereotype.Component

@Component
class SearchBookUseCase(
    private val authorQueryRepository: AuthorQueryRepository,
    private val bookQueryRepository: BookQueryRepository
) {

    /**
     * 著者名から書籍を検索し、重複を排除して返す。
     */
    fun exec(authorName: String): List<BookOutput> {
        return runUseCase {
            val name = AuthorName.of(authorName)
            val authors = authorQueryRepository.findByName(name)
            val authorIds = authors.map { author -> Id.generate { author.id } }
            bookQueryRepository.findByAuthorIds(authorIds)
                .distinctBy { it.id }
                .map { book -> book.toOutput() }
        }
    }
}
