package com.example.bookmanager.application

import com.example.bookmanager.domain.Author
import com.example.bookmanager.domain.AuthorCommandRepository
import com.example.bookmanager.domain.AuthorDomainService
import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.BirthDate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class RegisterAuthorUseCase(
    private val authorCommandRepository: AuthorCommandRepository,
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
            authorDomainService.ensureNotDuplicated(name, birthDate)

            val author = Author.create(name, birthDate)
            val saved = authorCommandRepository.save(author)
            saved.toResult()
        }
    }

    data class Parameter(
        val name: String,
        val birthDate: LocalDate
    )
}
