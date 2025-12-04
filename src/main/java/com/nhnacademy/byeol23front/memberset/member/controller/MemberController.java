package com.nhnacademy.byeol23front.memberset.member.controller;

import java.time.Duration;
import java.util.Objects;

import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.commons.exception.DecodingFailureException;
import com.nhnacademy.byeol23front.commons.exception.ErrorResponse;
import com.nhnacademy.byeol23front.memberset.domain.AccessToken;
import com.nhnacademy.byeol23front.memberset.domain.RefreshToken;
import com.nhnacademy.byeol23front.memberset.domain.Token;
import com.nhnacademy.byeol23front.memberset.member.dto.*;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Front Member", description = "프론트 서버 회원(로그인/로그아웃/정보수정) API")
public class MemberController {
	private final MemberService memberService;
	private final CookieProperties cookieProperties;

	@Value("${jwt.refresh-cookie.expiration}")
	private Long refreshCookieExp;


	@GetMapping("/register")
	@Operation(summary = "회원가입 폼 화면", description = "회원가입 폼 페이지를 반환합니다.")
	public String showRegisterForm() {
		return "member/register";
	}

	@PostMapping("/register")
	@Operation(summary = "회원가입 요청", description = "회원가입 폼에서 입력한 정보를 이용해 신규 회원을 등록합니다.")
	public String register(@ModelAttribute MemberRegisterRequest request, BindingResult br) {
		log.info("request:{}", request);
		if (br.hasErrors()) {
			return "member/register";
		}
		memberService.register(request);
		return "member/login";
	}

	@GetMapping("/login")
	@Operation(summary = "로그인 폼 화면", description = "로그인 폼 페이지를 반환합니다.")
	public String showLoginForm(@RequestParam(name = "bookId", required = false) Long bookId,
								@RequestParam(name = "quantity", required = false) Integer quantity,
								@RequestParam(name = "loginFailed", required = false) Boolean loginFailed,
								@RequestParam(name = "errorMsg", required = false) String errorMsg,
								Model model) {

		if (bookId != null && quantity != null) {
			model.addAttribute("bookId", bookId);
			model.addAttribute("quantity", quantity);
		}

		if (Boolean.TRUE.equals(loginFailed)) {
			model.addAttribute("loginFailed", true);
			model.addAttribute("loginErrorMsg", errorMsg);
		}

		return "member/login";
	}


	@PostMapping("/login")
	@Operation(summary = "로그인 요청", description = "아이디와 비밀번호를 이용해 로그인하고 토큰 쿠키를 설정합니다.")
	public String login(@ModelAttribute LoginRequestTmp tmp, HttpServletResponse response) {

		LoginRequest request = new LoginRequest(tmp.loginId(), tmp.loginPassword());
		LoginResponse loginResponse = memberService.login(request);

		ResponseCookie refreshCookie = createCookie(new RefreshToken(loginResponse.refreshToken()));
		ResponseCookie accessCookie = createCookie(new AccessToken(loginResponse.accessToken()));

		//쿠키 적용
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

		if (!Objects.isNull(tmp.bookIds()) && !Objects.isNull(tmp.quantities())) {
			return String.format("redirect:/orders/direct?bookId=%d&quantity=%d",
				tmp.bookIds(), tmp.quantities());
		}

		return "redirect:/";
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃 요청", description = "현재 로그인된 사용자의 토큰 쿠키를 삭제하고 로그아웃합니다.")
	public String logout(@ModelAttribute LogoutRequest request, HttpServletResponse response) {
		memberService.logout();

		response.addHeader("Set-Cookie", deleteCookie("Access-Token", "/"));
		response.addHeader("Set-Cookie", deleteCookie("Refresh-Token", "/"));

		return "redirect:/";
	}

	@GetMapping("/check-id")
	@ResponseBody
	@Operation(summary = "아이디 중복 체크", description = "입력한 로그인 아이디가 이미 사용 중인지 확인합니다.")
	public FindLoginIdResponse findLoginId(@RequestParam String loginId) {
		return memberService.findLoginId(loginId);
	}

	@PostMapping("/check-duplication")
	@ResponseBody
	@Operation(summary = "회원 정보 중복 체크", description = "닉네임/이메일/전화번호 등의 중복 여부를 확인합니다.")
	public ValueDuplicationCheckResponse checkDuplication(
		@RequestBody ValueDuplicationCheckRequest request) {
		 return memberService.checkDuplication(request);
	}

	private String deleteCookie(String name, String path) {
		return ResponseCookie.from(name, "")
				.path(path)
				.httpOnly(true)
				.secure(false)
				.sameSite("Lax")
				.maxAge(0)
				.build().toString();
	}

	@PostMapping("/put")
	@ResponseBody
	@Operation(summary = "회원 정보 수정", description = "프론트에서 전달된 회원 정보로 백엔드에 수정 요청을 전송합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공",
			content = @Content(schema = @Schema(implementation = MemberUpdateResponse.class))),
		@ApiResponse(responseCode = "400", description = "검증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest request){
		MemberUpdateResponse response = memberService.updateMember(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/put/password")
	@ResponseBody
	@Operation(summary = "비밀번호 변경", description = "현재 사용자의 비밀번호 변경 요청을 백엔드로 전달합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "변경 성공",
			content = @Content(schema = @Schema(implementation = MemberPasswordUpdateResponse.class))),
		@ApiResponse(responseCode = "400", description = "검증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberPasswordUpdateResponse> updatePassword(@RequestBody MemberPasswordUpdateRequest request){
		MemberPasswordUpdateResponse response = memberService.updateMemberPassword(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/delete")
	@ResponseBody
	@Operation(summary = "회원 탈퇴", description = "현재 사용자의 회원 탈퇴 요청을 백엔드로 전달합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "탈퇴 성공"),
		@ApiResponse(responseCode = "400", description = "요청 오류",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Void> deleteMember() {
		memberService.deleteMember();
		return ResponseEntity.noContent().build();
	}


	private ResponseCookie createCookie(Token token) {
		String path;
		String tokenType;
		Long expiration;			//중요: https요청에는 none으로 설정해도 됨, http요청은 secure가 false상태이므로 브라우저에서 none에 대한 쿠키는 거부하여 lax로 설정
		if(token instanceof RefreshToken) {
			tokenType = "Refresh-Token";
			expiration = refreshCookieExp;
			path = "/";
		} else if (token instanceof AccessToken) {
			tokenType = "Access-Token";
			expiration = -1L;		//session 방식
			path = "/";
		} else {
			throw new DecodingFailureException("토큰 에러");
		}
		return ResponseCookie.from(tokenType, token.getValue())
				.httpOnly(true)					//XSS
				.secure(cookieProperties.isSecure())					//중요: 실제 배포 환경에선 https요청으로 변경 / secure -> true
				.sameSite(cookieProperties.getSameSite())				//CSRF
				.path(path)
				.maxAge(Duration.ofMinutes(expiration))
				.build();
	}
}
