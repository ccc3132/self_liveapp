-- /uss/umt/** (회원관리) URL 을 관리자(ROLE_ADMIN)만 접근하도록 제한한다.
--
-- 공통컴포넌트 초기 데이터는 회원관리 URL 패턴(web-000004)을 ROLE_ANONYMOUS 에 열어 두었다(회원가입 화면이 공개여야 하므로).
-- 그런데 업무사용자 목록 화면(EgovEmplyrManage.do)은 "로그인했는지"만 확인하고 관리자 여부는 확인하지 않으며,
-- 아이디 중복 확인 등도 익명에게 열려 있게 된다. 이 프로젝트는 업무사용자 관리만 도입했고 회원가입 화면은 없으므로,
-- 회원관리 URL 전체를 관리자 전용으로 바꾼다. 회원가입 화면을 도입하면 해당 URL 만 따로 익명 허용한다.
DELETE FROM COMTNAUTHORROLERELATE WHERE ROLE_CODE = 'web-000004';
INSERT INTO COMTNAUTHORROLERELATE (AUTHOR_CODE, ROLE_CODE, CREAT_DT) VALUES ('ROLE_ADMIN', 'web-000004', NOW());
