package com.example.post.adapter.out.persistence;

import com.example.post.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
		name = "members",
		uniqueConstraints = @UniqueConstraint(name = "uk_members_email", columnNames = "email")
)
class MemberJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(nullable = false)
	private Instant createdAt;

	protected MemberJpaEntity() {
	}

	private MemberJpaEntity(Long id, String email, String passwordHash, String nickname, Instant createdAt) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.nickname = nickname;
		this.createdAt = createdAt;
	}

	static MemberJpaEntity from(Member member) {
		return new MemberJpaEntity(
				member.getId(),
				member.getEmail(),
				member.getPasswordHash(),
				member.getNickname(),
				member.getCreatedAt()
		);
	}

	Member toDomain() {
		return Member.rehydrate(id, email, passwordHash, nickname, createdAt);
	}
}
