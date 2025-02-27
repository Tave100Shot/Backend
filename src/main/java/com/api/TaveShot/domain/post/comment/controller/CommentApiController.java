package com.api.TaveShot.domain.post.comment.controller;

import com.api.TaveShot.domain.post.comment.dto.request.CommentCreateRequest;
import com.api.TaveShot.domain.post.comment.dto.request.CommentEditRequest;
import com.api.TaveShot.domain.post.comment.service.CommentService;
import com.api.TaveShot.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Comment API", description = "댓글 API입니다.")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentApiController {

    private final CommentService commentService;

    /* CREATE */
    @Operation(summary = "댓글 등록", description = "지정된 게시글에 댓글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PostMapping("/post/{postId}/comments")
    public SuccessResponse<Long> register(final @PathVariable Long postId, final @RequestBody CommentCreateRequest commentCreateRequest) {
        Long createdCommentId = commentService.register(postId, commentCreateRequest);
        return new SuccessResponse<>(createdCommentId);
    }


    /* UPDATE */
    @Operation(summary = "댓글 수정", description = "지정된 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @PatchMapping("/post/comments/{commentId}")
    public SuccessResponse<Long> edit(final @PathVariable Long commentId, final @RequestBody CommentEditRequest commentEditRequest) {
        commentService.edit(commentId, commentEditRequest);
        return new SuccessResponse<>(commentId);
    }


    /* DELETE */
    @Operation(summary = "댓글 삭제", description = "지정된 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class)))
    })
    @DeleteMapping("/post/comments/{commentId}")
    public SuccessResponse<Long> delete(final @PathVariable Long commentId) {
        commentService.delete(commentId);
        return new SuccessResponse<>(commentId);
    }
}
