<!-- 
전자정부프레임워크 관련 테스트 홈페이지 파일입니다. 헤드 부분입니다.
###
 -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
<!--  w5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js" integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js" integrity="sha384-SR1sx49pcuLnqZUnnPwx6FCym0wLsk5JZuNx2bPPENzswTNFaQU1RDvt3wT4gWFG" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.min.js" integrity="sha384-j0CNLUeiqtyaRmlzUHCPZ+Gy5fQu0dQ6eZ/xAww941Ai1SxSY+0EQqNXNE6DZiVc" crossorigin="anonymous"></script>
<link href="/dms/css/main.css" rel="stylesheet">
<!-- a태그 링크클릭 색상 없애기  -->
<style type="text/css">
A:link {text-decoration:none;}
A:visited {text-decoration:none;}
A:active {text-decoration:none;}
A:hover {text-decoration:none;}

</style>


</head>

<!--  최상단 메뉴 -->
<nav id=mainNav onmouseover='mouse_show();' onmouseout='mouse_hide();'>
	<div class="container-fluid" style="background-color:#12357a" >
		<div style="height:10px;">&nbsp;</div>
		<table>
			<tr>
				<td width=10%><a class="navbar-brand mr-auto" href="https://liveapp.co.kr/"> <img src="/selftest/dms/img/t_l_logo.png" height="50"></a></td>
				
				<td width=10%><a href="/dms/index.jsp" style="color:white;">&nbsp;| E-Class System&nbsp;&nbsp;</a></td>
				<td width=10%><a href="/dms/index.jsp" style="color:white;">학습홈</a></td>
				<td width=10%>학습공지</td>
				<td width=10%>학습관리</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
	</div>
</nav>

	<!-- 서브 메뉴 -->
	<div id="sub_menu" >
		<div class="container-fluid" onmouseover='mouse_show();' onmouseout='mouse_hide();' style="width:100%;">
			<table>
				<tr>
					<td width=10%>&nbsp;</td>
					<td width=10%>&nbsp;</td>
					<td width=10%>&nbsp;</td>
					<td width=10%><a class="dropdown-item" href="/dms/jsp/board.jsp?bo_table=notice">수강과목 공지</a></td>
					<td width=10%><a class="dropdown-item" href="/dms/jsp/login.jsp">로그인</a></td> 
					<!-- login.jsp 삽입 필요 (기능, 백엔드) -->
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</table>
		</div>
	</div>
<!-- 마우스 메뉴 보이기 -->
<script>

mouse_hide();

function mouse_show() {
	document.all.sub_menu.style="display:block;";
}

function mouse_hide() {
	document.all.sub_menu.style="display:none;";
}
document
</script>
<!-- head 종료 -->