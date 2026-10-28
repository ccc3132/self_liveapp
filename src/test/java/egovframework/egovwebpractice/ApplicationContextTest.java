package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import egovframework.egovwebpractice.common.service.SystemCheckService;
import egovframework.egovwebpractice.common.service.SystemCheckVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 메모리 H2 로 Flyway 마이그레이션, EgovAbstractServiceImpl/EgovAbstractMapper 동작을 확인한다. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
class ApplicationContextTest {

    @Autowired
    private SystemCheckService systemCheckService;

    @Test
    void 마이그레이션_후_서비스_DAO_조회가_동작한다() throws Exception {
        SystemCheckVO vo = systemCheckService.selectSystemCheck();
        assertNotNull(vo);
        assertEquals(1, vo.getId());
        assertEquals("h2", vo.getDbProduct());
    }
}
