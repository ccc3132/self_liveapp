# 11. 디자인/UX 가이드라인

> [메인 README](../../README.md) · 이전: [10. 외부 서버에 배포하기](10_deploy_readme.md)

새 화면을 만들거나 기존 화면을 고칠 때 지키는 UI/UX 규칙 모음입니다. CSS 톤(색상/여백/버튼 등)은
`com.css` 의 "2026 UI 현대화 오버레이"가 전역으로 처리하므로 여기서는 다루지 않고, **레이아웃/상호작용
패턴** 위주로 적습니다.

## 등록/수정/상세 화면은 모달(팝업)로 — 전체 적용 완료 (2026-11-07)

- 사용자 피드백(2026-11-06): "수정창이나 등록창이나 조회창들은 팝업? 모달창으로 보는게 좋을것 같다."
  → 이후 "모든 메뉴 등록·수정·조회창이 전부 모달로 되어 있는지 확인해" → "그 작업 전부 하고 readme
  갱신까지 진행하자"는 요청에 따라 **상속받은 공통컴포넌트 화면을 포함해 전체 적용 완료**.
- **공용 컴포넌트**: `js/egovframework/com/cmm/EgovIframeModal.js` — `EgovIframeModal.open({url|form,
  title, doneUrlIncludes})`. 등록/수정 폼 JSP 자체는 전혀 건드리지 않고, 목록 화면의 트리거(버튼/링크)만
  바꿔서 같은 URL을 모달 안 `<iframe>`에 띄운다. 저장 후 서버가 "목록 URL"로 돌아오면(redirect) 또는
  "저장 액션 자신의 URL"에 머물면(forward — Spring 의 `forward:` 는 브라우저가 보는 URL 자체가 바뀌지
  않는다) 그 사실을 `doneUrlIncludes` 로 감지해서 모달을 자동으로 닫고 목록을 새로고침한다.
  - 레거시 `fn_egov_postNavigate` (게시판/주소록/쪽지/일정 등 여러 모듈이 공유하는 페이지 이동
    유틸리티, `js/egovframework/com/cmm/egovPostNavigate.js`)에도 선택적 `modalOptions` 인자를 추가해
    기존 호출부를 깨지 않고 모달로 전환할 수 있게 했다.
  - 모달 스타일은 `com.css` 의 "2026 UI 현대화 오버레이"에 `.egov-modal-*` 로 포함되어 있어, 이미
    `com.css` 를 링크하는 모든 상속 JSP 가 추가 include 없이 바로 쓸 수 있다.
- **doneUrlIncludes 를 고를 때 반드시 확인할 것 — redirect vs forward**: 컨트롤러가 성공 시
  `"redirect:/.../목록.do"` 를 반환하면 브라우저가 실제로 그 목록 URL로 이동하므로 `doneUrlIncludes`
  에 **목록 URL**을 쓰면 된다. 반대로 `"forward:/.../목록.do"` 를 반환하면 서버 내부적으로만 다른
  핸들러로 넘어갈 뿐 브라우저가 보는 URL은 바뀌지 않으므로, `doneUrlIncludes` 에는 **그 저장 폼 자신의
  action URL**(예: `insertLeaderSchdul.do`)을 써야 한다. 이걸 거꾸로 짚으면 폼 저장은 되는데 모달이
  자동으로 안 닫히는 버그가 난다 — 실제로 PR4(주소록)에서 한 번 이 실수를 했다가 PR5에서 핫픽스했다.
  새 화면에 적용할 때는 꼭 해당 컨트롤러 메서드의 반환값(`redirect:` 인지 `forward:` 인지)을 먼저
  확인할 것.
- **적용 범위**: 전자결재(`apv`, 결재함은 처음부터 모달), 게시판(`cop.bbs`), 쪽지(`uss.ion.ntm/ntr/nts`),
  주소록(`cop.adb`), 직원관리(`uss.umt`), 일정관리 전 모듈(`cop.smt` 의 sim/sdm/lsm/mtm/dsm/wmr/mrm/djm).
  권한그룹관리(`sec.rgm`)는 원래부터 목록+편집이 한 화면이라 대상에서 제외.
- **예외(그대로 유지)**: 주소록의 우편번호 검색 팝업류(`EgovAddressBookPopup.jsp` + `EgovModalPopupFrame.jsp`)
  처럼 이미 자체 팝업-안-팝업 구조를 가진 화면, 결재자/직원 검색처럼 `window.open` 으로 여는 선택 팝업은
  건드리지 않았다 — `window.opener` 는 실제로 `window.open` 을 호출한 창을 가리키므로 그 호출 창이
  모달 iframe 안에 한 단계 더 중첩돼도 정상 동작한다(조사로 확인).
- **새 화면을 만들 때**: 목록 JSP 의 등록/수정/상세 트리거를 처음부터 `EgovIframeModal.open(...)` 으로
  작성하면 된다 — 더 이상 "일단 페이지 이동으로 만들고 나중에 모달로 바꾼다"는 단계를 거칠 필요 없음.

## 결정 전 참고

- 전체 페이지 전환이 더 적합한 경우(내용이 길어 모달 안에 다 넣기 어려운 등록 폼, 별도 URL로 공유/북마크가
  필요한 화면)는 기존 방식을 유지해도 됩니다. 모든 화면을 기계적으로 모달화하지는 않습니다.
- 쪽지 쓰기처럼 사이드바에서 직접 진입하는 독립 화면(목록 화면이 여는 구조가 아닌 것)은 모달 전환
  대상이 아닙니다.

---
[메인 README](../../README.md) · 이전: [10. 외부 서버에 배포하기](10_deploy_readme.md)
