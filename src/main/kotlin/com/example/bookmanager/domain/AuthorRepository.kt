package com.example.bookmanager.domain

import com.example.bookmanager.domain.query.AuthorView

interface AuthorRepository {
    fun save(author: Author): Author
    fun findById(id: AuthorId): Author?
    fun findByName(name: AuthorName): List<Author>
    fun findByIds(authorIds: Collection<AuthorId>): List<Author>
}
