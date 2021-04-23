<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 글쓰기</title>
</head>
<body>

	<hr/>
	<nav><%@include file="/WEB-INF/views/menu.jsp" %></nav>
	<hr/>

	<form name="write_form" enctype="multipart/form-data">
		<table>
			<c:if test="${member.mms_userid != null }">
				<tr>
					<td>제목</td>
					<td><input class="chk" id="cms_title" name="cms_title" title="제목을 입력해주세요."/></td>
				</tr>
				
				<tr>
					<td>내용</td>
					<td><textarea class="chk" id="cms_content" name="cms_content" title="내용을 입력해주세요."></textarea></td>
				</tr>
				
				<tr>
					<td>작성자</td>
					<td><input class="chk" id="cms_writer" name="cms_writer" title="이름을 입력해주세요."/></td>
				</tr>
				
				<tr>
					<td colspan="2"><input type="file" style="float:left;" name="file_1"></td>
				</tr>
				
				<tr>
					<td colspan="2" id="file_index" style="text-align:left;"></td>
				</tr>
			</c:if>
			
			<c:if test="${member.mms_userid == null }">
				<p>로그인 후에 사용하실 수 있습니다</p>
			</c:if>
		</table>
	</form>
	<hr/>
	
	<div>
		<button type="button" id="write_btn" name="write_btn">등록</button>
		<button type="button" class="file_add_btn">파일추가</button>
		<button type="button" id="cancel_ctn" name="cancel_ctn">취소</button>
	</div>
	
<script type="text/javascript">

	$(document).ready(function(){
		
		var form_obj = $("form[name='write_form']");
		
		$("#write_btn").on("click", function(){
			
			if(fn_valiChk()) {
				return false;
			}
			
			form_obj.attr("action", "/dms_board/dms_0010_write");
			form_obj.attr("method", "post");
			form_obj.submit();
		});
		
		$("#cancel_ctn").on("click", function(){
			location.href = "/dms_board/dms_0010_list";
		});
		
		$(".file_add_btn").on("click", function(){
			
			var file_index = 1;
			
			$("#file_index").append("<div><input type='file' style='float:left; width:700px;' name='file_"+(file_index++)+"'>"
									+"</button>"
									+"<button type='button' style='float:right;' id='file_del_btn'>"
									+"삭제"
									+"</button></div>");
		
			$(document).on("click", "#file_del_btn", function(){
				$(this).parent().remove();
			});
		
		});
		
	});
	
	function fn_valiChk() {
		
		var reg_form = $("form[name='write_form'] .chk").length;
		
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