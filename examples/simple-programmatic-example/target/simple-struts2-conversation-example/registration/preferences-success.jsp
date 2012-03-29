<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/struts-conversation-tags" prefix="sc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Confirmation</title>
</head>
<body>
Please confirm that the below information is correct:
<table>
	<s:label value="Name"/><s:property value="contact.firstName"/>
</table>
<a href="<sc:url action='end'/>">Confirm</a>

</body>
</html>