package egovframework.egovwebpractice.common.service;

/** 표준프레임워크 서비스 규칙: 서비스 인터페이스. 메인 화면 요약 정보를 조회한다. */
public interface DashboardService {

    DashboardVO selectDashboard(String uniqId) throws Exception;
}
