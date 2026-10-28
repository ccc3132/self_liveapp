package egovframework.egovwebpractice.common.service.impl;

import egovframework.egovwebpractice.common.service.SystemCheckService;
import egovframework.egovwebpractice.common.service.SystemCheckVO;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.stereotype.Service;

@Service
public class SystemCheckServiceImpl implements SystemCheckService {

    private final SystemCheckMapper mapper;
    private final DataSource dataSource;

    public SystemCheckServiceImpl(SystemCheckMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    @Override
    public SystemCheckVO getSystemCheck() {
        SystemCheckVO vo = mapper.selectSystemCheck();
        try (Connection conn = dataSource.getConnection()) {
            vo.setDbProduct(conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            vo.setDbProduct("unknown");
        }
        return vo;
    }
}
