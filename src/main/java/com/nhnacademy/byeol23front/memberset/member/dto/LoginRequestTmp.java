package com.nhnacademy.byeol23front.memberset.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginRequestTmp {
    String loginId;
    String loginPassword;
    String redirectUrl;
    List<Long> bookIds;
    List<Integer> quantities;
}
