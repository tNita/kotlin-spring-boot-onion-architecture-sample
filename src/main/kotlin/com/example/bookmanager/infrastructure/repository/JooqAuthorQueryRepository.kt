package com.example.bookmanager.infrastructure.repository

import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.AuthorName
import com.example.bookmanager.domain.query.AuthorQueryRepository
import com.example.bookmanager.domain.query.AuthorView
import com.example.bookmanager.jooq.tables.Authors.AUTHORS
import com.example.bookmanager.jooq.tables.records.AuthorsRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqAuthorQueryRepository(
    private val dsl: DSLContext,
) : AuthorQueryRepository {
    override fun findById(id: AuthorId): AuthorView? =
        dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id.value))
            .fetchOne()
            ?.toView()

    override fun findByName(name: AuthorName): List<AuthorView> =
        dsl.selectFrom(AUTHORS)
            .where(AUTHORS.NAME.eq(name.value))
            .fetch()
            .mapNotNull { it.toView() }

    override fun search(id: AuthorId?, name: AuthorName?): List<AuthorView> {
        val conditions = mutableListOf<Condition>()
        id?.let { conditions.add(AUTHORS.ID.eq(it.value)) }
        name?.let { conditions.add(AUTHORS.NAME.containsIgnoreCase(it.value)) }
        if (conditions.isEmpty()) return emptyList()

        return dsl.selectFrom(AUTHORS)
            .where(conditions)
            .fetch()
            .mapNotNull { it.toView() }
    }

    private fun AuthorsRecord.toView(): AuthorView? {
        val id = this.id ?: return null
        val name = this.name ?: return null
        val birthDate = this.birthDate ?: return null
        return AuthorView(
            id = id,
            name = name,
            birthDate = birthDate
        )
    }
}
