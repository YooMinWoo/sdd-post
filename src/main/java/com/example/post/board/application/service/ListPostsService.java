package com.example.post.board.application.service;

import com.example.post.board.application.port.in.ListPostsQuery;
import com.example.post.board.application.port.in.ListPostsResult;
import com.example.post.board.application.port.in.ListPostsUseCase;
import com.example.post.board.application.port.in.PostSummaryResult;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListPostsService implements ListPostsUseCase {

	private static final int MAX_PAGE_SIZE = 100;

	private final PostRepositoryPort postRepositoryPort;
	private final AuthorMemberPort authorMemberPort;

	public ListPostsService(PostRepositoryPort postRepositoryPort, AuthorMemberPort authorMemberPort) {
		this.postRepositoryPort = postRepositoryPort;
		this.authorMemberPort = authorMemberPort;
	}

	@Override
	@Transactional(readOnly = true)
	public ListPostsResult listPosts(ListPostsQuery query) {
		int page = validatePage(query.page());
		int size = validateSize(query.size());
		PostPageResult result = postRepositoryPort.findAllOrderByCreatedAtDesc(page, size);
		Map<Long, String> authors = getAuthorsById(result.posts());
		List<PostSummaryResult> posts = result.posts().stream()
				.map(post -> toSummaryResult(post, authors))
				.toList();

		return new ListPostsResult(
				posts,
				result.page(),
				result.size(),
				result.totalElements(),
				result.totalPages(),
				result.first(),
				result.last()
		);
	}

	private Map<Long, String> getAuthorsById(List<Post> posts) {
		Set<Long> authorMemberIds = posts.stream()
				.map(Post::getAuthorMemberId)
				.collect(Collectors.toSet());
		if (authorMemberIds.isEmpty()) {
			return Map.of();
		}
		return authorMemberPort.getNicknamesByIds(authorMemberIds);
	}

	private static PostSummaryResult toSummaryResult(Post post, Map<Long, String> authors) {
		String author = authors.get(post.getAuthorMemberId());
		if (author == null) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return new PostSummaryResult(
				post.getId(),
				post.getTitle(),
				post.getAuthorMemberId(),
				author,
				post.getCreatedAt()
		);
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
