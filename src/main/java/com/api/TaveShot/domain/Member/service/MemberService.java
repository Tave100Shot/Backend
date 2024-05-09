package com.api.TaveShot.domain.Member.service;


import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.editor.MemberEditor;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
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
    public Long updateMemberDetails(String gitEmail, String bojName) {
        Member currentMember = SecurityUtil.getCurrentMember();

        MemberEditor memberEditor = getMemberEditor(gitEmail, bojName, currentMember);

        currentMember.updateMemberInfo(memberEditor);
        memberRepository.save(currentMember);

        return currentMember.getId();
    }

    private MemberEditor getMemberEditor(String gitEmail, String bojName, Member member) {
        MemberEditor.MemberEditorBuilder editorBuilder = MemberEditor.builder();

        if (gitEmail != null) {
            editorBuilder.gitEmail(gitEmail);
        }

        if (bojName != null) {
            editorBuilder.bojName(bojName);
        }

        return editorBuilder.build();
    }
}


