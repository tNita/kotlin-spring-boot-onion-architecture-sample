package com.example.bookmanager.domain

import org.springframework.stereotype.Component

/**
 * 著者に関する整合性チェックを担うドメインサービス。
 */
@Component
class AuthorDomainService(
    private val authorRepository: AuthorRepository
) {

    fun ensureNotDuplicated(name: AuthorName, birthDate: BirthDate) {
        val alreadyExists = authorRepository.findByName(name).any { it.birthDate == birthDate }
        if (alreadyExists) {
            throw DomainException(DomainErrorCode.AUTHOR_DUPLICATE, "Author already registered")
        }
    }

    fun ensureAllExist(authorIds: Collection<AuthorId>) {
        val missing = authorIds.filter { authorRepository.findById(it) == null }
        if (missing.isNotEmpty()) {
            val joinedIds = missing.joinToString(",") { it.value.toString() }
            throw DomainException(DomainErrorCode.AUTHOR_NOT_FOUND, "Author not found: $joinedIds")
        }
    }
}
