package com.api.TaveShot.domain.Member.service;


import static com.api.TaveShot.global.util.SecurityUtil.*;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.dto.request.MemberUpdateInfo;
import com.api.TaveShot.domain.Member.editor.MemberEditor;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        if (isEmailChanged) {
            // 이메일 변경시 이메일 인증 상태 및 구독 상태 재설정
            findMember.changeGitEmail(newGitEmail);
            // 모든 구독 정보 삭제
            subscriptionRepository.deleteAll(subscriptionRepository.findByMemberId(findMember.getId()));
        }

        MemberEditor memberEditor = getMemberEditor(updateInfo, findMember);

        findMember.changeBojInfo(memberEditor);
        return currentMember.getId();

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
}


