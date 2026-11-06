<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재 신청서 작성</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/cmm/jqueryui.css' />">
    <script src="<c:url value='/js/egovframework/com/cmm/jquery.js' />"></script>
    <script src="<c:url value='/js/egovframework/com/cmm/jqueryui.js' />"></script>
    <c:if test="${template.fileAtchPosblAt == 'Y'}">
        <script src="<c:url value='/js/egovframework/com/cmm/fms/EgovMultiFiles.js' />"></script>
    </c:if>
    <style>
        .apv-field-wrap { margin-bottom: 14px; }
        .apv-field-wrap label { display: block; font-weight: 600; margin-bottom: 6px; font-size: 13.5px; }
        .apv-field-wrap input[type=text], .apv-field-wrap input[type=number], .apv-field-wrap input[type=date],
        .apv-field-wrap textarea, .apv-field-wrap select { width: 100%; max-width: 420px; }
        .apv-field-wrap textarea { min-height: 80px; }
        .apv-daterange { display: flex; align-items: center; gap: 8px; }
        #approverList { list-style: none; padding: 0; margin: 0 0 10px; max-width: 420px; }
        #approverList li { display: flex; align-items: center; gap: 8px; border: 1px solid #e7e9ee; border-radius: 7px; padding: 6px 10px; margin-bottom: 6px; background: #fff; }
        #approverList li .apv-approver-handle { cursor: grab; color: #9aa0ab; }
        #approverList li .apv-approver-step { font-size: 11px; font-weight: 700; color: #3a6df0; }
        #approverList li .apv-approver-nm { flex: 1; }
    </style>
</head>
<body>
    <h2>&#9999;결재 신청서 작성 - <c:out value="${template.templateNm}"/></h2>
    <p><c:out value="${template.description}"/></p>

    <form id="submitForm" method="post" action="<c:url value='/apv/insertApvSubmission.do' />" enctype="multipart/form-data">
        <c:if test="${not empty _csrf}"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/></c:if>
        <input type="hidden" name="templateId" value="${template.templateId}">
        <input type="hidden" name="fieldValuesJson" id="fieldValuesJson">
        <input type="hidden" name="approverIds" id="approverIdsInput">
        <input type="hidden" name="approverNms" id="approverNmsInput">

        <c:forEach var="f" items="${fields}">
            <div class="apv-field-wrap" data-field-id="${f.id}" data-field-type="${f.type}">
                <label>${f.label}<c:if test="${f.required}"> *</c:if></label>

                <c:choose>
                    <c:when test="${f.type == 'textarea'}">
                        <textarea class="apv-input" <c:if test="${f.required}">required</c:if>></textarea>
                    </c:when>
                    <c:when test="${f.type == 'number'}">
                        <input type="number" class="apv-input" <c:if test="${f.required}">required</c:if>>
                    </c:when>
                    <c:when test="${f.type == 'date'}">
                        <input type="date" class="apv-input" <c:if test="${f.required}">required</c:if>>
                    </c:when>
                    <c:when test="${f.type == 'daterange'}">
                        <div class="apv-daterange">
                            <input type="date" class="apv-input" data-role="start" <c:if test="${f.required}">required</c:if>>
                            <span>~</span>
                            <input type="date" class="apv-input" data-role="end" <c:if test="${f.required}">required</c:if>>
                        </div>
                    </c:when>
                    <c:when test="${f.type == 'select'}">
                        <select class="apv-input" <c:if test="${f.required}">required</c:if>>
                            <option value="">-- 선택 --</option>
                            <c:forEach var="opt" items="${f.options}">
                                <option value="${opt}">${opt}</option>
                            </c:forEach>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <input type="text" class="apv-input" <c:if test="${f.required}">required</c:if>>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>

        <div class="apv-field-wrap">
            <label>결재자 (순서대로 결재가 진행됩니다) *</label>
            <ul id="approverList"></ul>
            <button type="button" class="btn_s" onclick="openApproverPopup()">+ 결재자 추가</button>
        </div>

        <c:if test="${template.fileAtchPosblAt == 'Y'}">
            <div class="apv-field-wrap">
                <label>첨부파일</label>
                <input name="file_1" id="egovComFileUploader" type="file" multiple>
                <div id="egovComFileList"></div>
            </div>
        </c:if>

        <button type="submit" class="btn_submit">제출</button>
    </form>

    <script>
        function openApproverPopup() {
            window.open('<c:url value="/apv/EgovApvApproverPopup.do" />', 'apvApproverPopup', 'width=560,height=500,scrollbars=yes');
        }

        // 결재자 선택 팝업에서 선택 시 호출되는 콜백(opener.fnEgovApvAddApprover).
        function fnEgovApvAddApprover(uniqId, emplyrNm) {
            var list = document.getElementById('approverList');
            if ([...list.children].some(function (li) { return li.getAttribute('data-uniq-id') === uniqId; })) {
                alert('이미 추가된 결재자입니다.');
                return;
            }
            var li = document.createElement('li');
            li.setAttribute('data-uniq-id', uniqId);
            li.innerHTML =
                '<span class="apv-approver-handle">&#9776;</span>' +
                '<span class="apv-approver-step"></span>' +
                '<span class="apv-approver-nm">' + emplyrNm + '</span>' +
                '<button type="button" class="btn_s" onclick="this.closest(\'li\').remove(); renumberApprovers();">삭제</button>';
            list.appendChild(li);
            renumberApprovers();
        }

        function renumberApprovers() {
            document.querySelectorAll('#approverList > li').forEach(function (li, idx) {
                li.querySelector('.apv-approver-step').textContent = (idx + 1) + '차';
            });
        }

        $(function () {
            $('#approverList').sortable({
                handle: '.apv-approver-handle',
                update: renumberApprovers
            });

            <c:if test="${template.fileAtchPosblAt == 'Y'}">
            var multiSelector = new MultiSelector(document.getElementById('egovComFileList'), 5);
            multiSelector.addElement(document.getElementById('egovComFileUploader'));
            </c:if>
        });

        document.getElementById('submitForm').addEventListener('submit', function (e) {
            var approverIds = [...document.querySelectorAll('#approverList > li')].map(function (li) { return li.getAttribute('data-uniq-id'); });
            var approverNms = [...document.querySelectorAll('#approverList > li .apv-approver-nm')].map(function (el) { return el.textContent; });
            if (approverIds.length === 0) {
                alert('결재자를 한 명 이상 추가하세요.');
                e.preventDefault();
                return;
            }
            document.getElementById('approverIdsInput').value = approverIds.join(';');
            document.getElementById('approverNmsInput').value = approverNms.join(';');

            var values = {};
            document.querySelectorAll('.apv-field-wrap[data-field-id]').forEach(function (wrap) {
                var fieldId = wrap.getAttribute('data-field-id');
                var type = wrap.getAttribute('data-field-type');
                if (type === 'daterange') {
                    values[fieldId] = {
                        start: wrap.querySelector('[data-role=start]').value,
                        end: wrap.querySelector('[data-role=end]').value
                    };
                } else {
                    var input = wrap.querySelector('.apv-input');
                    values[fieldId] = input ? input.value : '';
                }
            });
            document.getElementById('fieldValuesJson').value = JSON.stringify(values);
        });
    </script>
</body>
</html>
