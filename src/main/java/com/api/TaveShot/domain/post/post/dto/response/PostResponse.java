package com.api.TaveShot.domain.post.post.dto.response;

import com.api.TaveShot.domain.Member.domain.Tier;
import com.api.TaveShot.domain.post.comment.dto.response.CommentListResponse;
import com.api.TaveShot.domain.post.image.converter.ImageConverter;
import com.api.TaveShot.domain.post.image.domain.Image;
import com.api.TaveShot.domain.post.image.dto.ImageResponse;
import com.api.TaveShot.global.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** 게시글 정보 리턴할 Response 클래스 **/
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "게시글 정보 응답 클래스")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "제목입니다")
    private String title;

    @Schema(description = "게시글 내용", example = "내용입니다")
    private String content;

    @Schema(description = "작성자 Github 이름", example = "toychip")
    private String writer;

    @Schema(description = "작성자 티어", example = "Gold")
    private Tier writerTier;

    @Schema(description = "조회수", example = "100")
    private int view;

    @Schema(description = "댓글 수", example = "10")
    private int commentCount;

    @Schema(description = "작성자 ID", example = "1")
    private Long writerId;

    @Schema(description = "작성자 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private String writerProfileImgUrl;

    @Schema(description = "작성 시간", example = "2024-05-21T15:51:30")
    private String writtenTime;

    @Schema(description = "이미지 URL 목록")
    private List<ImageResponse> imageUrls;

    @Schema(description = "댓글 목록 응답")
    private CommentListResponse commentListResponse;

    @Builder
    public PostResponse(Long postId, String title, String content, String writer, Tier tier, Integer view,
                        Integer commentCount, Long writerId, String writerProfileImgUrl, LocalDateTime createdDate, List<Image> images) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.writerTier = tier;
        this.view = view;
        this.commentCount = commentCount;
        this.writerId = writerId;
        this.writerProfileImgUrl = writerProfileImgUrl;
        writtenTime = TimeUtil.formatCreatedDate(createdDate);
        this.imageUrls = ImageConverter.imageToImageResponse(images);
    }
}