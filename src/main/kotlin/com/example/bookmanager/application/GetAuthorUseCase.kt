package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.query.AuthorQueryRepository
import org.springframework.stereotype.Component

@Component
class GetAuthorUseCase(
    private val authorQueryRepository: AuthorQueryRepository
) {

    /**
     * 著者IDをキーに取得し、見つからない場合は業務エラーを返す。
     */
    fun exec(authorId: AuthorId): AuthorResult = runUseCase {
        authorQueryRepository.findById(authorId)?.toResult()
            ?: throw ApplicationException(
                ApplicationErrorCode.AUTHOR_NOT_FOUND,
                "Author not found: ${authorId.value}"
            )
    }
}
