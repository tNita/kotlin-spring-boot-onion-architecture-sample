package com.example.bookmanager.domain

interface AuthorCommandRepository {
    fun save(author: Author): Author
}
