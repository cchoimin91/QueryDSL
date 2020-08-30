package study.querydsl.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class HelloController {

    private final MemberJpaRepository memberJpaRepository;

    @GetMapping
    public String hello(){
        return "hello";
    }

    @GetMapping("/v1/members")
    public List<MemberTeamDto> list(MemberSearchCondition param) {
        System.out.println("param = " + param);
        return memberJpaRepository.searchByBooleanExpression(param);
    }



}
