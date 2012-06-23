<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/struts-conversation-tags" prefix="sc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="/simple-example/static/css/registration.css" />
<link rel="shortcut icon" href="/simple-example/static/img/strutsConvo.ICO" type="image/x-icon" /> 
<title>Musical Preferences</title>
</head>
<body>
Please indicate your musical preferences:

<sc:form action="preferences">
	<s:textfield label="A band you like" key="preferences.band1" />
	<s:textfield label="another band you like" key="preferences.band2" />
	<s:textfield label="and another band you like" key="preferences.band3" />
	<s:textfield label="favorite type of music" key="preferences.genre" />
	<s:textfield label="favorite instrument" key="preferences.instrument" />
	<s:submit/>
</sc:form>
</body>
</html>