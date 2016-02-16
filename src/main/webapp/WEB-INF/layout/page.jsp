<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:message code="pagesrc.appname" /></title>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/resources/css/style.css" />" />
	<script src="<s:url value="/resources/js/submitform.js" />" ></script>
</head>
<body>
	<div id="wrapper">
		<div id="header">
			<t:insertAttribute name="header" />
		</div>
		<div id="content">
			<t:insertAttribute name="body" />
		</div>
		<div id="footer">
			<t:insertAttribute name="footer" />
		</div>
	</div>
</body>
</html>