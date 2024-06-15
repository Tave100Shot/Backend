package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.domain.Member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class EmailSendStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "newsletter_id")
    private Newsletter newsletter;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String email;

    private boolean success;

    @Builder
    public EmailSendStatus(Long id, Newsletter newsletter, Member member, String email) {
        this.id = id;
        this.newsletter = newsletter;
        this.member = member;
        this.email = email;
        this.success = false;
    }

    public void markSuccess() {
        this.success = true;
    }
    public void markFailed() {
        this.success = false;
    }
}
