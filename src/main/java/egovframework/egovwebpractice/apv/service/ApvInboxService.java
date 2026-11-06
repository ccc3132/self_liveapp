package egovframework.egovwebpractice.apv.service;

import java.util.List;
import java.util.Map;

/** 결재함: 내가 결재할 문서 조회 + 승인/반려 처리(순차 결재선 상태 전이). */
public interface ApvInboxService {

    List<Map<String, Object>> selectPendingList(String approverId);

    void approve(Long submissionId, Long lineId, Integer stepOrder, String approverId);

    void reject(Long submissionId, Long lineId, String approverId, String comment);
}
