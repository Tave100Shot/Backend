package com.api.TaveShot.domain.Member.service;


import static com.api.TaveShot.global.util.SecurityUtil.*;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.dto.request.MemberUpdateInfo;
import com.api.TaveShot.domain.Member.dto.response.MemberInfoResponse;
import com.api.TaveShot.domain.Member.dto.response.MemberPagingResponse;
import com.api.TaveShot.domain.Member.dto.response.MemberSingleResponse;
import com.api.TaveShot.domain.Member.editor.MemberEditor;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public Long updateMemberDetails(final MemberUpdateInfo updateInfo) {
        Member currentMember = getCurrentMember();
        Member findMember = memberRepository.findByIdActivated(currentMember.getId());

        String newGitEmail = updateInfo.getGitEmail();
        boolean isEmailChanged = (newGitEmail != null && !newGitEmail.equals(findMember.getGitEmail()));

        if (isEmailChanged || newGitEmail == null) {
            // 이메일 변경시 이메일 인증 상태 및 구독 상태 재설정
            findMember.changeGitEmail(newGitEmail);
            // 모든 구독 정보 삭제
            subscriptionRepository.deleteAll(subscriptionRepository.findByMemberId(findMember.getId()));
        }

        MemberEditor memberEditor = getMemberEditor(updateInfo, findMember);

        findMember.changeBojInfo(memberEditor);
        return currentMember.getId();

    }

    @Transactional
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findByIdActivated(memberId);
        return new MemberInfoResponse(
                member.getGitEmail(),
                member.getBojName()
        );
    }

    private MemberEditor getMemberEditor(MemberUpdateInfo updateInfo, Member member) {
        MemberEditor.MemberEditorBuilder editorBuilder = member.toEditor();
        return editorBuilder
                .gitEmail(updateInfo.getGitEmail())
                .bojName(updateInfo.getBojName())
                .build();
    }

    public Member findById(Long id) {
        return memberRepository.findByIdActivated(id);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public MemberPagingResponse getMemberPaging(
            final String memberName, final String memberEmail, final Pageable pageable
    ) {
        Page<Member> memberPaging = memberRepository.getMemberPaging(memberName, memberEmail, pageable);
        List<MemberSingleResponse> memberSingleResponses = memberPaging.stream()
                .map(MemberSingleResponse::from)
                .toList();

        MemberPagingResponse response = MemberPagingResponse.of(
                memberSingleResponses, memberPaging.getTotalPages(),
                memberPaging.getTotalElements(), memberPaging.isFirst(), memberPaging.isLast()
        );

        return response;
    }

}


