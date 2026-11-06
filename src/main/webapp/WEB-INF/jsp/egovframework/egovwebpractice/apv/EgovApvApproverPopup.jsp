<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재자 선택</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h3>결재자 선택</h3>
    <form method="get" action="<c:url value='/apv/EgovApvApproverPopup.do' />" class="search_box">
        <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="이름 검색">
        <button type="submit" class="btn_submit">검색</button>
    </form>
    <table class="board_list">
        <caption class="blind">직원 목록</caption>
        <thead>
            <tr><th scope="col">이름</th><th scope="col">소속</th><th scope="col"></th></tr>
        </thead>
        <tbody>
            <c:if test="${empty employeeList}">
                <tr><td colspan="3">검색 결과가 없습니다.</td></tr>
            </c:if>
            <c:forEach var="e" items="${employeeList}">
                <tr>
                    <td><c:out value="${e.emplyr_nm}"/></td>
                    <td><c:out value="${e.orgnzt_id}"/></td>
                    <td>
                        <button type="button" class="btn_s"
                            onclick="opener.fnEgovApvAddApprover('<c:out value='${e.uniq_id}'/>', '<c:out value='${e.emplyr_nm}'/>'); window.close();">선택</button>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
