package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {
        // 전체 회원 정보를 List에 저장
        List<Member> members = memberRepository.findAll();

        // ModelView 생성 (뷰 논리적 이름 : members)
        ModelView modelView = new ModelView("members");
        // 해당 정보 저장
        modelView.getModel().put("members", members);
        return modelView;
    }
}
