<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재 양식 관리</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
</head>
<body>
    <h2>⚙결재 양식 관리</h2>

    <div class="search_box">
        <a class="btn_submit" href="<c:url value='/apv/admin/EgovApvTemplateBuilder.do' />">+ 신규 양식 만들기</a>
    </div>

    <table class="board_list">
        <caption class="blind">결재 양식 목록</caption>
        <thead>
            <tr>
                <th scope="col">템플릿명</th>
                <th scope="col">설명</th>
                <th scope="col">첨부파일</th>
                <th scope="col">사용유무</th>
                <th scope="col">등록일</th>
                <th scope="col">관리</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty templateList}">
                <tr><td colspan="6">등록된 양식이 없습니다.</td></tr>
            </c:if>
            <c:forEach var="t" items="${templateList}">
                <tr>
                    <td><a href="<c:url value='/apv/admin/EgovApvTemplateBuilder.do'><c:param name="templateId" value="${t.templateId}"/></c:url>"><c:out value="${t.templateNm}"/></a></td>
                    <td><c:out value="${t.description}"/></td>
                    <td>${t.fileAtchPosblAt == 'Y' ? '허용' : '-'}</td>
                    <td>${t.useAt == 'Y' ? '사용' : '미사용'}</td>
                    <td><fmt:formatDate value="${t.frstRegistPnttm}" pattern="yyyy-MM-dd"/></td>
                    <td>
                        <form method="post" action="<c:url value='/apv/admin/toggleApvTemplateUseAt.do' />" style="display:inline">
                            <c:if test="${not empty _csrf}"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/></c:if>
                            <input type="hidden" name="templateId" value="${t.templateId}">
                            <input type="hidden" name="useAt" value="${t.useAt == 'Y' ? 'N' : 'Y'}">
                            <button type="submit" class="btn_s">${t.useAt == 'Y' ? '미사용 전환' : '사용 전환'}</button>
                        </form>
                        <form method="post" action="<c:url value='/apv/admin/deleteApvTemplate.do' />" style="display:inline"
                              onsubmit="return confirm('삭제하시겠습니까? 신청 이력이 있는 양식은 삭제할 수 없습니다.');">
                            <c:if test="${not empty _csrf}"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/></c:if>
                            <input type="hidden" name="templateId" value="${t.templateId}">
                            <button type="submit" class="btn_s">삭제</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
