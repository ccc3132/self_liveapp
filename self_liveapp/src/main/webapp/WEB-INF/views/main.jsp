<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>e-러닝 플랫폼 SELF</title>

</head>
<body>

	<%@include file="/WEB-INF/views/menu.jsp" %>

	<div class="container">
		<div style="height:20px;"></div>
		
		<div class="row">
			<div class="col-md-6">
				<img src="/resources/common/img/univ_main.png" class="rounded">
			</div>
            <div class="col-md-6">
                 <div class="card shadow mb-4">
					<div class="card-header py-3">
						<font class="m-0 font-weight-bold text-primary" style="font-size:13px;">로그인</font>
					</div>
                        
					<div class="card-body">
						<!--  login 기능 필요 -->						
						<form name="main_form">
						<!-- login 기능 -->
								<div class="row">
									
									<!-- 년도 선택 -->
									<div class="col-md-6">
										<select style="font-size:13px;" id="year" name="year" class="form-control form-control-sm">
											<option value="2020">
												2020년도	
											</option>
											
											<option value="2021" selected="selected">
												2021년도	
											</option>												
										</select><br>
									</div>
									
									<!-- 학기 선택 -->
									<div class="col-md-6">
								
										<select style="font-size:13px;" id="term" name="term" class="form-control form-control-sm">
											<option value="01">1학기 </option>
											<option value="02">2학기
											</option>
										</select><br>
									</div>
									<c:if test="${member == null }">	
									<div class="col-md-3">
										<label for="userid" style="font-size:13px">아이디</label> <br>
									</div>
					
									<div class="col-md-9">
										<input type="text" class="form-control form-control-sm " id="mms_userid" name="mms_userid"
										placeholder="학생(학번), 교수&amp;강사(부여코드)" style="font-size:13px;"><br>
									</div>
					
									<div class="col-md-3">
										<label for="userpass" style="font-size:13px;">비밀번호</label><br>
									</div>
					
									<div class="col-md-9">
								       <input type="password" id="mms_userpass" name="mms_userpass" class="form-control form-control-sm " 
								       placeholder="생년월일 앞6자리" style="font-size:13px;"> <br>
									</div>
					
									<div class="col-md-12">
										<button type="submit" class="btn btn-secondary  btn-block " style="font-size:13px;background-color:#6F69FD; width:100%;"
										id="login_btn" name="login_btn">로그인</button>
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
								</div>
							</form>
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

<%@ include file="tail.jsp" %>
</body>
</html>