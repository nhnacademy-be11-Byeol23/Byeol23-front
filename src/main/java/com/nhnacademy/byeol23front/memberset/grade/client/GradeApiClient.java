package com.nhnacademy.byeol23front.memberset.grade.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "gradeApiClient")
public interface GradeApiClient {

}
