package com.nhnacademy.byeol23front.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

//닉네임 필드를 추가하기 위해서 커스텀 클래스로 생성
@Getter
public class MemberPrincipal implements UserDetails {

	private final Long memberId;
	private final String nickname;
	private final String role;
	private final Collection<? extends GrantedAuthority> authorities;

	public MemberPrincipal(Long memberId, String nickname, String role, Collection<? extends GrantedAuthority> authorities) {
		this.memberId = memberId;
		this.nickname = nickname;
		this.role = role;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return memberId.toString();
	}

	//스프링 시큐리티에서 계정 상태를 체크할 때 검사하는 인증 조건들
	//현재는 JWT기반으로 인증하기 때문에 사용할 필요가 없어서 모두 True를 반환
	//계정 만료 여부
	@Override
	public boolean isAccountNonExpired() { return true; }

	//계정 잠김 여부
	@Override
	public boolean isAccountNonLocked() { return true; }

	//패스워드 만료 여부(90일초과)
	@Override
	public boolean isCredentialsNonExpired() { return true; }

	//계정 활성여부
	@Override
	public boolean isEnabled() { return true; }
}
