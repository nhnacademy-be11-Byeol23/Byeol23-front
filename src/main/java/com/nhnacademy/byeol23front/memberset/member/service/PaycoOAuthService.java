package com.nhnacademy.byeol23front.memberset.member.service;

import com.nhnacademy.byeol23front.memberset.member.dto.PaycoTokenResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfo;

public interface PaycoOAuthService {
	public String generateState();
	public String buildAuthorizeUrl(String state);
	public PaycoTokenResponse issueTokenFromPayco(String code);
	public PaycoUserInfo getUserInfo(String accessToken);

}
