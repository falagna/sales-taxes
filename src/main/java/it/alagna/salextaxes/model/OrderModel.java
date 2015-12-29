package it.alagna.salextaxes.model;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

	List<OrderEntryModel> entries;
	
	public List<OrderEntryModel> getEntries() {
		if(entries == null)
		{
			entries = new ArrayList<OrderEntryModel>();
		}
		
		return entries;
	}

	public void setEntries(List<OrderEntryModel> entries) {
		this.entries = entries;
	}
}
