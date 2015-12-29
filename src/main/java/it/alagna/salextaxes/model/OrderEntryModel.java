package it.alagna.salextaxes.model;

import java.math.BigDecimal;

public class OrderEntryModel {
	
	public static final BigDecimal BASIC_TAX_RATE = BigDecimal.valueOf(0.10); // 10%
	public static final BigDecimal IMPORT_TAX_RATE = BigDecimal.valueOf(0.05); // 5%
	public static final String FULL_DESCRIPTION_FORMAT = "%d%s %s";
	public static final String PRICEPERUNIT_DESCRIPTION_FORMAT = " at %s each";
	public static final String TOTALPRICE_DESCRIPTION_FORMAT = ": %s";
	public static final String IMPORTED_STRING = " imported";
	
	private int quantity = 1;
	private String description;
	private boolean imported = false;
	private boolean taxExempt = false;
	private BigDecimal pricePerUnit;
	
	@Override
	public String toString()
	{
		return getPricePerUnitDescription();
	}
	
	public String getFullDescription()
	{
		String importedString = imported ? IMPORTED_STRING : "";
		return String.format(FULL_DESCRIPTION_FORMAT, quantity, importedString, description, pricePerUnit);
	}
	
	public String getPricePerUnitDescription()
	{
		return getFullDescription() + String.format(PRICEPERUNIT_DESCRIPTION_FORMAT, pricePerUnit);
	}
	
	public String getTotalPriceDescription(BigDecimal totalPrice)
	{
		return getFullDescription() + String.format(TOTALPRICE_DESCRIPTION_FORMAT, totalPrice);
	}
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isImported() {
		return imported;
	}
	public void setImported(boolean imported) {
		this.imported = imported;
	}
	public boolean isTaxExempt() {
		return taxExempt;
	}
	public void setTaxExempt(boolean taxExempt) {
		this.taxExempt = taxExempt;
	}
	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}
	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
}
