<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>메일 수신 계정 설정</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h1>메일 수신(IMAP) 계정 설정</h1>
    <p>비밀번호는 암호화해서 저장합니다. 저장 후에는 비밀번호 칸이 항상 비어 보입니다(다시 입력해야 바뀜).</p>
    <form method="post" action="${pageContext.request.contextPath}/mail/saveMailAccount.do">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <p>IMAP 호스트: <input type="text" name="imapHost" value="${account.imapHost}" required="required"/></p>
        <p>포트: <input type="number" name="imapPort" value="${account.imapPort}" required="required"/></p>
        <p>SSL 사용:
            <select name="useSsl">
                <option value="Y" ${account.useSsl == 'Y' ? 'selected' : ''}>예</option>
                <option value="N" ${account.useSsl == 'N' ? 'selected' : ''}>아니오</option>
            </select>
        </p>
        <p>사용자명(이메일): <input type="text" name="imapUsername" value="${account.imapUsername}" required="required"/></p>
        <p>비밀번호: <input type="password" name="password" required="required"/></p>
        <button type="submit">저장</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/mail/EgovMailInbox.do">받은편지함 보기</a></p>
</body>
</html>
