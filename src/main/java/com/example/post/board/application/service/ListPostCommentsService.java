package com.example.post.board.application.service;

import com.example.post.board.application.port.in.CommentSummaryResult;
import com.example.post.board.application.port.in.ListPostCommentsQuery;
import com.example.post.board.application.port.in.ListPostCommentsResult;
import com.example.post.board.application.port.in.ListPostCommentsUseCase;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.CommentPageResult;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListPostCommentsService implements ListPostCommentsUseCase {

	private static final int MAX_PAGE_SIZE = 100;

	private final PostRepositoryPort postRepositoryPort;
	private final CommentRepositoryPort commentRepositoryPort;
	private final AuthorMemberPort authorMemberPort;

	public ListPostCommentsService(
			PostRepositoryPort postRepositoryPort,
			CommentRepositoryPort commentRepositoryPort,
			AuthorMemberPort authorMemberPort
	) {
		this.postRepositoryPort = postRepositoryPort;
		this.commentRepositoryPort = commentRepositoryPort;
		this.authorMemberPort = authorMemberPort;
	}

	@Override
	@Transactional(readOnly = true)
	public ListPostCommentsResult listPostComments(ListPostCommentsQuery query) {
		Long postId = validatePostId(query.postId());
		int page = validatePage(query.page());
		int size = validateSize(query.size());
		postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));

		CommentPageResult result = commentRepositoryPort.findAllByPostIdOrderByCreatedAtDesc(postId, page, size);
		Map<Long, String> authors = getAuthorsById(result.comments());
		List<CommentSummaryResult> comments = result.comments().stream()
				.map(comment -> toSummaryResult(comment, authors))
				.toList();

		return new ListPostCommentsResult(
				comments,
				result.page(),
				result.size(),
				result.totalElements(),
				result.totalPages(),
				result.first(),
				result.last()
		);
	}

	private Map<Long, String> getAuthorsById(List<Comment> comments) {
		Set<Long> authorMemberIds = comments.stream()
				.map(Comment::getAuthorMemberId)
				.collect(Collectors.toSet());
		if (authorMemberIds.isEmpty()) {
			return Map.of();
		}
		return authorMemberPort.getNicknamesByIds(authorMemberIds);
	}

	private static CommentSummaryResult toSummaryResult(Comment comment, Map<Long, String> authors) {
		String author = authors.get(comment.getAuthorMemberId());
		if (author == null) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return new CommentSummaryResult(
				comment.getId(),
				comment.getAuthorMemberId(),
				author,
				comment.getContent(),
				comment.getCreatedAt()
		);
	}

	private static Long validatePostId(Long postId) {
		if (postId == null || postId <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return postId;
	}

	private static int validatePage(Integer page) {
		if (page == null || page < 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return page;
	}

	private static int validateSize(Integer size) {
		if (size == null || size < 1 || size > MAX_PAGE_SIZE) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return size;
	}
}
