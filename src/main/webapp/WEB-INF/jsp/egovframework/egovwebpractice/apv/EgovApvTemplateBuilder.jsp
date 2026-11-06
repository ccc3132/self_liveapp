<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결재 양식 빌더</title>
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css' />">
    <link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/cmm/jqueryui.css' />">
    <script src="<c:url value='/js/egovframework/com/cmm/jquery.js' />"></script>
    <script src="<c:url value='/js/egovframework/com/cmm/jqueryui.js' />"></script>
    <style>
        .apv-builder { display: flex; gap: 20px; align-items: flex-start; }
        .apv-palette { width: 180px; flex: none; }
        .apv-palette-item { display: flex; align-items: center; width: 100%; margin-bottom: 6px; padding: 7px 10px; border-radius: 7px; background: #3a6df0; color: #fff; font-size: 13px; cursor: grab; box-shadow: 0 1px 2px rgba(20,25,40,.08); }
        .apv-palette-item:active { cursor: grabbing; }
        .apv-palette-item.ui-draggable-dragging { opacity: .85; z-index: 1000; }
        .apv-canvas { flex: 1; min-width: 0; }
        .apv-canvas ul { list-style: none; padding: 0; margin: 0; min-height: 60px; }
        .apv-canvas ul.apv-drop-hover { outline: 2px dashed #3a6df0; outline-offset: 4px; border-radius: 8px; }
        .apv-field { border: 1px solid #e7e9ee; border-radius: 8px; padding: 10px 12px; margin-bottom: 8px; background: #fff; }
        .apv-field .apv-field-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
        .apv-field .apv-drag-handle { cursor: grab; color: #9aa0ab; flex: none; font-size: 16px; }
        .apv-field .apv-type-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; background: #eaf0ff; color: #3a6df0; font-size: 11px; font-weight: 700; flex: none; }
        .apv-field input[type=text] { flex: 1; }
        .apv-field .apv-options-row { display: none; }
        .apv-field.apv-type-select .apv-options-row { display: flex; }
        .apv-field.ui-sortable-helper { box-shadow: 0 4px 16px rgba(20,25,40,.18); }
        .apv-placeholder { border: 2px dashed #3a6df0; border-radius: 8px; margin-bottom: 8px; background: #eaf0ff; }
        .apv-empty { color: #9aa0ab; padding: 20px; text-align: center; border: 1px dashed #d7dae0; border-radius: 8px; }
    </style>
</head>
<body>
    <h2>⚙결재 양식 빌더</h2>

    <form id="templateForm" method="post"
          action="<c:url value='${empty template ? "/apv/admin/insertApvTemplate.do" : "/apv/admin/updateApvTemplate.do"}' />">
        <c:if test="${not empty _csrf}"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/></c:if>
        <c:if test="${not empty template}"><input type="hidden" name="templateId" value="${template.templateId}"></c:if>
        <input type="hidden" name="fieldSchema" id="fieldSchema">

        <div class="search_box">
            <label>템플릿명: <input type="text" name="templateNm" id="templateNm" required maxlength="100"
                   value="<c:out value='${template.templateNm}'/>"></label>
            <label>설명: <input type="text" name="description" maxlength="500"
                   value="<c:out value='${template.description}'/>"></label>
            <label><input type="checkbox" name="fileAtchPosblAt" id="fileAtchPosblAt" value="Y"
                   ${template.fileAtchPosblAt == 'Y' ? 'checked' : ''}> 첨부파일 허용</label>
            <button type="submit" class="btn_submit">저장</button>
        </div>

        <div class="apv-builder">
            <div class="apv-palette board_list" style="padding:12px;">
                <strong>필드 추가</strong><br><br>
                <p style="font-size:11.5px; color:#9aa0ab; margin:0 0 8px;">오른쪽 캔버스로 끌어다 놓으세요.</p>
                <div class="apv-palette-item" data-type="text">+ 한 줄 텍스트</div>
                <div class="apv-palette-item" data-type="textarea">+ 여러 줄 텍스트</div>
                <div class="apv-palette-item" data-type="number">+ 숫자</div>
                <div class="apv-palette-item" data-type="date">+ 날짜</div>
                <div class="apv-palette-item" data-type="daterange">+ 기간(시작~종료)</div>
                <div class="apv-palette-item" data-type="select">+ 드롭다운</div>
            </div>
            <div class="apv-canvas">
                <ul id="canvas"></ul>
                <p class="apv-empty" id="emptyHint">왼쪽에서 필드를 추가하세요.</p>
            </div>
        </div>
    </form>

    <textarea id="existingSchema" style="display:none"><c:out value="${template.fieldSchema}"/></textarea>

    <script>
        var typeLabels = { text: '한 줄 텍스트', textarea: '여러 줄 텍스트', number: '숫자', date: '날짜', daterange: '기간', select: '드롭다운' };
        var seq = 0;

        function addField(type, data) {
            seq++;
            var id = 'f' + Date.now() + '_' + seq;
            var li = document.createElement('li');
            li.className = 'apv-field apv-type-' + type;
            li.setAttribute('data-id', id);
            li.setAttribute('data-type', type);
            li.innerHTML =
                '<div class="apv-field-row">' +
                    '<span class="apv-drag-handle" title="드래그해서 순서 변경">&#9776;</span>' +
                    '<span class="apv-type-badge">' + typeLabels[type] + '</span>' +
                    '<input type="text" class="apv-label" placeholder="필드 라벨" value="' + escapeAttr(data && data.label || '') + '">' +
                    '<label style="flex:none"><input type="checkbox" class="apv-required" ' + (data && data.required ? 'checked' : '') + '> 필수</label>' +
                    '<button type="button" class="btn_s" onclick="removeField(this)">삭제</button>' +
                '</div>' +
                '<div class="apv-field-row apv-options-row">' +
                    '<input type="text" class="apv-options" placeholder="옵션(콤마로 구분, 예: 연차,반차,병가)" value="' + escapeAttr(data && data.options ? data.options.join(',') : '') + '">' +
                '</div>';
            document.getElementById('canvas').appendChild(li);
            updateEmptyHint();
        }

        function escapeAttr(s) {
            return String(s).replace(/&/g, '&amp;').replace(/"/g, '&quot;');
        }

        function removeField(btn) {
            btn.closest('li').remove();
            updateEmptyHint();
        }

        function updateEmptyHint() {
            var canvas = document.getElementById('canvas');
            document.getElementById('emptyHint').style.display = canvas.children.length ? 'none' : 'block';
        }

        document.getElementById('templateForm').addEventListener('submit', function (e) {
            var items = document.querySelectorAll('#canvas > li');
            var schema = [];
            items.forEach(function (li) {
                var type = li.getAttribute('data-type');
                var field = {
                    id: li.getAttribute('data-id'),
                    type: type,
                    label: li.querySelector('.apv-label').value,
                    required: li.querySelector('.apv-required').checked
                };
                if (type === 'select') {
                    field.options = li.querySelector('.apv-options').value.split(',').map(function (s) { return s.trim(); }).filter(Boolean);
                }
                schema.push(field);
            });
            if (schema.length === 0) {
                alert('필드를 하나 이상 추가하세요.');
                e.preventDefault();
                return;
            }
            document.getElementById('fieldSchema').value = JSON.stringify(schema);
        });

        // 수정 화면이면 기존 필드 구성을 복원한다.
        (function () {
            var raw = document.getElementById('existingSchema').value.trim();
            if (!raw) { updateEmptyHint(); return; }
            try {
                JSON.parse(raw).forEach(function (f) { addField(f.type, f); });
            } catch (e) { /* 손상된 데이터는 무시하고 빈 캔버스로 시작 */ }
            updateEmptyHint();
        })();

        // 드래그앤드롭: 팔레트 → 캔버스로 끌어다 놓으면 필드 추가, 캔버스 안에서는 드래그 핸들로 순서 변경(sortable).
        $(function () {
            $('.apv-palette-item').draggable({
                helper: 'clone',
                revert: 'invalid',
                revertDuration: 150,
                zIndex: 1000
            });
            $('#canvas').droppable({
                accept: '.apv-palette-item',
                tolerance: 'pointer',
                hoverClass: 'apv-drop-hover',
                drop: function (event, ui) {
                    addField(ui.draggable.data('type'));
                }
            });
            $('#canvas').sortable({
                handle: '.apv-drag-handle',
                placeholder: 'apv-placeholder',
                items: '> li.apv-field',
                forcePlaceholderSize: true
            });
        });
    </script>
</body>
</html>
