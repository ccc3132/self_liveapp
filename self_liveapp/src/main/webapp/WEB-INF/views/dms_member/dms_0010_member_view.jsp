<!-- 학생 관련 마이페이지 화면  -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  	<%@include file="/WEB-INF/views/menu.jsp" %>

<meta charset="UTF-8">
</head>
<body>
	<br><br>
	
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-6">
				<section>
					<div class="card">
						<div class="card-header">
							수강과목
						</div>
						<div class="card-body">
							<% 
							/*
							수강과목
							*/
							
							/*
							//<c:if test="${board != null }">
							1. 수강과목 있을 경우
							2. 수강과목 없을 경우
							//</c:if>
							*/
							%>
						</div>
					</div>
				</section>
			</div>
			
			<div class="col-md-6">
				<div class="row">
					<div class="col-md-12">
						<section>
							<div class="card">
								<div class="card-header">
									<p>수강과목별 교수님 공지</p>
									<p><span style="color:red;">[과목별 교수 공지는 최근 게시물 한개만 표시됩니다.]</span></p>
								</div>
								
								<div class="card-body">
								<!-- 
								
								1. 교수님 있을 경우  표시
								1-1. 교수님 있을 경우 최근게시물 하나 표기
								
								2. 교수님 없을 경우 표시(빈칸)
								
								 -->
								</div>
							</div>
						</section>
					</div>
				</div>
			</div>
		</div>
		
		<br><br>
		
		<div class="row">
			<div class="col-md-6"></div>
			<div class="col-md-6">
				<div class="row">
					<div class="col-md-12">
						<div class="card">
							<div class="card-header">
								LMS 공지
							</div>
							<div class="card-body">
							
								<div class="row">
									<div class="col-md-12">
										${member.mms_username }님! 반갑습니다.	
									</div>
									<div class="col-md-12">
									
									</div>
								</div>
								
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>
	</div>

		<br><br>
	
	<%@include file="/WEB-INF/views/tail.jsp" %>
</body>
</html>