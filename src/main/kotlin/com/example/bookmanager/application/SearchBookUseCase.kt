package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.AuthorRepository
import com.example.bookmanager.domain.BookRepository
import org.springframework.stereotype.Component

@Component
class SearchBookUseCase(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

    /**
     * 著者名から書籍を検索し、重複を排除して返す。
     */
    fun exec(authorName: String): List<BookOutput> {
        val name = AuthorName.of(authorName)
        val authors = authorRepository.findByName(name)
        return authors
            .flatMap { author -> bookRepository.findByAuthorId(author.id) }
            .distinctBy { it.id }
            .map { book -> BookOutput(book) }
    }
}
