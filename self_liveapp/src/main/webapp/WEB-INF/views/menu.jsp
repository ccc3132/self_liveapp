<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>e-러닝 플랫폼 SELF</title>
</head>
<body class="bg-light">
	<nav class="navbar fixed-top" id=mainNav onmouseover='mouse_show();' onmouseout='mouse_hide();'>
		<div class="container-fluid" style="text-align:left; font-size:13px;">
			<table class="table table-borderless">
				<tr>
					<td width=10%><a class="navbar-brand mr-auto" href="https://liveapp.co.kr"> <img src="/resources/common/img/t_l_logo.png" height="50"></a></td>
					<td width=15%><div style="height:10px;"></div><a href="" style="color:white; font-size:16px;">&nbsp;| E-Class System&nbsp;&nbsp;</a></td>
					<td width=10%><div style="height:10px"></div>
					<a href="" style="color:white;">학습홈</a>
					</td>
					<td width=10%><div style="height:10px;"></div>학습공지</td>
					<td width=10%><div style="height:10px;"></div>학습관리</td>
					<td width=10%><div style="height:10px;"></div>
						<c:if test="${member != null }">
							<a href="/dms_member/dms_0010_memberupdate_view"  style='color:white;'  ><font color=#D4D5D6>회원정보수정</font></a>
						</c:if>				
					</td>
					<td width=10%><div style="height:10px;"></div>
						<c:if test="${member != null }">
							<a href="/logout"  style='color:white;'  ><font color=#D4D5D6>로그아웃</font>&nbsp;&nbsp;&nbsp;&nbsp;</a>
						</c:if>		
					</td>
					<td></td>
				</tr>
			</table>
		</div>
	</nav>
	<div style="height:80px;">&nbsp;</div>
	<div id="sub_menu" class="container-fluid" onmouseover='mouse_show();' onmouseout='mouse_hide();'>
		<table class="table table-borderless">
			<tr>
				<td width=10%>&nbsp;</td>
				<td width=15%>&nbsp;</td>
				<td width=10%>&nbsp;</td>
				<td width=10%><a href="/dms_board/dms_0010_list">수강과목 공지</a></td>
				<td width=10%>	
					<span>
					<c:if test="${member == null }"><a href="/main">로그인</a></c:if>
					</span>
					
					<span>
					<c:if test="${member != null }">
						<p><a href="/dms_member/dms_0010_member_view">마이페이지</a></p>
						<p><a href="/">질의 응답</a></p>
						<p><a href="/">과제제출</a></p>
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