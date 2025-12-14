package com.example.bookmanager.presentation.book

import com.example.bookmanager.application.BookOutput
import com.example.bookmanager.application.UpdateBookUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/books")
@Validated
@Tag(name = "Books", description = "書籍管理API")
class BookUpdateController(
    private val updateBookUseCase: UpdateBookUseCase,
) {
    @PutMapping("/{id}")
    @Operation(
        summary = "書籍更新（指定項目のみ上書き）",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "更新成功",
                content = [Content(schema = Schema(implementation = BookResponse::class))],
            ),
            ApiResponse(responseCode = "400", description = "リクエスト不正"),
            ApiResponse(responseCode = "404", description = "書籍または著者が存在しない"),
        ],
    )
    fun update(
        @Parameter(description = "書籍ID", example = "018d1a2e-3b34-780a-a516-8a3f8a4f9a11")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateBookRequest,
    ): ResponseEntity<BookResponse> {
        val updated = updateBookUseCase.exec(request.toCommand(id))
        return ResponseEntity.ok(updated.toResponse())
    }
}
