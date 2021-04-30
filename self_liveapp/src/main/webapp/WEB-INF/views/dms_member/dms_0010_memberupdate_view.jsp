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

	<form name="memberupdate_form" id="memberupdate_form">
		<table>
			<tr>
				<td>아이디</td>
				<td>
					<input class="chk" type="text" id="mms_userid" name="mms_userid" value="${member.mms_userid }" readonly="readonly" title="아이디를 입력해주세요."/>
				</td>
			</tr>
			
			<tr>
				<td>패스워드</td>
				<td>
					<input class="chk" type="password" id="mms_userpass" name="mms_userpass" title="패스워드를 입력해주세요."/>
				</td>
			</tr>
			
			<tr>
				<td>이름</td>
				<td>
					<input class="chk" type="text" id="mms_username" name="mms_username" value="${member.mms_username }" title="이름을 입력해주세요."/>
				</td>
			</tr>
		</table>
	</form>
	<br/>
	
	<div>
		<button type="button" id="memberupdate_btn" name="memberupdate_btn">회원정보수정</button>
		<button type="button" id="cancel_btn" name="cancel_btn">취소</button>
	</div>
	
<script type="text/javascript">
	
	$(document).ready(function(){
		
		var form_obj = $("form[name='memberupdate_form']");
		
		$("#memberupdate_btn").on("click", function(){
			
			if(fn_valiChk()) {
				return false;
			}
			
			$.ajax({
				url : "/dms_member/passchk"
				, type : "POST"
				, dataType : "json"
				, data : $("#memberupdate_form").serializeArray()
				, success : function(data) {
					if(data == true) {
						if(confirm("회원정보를 수정하시겠습니까?")) {
							form_obj.attr("action", "/dms_member/dms_0010_memberupdate");
							form_obj.attr("method", "post");
							form_obj.submit();
						}
					} else {
						alert("패스워드가 틀렸습니다.");
						return;
					}
				}
			});
			
			
		});
		
		$("#cancel_btn").on("click", function(){
			location.href = "/main";
		});
		
	});
	
	function fn_valiChk() {
		
		var reg_form = $("form[name='memberupdate_form'] .chk").length;
		
		for(var i = 0; i < reg_form; i++) {
			if($(".chk").eq(i).val() == "" || $(".chk").eq(i).val() == null) {
				alert($(".chk").eq(i).attr("title"));
				return true;
			}
		}
		
	}
	
</script>
</body>
</html>