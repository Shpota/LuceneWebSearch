<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page_content">
	<br>
	<form action="/index" method="POST">
		<input type="text" class="input_text" placeholder="http://www.example.com" name="q">
		<input type="submit" class="input_button" value="<s:message code="pagesrc.index" />">
	</form>
	<br>
	<c:choose>
		<c:when test="${not empty successURL}">
			<div class="success"><s:message code="notofication.successurl" />: <c:out value="${successURL}" /></div>
		</c:when>
		<c:when test="${not empty failedURL}">
			<div class="alert-danger"><s:message code="notofication.failedurl" />: <c:out value="${failedURL}" /></div>
		</c:when>
	</c:choose>
</div>
