package org.byars.struts2.models;

import java.util.List;

public class Booking implements CheckoutItem {
	
	private String itemName;
	private Double itemPrice;
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	@Override
	public String getItemName() {
		return itemName;
	}
	
	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}
	
	@Override
	public Double getItemPrice() {
		return itemPrice;
	}
	@Override
	public List<String> getItemDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
