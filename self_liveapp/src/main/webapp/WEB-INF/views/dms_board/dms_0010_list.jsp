<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
	<c:if test="${member == null }">
		<script>
			alert("목록을 볼 권한이 업습니다. 회원이시라면 로그인 후 이용해 보십시오.");
			location.href="/dms_member/dms_0010_login_view";
		</script>
	</c:if>
	<!-- 메뉴 -->
	<%@include file="/WEB-INF/views/menu.jsp" %>
	<!--  -->
	
	<br><br>
	<div class="container-fluid" style="font-size:13px; width:100%">
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
				<div align="center">
				
				
				<c:if test="${param.bo_table == 'notice'}">					
				<font style="font-size:13px;"><b>학습공지 전체목록</b></font> <br>
					<div align="left">
					    <!-- 게시판 페이지 정보 및 버튼 시작 수정 필요-->
					            전체  건 ${scri.page} 페이지
					</div>
				</c:if>

				<c:if test="${param.bo_table == 'qna'}">					
				<font style="font-size:13px;"><b>질의응답 전체목록</b></font> <br>
					<div align="left">
					    <!-- 게시판 페이지 정보 및 버튼 시작 수정 필요-->
					            전체 a건 ${scri.page} 페이지
					</div>
				</c:if>
				
				<c:if test="${param.bo_table == 'pds'}">					
				<font style="font-size:13px;"><b>과제제출 전체목록</b></font> <br>
					<div align="left">
					    <!-- 게시판 페이지 정보 및 버튼 시작 수정 필요-->
					            전체 a건 ${scri.page} 페이지
					</div>
				</c:if>				
				
					<br><br>
					<table class="table table-bordered table-sm">
						 <tr class='table-info' >
							<td align=center >번호</td>
							<td align=center >제목</td>
							<td align=center >글쓴이</td>
							<td align=center >날짜</td>
						</tr>
				
						<c:if test="${param.bo_table == 'notice'}">	
							<c:forEach items="${list }" var="list">
								<tr>
									<td align=center ><c:out value="${list.cms_bno }"></c:out></td>
									<td align=center >
										<a href="/dms_board/dms_0010_read_view?bo_table=notice&cms_bno=${list.cms_bno }
											&page=${scri.page}&perPageNum=${scri.perPageNum}
											&searchType=${scri.searchType}&keyword=${scri.keyword}">
											<c:out value="${list.cms_title }"></c:out>	
										</a>
									</td>
									<td align=center ><c:out value="${list.cms_writer }"></c:out></td>
									<td align=center ><fmt:formatDate value="${list.cms_regdate }" pattern="yyyy-MM-dd"/></td>
								</tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${param.bo_table == 'qna'}">
							
						</c:if>
						
						<c:if test="${param.bo_table == 'pds'}">
							
						</c:if>						
						
						
					</table>
					<br/>
			
					<div style="display:flex; justify-content:center;">
						<select name="searchType">
							<option value="t"<c:out value="${scri.searchType eq 't' ? 'selected' : ''}"/>>제목</option>
							<option value="c"<c:out value="${scri.searchType eq 'c' ? 'selected' : ''}"/>>내용</option>
							<option value="w"<c:out value="${scri.searchType eq 'w' ? 'selected' : ''}"/>>작성자</option>
							<option value="tc"<c:out value="${scri.searchType eq 'tc' ? 'selected' : ''}"/>>제목+내용</option>
						</select>
					
						<input type="text" name="keyword" id="keywordInput" value="${scri.keyword}" style="width:200px;"/>
					
						<div align="center">
							<button type="button" id="search_btn" name="search_btn" class="btn btn-info btn-sm">검색</button>
							<button type="button" id="write_btn" name="write_btn" class="btn btn-info btn-sm">글쓰기</button>
						</div>
					</div>
			
					<div style="display:flex; justify-content:center;">
						<c:if test="${pageMaker.prev}">
							<a href="dms_0010_list${pageMaker.makeSearch(pageMaker.startPage - 1)}&bo_table=${param.bo_table}">이전</a>
						</c:if> 
						
						<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="idx">
							<a href="dms_0010_list${pageMaker.makeSearch(idx)}&bo_table=${param.bo_table}">${idx}&nbsp;</a>
						</c:forEach>
						
						<c:if test="${pageMaker.next && pageMaker.endPage > 0}">
							<a href="dms_0010_list${pageMaker.makeSearch(pageMaker.endPage + 1)}&bo_table=${param.bo_table}">다음</a>
						</c:if>
					</div>
				</div>
			</div>
		</div>	
	</div>
	
	
	

	
<script type="text/javascript">
	
	$(document).ready(function(){
		$("#search_btn").on("click", function(){
			self.location = "dms_0010_list" + '${pageMaker.makeQuery(1)}'
				+ "&bo_table=notice"
				+ "&searchType=" + $("select option:selected").val()
				+ "&keyword=" + encodeURIComponent($('#keywordInput').val());
		});
		
		$("#write_btn").on("click", function(){
			location.href = "/dms_board/dms_0010_write_view";
		});
	});
	
</script>

<%@include file="/WEB-INF/views/tail.jsp" %>
</body>
</html>