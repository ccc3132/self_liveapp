<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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

    <section>
        <h2>현재 접속 중인 사용자 (<span id="activeUserCount"><c:out value="${fn:length(dashboard.activeUsers)}"/></span>명)</h2>
        <ul id="activeUserList">
            <c:forEach var="u" items="${dashboard.activeUsers}">
                <li><c:out value="${u.name}"/> (<c:out value="${u.loginId}"/>)</li>
            </c:forEach>
        </ul>
    </section>

    <script>
        // 15초마다 접속 중인 사용자 목록을 다시 받아와서 갱신한다(실시간에 가깝게, 별도 WebSocket 없이).
        function refreshActiveUsers() {
            fetch('${pageContext.request.contextPath}/main/activeUsers.do')
                .then(function (r) { return r.json(); })
                .then(function (data) {
                    document.getElementById('activeUserCount').textContent = data.count;
                    var list = document.getElementById('activeUserList');
                    list.innerHTML = '';
                    data.users.forEach(function (u) {
                        var li = document.createElement('li');
                        li.textContent = u.name + ' (' + u.loginId + ')';
                        list.appendChild(li);
                    });
                })
                .catch(function () { /* 다음 주기에 다시 시도 */ });
        }
        setInterval(refreshActiveUsers, 15000);
    </script>
</body>
</html>
