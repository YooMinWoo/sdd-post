package com.example.post.board.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {

	Page<PostJpaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
