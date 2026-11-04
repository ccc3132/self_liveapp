<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>egov-web-practice</title>
    <style>
        body { font-family: sans-serif; margin: 40px; }
        section { margin-bottom: 24px; }
        h2 { font-size: 16px; border-bottom: 1px solid #ddd; padding-bottom: 6px; }
        ul { list-style: none; padding: 0; }
        li { padding: 4px 0; }
        .empty { color: #888; }
    </style>
</head>
<body>
    <h1>egov-web-practice</h1>
    <p>
        <c:if test="${not empty loginVO}">
            <c:out value="${loginVO.name}"/>(<c:out value="${loginVO.orgnztNm}"/>)님, 환영합니다.
        </c:if>
    </p>

    <section>
        <h2>오늘 일정 (<a href="${pageContext.request.contextPath}/cop/smt/sim/EgovIndvdlSchdulManageMainList.do">바로가기</a>)</h2>
        <c:if test="${empty dashboard.todaySchedules}"><p class="empty">오늘 등록된 일정이 없습니다.</p></c:if>
        <ul>
            <c:forEach var="s" items="${dashboard.todaySchedules}">
                <li><c:out value="${s.schdul_nm}"/> (<c:out value="${s.schdul_place}"/>)</li>
            </c:forEach>
        </ul>
    </section>

    <section>
        <h2>최근 공지 (<a href="${pageContext.request.contextPath}/cop/bbs/selectArticleList.do?bbsId=BBSMSTR_000000000001">바로가기</a>)</h2>
        <c:if test="${empty dashboard.recentNotices}"><p class="empty">등록된 공지가 없습니다.</p></c:if>
        <ul>
            <c:forEach var="n" items="${dashboard.recentNotices}">
                <li><c:out value="${n.ntt_sj}"/></li>
            </c:forEach>
        </ul>
    </section>

    <section>
        <h2>쪽지 (<a href="${pageContext.request.contextPath}/uss/ion/ntr/listNoteRecptn.do">바로가기</a>)</h2>
        <p>안 읽은 쪽지 <c:out value="${dashboard.unreadNoteCount}"/>건</p>
    </section>
</body>
</html>
