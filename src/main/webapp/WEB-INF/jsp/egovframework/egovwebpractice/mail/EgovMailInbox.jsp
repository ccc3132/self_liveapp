<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>받은편지함</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h1>받은편지함</h1>
    <p><a href="${pageContext.request.contextPath}/mail/EgovMailAccountSetting.do">계정 설정</a></p>
    <c:if test="${not empty error}"><p style="color:red;"><c:out value="${error}"/></p></c:if>
    <c:if test="${empty error and empty mailList}"><p>받은 메일이 없습니다.</p></c:if>
    <ul>
        <c:forEach var="mail" items="${mailList}">
            <li><c:out value="${mail.from}"/> - <c:out value="${mail.subject}"/></li>
        </c:forEach>
    </ul>
</body>
</html>
