package com.example.bookmanager.domain

import com.example.bookmanager.domain.query.AuthorQueryRepository
import org.springframework.stereotype.Component

/**
 * 著者に関する整合性チェックを担うドメインサービス。
 */
@Component
class AuthorDomainService(
    private val authorQueryRepository: AuthorQueryRepository
) {

    fun ensureNotDuplicated(name: AuthorName, birthDate: BirthDate) {
        val alreadyExists = authorQueryRepository.findByName(name).any { it.birthDate.isEqual(birthDate.value) }
        if (alreadyExists) {
            throw DomainException(DomainErrorCode.AUTHOR_DUPLICATE, "Author already registered")
        }
    }

    fun ensureAllExist(authorIds: Collection<AuthorId>) {
        val missing = authorIds.filter { authorQueryRepository.findById(it) == null }
        if (missing.isNotEmpty()) {
            val joinedIds = missing.joinToString(",") { it.value.toString() }
            throw DomainException(DomainErrorCode.AUTHOR_NOT_FOUND, "Author not found: $joinedIds")
        }
    }
}
