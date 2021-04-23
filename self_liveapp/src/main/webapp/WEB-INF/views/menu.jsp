<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인화면</title>
</head>
<body>
	<nav id=mainNav onmouseover='mouse_show();' onmouseout='mouse_hide();'>
		<div class="container-fluid" style="background-color:#12357a" >
			<div style="height:10px;">&nbsp;</div>
			<table>
				<tr>
					<td width=10%><a class="navbar-brand mr-auto" href="https://liveapp.co.kr/"> <img src="dms/img/t_l_logo.png" height="50"></a></td>
					
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

	<div id="sub_menu" >
		<div class="container-fluid" onmouseover='mouse_show();' onmouseout='mouse_hide();' style="width:100%;">
			<table>
				<tr>
					<td width=10%>&nbsp;</td>
					<td width=10%>&nbsp;</td>
					<td width=10%>&nbsp;</td>
					<td width=10%><a class="dropdown-item" href="/dms/jsp/board.jsp?bo_table=notice">수강과목 공지</a></td>
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
	</div>	
</body>
</html>