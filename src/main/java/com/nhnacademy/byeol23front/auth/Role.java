package com.nhnacademy.byeol23front.auth;

import java.util.Comparator;

public enum Role implements Comparator<Role> {
	ADMIN(2),
	USER(1),
	ANONYMOUS(0);

	private final int roleCode;

	Role(int roleCode) {
		this.roleCode = roleCode;
	}

	Role(String roleName) {
		this.roleCode = Role.valueOf(roleName).roleCode;
	}

	@Override
	public int compare(Role o1, Role o2) {
		return Integer.compare(o1.roleCode, o2.roleCode);
	}
}
