package egovframework.egovwebpractice.common.service.impl;

import egovframework.egovwebpractice.common.service.DashboardService;
import egovframework.egovwebpractice.common.service.DashboardVO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("dashboardService")
@Transactional(readOnly = true)
public class DashboardServiceImpl extends EgovAbstractServiceImpl implements DashboardService {

    private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final DashboardDAO dashboardDAO;

    public DashboardServiceImpl(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    @Override
    public DashboardVO selectDashboard(String uniqId) {
        String today = LocalDate.now().format(YYYYMMDD);

        DashboardVO vo = new DashboardVO();
        vo.setTodaySchedules(dashboardDAO.selectTodaySchedules(uniqId, today));
        vo.setRecentNotices(dashboardDAO.selectRecentNotices());
        vo.setUnreadNoteCount(dashboardDAO.selectUnreadNoteCount(uniqId));
        return vo;
    }
}
