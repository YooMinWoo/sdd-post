package com.example.post.member.adapter.out.security;

import com.example.post.member.application.port.out.PasswordEncoderPort;
import com.example.post.member.application.port.out.PasswordMatcherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort, PasswordMatcherPort {

	private final PasswordEncoder passwordEncoder;

	public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String encode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}
