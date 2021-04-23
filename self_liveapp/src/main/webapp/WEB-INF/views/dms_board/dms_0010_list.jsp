<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/include/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
</head>
<body>

	<hr/>
	<nav><%@include file="/WEB-INF/views/menu.jsp" %></nav>
	<hr/>
	
	<table>
		<tr>
			<td>번호</td>
			<td>제목</td>
			<td>작성자</td>
			<td>등록일</td>
			<td>조회수</td>
		</tr>
		
		<c:forEach items="${list }" var="list">
			<tr>
				<td><c:out value="${list.cms_bno }"></c:out></td>
				<td>
					<a href="/dms_board/dms_0010_read_view?cms_bno=${list.cms_bno }
						&page=${scri.page}&perPageNum=${scri.perPageNum}
						&searchType=${scri.searchType}&keyword=${scri.keyword}">
						<c:out value="${list.cms_title }"></c:out>	
					</a>
				</td>
				<td><c:out value="${list.cms_writer }"></c:out></td>
				<td><fmt:formatDate value="${list.cms_regdate }" pattern="yyyy-MM-dd"/></td>
				<td><c:out value="${list.cms_hit }"></c:out></td>
			</tr>
		</c:forEach>
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
	
		<button type="button" id="search_btn" name="search_btn">검색</button>
		<button type="button" id="write_btn" name="write_btn">글쓰기</button>
	</div>
	
	<div style="display:flex; justify-content:center;">
		<c:if test="${pageMaker.prev}">
			<li><a href="dms_0010_list${pageMaker.makeSearch(pageMaker.startPage - 1)}">이전</a></li>
		</c:if> 
		
		<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="idx">
			<li><a href="dms_0010_list${pageMaker.makeSearch(idx)}">${idx}</a></li>
		</c:forEach>
		
		<c:if test="${pageMaker.next && pageMaker.endPage > 0}">
			<li><a href="dms_0010_list${pageMaker.makeSearch(pageMaker.endPage + 1)}">다음</a></li>
		</c:if>
	</div>
	
<script type="text/javascript">
	
	$(document).ready(function(){
		$("#search_btn").on("click", function(){
			self.location = "dms_0010_list" + '${pageMaker.makeQuery(1)}'
				+ "&searchType=" + $("select option:selected").val()
				+ "&keyword=" + encodeURIComponent($('#keywordInput').val());
		});
		
		$("#write_btn").on("click", function(){
			location.href = "/dms_board/dms_0010_write_view";
		});
	});
	
</script>
</body>
</html>