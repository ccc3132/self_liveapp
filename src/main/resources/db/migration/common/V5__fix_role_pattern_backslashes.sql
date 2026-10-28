-- 공통컴포넌트의 URL 권한 패턴(COMTNROLEINFO.ROLE_PTTRN) 보정
--
-- 원본 DML 은 정규식을 '\\A/uat/uia/.*\\.do.*\\Z' 처럼 역슬래시 두 개로 적었다. MySQL/Oracle 은 문자열 리터럴에서
-- 역슬래시 두 개를 하나로 해석하지만, PostgreSQL 은 그대로 두 개를 저장한다. 그러면 정규식 \\A 는
-- "역슬래시 + A" 라는 뜻이 되어 어떤 URL 과도 맞지 않고, 로그인 화면이 허용되지 않아 무한 리다이렉트가 발생한다.
-- 이미 적용된 V3 는 수정하지 않고, 이 마이그레이션으로 역슬래시 두 개를 하나로 바로잡는다.
UPDATE COMTNROLEINFO
   SET ROLE_PTTRN = REPLACE(ROLE_PTTRN, CHR(92) || CHR(92), CHR(92))
 WHERE POSITION(CHR(92) || CHR(92) IN ROLE_PTTRN) > 0;
