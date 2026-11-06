<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>egov-web-practice</title>
    <style>
        :root[data-theme="light"] { --bg: #f4f5f7; --panel: #ffffff; --fg: #202329; --muted: #868b94; --accent: #3a6df0; --accent-fg: #ffffff; --border: #e7e9ee; --hover: #eef1f8; --shadow: 0 1px 2px rgba(20,25,40,.06), 0 4px 16px rgba(20,25,40,.04);
            --sb-bg: linear-gradient(180deg, #16233f 0%, #101a30 100%); --sb-fg: #e7ecf8; --sb-muted: #8fa0c4; --sb-border: rgba(255,255,255,.08); --sb-hover: rgba(255,255,255,.08); --sb-active-bg: linear-gradient(90deg, #3a6df0, #5a86ff); --sb-active-fg: #ffffff; }
        :root[data-theme="dark"]  { --bg: #16171b; --panel: #1e2025; --fg: #e9eaee; --muted: #8d919b; --accent: #6fa0ff; --accent-fg: #0d1117; --border: #2c2f36; --hover: #292c33; --shadow: 0 1px 2px rgba(0,0,0,.3), 0 4px 16px rgba(0,0,0,.25);
            --sb-bg: linear-gradient(180deg, #121318 0%, #0b0c10 100%); --sb-fg: #e9eaee; --sb-muted: #878c98; --sb-border: rgba(255,255,255,.06); --sb-hover: rgba(255,255,255,.06); --sb-active-bg: linear-gradient(90deg, #6fa0ff, #8fb4ff); --sb-active-fg: #0d1117; }
        :root[data-theme="blue"]  { --bg: #0b2648; --panel: #0f3464; --fg: #eef3fb; --muted: #a9c0e0; --accent: #7ec8ff; --accent-fg: #06213f; --border: #1c4a7e; --hover: #154578; --shadow: 0 1px 2px rgba(0,0,0,.3), 0 4px 16px rgba(0,0,0,.25);
            --sb-bg: linear-gradient(180deg, #061833 0%, #040f22 100%); --sb-fg: #eef3fb; --sb-muted: #86a2cc; --sb-border: rgba(255,255,255,.08); --sb-hover: rgba(255,255,255,.08); --sb-active-bg: linear-gradient(90deg, #7ec8ff, #9fd6ff); --sb-active-fg: #06213f; }
        * { box-sizing: border-box; }
        html, body { height: 100%; margin: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Malgun Gothic', 'Apple SD Gothic Neo', Roboto, sans-serif; background: var(--bg); color: var(--fg); -webkit-font-smoothing: antialiased; }
        * { scrollbar-width: thin; scrollbar-color: var(--muted) transparent; }
        ::-webkit-scrollbar { width: 10px; height: 10px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background-color: var(--muted); border-radius: 20px; border: 2px solid var(--bg); background-clip: padding-box; }
        ::-webkit-scrollbar-thumb:hover { background-color: var(--accent); background-clip: padding-box; }
        ::-webkit-scrollbar-button { display: none; width: 0; height: 0; }
        ::-webkit-scrollbar-corner { background: transparent; }
        .layout { display: flex; height: 100vh; }
        .sidebar { width: 232px; flex: 0 0 232px; background: var(--sb-bg); border-right: 1px solid var(--sb-border); overflow-y: auto; box-shadow: var(--shadow); z-index: 1; scrollbar-color: rgba(255,255,255,.25) transparent; }
        .sidebar::-webkit-scrollbar-thumb { background-color: rgba(255,255,255,.25); border-color: transparent; }
        .sidebar .brand { display: flex; align-items: center; gap: 8px; padding: 18px 16px 14px; }
        .sidebar .brand .mark { width: 26px; height: 26px; border-radius: 7px; background: var(--accent); color: var(--accent-fg); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px; flex: none; }
        .sidebar .brand .name { font-size: 14px; font-weight: 700; letter-spacing: -.2px; color: var(--sb-fg); }
        .sidebar .group-title { font-size: 10.5px; font-weight: 700; color: var(--sb-muted); padding: 16px 16px 6px; text-transform: uppercase; letter-spacing: .06em; }
        .sidebar a { display: flex; align-items: center; gap: 10px; margin: 1px 8px; padding: 8px 10px; border-radius: 8px; color: var(--sb-fg); text-decoration: none; font-size: 13.5px; transition: background .12s ease, color .12s ease; }
        .sidebar a .ic { width: 16px; text-align: center; font-size: 13px; opacity: .85; flex: none; }
        .sidebar a:hover { background: var(--sb-hover); }
        .sidebar a.active { background: var(--sb-active-bg); color: var(--sb-active-fg); font-weight: 600; box-shadow: 0 2px 8px rgba(58,109,240,.35); }
        .sidebar a.active .ic { opacity: 1; }
        .main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
        .topbar { display: flex; align-items: center; justify-content: space-between; padding: 10px 18px; background: var(--panel); border-bottom: 1px solid var(--border); box-shadow: var(--shadow); z-index: 1; }
        .topbar .user { font-size: 13.5px; font-weight: 600; display: flex; align-items: center; gap: 8px; }
        .topbar .user .avatar { width: 26px; height: 26px; border-radius: 50%; background: var(--accent); color: var(--accent-fg); display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; }
        .topbar .right { display: flex; align-items: center; gap: 8px; }
        .topbar select { font-size: 12.5px; padding: 6px 10px; border-radius: 7px; border: 1px solid var(--border); background: var(--bg); color: var(--fg); cursor: pointer; }
        iframe { flex: 1; border: 0; width: 100%; background: #fff; }
        form.logout { display: inline; margin-left: 0; }
        form.logout button { font-size: 12.5px; cursor: pointer; padding: 6px 12px; border-radius: 7px; border: 1px solid var(--border); background: var(--panel); color: var(--fg); transition: background .12s ease; }
        form.logout button:hover { background: var(--hover); }
    </style>
</head>
<body>
    <div class="layout">
        <nav class="sidebar">
            <div class="brand"><span class="mark">E</span><span class="name">egov-web-practice</span></div>
            <a href="javascript:void(0)" class="active" onclick="go('/main.do', this)"><span class="ic">&#9679;</span>대시보드</a>

            <div class="group-title">게시판</div>
            <a href="javascript:void(0)" onclick="go('/cop/bbs/selectArticleList.do?bbsId=BBSMSTR_000000000001', this)"><span class="ic">&#128196;</span>공지사항</a>
            <a href="javascript:void(0)" onclick="go('/cop/bbs/selectBBSMasterInfs.do', this)"><span class="ic">&#9881;</span>게시판 관리(관리자)</a>

            <div class="group-title">일정관리</div>
            <a href="javascript:void(0)" onclick="go('/cop/smt/sim/EgovIndvdlSchdulManageMonthList.do', this)"><span class="ic">&#128197;</span>내 일정</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/sdm/EgovDeptSchdulManageMonthList.do', this)"><span class="ic">&#128197;</span>부서 일정(관리자)</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/sam/EgovAllSchdulManageList.do', this)"><span class="ic">&#128467;</span>전체 일정</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/mtm/selectMemoTodoList.do', this)"><span class="ic">&#128221;</span>메모할일</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/dsm/EgovDiaryManageList.do', this)"><span class="ic">&#128214;</span>업무일지</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/wmr/selectWikMnthngReprtList.do', this)"><span class="ic">&#128203;</span>주간/월간 보고</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/mrm/selectMemoReprtList.do', this)"><span class="ic">&#128172;</span>메모 보고</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/djm/selectDeptJobList.do', this)"><span class="ic">&#128188;</span>부서 업무</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/lsm/usr/selectLeaderSchdulList.do', this)"><span class="ic">&#128081;</span>지휘 일정</a>

            <div class="group-title">소통</div>
            <a href="javascript:void(0)" onclick="go('/uss/ion/ntm/registEgovNoteManage.do', this)"><span class="ic">&#128221;</span>쪽지 쓰기</a>
            <a href="javascript:void(0)" onclick="go('/uss/ion/ntr/listNoteRecptn.do', this)"><span class="ic">&#9993;</span>쪽지(받은함)</a>
            <a href="javascript:void(0)" onclick="go('/uss/ion/nts/listNoteTrnsmit.do', this)"><span class="ic">&#128228;</span>쪽지(보낸함)</a>
            <a href="javascript:void(0)" onclick="go('/cop/adb/selectAdbkList.do', this)"><span class="ic">&#128218;</span>주소록</a>
            <a href="javascript:void(0)" onclick="go('/msg/EgovMessengerMain.do', this)"><span class="ic">&#128172;</span>메신저</a>

            <div class="group-title">메일</div>
            <a href="javascript:void(0)" onclick="go('/mail/EgovMailInbox.do', this)"><span class="ic">&#128229;</span>받은 메일함</a>
            <a href="javascript:void(0)" onclick="go('/cop/ems/insertSndngMailView.do', this)"><span class="ic">&#128228;</span>메일 발송</a>
            <a href="javascript:void(0)" onclick="go('/mail/EgovMailAccountSetting.do', this)"><span class="ic">&#128231;</span>메일 수신 설정</a>

            <div class="group-title">결재</div>
            <a href="javascript:void(0)" onclick="go('/apv/EgovApvSubmitSelect.do', this)"><span class="ic">&#9999;</span>결재 상신</a>
            <a href="javascript:void(0)" onclick="go('/apv/EgovApvInbox.do', this)"><span class="ic">&#128203;</span>결재함</a>
            <a href="javascript:void(0)" onclick="go('/apv/admin/EgovApvTemplateList.do', this)"><span class="ic">&#128196;</span>양식 관리(관리자)</a>

            <div class="group-title">관리자</div>
            <a href="javascript:void(0)" onclick="go('/uss/umt/EgovEmplyrManage.do', this)"><span class="ic">&#128100;</span>직원 관리</a>
            <a href="javascript:void(0)" onclick="go('/sec/rgm/EgovAuthorGroupList.do', this)"><span class="ic">&#128273;</span>권한 지정</a>
            <a href="javascript:void(0)" onclick="go('/cop/smt/lsm/mng/selectLeaderSttusList.do', this)"><span class="ic">&#9878;</span>지휘 현황 관리</a>
        </nav>
        <div class="main">
            <div class="topbar">
                <span class="user">
                    <c:if test="${not empty loginVO}">
                        <span class="avatar"><c:out value="${fn:substring(loginVO.name, 0, 1)}"/></span>
                        <c:out value="${loginVO.name}"/>(<c:out value="${loginVO.orgnztNm}"/>)님
                    </c:if>
                </span>
                <span class="right">
                    <select id="themeSelect" onchange="setTheme(this.value)">
                        <option value="light">라이트</option>
                        <option value="dark">다크</option>
                        <option value="blue">블루</option>
                    </select>
                    <form class="logout" method="post" action="${pageContext.request.contextPath}/uat/uia/actionLogout.do">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit">로그아웃</button>
                    </form>
                </span>
            </div>
            <iframe id="contentFrame" name="contentFrame" src="${pageContext.request.contextPath}/main.do"></iframe>
        </div>
    </div>
    <script>
        var ctx = "${pageContext.request.contextPath}";
        function go(path, el) {
            document.getElementById('contentFrame').src = ctx + path;
            document.querySelectorAll('.sidebar a.active').forEach(function (a) { a.classList.remove('active'); });
            if (el) { el.classList.add('active'); }
        }

        function getCookie(name) {
            var m = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'));
            return m ? decodeURIComponent(m[1]) : null;
        }
        function setTheme(theme) {
            document.documentElement.setAttribute('data-theme', theme);
            document.cookie = 'theme=' + encodeURIComponent(theme) + ';path=' + (ctx || '/') + ';max-age=31536000';
            document.getElementById('themeSelect').value = theme;
        }
        setTheme(getCookie('theme') || 'light');
    </script>
</body>
</html>
