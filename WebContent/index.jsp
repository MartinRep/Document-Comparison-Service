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
	<form bgcolor="white" method="POST" enctype="multipart/form-data" action="UploadHandler">
		<fieldset>
			<legend><h3>Document Comparison Service</h3></legend>
			<b>Document Title :</b><br>
			<input id="title" name="txtTitle" type="text" pattern=[a-zA-z0-9_]{1,9} 
			placeholder="Please enter the Document title here" size="50" />
			<p/>
			<p id="error"><font color="RED">${message}</font></p>
			<input id="file" type="file" name="txtDocument"/>
			<center><input id="submitBt" type="submit" value="Compare Document" disabled="true"></center>
		</fieldset>							
	</form>
</body>
</html>