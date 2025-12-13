package com.example.bookmanager.presentation.book

import com.example.bookmanager.application.BookOutput
import com.example.bookmanager.application.GetBookUseCase
import com.example.bookmanager.application.RegisterBookUseCase
import com.example.bookmanager.application.SearchBookUseCase
import com.example.bookmanager.application.UpdateBookUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/books")
@Validated
@Tag(name = "Books", description = "書籍管理API")
class BookController(
    private val registerBookUseCase: RegisterBookUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val searchBookUseCase: SearchBookUseCase,
    private val getBookUseCase: GetBookUseCase,
) {
    @PostMapping
    @Operation(
        summary = "書籍登録",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "登録成功",
                content = [Content(schema = Schema(implementation = BookResponse::class))],
            ),
            ApiResponse(responseCode = "400", description = "リクエスト不正"),
            ApiResponse(responseCode = "404", description = "著者が存在しない"),
        ],
    )
    fun register(
        @Valid @RequestBody request: RegisterBookRequest,
    ): ResponseEntity<BookResponse> {
        val created = registerBookUseCase.exec(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponse())
    }

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

    @GetMapping("/{id}")
    @Operation(
        summary = "書籍詳細取得",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "取得成功",
                content = [Content(schema = Schema(implementation = BookResponse::class))],
            ),
            ApiResponse(responseCode = "404", description = "書籍が存在しない"),
        ],
    )
    fun find(
        @Parameter(description = "書籍ID", example = "018d1a2e-3b34-780a-a516-8a3f8a4f9a11")
        @PathVariable id: UUID,
    ): ResponseEntity<BookResponse> {
        val result = getBookUseCase.exec(id)
        return ResponseEntity.ok(result.toResponse())
    }

    @GetMapping("/search")
    @Operation(
        summary = "著者名で書籍検索",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "取得成功",
                content = [Content(array = ArraySchema(schema = Schema(implementation = BookResponse::class)))],
            ),
            ApiResponse(responseCode = "400", description = "リクエスト不正"),
        ],
    )
    fun search(
        @Parameter(description = "著者名（完全一致）", example = "夏目漱石")
        @RequestParam @NotBlank authorName: String,
    ): List<BookResponse> = searchBookUseCase.exec(authorName).map(BookOutput::toResponse)
}
