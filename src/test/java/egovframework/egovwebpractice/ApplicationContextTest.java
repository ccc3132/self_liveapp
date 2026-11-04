package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import egovframework.egovwebpractice.common.service.DashboardService;
import egovframework.egovwebpractice.common.service.DashboardVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 내장 PostgreSQL(target/test-pg)로 Flyway 마이그레이션, EgovAbstractServiceImpl/EgovAbstractMapper 동작을 확인한다. */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg")
class ApplicationContextTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void 마이그레이션_후_서비스_DAO_조회가_동작한다() throws Exception {
        DashboardVO vo = dashboardService.selectDashboard("USRCNFRM_00000000000");
        assertNotNull(vo);
        assertNotNull(vo.getRecentNotices());
        assertEquals(0, vo.getUnreadNoteCount());
    }
}
