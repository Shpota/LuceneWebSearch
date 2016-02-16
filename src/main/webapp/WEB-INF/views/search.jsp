<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="page_content">
	<br>
	<form action="/search" id="search_from" method="POST">
		<input type="text" class="input_text" name="q" value="${q}">
		<input type="hidden" id="page_num_input" name="page" value="0">
		<input type="submit" class="input_button" value="<s:message code="pagesrc.search" />">
	</form>
	<c:if test="${hitsNumber ne null}">
		<div class="resultsStat">
			<s:message code="pagesrc.resultsquantiry_first" /> 
			${hitsNumber}
			<s:message code="pagesrc.resultsquantiry_second" />
		</div>
	</c:if>
	<br>
	<c:forEach items="${results}" var="res">
		<a href="${res.url}"><c:out value="${res.title}" /></a>
		<br>
		<label class="green"><c:out value="${res.url}" /></label>
		<br>
		<br>
	</c:forEach>
	<br>
	<c:if test="${numberOfPages > 1}">
		<s:message code="pagesrc.page" />:
		<c:forEach begin="1" end="${numberOfPages}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index - 1 == currentPage}">
					<b>${loop.index}</b>
				</c:when>
				<c:otherwise>
					<a class="highlited" href="#" onclick="submitSearchForm(${loop.index - 1})">${loop.index}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:if>
</div>