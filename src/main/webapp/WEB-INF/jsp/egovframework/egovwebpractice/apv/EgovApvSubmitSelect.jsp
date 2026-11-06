<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재 신청</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
    <script src="<c:url value='/js/egovframework/com/cmm/EgovIframeModal.js' />"></script>
</head>
<body>
    <h2>&#9999;결재 신청</h2>

    <table class="board_list">
        <caption class="blind">신청 가능한 결재 양식 목록</caption>
        <thead>
            <tr>
                <th scope="col">양식명</th>
                <th scope="col">설명</th>
                <th scope="col"></th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty templateList}">
                <tr><td colspan="3">신청 가능한 양식이 없습니다. 관리자에게 문의하세요.</td></tr>
            </c:if>
            <c:forEach var="t" items="${templateList}">
                <tr>
                    <td><c:out value="${t.templateNm}"/></td>
                    <td><c:out value="${t.description}"/></td>
                    <td><a class="btn_submit" href="<c:url value='/apv/EgovApvSubmitForm.do'><c:param name="templateId" value="${t.templateId}"/></c:url>"
                           onclick="EgovIframeModal.open({url: this.href, title: '결재 신청 - ${t.templateNm}', doneUrlIncludes: 'EgovApvSubmitSelect.do'}); return false;">신청</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
