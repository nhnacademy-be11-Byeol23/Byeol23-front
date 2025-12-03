package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberApiClient memberApiClient;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute MemberRegisterRequest request, BindingResult br) {
        if (br.hasErrors()) {
            return "member/register";
        }
        memberApiClient.registerRequest(request);
        return "member/login";
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(required = false) String nonMemberRedirect,
            @RequestParam(required = false) String memberRedirect,
            @RequestParam(required = false) List<Long> bookIds,
            @RequestParam(required = false) List<Integer> quantities,
            Model model) {

        if (bookIds != null && !bookIds.isEmpty()) {
            model.addAttribute("bookIds", bookIds);
            model.addAttribute("quantities", quantities);
        }

        model.addAttribute("nonMemberRedirect", nonMemberRedirect);
        model.addAttribute("memberRedirect", memberRedirect);

        return "member/login";
    }


    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestTmp tmp, HttpServletResponse response) {
        LoginRequest request = new LoginRequest(tmp.getLoginId(), tmp.getLoginPassword());

        ResponseEntity<LoginResponse> feignResponse = memberApiClient.login(request);
        List<String> setCookies = feignResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

        if (setCookies != null) {
            setCookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c));
            setCookies.forEach(c -> log.info("Upstream Set-Cookie: {}", c));
        }

        if (tmp.getBookIds() != null && !tmp.getBookIds().isEmpty()) {
            String baseUrl = tmp.getRedirectUrl() != null ? tmp.getRedirectUrl() : "/orders";

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tmp.getBookIds().size(); i++) {
                sb.append("&bookIds=").append(tmp.getBookIds().get(i));
                sb.append("&quantities=").append(tmp.getQuantities().get(i));
            }
            String finalRedirectUrl = baseUrl + "?" + sb.substring(1);
            log.info(finalRedirectUrl);

            return "redirect:" + finalRedirectUrl;
        }

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(@ModelAttribute LogoutRequest request, HttpServletResponse response) {
        ResponseEntity<LogoutResponse> feignResponse = memberApiClient.logout();

        response.addHeader("Set-Cookie", deleteCookie("Access-Token", "/"));
        response.addHeader("Set-Cookie", deleteCookie("Refresh-Token", "/members"));

        return "redirect:/";
    }

    @GetMapping("/check-id")
    @ResponseBody
    public CheckIdResponse checkId(@RequestParam String loginId) {
        CheckIdResponse response = memberApiClient.checkId(loginId);
        return response;
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
    public ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest req){
        return memberApiClient.updateMember(req);
    }

    @PostMapping("/put/password")
    @ResponseBody
    public ResponseEntity<MemberPasswordUpdateResponse> updatePassword(@RequestBody MemberPasswordUpdateRequest req){
        return memberApiClient.updateMemberPassword(req);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteMember(){
        return memberApiClient.deleteMember();
    }
}
