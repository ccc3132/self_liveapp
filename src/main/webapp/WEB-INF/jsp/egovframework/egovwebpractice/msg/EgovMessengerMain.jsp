<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>메신저</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h1>메신저</h1>
    <p><a href="${pageContext.request.contextPath}/uss/ion/ntm/listEgovNoteEmpListPopup.do">새 대화 상대 찾기(직원 목록)</a></p>
    <c:if test="${empty conversationList}"><p>대화가 없습니다.</p></c:if>
    <ul>
        <c:forEach var="conv" items="${conversationList}">
            <li>
                <a href="${pageContext.request.contextPath}/msg/EgovMessengerConversation.do?partnerId=${conv.partnerId}&partnerNm=${conv.partnerNm}">
                    <c:out value="${conv.partnerNm}"/> - <c:out value="${conv.lastContent}"/>
                </a>
                <c:if test="${conv.unreadCount > 0}"> (안읽음 <c:out value="${conv.unreadCount}"/>)</c:if>
            </li>
        </c:forEach>
    </ul>
</body>
</html>
