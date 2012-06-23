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
<title>Confirmation</title>  
</head>
<body>
Please confirm that the below information is correct:
<s:set name="tester" value="contact.firstName"/>
<table>
	<thead><tr><th>Contact Information<th></thead>
	<tr>
		<td>Name:  </td><td><s:property value="tester"/>  <s:property value="contact.lastName"/></td>
	</tr>
	<tr>
		<td>Address Line 1:  </td><td><s:property value="contact.addressLine1"/></td>
	</tr>
	<tr>
		<td>Address Line 2:  </td><td><s:property value="contact.addressLine2"/></td>
	</tr>
	<tr>
		<td>City:  </td><td><s:property value="contact.city"/></td>
	</tr>
	<tr>
		<td>State:  </td><td><s:property value="contact.state"/></td>
	</tr>
	<tr>
		<td>Zip:  </td><td><s:property value="contact.postalCode"/></td>
	</tr>
</table>
<table>
	<thead><tr><th>Preferences<th></thead>
	<tr>
		<td>Band 1:  </td><td><s:property value="preferences.band1"/></td>
	</tr>
	<tr>
		<td>Band 2:  </td><td><s:property value="preferences.band2"/></td>
	</tr>
	<tr>
		<td>Band 3:  </td><td><s:property value="preferences.band3"/></td>
	</tr>
	<tr>
		<td>Genre:  </td><td><s:property value="preferences.genre"/></td>
	</tr>
	<tr>
		<td>Instrument:  </td><td><s:property value="preferences.instrument"/></td>
	</tr>
</table>
<a href="<sc:url action='end'/>">Confirm</a>

</body>
</html>