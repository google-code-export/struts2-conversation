package org.byars.struts2.models;

import java.util.List;

public interface CheckoutItem {

	public String getItemName();
	public Double getItemPrice();
	public List<String> getItemDescription();
	
}
