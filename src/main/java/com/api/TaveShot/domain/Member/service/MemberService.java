package com.api.TaveShot.domain.Member.service;


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
        Member currentMember = SecurityUtil.getCurrentMember();

        MemberEditor memberEditor = getMemberEditor(updateInfo, currentMember);

        currentMember.updateMemberInfo(memberEditor);
        memberRepository.save(currentMember);

        return currentMember.getId();
    }

    private MemberEditor getMemberEditor(MemberUpdateInfo updateInfo, Member member) {
        MemberEditor.MemberEditorBuilder editorBuilder = member.toEditor();
        return editorBuilder
                .gitEmail(updateInfo.getGitEmail())
                .bojName(updateInfo.getBojName())
                .build();
    }
}


