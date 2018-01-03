<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Document Comparison Service</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/lib/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/index.js"></script>
</head>
<body>
	<form method="POST" enctype="multipart/form-data" action="upload">
		<fieldset>
			<legend><big>Document Comparison Service</big></legend>
			<b>Document Title :</b><br>
			<input id="title" name="txtTitle" type="text" placeholder="Please Choose File first" size="50" />
			<p/>
			<p id="error"><font color="RED">${message}</font></p>
			<input id="file" type="file" name="txtDocument"/>
			<center><button id="submitBt" onClick="return Validate()" disabled="disabled">Compare Document</button></center>
		</fieldset>							
	</form>
</body>
</html>