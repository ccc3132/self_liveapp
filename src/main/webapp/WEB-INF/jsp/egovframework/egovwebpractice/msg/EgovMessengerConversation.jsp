<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>메신저 - <c:out value="${partnerNm}"/></title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h1><c:out value="${partnerNm}"/> 님과의 대화</h1>
    <p><a href="${pageContext.request.contextPath}/msg/EgovMessengerMain.do">목록으로</a></p>
    <ul>
        <c:forEach var="m" items="${messageList}">
            <li><strong><c:out value="${m.senderNm}"/></strong>: <c:out value="${m.content}"/></li>
        </c:forEach>
    </ul>

    <form method="post" action="${pageContext.request.contextPath}/msg/sendMessage.do">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="receiverId" value="${partnerId}"/>
        <input type="hidden" name="partnerNm" value="${partnerNm}"/>
        <input type="text" name="content" maxlength="2000" required="required"/>
        <button type="submit">보내기</button>
    </form>
</body>
</html>
