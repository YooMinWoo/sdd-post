package com.example.post.board.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {
}
