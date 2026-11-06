<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재함</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
    <style>
        .apv-tabs { display: flex; gap: 6px; margin-bottom: 10px; }
        .apv-tab-btn { padding: 8px 16px; border-radius: 7px 7px 0 0; border: 1px solid #e7e9ee; border-bottom: none; background: #f4f5f7; cursor: pointer; font-size: 13.5px; }
        .apv-tab-btn.active { background: #fff; font-weight: 700; color: #3a6df0; }
        .apv-tab-panel { display: none; }
        .apv-tab-panel.active { display: block; }
        .apv-status-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 700; }
        .apv-status-대기 { background: #fff3cd; color: #8a6d1d; }
        .apv-status-진행중 { background: #fff3cd; color: #8a6d1d; }
        .apv-status-승인 { background: #e3f6e8; color: #1f7a3d; }
        .apv-status-반려 { background: #fde3e3; color: #b32424; }

        .apv-backdrop { position: fixed; inset: 0; background: rgba(20,25,40,.45); display: flex; align-items: center; justify-content: center; z-index: 1000; }
        .apv-backdrop.hidden { display: none; }
        .apv-modal { width: min(560px, 92vw); max-height: 85vh; background: #fff; border-radius: 12px; box-shadow: 0 8px 32px rgba(20,25,40,.25); display: flex; flex-direction: column; overflow: hidden; }
        .apv-modal .apv-modal-head { padding: 16px 20px; border-bottom: 1px solid #eef0f4; display: flex; align-items: center; justify-content: space-between; }
        .apv-modal .apv-modal-head h3 { margin: 0; font-size: 15px; }
        .apv-modal .apv-modal-close { cursor: pointer; font-size: 18px; color: #9aa0ab; border: none; background: none; }
        .apv-modal .apv-modal-body { padding: 16px 20px; overflow-y: auto; font-size: 13.5px; }
        .apv-modal .apv-field-row { margin-bottom: 10px; }
        .apv-modal .apv-field-row .apv-field-label { font-weight: 600; margin-bottom: 2px; }
        .apv-line-timeline { list-style: none; padding: 0; margin: 10px 0 0; }
        .apv-line-timeline li { display: flex; align-items: center; gap: 8px; padding: 6px 0; border-bottom: 1px solid #f5f6f9; }
        .apv-line-timeline .apv-line-step { font-weight: 700; color: #3a6df0; width: 34px; flex: none; }
        .apv-modal .apv-modal-foot { padding: 12px 20px; border-top: 1px solid #eef0f4; }
        .apv-modal .apv-modal-foot textarea { width: 100%; margin-bottom: 8px; min-height: 60px; }
    </style>
</head>
<body>
    <h2>&#128203;결재함</h2>

    <div class="apv-tabs">
        <button type="button" class="apv-tab-btn active" id="tabBtnPending" onclick="switchTab('pending')">내가 결재할 문서 (<c:out value="${fn:length(pendingList)}" />)</button>
        <button type="button" class="apv-tab-btn" id="tabBtnMine" onclick="switchTab('mine')">내가 상신한 문서</button>
    </div>

    <div class="apv-tab-panel active" id="tabPending">
        <table class="board_list">
            <caption class="blind">내가 결재할 문서 목록</caption>
            <thead><tr><th scope="col">제목</th><th scope="col">양식</th><th scope="col">신청자</th><th scope="col">신청일</th><th scope="col"></th></tr></thead>
            <tbody>
                <c:if test="${empty pendingList}"><tr><td colspan="5">결재할 문서가 없습니다.</td></tr></c:if>
                <c:forEach var="p" items="${pendingList}">
                    <tr>
                        <td><c:out value="${p.title}"/></td>
                        <td><c:out value="${p.template_nm}"/></td>
                        <td><c:out value="${p.requester_nm}"/></td>
                        <td><fmt:formatDate value="${p.submitted_at}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td><button type="button" class="btn_s" onclick="openDetail('${p.submission_id}')">상세보기</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="apv-tab-panel" id="tabMine">
        <table class="board_list">
            <caption class="blind">내가 상신한 문서 목록</caption>
            <thead><tr><th scope="col">제목</th><th scope="col">양식</th><th scope="col">상태</th><th scope="col">신청일</th><th scope="col"></th></tr></thead>
            <tbody>
                <c:if test="${empty mySubmissionList}"><tr><td colspan="5">상신한 문서가 없습니다.</td></tr></c:if>
                <c:forEach var="m" items="${mySubmissionList}">
                    <tr>
                        <td><c:out value="${m.title}"/></td>
                        <td><c:out value="${m.templateNm}"/></td>
                        <td><span class="apv-status-badge apv-status-${m.status}"><c:out value="${m.status}"/></span></td>
                        <td><fmt:formatDate value="${m.submittedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td><button type="button" class="btn_s" onclick="openDetail('${m.submissionId}')">상세보기</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="apv-backdrop hidden" id="apvBackdrop">
        <div class="apv-modal">
            <div class="apv-modal-head">
                <h3 id="apvModalTitle"></h3>
                <button type="button" class="apv-modal-close" onclick="closeDetail()">&times;</button>
            </div>
            <div class="apv-modal-body" id="apvModalBody"></div>
            <div class="apv-modal-foot" id="apvModalFoot"></div>
        </div>
    </div>

    <script>
        function switchTab(name) {
            document.getElementById('tabPending').classList.toggle('active', name === 'pending');
            document.getElementById('tabMine').classList.toggle('active', name === 'mine');
            document.getElementById('tabBtnPending').classList.toggle('active', name === 'pending');
            document.getElementById('tabBtnMine').classList.toggle('active', name === 'mine');
        }

        var typeLabels = { text: '한 줄 텍스트', textarea: '여러 줄 텍스트', number: '숫자', date: '날짜', daterange: '기간', select: '드롭다운' };

        function formatValue(type, value) {
            if (value === null || value === undefined || value === '') { return '-'; }
            if (type === 'daterange' && typeof value === 'object') {
                return (value.start || '') + ' ~ ' + (value.end || '');
            }
            return value;
        }

        function closeDetail() {
            document.getElementById('apvBackdrop').classList.add('hidden');
        }

        function openDetail(submissionId) {
            fetch('<c:url value="/apv/selectApvSubmissionDetailAjax.do" />?submissionId=' + submissionId)
                .then(function (r) { return r.json(); })
                .then(function (data) {
                    var s = data.submission;
                    document.getElementById('apvModalTitle').textContent = s.title + ' (' + s.templateNm + ')';

                    var bodyHtml = '<div class="apv-field-row"><div class="apv-field-label">신청자</div><div>' + s.requesterNm + '</div></div>';
                    bodyHtml += '<div class="apv-field-row"><div class="apv-field-label">상태</div><div><span class="apv-status-badge apv-status-' + s.status + '">' + s.status + '</span></div></div>';
                    data.renderFields.forEach(function (f) {
                        bodyHtml += '<div class="apv-field-row"><div class="apv-field-label">' + f.label + '</div><div>' + formatValue(f.type, f.value) + '</div></div>';
                    });

                    bodyHtml += '<div class="apv-field-row"><div class="apv-field-label">결재선</div><ul class="apv-line-timeline">';
                    (s.lines || []).forEach(function (line) {
                        bodyHtml += '<li><span class="apv-line-step">' + line.stepOrder + '차</span>' +
                            '<span>' + line.approverNm + '</span>' +
                            '<span class="apv-status-badge apv-status-' + line.status + '">' + line.status + '</span>' +
                            (line.commentCn ? '<span>- ' + line.commentCn + '</span>' : '') + '</li>';
                    });
                    bodyHtml += '</ul></div>';
                    document.getElementById('apvModalBody').innerHTML = bodyHtml;

                    var footEl = document.getElementById('apvModalFoot');
                    if (data.actionableLineId) {
                        var stepOrder = (s.lines.find(function (l) { return l.lineId === data.actionableLineId; }) || {}).stepOrder;
                        footEl.innerHTML =
                            '<textarea id="apvComment" placeholder="반려 시 사유를 입력하세요(승인 시 생략 가능)"></textarea>' +
                            '<button type="button" class="btn_submit" onclick="submitDecision(' + submissionId + ',' + data.actionableLineId + ',' + stepOrder + ',\'approve\')">승인</button> ' +
                            '<button type="button" class="btn_s" onclick="submitDecision(' + submissionId + ',' + data.actionableLineId + ',' + stepOrder + ',\'reject\')">반려</button>';
                    } else {
                        footEl.innerHTML = '';
                    }

                    document.getElementById('apvBackdrop').classList.remove('hidden');
                });
        }

        function submitDecision(submissionId, lineId, stepOrder, action) {
            var comment = document.getElementById('apvComment') ? document.getElementById('apvComment').value : '';
            if (action === 'reject' && !comment.trim()) {
                alert('반려 사유를 입력하세요.');
                return;
            }
            var form = document.createElement('form');
            form.method = 'post';
            form.action = '<c:url value="/apv/" />' + (action === 'approve' ? 'approveApvSubmission.do' : 'rejectApvSubmission.do');

            function addHidden(name, value) {
                var input = document.createElement('input');
                input.type = 'hidden'; input.name = name; input.value = value;
                form.appendChild(input);
            }
            <c:if test="${not empty _csrf}">addHidden('${_csrf.parameterName}', '${_csrf.token}');</c:if>
            addHidden('submissionId', submissionId);
            addHidden('lineId', lineId);
            if (action === 'approve') { addHidden('stepOrder', stepOrder); }
            else { addHidden('comment', comment); }

            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>
