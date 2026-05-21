package com.example.post.board.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {
}
