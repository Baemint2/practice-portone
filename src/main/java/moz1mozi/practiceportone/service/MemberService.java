package moz1mozi.practiceportone.service;

import lombok.RequiredArgsConstructor;
import moz1mozi.practiceportone.entity.Member;
import moz1mozi.practiceportone.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 자동 생성
    @Transactional
    public Member autoRegister() {
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email("example@example.com")
                .address("서울특별시 은평구 응암동")
                .build();

        return memberRepository.save(member);
    }
}
