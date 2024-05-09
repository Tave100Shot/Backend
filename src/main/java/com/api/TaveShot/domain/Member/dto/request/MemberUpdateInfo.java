package com.api.TaveShot.domain.Member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateInfo {
    private String gitEmail;
    private String bojName;
}
