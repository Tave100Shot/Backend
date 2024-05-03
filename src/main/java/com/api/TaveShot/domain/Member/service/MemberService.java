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
        if (currentMember == null) {
            throw new ApiException(ErrorType._UNAUTHORIZED);
        }

        if (gitEmail == null && bojName == null) {
            throw new ApiException(ErrorType._INVALID_INPUT);
        }

        boolean updated = false;
        MemberEditor.MemberEditorBuilder builder = MemberEditor.builder();

        if (gitEmail != null) {
            builder.gitEmail(gitEmail);
            updated = true;
        }

        if (bojName != null) {
            builder.bojName(bojName);
            updated = true;
        }

        if (updated) {
            MemberEditor memberEditor = builder.build();
            currentMember.updateMemberInfo(memberEditor);
            memberRepository.save(currentMember);
        }

        return currentMember.getId();
    }
}


