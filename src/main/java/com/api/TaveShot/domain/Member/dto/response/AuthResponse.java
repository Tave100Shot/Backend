package com.api.TaveShot.domain.Member.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(Long memberId, String gitLoginId, String mail, boolean isAdmin,String gitProfileImageUrl, boolean secondAuth) {
}
