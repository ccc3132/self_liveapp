package egovframework.egovwebpractice.common.service.impl;

import egovframework.egovwebpractice.common.service.SystemCheckService;
import egovframework.egovwebpractice.common.service.SystemCheckVO;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("systemCheckService")
@Transactional(readOnly = true)
public class SystemCheckServiceImpl extends EgovAbstractServiceImpl implements SystemCheckService {

    private final SystemCheckDAO systemCheckDAO;

    public SystemCheckServiceImpl(SystemCheckDAO systemCheckDAO) {
        this.systemCheckDAO = systemCheckDAO;
    }

    @Override
    public SystemCheckVO selectSystemCheck() throws Exception {
        SystemCheckVO vo = systemCheckDAO.selectSystemCheck();
        if (vo == null) {
            throw processException("error.systemcheck.notfound");
        }
        String databaseId = systemCheckDAO.selectDatabaseId();
        vo.setDbProduct(databaseId != null ? databaseId : "unknown");
        return vo;
    }
}
