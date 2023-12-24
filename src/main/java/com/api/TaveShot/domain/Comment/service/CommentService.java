package com.api.TaveShot.domain.Comment.service;

import com.api.TaveShot.domain.Comment.converter.CommentConverter;
import com.api.TaveShot.domain.Comment.domain.Comment;
import com.api.TaveShot.domain.Comment.dto.request.CommentCreateRequest;
import com.api.TaveShot.domain.Comment.dto.request.CommentUpdateRequest;
import com.api.TaveShot.domain.Comment.dto.response.CommentResponse;
import com.api.TaveShot.domain.Comment.repository.CommentRepository;
import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.domain.Tier;
import com.api.TaveShot.domain.post.post.domain.Post;
import com.api.TaveShot.domain.post.post.repository.PostRepository;
import com.api.TaveShot.domain.post.post.service.PostService;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.util.SecurityUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional // 데이터 변경하는 메서드에만 명시적으로 적용
    public Long register(final Long postId, final CommentCreateRequest request) {

        Member currentMember = getCurrentMember();
        validateAuthority(request, currentMember);

        // 어떤 게시판인지 Post에서 검증하는 것이 아닌 request에서 받고, member의 티어와 비교하면 됨 validateAuthority 참고
        Post post = getPost(postId);

        // ---------------- 부모 댓글 유무 확인 ----------------
        Long parentCommentId = request.getParentCommentId();

        Optional<Comment> parentCommentOptional = findParentComment(parentCommentId);

        return createComment(request, currentMember, post, parentCommentOptional);
    }

    private Long createComment(final CommentCreateRequest request, final Member currentMember,
                               final Post post, final Optional<Comment> parentCommentOptional) {
        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            return createWithParent(request, currentMember, post, parentComment);
        }

        return createNotParent(request, currentMember, post);
    }

    private void validateAuthority(final CommentCreateRequest request, final Member currentMember) {
        postService.validateAuthority(request.getPostTier(), currentMember);
    }

    private Member getCurrentMember() {
        return SecurityUtil.getCurrentMember();
    }

    private Post getPost(final Long postId) {
        return postService.findById(postId);
    }

    private Optional<Comment> findParentComment(final Long parentCommentId) {
        if (parentCommentId != null) {
            return commentRepository.findById(parentCommentId);
        }
        return Optional.empty();
    }

    private Long createWithParent(final CommentCreateRequest request, final Member currentMember,
                                  final Post post, final Comment findParentComment) {
        Comment comment = CommentConverter.createDtoToEntity(request.getComment(), currentMember,
                post, findParentComment);
        commentRepository.save(comment);
        return comment.getId();
    }

    private Long createNotParent(final CommentCreateRequest request, final Member currentMember,
                                 final Post post) {
        Comment comment = CommentConverter.createDtoToEntity(request.getComment(), currentMember, post);
        commentRepository.save(comment);
        return comment.getId();
    }

    public List<CommentResponse> findAll(Long postId, Pageable pageable) {
        Post post = getPost(postId);
        List<Comment> comments = commentRepository.findByParentCommentIsNull(post);
        return comments.stream()
                .map(comment -> CommentResponse.fromEntity(comment))
                .toList();
    }

    @Transactional
    public void update(Long postId, Long commentId, CommentUpdateRequest dto) {
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. " + commentId));

        comment.update(dto.getComment());
    }

    @Transactional
    public void delete(Long postId, Long commentId) {
        Comment comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new ApiException(ErrorType._POST_NOT_FOUND));

        commentRepository.delete(comment);
    }

    @Transactional
    public Long saveReply(Long postId, Long parentCommentId, CommentCreateRequest dto) {
        Member currentMember = getCurrentMember();

        Post post = getPost(postId);

        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() ->
                new IllegalArgumentException("부모 댓글이 존재하지 않습니다. id=" + parentCommentId));

        Comment replyComment = Comment.builder()
                .comment(dto.getComment())
                .member(currentMember)
                .post(post)
                .parentComment(parentComment)
                .build();
        commentRepository.save(replyComment);

        return replyComment.getId();
    }

    public List<CommentResponse> findAllWithReplies(Long postId) {
        Post post = getPost(postId);

        List<Comment> topLevelComments = commentRepository.findByParentCommentIsNull(post);

        return topLevelComments.stream()
                .map(comment -> CommentResponse.fromEntity(comment))
                .collect(Collectors.toList());
    }
}
