-- 모든 DB 에서 공통으로 쓰는 ANSI 호환 SQL
-- DB마다 문법이 달라지는 스크립트는 db/migration/<vendor>/ 에 둔다.

CREATE TABLE system_check (
    id         INTEGER      NOT NULL PRIMARY KEY,
    message    VARCHAR(200) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_check (id, message) VALUES (1, 'DB 연결과 마이그레이션이 정상 동작합니다.');
