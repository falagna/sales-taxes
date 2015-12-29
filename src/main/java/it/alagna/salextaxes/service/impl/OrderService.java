package it.alagna.salextaxes.service.impl;

import it.alagna.salextaxes.exception.OrderEntryException;
import it.alagna.salextaxes.model.OrderEntryModel;
import it.alagna.salextaxes.model.OrderModel;
import it.alagna.salextaxes.service.IOrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderService implements IOrderService
{
	public static final String SALESTAXES_DESCRIPTION_FORMAT = "Sales Taxes: %s";
	public static final String TOTAL_DESCRIPTION_FORMAT = "Total: %s";
	
	public static final BigDecimal ROUNDING_FACTOR = BigDecimal.valueOf(0.05);

	public BigDecimal evaluateTotalTaxes(OrderModel order) throws OrderEntryException
	{
		BigDecimal totalTaxes = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		for(OrderEntryModel entry : order.getEntries())
		{
			totalTaxes = totalTaxes.add(evaluateTotalTaxes(entry));
		}
		
		return totalTaxes;
	}
	
	public BigDecimal evaluateTotalPrice(OrderModel order) throws OrderEntryException
	{
		BigDecimal totalPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		for(OrderEntryModel entry : order.getEntries())
		{
			totalPrice = totalPrice.add(evaluateTotalPrice(entry));
		}
		
		return totalPrice;
	}
	
	public BigDecimal evaluateTotalTaxes(OrderEntryModel entry) throws OrderEntryException
	{
		assertEvaluable(entry);
		
		BigDecimal totalTaxes = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		if(!entry.isTaxExempt())
		{
			BigDecimal amount = evaluateNetPrice(entry).multiply(OrderEntryModel.BASIC_TAX_RATE);
			totalTaxes = totalTaxes.add(roundTax(amount));
		}
		if(entry.isImported())
		{
			BigDecimal amount = evaluateNetPrice(entry).multiply(OrderEntryModel.IMPORT_TAX_RATE);
			totalTaxes = totalTaxes.add(roundTax(amount));
		}
		
		return totalTaxes;
	}
	
	private BigDecimal roundTax(BigDecimal tax)
	{
		/**	Rounds tax to the nearest 0.05
		
		 Example: 5% of 11.25 = 0.5625
		 0.55 <= 0.5625 <= 0.60
		 
		 Divide all by 0.05:
		 11 <= 11.25 <= 12
		 
		 Round to the nearest integer:
		 11.25 -> 11
		 
		 Then multiply again by 0.05
		 11 * 0.05 = 0.55
		
		 **/	
		return tax.divide(ROUNDING_FACTOR).setScale(0, RoundingMode.HALF_UP).multiply(ROUNDING_FACTOR).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal evaluateNetPrice(OrderEntryModel entry) throws OrderEntryException
	{
		assertEvaluable(entry);
		return entry.getPricePerUnit().multiply(BigDecimal.valueOf(entry.getQuantity()));
	}
	
	public BigDecimal evaluateTotalPrice(OrderEntryModel entry) throws OrderEntryException
	{
		assertEvaluable(entry);
		return evaluateNetPrice(entry).add(evaluateTotalTaxes(entry));
	}
	
	public void assertEvaluable(OrderEntryModel entry) throws OrderEntryException
	{
		if(entry.getPricePerUnit() == null || entry.getPricePerUnit().compareTo(BigDecimal.ZERO) < 0)
		{
			throw new OrderEntryException("pricePerUnit not set or invalid");
		}
		
		if(entry.getQuantity() < 0)
		{
			throw new OrderEntryException("quantity should be positive");
		}
		
		if(entry.getDescription() == null)
		{
			throw new OrderEntryException("description not set");
		}
	}
	
	public String toInputString(OrderModel order) throws OrderEntryException
	{
		StringBuilder builder = new StringBuilder();
		
		for(OrderEntryModel entry : order.getEntries())
		{
			builder.append("\n").append(entry.toString());
		}
		
		return builder.length() > 0 ? builder.substring(1) : "";
	}
	
	public String toOutputString(OrderModel order) throws OrderEntryException
	{
		StringBuilder builder = new StringBuilder();
		
		for(OrderEntryModel entry : order.getEntries())
		{
			builder.append(entry.getTotalPriceDescription(evaluateTotalPrice(entry))).append("\n");
		}
		
		builder.append(String.format(SALESTAXES_DESCRIPTION_FORMAT, evaluateTotalTaxes(order))).append("\n");
		builder.append(String.format(TOTAL_DESCRIPTION_FORMAT, evaluateTotalPrice(order)));

		return builder.toString();
	}
}
