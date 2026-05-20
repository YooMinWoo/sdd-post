package com.example.post.board.application.port.out;

import java.util.Map;
import java.util.Set;

public interface AuthorMemberPort {

	String getNicknameById(Long memberId);

	Map<Long, String> getNicknamesByIds(Set<Long> memberIds);
}
