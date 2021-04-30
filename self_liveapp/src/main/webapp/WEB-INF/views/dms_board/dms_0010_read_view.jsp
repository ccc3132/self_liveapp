<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 조회</title>
</head>
<body>

	<%@include file="/WEB-INF/views/menu.jsp" %>
	<br><br>

	
	
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-4">
				<div class="card border-primary mb-3" style="max-width: 100%;">
			    	<div class="card-header">
						수강과목
					</div>
				
					<div class="card-body text-primary" style="font-size:13px;">
						<table class="table table-bordered"> 
							<tbody>
								<tr>
									<td><a style="font-size:13px;" class="btn btn-outline-info btn-block " href="https://self.liveapp.co.kr/comm/board.php?bo_table=notice&amp;sca=PRO001_01">
										<b>잠언강해</b> <font color="gray">(01분반)</font></a>
									</td>
								</tr>
								<tr></tr>
							</tbody>
						</table>
					</div>
					
				</div>
			</div>
			
			<div class="col-md-8">

				<form name="read_form" enctype="multipart/form-data">
					<input type="hidden" id="cms_bno" name="cms_bno" value="${read.cms_bno}" />
					<input type="hidden" id="page" name="page" value="${scri.page }"/>
					<input type="hidden" id="perPageNum" name="perPageNum" value="${scri.perPageNum }"/>
					<input type="hidden" id="searchType" name="searchType" value="${scri.searchType }"/>
					<input type="hidden" id="keyword" name="keyword" value="${scri.keyword }"/>
					<input type="hidden" id="cms_file_no" name="cms_file_no" value=""/>
				
				<div class="row">
					<div class ="col-md-12">
						<input id="cms_title" name="cms_title" value="${read.cms_title }" readonly="readonly"/>
						<br>
					</div>

					<div class="col-md-12">
						<textarea id="cms_content" name="cms_content" readonly="readonly" style="resize:none;"><c:out value="${read.cms_content }"></c:out></textarea>
						<br>
					</div>			
				</div>
				
					<!-- 
					<table>
						<tr>
							<td>제목</td>
							<td><input id="cms_title" name="cms_title" value="${read.cms_title }" readonly="readonly"/></td>
						</tr>
						
						<tr>
							<td>내용</td>
							<td>
								<textarea id="cms_content" name="cms_content" readonly="readonly" style="resize:none;"><c:out value="${read.cms_content }"></c:out></textarea>
							</td>
						</tr>
						
						<tr>
							<td>작성자</td>
							<td><input id="cms_writer" name="cms_writer" value="${read.cms_writer }" readonly="readonly"/></td>
						</tr>
						
						<tr>
							<td>작성날짜</td>
							<td><fmt:formatDate value="${read.cms_regdate }" pattern="yyyy-MM-dd"/></td>
						</tr>
						
						<tr>
							<td>파일목록</td>
							<td>
								<c:forEach items="${file }" var="file">
									<a href="#" onclick="fn_file_down('${file.cms_file_no}'); return false;">${file.cms_org_file_name }</a>(${file.cms_file_size }kb)<br/>
								</c:forEach>
							</td>
						</tr>
					</table>
					-->
				</form>
				
				<div>
					<button class="btn btn-info btn-sm" type="button" id="reply_btn" name="reply_btn">답글</button>
					<button class="btn btn-info btn-sm" type="button" id="update_btn" name="update_btn">수정</button>
					<button class="btn btn-info btn-sm" type="button" id="delete_btn" name="delete_btn">삭제</button>
					<button class="btn btn-info btn-sm" type="button" id="cancel_btn" name="cancel_btn">취소</button>
				</div>		
			</div>
		</div>
	</div>

	
<script type="text/javascript">
	
	$(document).ready(function(){
		
		var form_obj = $("form[name='read_form']");
		
		$("#reply_btn").on("click", function(){	
			form_obj.attr("action", "/dms_reply/dms_0010_reply_view");
			form_obj.attr("method", "get");
			form_obj.submit();
		});
		
		$("#update_btn").on("click", function(){
			form_obj.attr("action", "/dms_board/dms_0010_update_view");
			form_obj.attr("method", "get");
			form_obj.submit();
		});
		
		$("#delete_btn").on("click", function(){
			
			var deleteYN = confirm("삭제하시겠습니까?");
			
			if(deleteYN == true) {
				form_obj.attr("action", "/dms_board/dms_0010_delete");
				form_obj.attr("method", "post");
				form_obj.submit();
			}
			
		});
		
		$("#cancel_btn").on("click", function(){
			location.href = "/dms_board/dms_0010_list?page=${scri.page}"
							+ "&perPageNum=${scri.perPageNum}"
							+ "&searchType=${scri.searchType}"
							+ "&keyword=${scri.keyword}";
		});
		
	});
	
	function fn_file_down(cms_file_no) {
		var form_obj = $("form[name='read_form']");
		
		$("#cms_file_no").attr("value", cms_file_no);
		form_obj.attr("action", "/file_down");
		form_obj.submit();
	}
	
</script>

	<%@include file="/WEB-INF/views/tail.jsp" %>
</body>
</html>