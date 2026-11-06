/**
 * 공용 iframe 모달: 등록/수정/상세 화면을 기존 페이지 이동 대신 모달 안의 iframe으로 띄운다.
 * 등록/수정 폼 JSP 자체는 전혀 건드리지 않는다 — 폼이 저장 후 서버 redirect/forward로
 * "목록 화면 URL"로 돌아오는 순간을 doneUrlIncludes 로 감지해서 모달을 닫고 목록을 새로고침한다.
 *
 * 사용법:
 *   EgovIframeModal.open({ url: '/path.do', title: '제목', doneUrlIncludes: 'selectXxxList.do' });
 *   // 검색폼 등 기존 폼을 그대로 POST 하고 싶으면:
 *   EgovIframeModal.open({ form: document.someForm, title: '제목', doneUrlIncludes: 'selectXxxList.do' });
 */
(function (window, document) {
    var IFRAME_NAME = 'egovModalIframe';
    var dom = null;

    function ensureDom() {
        if (dom) { return dom; }
        var backdrop = document.createElement('div');
        backdrop.className = 'egov-modal-backdrop hidden';
        backdrop.innerHTML =
            '<div class="egov-modal-box">' +
                '<div class="egov-modal-head">' +
                    '<h3 class="egov-modal-title"></h3>' +
                    '<button type="button" class="egov-modal-close" aria-label="닫기">&times;</button>' +
                '</div>' +
                '<iframe class="egov-modal-iframe" name="' + IFRAME_NAME + '"></iframe>' +
            '</div>';
        document.body.appendChild(backdrop);

        var iframe = backdrop.querySelector('.egov-modal-iframe');
        var title = backdrop.querySelector('.egov-modal-title');
        var closeBtn = backdrop.querySelector('.egov-modal-close');

        closeBtn.addEventListener('click', close);
        backdrop.addEventListener('click', function (e) {
            if (e.target === backdrop) { close(); }
        });
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && !backdrop.classList.contains('hidden')) { close(); }
        });

        dom = { backdrop: backdrop, iframe: iframe, title: title };
        return dom;
    }

    function close() {
        if (!dom) { return; }
        dom.backdrop.classList.add('hidden');
        dom.iframe.src = 'about:blank';
    }

    function open(opts) {
        var d = ensureDom();
        d.title.textContent = opts.title || '';
        d.backdrop.classList.remove('hidden');

        var doneUrlIncludes = opts.doneUrlIncludes;
        d.iframe.onload = function () {
            if (!doneUrlIncludes) { return; }
            var href;
            try {
                href = d.iframe.contentWindow.location.href;
            } catch (e) {
                return; // 다른 오리진이면 접근 불가 - 이 앱에서는 발생하지 않지만 방어적으로 무시
            }
            if (href.indexOf(doneUrlIncludes) !== -1) {
                close();
                window.location.reload();
            }
        };

        if (opts.form) {
            opts.form.target = IFRAME_NAME;
            opts.form.submit();
        } else if (opts.url) {
            d.iframe.src = opts.url;
        }
    }

    window.EgovIframeModal = { open: open, close: close };
})(window, document);
