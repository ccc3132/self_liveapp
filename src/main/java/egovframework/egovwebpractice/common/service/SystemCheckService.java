package egovframework.egovwebpractice.common.service;

public interface SystemCheckService {

    /** DB 연결과 마이그레이션 결과를 확인하기 위한 조회 */
    SystemCheckVO getSystemCheck();
}
