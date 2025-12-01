package com.nhnacademy.byeol23front.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authHelper")
public class AuthHelper {

	public boolean isLoggedIn() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return false;
		}
		if (auth instanceof AnonymousAuthenticationToken) {
			return false;
		}
		return auth.isAuthenticated();
	}

	public boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) {
			return false;
		}
		if(auth instanceof AnonymousAuthenticationToken) {
			return false;
		}

		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

	}
}