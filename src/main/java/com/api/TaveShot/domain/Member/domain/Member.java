package com.api.TaveShot.domain.Member.domain;

import com.api.TaveShot.domain.Member.editor.MemberEditor;
import com.api.TaveShot.domain.Member.editor.MemberEditor.MemberEditorBuilder;
import com.api.TaveShot.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gitId;
    private String gitLoginId;
    private String gitEmail;
    private String gitName;
    private String profileImageUrl;
    private String bojName;
    private boolean isSubscribed;;
    private boolean emailVerified;
    private String verificationToken;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Tier tier = Tier.BEGINNER;

    @Enumerated
    private Role role;

    public String tierName() {
        return tier.name();
    }

    public MemberEditorBuilder toEditor() {
        return MemberEditor.builder()
                .bojName(bojName)
                .tier(tier);
    }

    public void changeBojInfo(MemberEditor memberEditor) {
        this.bojName = memberEditor.getBojName();
        this.tier = memberEditor.getTier();
    }

    public void updateMemberInfo(MemberEditor memberEditor) {
        if (memberEditor.getGitEmail() != null) {
            this.gitEmail = memberEditor.getGitEmail();
        }
        if (memberEditor.getBojName() != null) {
            this.bojName = memberEditor.getBojName();
        }
    }

    public void emailVerifiedSuccess() {
        this.emailVerified = true;
    }
    public void Subscribed() {
        this.isSubscribed = true;
    }
}

