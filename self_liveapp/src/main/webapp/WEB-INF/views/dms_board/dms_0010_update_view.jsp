<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<hr/>
	<nav><%@include file="/WEB-INF/views/menu.jsp" %></nav>
	<hr/>

	<form name="update_form"  enctype="multipart/form-data">
		<input type="hidden" id="cms_bno" name="cms_bno" value="${update.cms_bno }"/>
		<input type="hidden" id="page" name="page" value="${scri.page }"/>
		<input type="hidden" id="perPageNum" name="perPageNum" value="${scri.perPageNum }"/>
		<input type="hidden" id="searchType" name="searchType" value="${scri.searchType }"/>
		<input type="hidden" id="keyword" name="keyword" value="${scri.keyword }"/>
		<input type="hidden" id="fileNoDel" name="fileNoDel[]" value=""/>
		<input type="hidden" id="fileNameDel" name="fileNameDel[]" value=""/>

		<table>
			<tr>
				<td>제목</td>
				<td><input class="chk" id="cms_title" name="cms_title" value="${update.cms_title }" title="제목을 입력해주세요."/></td>
			</tr>
			
			<tr>
				<td>내용</td>
				<td>
					<textarea class="chk" id="cms_content" name="cms_content" title="내용을 입력해주세요." style="resize:none;"><c:out value="${update.cms_content }"></c:out></textarea>
				</td>
			</tr>
			
			<tr>
				<td>작성자</td>
				<td><input class="chk" id="cms_writer" name="cms_writer" value="${update.cms_writer }" readonly="readonly"/></td>
			</tr>
			
			<tr>
				<td id="file_index" colspan="2" style="text-align:left;">
					<c:forEach items="${file }" var="file" varStatus="var">
						<div>
							<input type="hidden" id="cms_file_no" name="cms_file_no_${var.index }" value="${file.cms_file_no }"/>
							<input type="hidden" id="cms_file_name" name="cms_file_name" value="cms_file_no_${var.index }"/>
						
							<a href="#" id="cms_file_name" onclick="return false;">
								${file.cms_org_file_name }
							</a>(${file.cms_file_size}kb)
						
							<button type="button" id="file_del_btn" onclick="fn_del('${file.cms_file_no}','cms_file_no_${var.index}');">삭제</button>						
						</div>		
					</c:forEach>
				</td>
			</tr>
		</table>
	</form>
	<hr/>
	
	<div>
		<button type="button" id="update_btn" name="update_btn">확인</button>
		<button type="button" id="cancel_btn" name="cancel_btn">취소</button>
		<button type="button" class="file_add_btn">파일추가</button>
	</div>
	
<script type="text/javascript">

var file_no_arry = new Array();
var file_name_arry = new Array();

	$(document).ready(function(){
		
		var form_obj = $("form[name='update_form']");
		
		$("#update_btn").on("click", function(){
			
			if(fn_valiChk()) {
				return false;
			}
			
			form_obj.attr("action", "/dms_board/dms_0010_update");
			form_obj.attr("method", "post");
			form_obj.submit();
		});
		
		$("#cancel_btn").on("click", function(){
			location.href = "/dms_board/dms_0010_read_view?cms_bno=${update.cms_bno}"
								+ "&page=${scri.page}"
								+ "&perPageNum=${scri.perPageNum}"
								+ "&searchType=${scri.searchType}"
								+ "&keyword=${scri.keyword}";
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
		
		var reg_form = $("form[name='update_form'] .chk").length;
		
		for(var i = 0; i < reg_form; i++) {
			if($(".chk").eq(i).val() == "" || $(".chk").eq(i).val() == null) {
				alert($(".chk").eq(i).attr("title"));
				return true;
			}
		}
		
	}
	
	function fn_del(value, name) {
		file_no_arry.push(value);
		file_name_arry.push(name);
		
		$("#fileNoDel").attr("value", file_no_arry);
		$("#fileNameDel").attr("value", file_name_arry);
		
	}
	
</script>
</body>
</html>