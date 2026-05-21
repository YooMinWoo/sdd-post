package com.example.post.board.adapter.out.persistence;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {

	Page<CommentJpaEntity> findAllByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

	@Query("""
			select c.postId as postId, count(c.id) as commentCount
			from CommentJpaEntity c
			where c.postId in :postIds
			group by c.postId
			""")
	List<CommentCountProjection> countByPostIdInGroupByPostId(@Param("postIds") Set<Long> postIds);

	@Modifying
	@Query("delete from CommentJpaEntity c where c.postId = :postId")
	void deleteByPostId(@Param("postId") Long postId);
}
