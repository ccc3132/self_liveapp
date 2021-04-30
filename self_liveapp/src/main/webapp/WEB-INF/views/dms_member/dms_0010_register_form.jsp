<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/include/include.jsp" %>
<meta charset="UTF-8">
<title>회원 비밀번호 확인 | e-러닝 플랫폼</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<div class="card">
					<div class="card-header py-3">
						<h6 class="m-0 font-weight-bold text-primary">회원 비밀번호 확인</h6>
					</div>
					<div class="card-body">
					<form name="" action="dms_0010_register" onsubmit="return fmemberconfirm_submit(this);" method="post"> <!-- 이동페이지 -->
						<div class="row">
						학번:<!--  -->  성명:<!--  --> 님의 현재 비밀번호
						</div>
						<div class="row">
							<input type="text" class="form-control form-control-sm " id="mms_" name="mms_"
										placeholder="비밀번호를 한번 더 입력해주세요" style="font-size:13px;">
						</div>
						<div class="row">
							<button type="submit" class="btn btn-primary btn-block btn-sm" style="font-size:13px;">확인</button><br>
						</div>
						<br><br>
						<div class="row">
							<a href="/main" class="btn btn-outline-info btn-block btn-sm">취소</a>
						</div>
					</form>	
					</div>
					
				</div>
			</div>
		</div>
	</div>
</body>
</html>