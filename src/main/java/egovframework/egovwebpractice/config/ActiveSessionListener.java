package egovframework.egovwebpractice.config;

import egovframework.com.cmm.LoginVO;
import egovframework.egovwebpractice.common.service.impl.ActiveUserTracker;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * 로그인 성공 시 세션에 "loginVO" 속성이 들어가는 것을 감지해 {@link ActiveUserTracker}에 등록하고,
 * 세션이 끝나면(로그아웃/타임아웃) 제거한다. 원본 로그인 코드({@code EgovLoginController})를 건드리지 않고
 * 세션 리스너로만 붙인다.
 */
public class ActiveSessionListener implements HttpSessionAttributeListener, HttpSessionListener {

    private final ActiveUserTracker tracker;

    public ActiveSessionListener(ActiveUserTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        trackIfLoginVO(event);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        trackIfLoginVO(event);
    }

    private void trackIfLoginVO(HttpSessionBindingEvent event) {
        if ("loginVO".equals(event.getName()) && event.getValue() instanceof LoginVO loginVO) {
            tracker.track(event.getSession().getId(), loginVO);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        tracker.untrack(event.getSession().getId());
    }
}
