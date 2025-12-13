package com.example.bookmanager.presentation.author

import com.example.bookmanager.application.GetAuthorUseCase
import com.example.bookmanager.application.RegisterAuthorUseCase
import com.example.bookmanager.domain.AuthorId
import com.example.bookmanager.domain.Id
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/authors")
@Validated
@Tag(name = "Authors", description = "著者管理API")
class AuthorController(
    private val registerAuthorUseCase: RegisterAuthorUseCase,
    private val getAuthorUseCase: GetAuthorUseCase,
) {

    @PostMapping
    @Operation(
        summary = "著者登録",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "登録成功",
                content = [Content(schema = Schema(implementation = AuthorResponse::class))],
            ),
            ApiResponse(responseCode = "400", description = "リクエスト不正"),
            ApiResponse(responseCode = "409", description = "著者の重複"),
        ],
    )
    fun register(
        @Valid @RequestBody request: RegisterAuthorRequest,
    ): ResponseEntity<AuthorResponse> {
        val created = registerAuthorUseCase.exec(
            RegisterAuthorUseCase.Command(
                name = request.name,
                birthDate = request.birthDate,
            ),
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponse())
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "著者取得",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "取得成功",
                content = [Content(schema = Schema(implementation = AuthorResponse::class))],
            ),
            ApiResponse(responseCode = "404", description = "著者が存在しない"),
        ],
    )
    fun find(
        @Parameter(description = "著者ID", example = "018d1a2e-3b34-780a-a516-8a3f8a4f9a11")
        @PathVariable id: UUID,
    ): ResponseEntity<AuthorResponse> {
        val authorId: AuthorId = Id.generate { id }
        val result = getAuthorUseCase.exec(authorId)
        return ResponseEntity.ok(result.toResponse())
    }
}
