package com.minjor.auth.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResp {
    private String accessToken;
    private String refreshToken;
}
