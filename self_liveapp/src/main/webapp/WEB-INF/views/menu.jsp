<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인화면 테스트</title>
</head>
<body>
	<nav class="navbar fixed-top" id=mainNav onmouseover='mouse_show();' onmouseout='mouse_hide();'>
		<div class="container-fluid" style="text-align:left;">
			<table class="table table-borderless">
				<tr>
					<td width=10%><a class="navbar-brand mr-auto" href="/"> <img src="/resources/common/img/t_l_logo.png" height="35"></a></td>
					<td width=15%><a href="/main.jsp" style="color:white;">&nbsp;| E-Class System&nbsp;&nbsp;</a></td>
					<td width=10%><a href="/main.jsp" style="color:white;">학습홈</a></td>
					<td width=10%>학습공지</td>
					<td width=10%>학습관리</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</table>
		</div>
	</nav>
	<div style="height:55px;">&nbsp;</div>
	<div id="sub_menu" class="container-fluid" onmouseover='mouse_show();' onmouseout='mouse_hide();'>
		<table class="table table-borderless">
			<tr>
				<td width=10%>&nbsp;</td>
				<td width=15%>&nbsp;</td>
				<td width=10%>&nbsp;</td>
				<td width=10%><a href="/WEB-INF/views/dms_board/dms_0010_list.jsp">수강과목 공지</a></td>
				<td width=10%>	
					<span>
					<c:if test="${member != null }"><a href="/logout">로그아웃</a></c:if>
					<c:if test="${member == null }"><a href="/main">로그인</a></c:if>
					</span>
					<span>
					<c:if test="${member != null }">
					<p>${member.mms_userid }님 안녕하세요.</p>
					</c:if>
					</span>
				</td> 
				<!-- login.jsp 삽입 필요 (기능, 백엔드) -->
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
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
<!-- menu 종료 -->
		
</body>
</html>