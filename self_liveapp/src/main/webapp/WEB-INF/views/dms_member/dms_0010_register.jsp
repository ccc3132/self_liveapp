<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
</head>
<body>


	<%@include file="/WEB-INF/views/menu.jsp" %>


	<form name="register_form">
		<table>
			<tr>
				<td>아이디</td>
				<td>
					<input class="chk" type="text" id="mms_userid" name="mms_userid" title="아이디를 입력해주세요."/>
				</td>
				<td style="width:80px;">
					<button class="idchk" type="button" id="idchk" onclick="fn_idChk();" value="N">중복확인</button>
				</td>
			</tr>
			
			<tr>
				<td>패스워드</td>
				<td colspan="2">
					<input class="chk" type="password" id="mms_userpass" name="mms_userpass" title="패스워드를 입력해주세요."/>
				</td>
			</tr>
			
			<tr>
				<td>이름</td>
				<td colspan="2">
					<input class="chk" type="text" id="mms_username" name="mms_username" title="이름을 입력해주세요."/>
				</td>
			</tr>
		</table>
	</form>
	<br/>
	
	<div>
		<button type="button" id="register_btn" name="register_btn">회원가입</button>
		<button type="button" id="cancel_btn" name="cancel_btn">취소</button>
	</div>
	
<script type="text/javascript">
	
	$(document).ready(function(){
		
		var form_obj = $("form[name='register_form']");
		
		$("#register_btn").on("click", function(){
			
			if(fn_valiChk()) {
				return false;
			}
			
			form_obj.attr("action", "/dms_member/dms_0010_register");
			form_obj.attr("method", "post");
			form_obj.submit();
		});
		
		$("#cancel_btn").on("click", function(){
			location.href = "/login";
		});
		
	});
	
	function fn_valiChk() {
		
		var reg_form = $("form[name='register_form'] .chk").length;
		
		for(var i = 0; i < reg_form; i++) {
			if($(".chk").eq(i).val() == "" || $(".chk").eq(i).val() == null) {
				alert($(".chk").eq(i).attr("title"));
				return true;
			}
		}
		
	}
	
	function fn_idChk() {
		$.ajax({
			url : "/dms_member/idchk"
			, type : "post"
			, dataType : "json"
			, data : {
				"mms_userid" : $("#mms_userid").val()
			}, success : function(data) {
				if(data == 1) {
					alert("중복된 아이디입니다.");
				} else if(data == 0) {
					$("#idchk").attr("value", "Y");
					alert("사용가능한 아이디입니다.");
				}
			}
		});
	}
	
</script>
</body>
</html>