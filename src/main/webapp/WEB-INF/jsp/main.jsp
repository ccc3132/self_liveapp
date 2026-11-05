<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>egov-web-practice</title>
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Malgun Gothic', 'Apple SD Gothic Neo', Roboto, sans-serif;
            margin: 0;
            padding: 28px 32px 40px;
            background: #f4f6fa;
            color: #20242c;
        }
        h1 { font-size: 21px; margin: 0 0 4px; letter-spacing: -.3px; }
        .welcome { color: #656b78; font-size: 13.5px; margin: 0 0 24px; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; }
        section {
            margin: 0;
            background: #fff;
            border: 1px solid #e7e9ee;
            border-radius: 12px;
            padding: 18px 20px;
            box-shadow: 0 1px 2px rgba(20,25,40,.04), 0 6px 18px rgba(20,25,40,.03);
        }
        section.wide { grid-column: 1 / -1; }
        h2 {
            display: flex; align-items: center; justify-content: space-between;
            font-size: 14px; font-weight: 700; margin: 0 0 12px; padding: 0 0 10px;
            border-bottom: 1px solid #eef0f4; color: #2a2d34;
        }
        h2 a { font-size: 12px; font-weight: 600; color: #3a6df0; text-decoration: none; }
        h2 a:hover { text-decoration: underline; }
        h2 .count { display: inline-flex; align-items: center; justify-content: center; min-width: 20px; height: 20px; padding: 0 6px; border-radius: 10px; background: #eaf0ff; color: #3a6df0; font-size: 11.5px; font-weight: 700; margin-left: 6px; }
        ul { list-style: none; padding: 0; margin: 0; }
        li { padding: 7px 0; border-bottom: 1px solid #f5f6f9; font-size: 13.5px; }
        li:last-child { border-bottom: 0; }
        .empty { color: #9aa0ab; font-size: 13px; padding: 6px 0; }
    </style>
</head>
<body>
    <h1>대시보드</h1>
    <p class="welcome">
        <c:if test="${not empty loginVO}">
            <c:out value="${loginVO.name}"/>(<c:out value="${loginVO.orgnztNm}"/>)님, 환영합니다.
        </c:if>
    </p>

    <div class="grid">
        <section>
            <h2>오늘 일정 <a href="${pageContext.request.contextPath}/cop/smt/sim/EgovIndvdlSchdulManageMainList.do">바로가기</a></h2>
            <c:if test="${empty dashboard.todaySchedules}"><p class="empty">오늘 등록된 일정이 없습니다.</p></c:if>
            <ul>
                <c:forEach var="s" items="${dashboard.todaySchedules}">
                    <li><c:out value="${s.schdul_nm}"/> (<c:out value="${s.schdul_place}"/>)</li>
                </c:forEach>
            </ul>
        </section>

        <section>
            <h2>최근 공지 <a href="${pageContext.request.contextPath}/cop/bbs/selectArticleList.do?bbsId=BBSMSTR_000000000001">바로가기</a></h2>
            <c:if test="${empty dashboard.recentNotices}"><p class="empty">등록된 공지가 없습니다.</p></c:if>
            <ul>
                <c:forEach var="n" items="${dashboard.recentNotices}">
                    <li><c:out value="${n.ntt_sj}"/></li>
                </c:forEach>
            </ul>
        </section>

        <section>
            <h2>쪽지 <a href="${pageContext.request.contextPath}/uss/ion/ntr/listNoteRecptn.do">바로가기</a></h2>
            <p style="font-size:13.5px; margin:0;">안 읽은 쪽지 <strong><c:out value="${dashboard.unreadNoteCount}"/></strong>건</p>
        </section>

        <section class="wide">
            <h2>현재 접속 중인 사용자<span class="count" id="activeUserCount"><c:out value="${fn:length(dashboard.activeUsers)}"/></span></h2>
            <ul id="activeUserList">
                <c:forEach var="u" items="${dashboard.activeUsers}">
                    <li><c:out value="${u.name}"/> (<c:out value="${u.loginId}"/>)</li>
                </c:forEach>
            </ul>
        </section>
    </div>

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
