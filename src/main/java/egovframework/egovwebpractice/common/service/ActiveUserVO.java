package egovframework.egovwebpractice.common.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 현재 로그인된 세션 한 건(접속 중인 사용자). */
@Getter
@Setter
public class ActiveUserVO {

    private String loginId;
    private String name;
    private LocalDateTime loginAt;
}
