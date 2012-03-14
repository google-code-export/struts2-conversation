<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%> 
<%@ taglib uri="/struts-conversation-tags" prefix="sc"%>    
	  	
	  	<sc:form action="continue-to-amenity-selection">
	  		<s:textfield label="Item" key="booking.itemName"/>
			<s:textfield label="Price" key="booking.itemPrice"/>
		   	<s:submit value="Continue to Amenity Selection"/>
	  	</sc:form>

	  	<a id="goBack" href='<sc:url action="begin-booking"/>'>go back</a>