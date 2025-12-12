package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.AuthorRepository
import org.springframework.stereotype.Component

@Component
class GetAuthorUseCase(
    private val authorRepository: AuthorRepository
) {

    /**
     * 著者IDをキーに取得し、見つからない場合は業務エラーを返す。
     */
    fun exec(authorId: AuthorId): AuthorOutput =
        authorRepository.findById(authorId)?.let { AuthorOutput(it) }
            ?: throw ApplicationException(
                ApplicationErrorCode.AUTHOR_NOT_FOUND,
                "Author not found: ${authorId.value}"
            )
}
