package org.byars.struts2.services;

import org.byars.struts2.models.Booking;
import org.springframework.beans.factory.annotation.Autowired;

public class MyService {

	@Autowired
	private Booking sibby;
	
	public void printPrice() {
		System.out.println("Sibby (service):  " + sibby);
		System.out.println("*****PRICE:  " + sibby.getItemPrice());
	}
}
