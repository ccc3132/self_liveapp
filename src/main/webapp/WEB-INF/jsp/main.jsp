<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>egov-web-practice</title>
</head>
<body>
    <h1>egov-web-practice</h1>
    <p>전자정부프레임워크 기반 프로젝트 뼈대입니다.</p>
    <ul>
        <li>DB 종류: <c:out value="${check.dbProduct}"/></li>
        <li>DB 상태: <c:out value="${check.message}"/></li>
    </ul>
</body>
</html>
