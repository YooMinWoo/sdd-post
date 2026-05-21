package com.example.post.board.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {

	Optional<PostJpaEntity> findByIdAndDeletedAtIsNull(Long id);

	Page<PostJpaEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

	Page<PostJpaEntity> findAllByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
			String titleKeyword,
			String contentKeyword,
			Pageable pageable
	);
}
