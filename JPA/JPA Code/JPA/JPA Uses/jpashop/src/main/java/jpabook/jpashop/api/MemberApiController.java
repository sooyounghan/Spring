package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * - 등록 V1 : 요청 값으로 Member 엔티티를 직접 받음
     * - 문제점
     *   1. 엔티티에 프레젠테이션 계층을 위한 로직 추가
     *   2. 엔티티에 API 검증을 위한 로직이 들어감 (@NotEmpty)
     *   3. 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각 API를 위한 모든 요청 요구사항을 담기는 어려움
     *   4. 엔티티가 변경되면 API 스펙이 변함
     *
     * - 결론
     *  : API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받음
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMember(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 등록 V2 : 요청 값으로 Member 엔티티 대신 별도의 DTO를 받음
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정 API
     */
    @PostMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 조회 V1 : 응답 값으로 엔티티를 직접 외부에 노출
     * - 문제점 :
     *   1. 엔티티에 프레젠테이션 계층을 위한 로직 추가
     *   2. 기본적으로 엔티티의 모든 값 노출
     *   3. 응답 스펙을 맞추기 위해 로직이 추가 (@JsonIgnore, 별도의 뷰 로직 등)
     *   4. 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어려움
     *   5. 엔티티가 변경되면 API 스펙이 변함
     *   6. 추가로 컬렉션을 직접 반환하면, 향후 API 스펙을 변경하기 어려움 (별도의 Result 클래스 생성으로 해결)
     *
     *  - 결론 :
     *    API 응답 스펙에 맞추어 별도의 DTO 반환
     */
    // 조회 V1 : 안 좋은 버전, 모든 엔티티가 노출 @JsonIgnore -> 최악으로, API가 이거 하나가 아님. 화면에 종속적이면 안 됨
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 조회 V2 : 응답 값으로 엔티티가 아닌 별도의 DTO를 반환
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();

        // Entity -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
