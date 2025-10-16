package com.nhnacademy.byeol23front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping
    public String mainPage(){

        return "testPage";
    }
}
