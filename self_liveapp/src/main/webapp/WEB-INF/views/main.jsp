<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인화면</title>
</head>
<body style="width:300px;">

	<%@include file="/WEB-INF/views/menu.jsp" %>


	<form name="main_form">
		<c:if test="${member == null }">
			<div>
				<label for="userid"></label>
				<input type="text" id="mms_userid" name="mms_userid"/>
			</div>
			
			<div>
				<label for="userpass"></label>
				<input type="password" id="mms_userpass" name="mms_userpass"/>
			</div>
			
			<div>
				<button type="button" id="login_btn" name="login_btn">로그인</button>
				<button type="button" id="register_btn" name="register_btn">회원가입</button>
			</div>
		</c:if>
		
		<c:if test="${member != null }">
			<div>
				<p>${member.mms_userid }님 환영합니다</p>
				<button type="button" id="memberupdate_btn" name="memberupdate_btn">회원정보수정</button>
				<button type="button" id="memberdelete_btn" name="memberdelete_btn">회원탈퇴</button>
				<button type="button" id="logout_btn" name="logout_btn">로그아웃</button>
			</div>
		</c:if>
		
		<c:if test="${msg == false }">
			<p>아이디와 비밀번호를 확인해주세요.</p>
		</c:if>
	</form>
	
<script type="text/javascript">
	
	$(document).ready(function(){
		
		var form_obj = $("form[name='main_form']");
		
		$("#login_btn").on("click", function(){
			form_obj.attr("action", "/login");
			form_obj.attr("method", "post");
			form_obj.submit();
		});
		
		$("#logout_btn").on("click", function(){
			form_obj.attr("action", "/logout");
			form_obj.attr("method", "post");
			form_obj.submit();
		});
		
		$("#register_btn").on("click", function(){
			form_obj.attr("action", "/dms_member/dms_0010_register");
			form_obj.attr("method", "get");
			form_obj.submit();
		});
		
		$("#memberupdate_btn").on("click", function(){
			form_obj.attr("action", "/dms_member/dms_0010_memberupdate_view");
			form_obj.attr("method", "get");
			form_obj.submit();
		});
		
		$("#memberdelete_btn").on("click", function(){
			form_obj.attr("action", "/dms_member/dms_0010_memberdelete_view");
			form_obj.attr("method", "get");
			form_obj.submit();
		});
		
	});
	
</script>
</body>
</html>