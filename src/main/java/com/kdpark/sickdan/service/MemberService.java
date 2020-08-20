package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
