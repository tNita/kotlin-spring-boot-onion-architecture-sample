package com.example.bookmanager.application

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.AuthorRepository
import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.BirthDate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class RegisterAuthorUseCase(
    private val authorRepository: AuthorRepository,
    private val authorDomainService: AuthorDomainService
) {

    /**
     * 著者の重複を避けつつ登録する。
     */
    @Transactional
    fun exec(parameter: Parameter): AuthorResult {
        return runUseCase {
            val name = AuthorName.of(parameter.name)
            val birthDate = BirthDate.of(parameter.birthDate)
            val author = Author.create(name, birthDate)
            authorDomainService.ensureNotDuplicated(author)

            val saved = authorRepository.save(author)
            saved.toResult()
        }
    }

    data class Parameter(
        val name: String,
        val birthDate: LocalDate
    )
}
