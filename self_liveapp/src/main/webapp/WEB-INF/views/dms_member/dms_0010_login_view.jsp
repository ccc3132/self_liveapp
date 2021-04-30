<!-- 로그인 페이지 화면 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  	<%@include file="/WEB-INF/views/menu.jsp" %>
	<title>로그인 | e-러닝 플랫폼 SELF</title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="col-md-12">
			<div class="card">
				<div class="card-header py-3">
					<h6 class="m-0 font-weight-bold text-primary">로그인</h6>
				</div>	
				<div class="card-body">
					<div class="row">
						<div class="col-md-6">	
							<select style="font-size:13px;" id="year" name="year" class="form-control form-control-sm">
								<option value="2020">2020년도</option>
								<option value="20221">2021년도</option>
							</select>
							<br>
						</div>
					
						<div class="col-md-6"> <!-- 학기 -->
							<select style="font-size:13px;" id="term" name="term" class="form-control form-control-sm">
								<option value="01">1학기</option>
								<option value="02">2학기</option>
							</select>	
							<br>
						</div>
					</div>
				
					<div class="row">
						<!-- 아이디 입력 -->
						<div class="col-md-3" style="font-size:12px;">
						    <label for="userid">아이디</label>
							<br>
						</div>
				
						<div class="col-md-9" style="font-size:12px;">
						    <input type="text" class="form-control form-control-sm " style="font-size:12px;" id="mms_userid" name="mms_userid" placeholder="학생(학번), 교수&amp;강사(부여코드)">
						<br>
						</div>
					</div>
					
					
					<div class="row">
					<!-- 비밀번호 입력 -->
					<div class="col-md-3" style="font-size:12px;">
					    <label for="userpass">비밀번호</label>
						<br>
					</div>
					
					<div class="col-md-9" style="font-size:12px;">
					    <input type="password" id="mms_userpass" name="mms_userpass" class="form-control form-control-sm" style="font-size:12px;" placeholder="생년월일 앞6자리"><br>
					</div>
					</div>
				
					<div class="col-md-12">
					    <button type="submit" class="btn btn-primary btn-block form-control-sm " id="login_btn" width=100% name="login_btn">전송</button>
						<br>
					</div>
					
					<div class="col-md-12" width=100%>
					<a href="/main" class="btn btn-danger btn-block form-control-sm " width=100%>취소</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

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


<div style="height:100px;"></div>
  	<%@include file="/WEB-INF/views/tail.jsp" %>


</body>
</html>