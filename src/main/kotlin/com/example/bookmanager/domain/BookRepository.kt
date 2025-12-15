package com.example.bookmanager.domain

interface BookRepository {
    fun save(book: Book): Book
    fun findById(id: BookId): Book?
}
