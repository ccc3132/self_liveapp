package egovframework.egovwebpractice.common.service;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** 메인 화면에 보여줄 요약 정보: 오늘 일정, 최근 공지, 안 읽은 쪽지 수, 접속 중인 사용자. */
@Getter
@Setter
public class DashboardVO {

    private List<Map<String, Object>> todaySchedules;
    private List<Map<String, Object>> recentNotices;
    private List<Map<String, Object>> popupNotices;
    private int unreadNoteCount;
    private List<ActiveUserVO> activeUsers;
}
