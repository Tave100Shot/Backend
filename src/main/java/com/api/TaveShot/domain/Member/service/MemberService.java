package com.api.TaveShot.domain.Member.service;


import static com.api.TaveShot.global.util.SecurityUtil.*;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.dto.request.MemberUpdateInfo;
import com.api.TaveShot.domain.Member.editor.MemberEditor;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.post.post.editor.PostEditor;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long updateMemberDetails(final MemberUpdateInfo updateInfo) {
        Member currentMember = getCurrentMember();
        Member findMember = memberRepository.findByIdActivated(currentMember.getId());

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


