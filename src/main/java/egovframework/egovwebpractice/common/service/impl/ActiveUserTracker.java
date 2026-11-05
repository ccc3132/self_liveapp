package egovframework.egovwebpractice.common.service.impl;

import egovframework.com.cmm.LoginVO;
import egovframework.egovwebpractice.common.service.ActiveUserVO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 현재 로그인된 세션을 세션 ID 기준으로 메모리에 들고 있는다(직접 개발, 여러 인스턴스로 띄우면 공유되지 않음 - 이
 * 프로젝트는 단일 인스턴스 전제). 로그인 성공 시 세션에 "loginVO" 속성이 들어가는 것을 세션 리스너
 * (ActiveSessionListener)가 감지해서 등록하고, 세션이 끝나면(로그아웃/타임아웃) 제거한다.
 */
@Component
public class ActiveUserTracker {

    private final Map<String, ActiveUserVO> bySessionId = new ConcurrentHashMap<>();

    public void track(String sessionId, LoginVO loginVO) {
        if (loginVO == null) {
            return;
        }
        ActiveUserVO vo = new ActiveUserVO();
        vo.setLoginId(loginVO.getId());
        vo.setName(loginVO.getName());
        vo.setLoginAt(LocalDateTime.now());
        bySessionId.put(sessionId, vo);
    }

    public void untrack(String sessionId) {
        bySessionId.remove(sessionId);
    }

    public List<ActiveUserVO> selectActiveUsers() {
        return bySessionId.values().stream()
                .sorted((a, b) -> b.getLoginAt().compareTo(a.getLoginAt()))
                .toList();
    }

    public int count() {
        return bySessionId.size();
    }
}
