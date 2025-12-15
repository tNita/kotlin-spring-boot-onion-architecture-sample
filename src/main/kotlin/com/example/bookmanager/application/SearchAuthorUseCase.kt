package com.example.bookmanager.application

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.query.AuthorQueryRepository
import com.example.bookmanager.shared.Id
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SearchAuthorUseCase(
    private val authorQueryRepository: AuthorQueryRepository,
) {
    /**
     * 著者IDまたは著者名(曖昧)で検索する。
     */
    fun exec(id: UUID?, name: String?): List<AuthorResult> = runUseCase {
        val authorId: AuthorId? = id?.let { uuid -> Id.generate { uuid } }
        val authorName = name?.takeIf { it.isNotBlank() }?.let { AuthorName.of(it) }
        if (authorId == null && authorName == null) {
            throw ApplicationException(ApplicationErrorCode.INVALID_REQUEST, "Either id or name must be provided")
        }
        authorQueryRepository.search(authorId, authorName)
            .map { it.toResult() }
    }
}
