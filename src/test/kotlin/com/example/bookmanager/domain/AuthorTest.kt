package com.example.bookmanager.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AuthorTest {

    @Test
    fun `プロフィール更新は不変でIDを保持する`() {
        val author = Author.ofExisting(
            id = Id.generate { UUID.fromString("01890a3f-54c1-7f1e-8b10-cbc61ff35f4d") },
            name = AuthorName.of("Old Name"),
            birthDate = BirthDate.of(LocalDate.parse("1990-01-01"))
        )

        val updated = author.updateProfile(
            AuthorName.of("New Name"),
            BirthDate.of(LocalDate.parse("1991-02-02"))
        )

        assertEquals("New Name", updated.name.value)
        assertEquals(LocalDate.parse("1991-02-02"), updated.birthDate.value)
        assertEquals(author.id, updated.id)
    }

    @Test
    fun `名前と生年月日が同じなら同一人物とみなす`() {
        val sameName = AuthorName.of("Same")
        val sameBirthDate = BirthDate.of(LocalDate.parse("1990-01-01"))
        val differentName = AuthorName.of("Different")

        val a = Author.create(sameName, sameBirthDate)
        val b = Author.create(sameName, sameBirthDate)
        val c = Author.create(differentName, sameBirthDate)

        assertTrue(a.isSamePerson(b))
        assertFalse(a.isSamePerson(c))
    }
}
