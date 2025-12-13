package com.example.bookmanager.domain

interface BookCommandRepository {
    fun save(book: Book): Book
    fun findById(id: BookId): Book?
}
