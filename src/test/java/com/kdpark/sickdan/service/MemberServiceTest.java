package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.MemberRelationship;
import com.kdpark.sickdan.domain.RelationshipStatus;
import com.kdpark.sickdan.dto.MemberDto;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.error.exception.InvalidParameterException;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.util.CryptUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    public void 회원가입_로컬_정상처리() throws Exception {
        //given
        Member member = Member.builder()
                .email("wert1229@naver.com")
                .password("{noop}1234")
                .displayName("wert")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        //when
        memberService.join(member);

        //then
        verify(memberRepository, times(1)).saveMember(member);
    }

    @Test
    public void 회원검색_이메일() throws Exception {
        //given
        when(memberRepository.findByEmail("wert1229@naver.com")).thenReturn(Member.builder()
                .email("wert1229@naver.com")
                .password("{noop}1234")
                .displayName("wert")
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        //when
        Member member = memberService.findByEmail("wert1229@naver.com");

        //then
        assertEquals("wert1229@naver.com", member.getEmail());
        assertEquals("wert", member.getDisplayName());
        assertEquals("ROLE_USER", member.getRoles().get(0));
    }

    @Test
    public void 회원검색_이메일_관계없는회원() throws Exception {
        //given
        Long requestingMemberId = 1L;
        String emailSearchingFor = "park@naver.com";

        when(memberRepository.findById(requestingMemberId)).thenReturn(Member.builder()
                .id(requestingMemberId)
                .email("wert1229@naver.com")
                .password("{noop}1234")
                .displayName("wert")
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        when(memberRepository.findByEmail(emailSearchingFor)).thenReturn(Member.builder()
                .id(2L)
                .email(emailSearchingFor)
                .password("{noop}1234")
                .displayName("park")
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        //when
        MemberDto.FriendSearchResult result =
                memberService.searchByFilter("email", emailSearchingFor, requestingMemberId);

        //then
        assertEquals(emailSearchingFor, result.getEmail());
        assertEquals(RelationshipStatus.NONE, result.getStatus());
    }

    @Test
    public void 회원검색_이메일_친구요청한회원() throws Exception {
        //given
        Long requestingMemberId = 1L;
        String emailSearchingFor = "park@naver.com";

        Member requestingMember = Member.builder()
                .id(requestingMemberId)
                .email("wert1229@naver.com")
                .build();

        Member requestedMember = Member.builder()
                .id(2L)
                .email(emailSearchingFor)
                .build();

        requestingMember.getRelationships().add(MemberRelationship.builder()
                .relatingMember(requestingMember)
                .relatedMember(requestedMember)
                .status(RelationshipStatus.REQUESTING)
                .build());

        when(memberRepository.findById(requestingMemberId)).thenReturn(requestingMember);

        when(memberRepository.findByEmail(emailSearchingFor)).thenReturn(requestedMember);

        //when
        MemberDto.FriendSearchResult result =
                memberService.searchByFilter("email", emailSearchingFor, requestingMemberId);

        //then
        assertEquals(emailSearchingFor, result.getEmail());
        assertEquals(RelationshipStatus.REQUESTING, result.getStatus());
    }

    @Test
    public void 회원검색_이메일_없는회원() throws Exception {
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () ->
                memberService.searchByFilter("email","park@naver.com", 1L));
    }

    @Test
    public void 회원검색_부정확한_필터() throws Exception {
        //given

        //when

        //then
        assertThrows(InvalidParameterException.class, () ->
                memberService.searchByFilter("invaild","park@naver.com", 1L));

    }

    @Test
    public void 회원검색_코드_잘못된코드() throws Exception {
        //given

        //when

        //then
        assertThrows(InvalidParameterException.class, () ->
                memberService.searchByFilter("code","1111", 1L));

    }
}