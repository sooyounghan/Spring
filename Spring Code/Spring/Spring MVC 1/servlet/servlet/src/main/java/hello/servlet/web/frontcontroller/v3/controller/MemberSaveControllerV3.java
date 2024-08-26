package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        // paramMap은 프론트 컨트롤러에서 request 정보
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        // 해당 정보를 Member에 저장
        Member member = new Member(username, age);
        memberRepository.save(member);

        // ModelView 객체 생성(뷰의 논리적 이름) 후, 해당 model에 member 저장 후 반환
        ModelView modelView = new ModelView("save-result");
        modelView.getModel().put("member", member);
        return modelView;
    }
}
