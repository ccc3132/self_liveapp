-- /sec/rgm/** (권한그룹: 사용자에게 권한을 지정하는 화면) URL 을 관리자(ROLE_ADMIN)만 접근하도록 제한한다.
--
-- 컨트롤러의 모든 핸들러에 @RequireAdmin 이 있지만, 이 화면은 권한 상승(자기 자신에게 ROLE_ADMIN 지정)에 쓰일 수 있는
-- 가장 민감한 화면이라 URL 수준에서도 한 번 더 막는다(이중 방어). 기본 규칙(web-000003)은 ROLE_USER 에도 모든 *.do 를 열어 둔다.
-- 접근 거부 화면(/sec/ram/accessDenied.do)은 /sec/rgm/ 이 아니므로 영향을 받지 않는다.
INSERT INTO COMTNROLEINFO (ROLE_CODE, ROLE_NM, ROLE_PTTRN, ROLE_DC, ROLE_TY, ROLE_SORT, ROLE_CREAT_DE)
VALUES ('web-000013', '권한그룹관리', '\A/sec/rgm/.*\.do.*\Z', '사용자 권한 지정 화면은 관리자만 접근', 'url', '1', NOW());
INSERT INTO COMTNAUTHORROLERELATE (AUTHOR_CODE, ROLE_CODE, CREAT_DT) VALUES ('ROLE_ADMIN', 'web-000013', NOW());
