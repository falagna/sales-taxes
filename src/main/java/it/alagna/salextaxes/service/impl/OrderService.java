package it.alagna.salextaxes.service.impl;

import it.alagna.salextaxes.exception.OrderEntryException;
import it.alagna.salextaxes.model.OrderEntryModel;
import it.alagna.salextaxes.model.OrderModel;
import it.alagna.salextaxes.service.IOrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * Implementation of {@link IOrderService} interface.
 * Provides utility methods for tax and order calculation.
 * 
 * @author falagna
 */
public class OrderService implements IOrderService
{
	public static final String SALESTAXES_DESCRIPTION_FORMAT = "Sales Taxes: %s";
	public static final String TOTAL_DESCRIPTION_FORMAT = "Total: %s";
	
	public static final BigDecimal ROUNDING_FACTOR = BigDecimal.valueOf(0.05);

	/**
	 * Evaluates the total amount of taxes in an order.
	 * 
	 * @param order - the order to evaluate taxes from
	 * @return the total amount of taxes
	 * @throws OrderEntryException if one of the entries cannot be evaluated
	 * 
	 */
	public BigDecimal evaluateTotalTaxes(OrderModel order) throws OrderEntryException
	{
		BigDecimal totalTaxes = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		for(OrderEntryModel entry : order.getEntries())
		{
			totalTaxes = totalTaxes.add(evaluateTotalTaxes(entry));
		}
		
		return totalTaxes;
	}
	
	/**
	 * Evaluates the total price of an order.
	 * 
	 * @param order - the order to evaluate taxes from
	 * @return the total amount of taxes
	 * @throws OrderEntryException if one of the entries cannot be evaluated
	 * 
	 */
	public BigDecimal evaluateTotalPrice(OrderModel order) throws OrderEntryException
	{
		BigDecimal totalPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		for(OrderEntryModel entry : order.getEntries())
		{
			totalPrice = totalPrice.add(evaluateTotalPrice(entry));
		}
		
		return totalPrice;
	}
	
	/**
	 * Evaluates the total amount of taxes in a single entry.
	 * 
	 * @param entry - the entry to evaluate taxes from
	 * @return the total amount of taxes
	 * @throws OrderEntryException if the entry cannot be evaluated
	 * 
	 */
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
	
	/**
	 * Evaluates the net price of a single entry, without taxes.
	 * 
	 * @param entry - the entry to evaluate
	 * @return the net price
	 * @throws OrderEntryException if the entry cannot be evaluated
	 * 
	 */
	public BigDecimal evaluateNetPrice(OrderEntryModel entry) throws OrderEntryException
	{
		assertEvaluable(entry);
		return entry.getPricePerUnit().multiply(BigDecimal.valueOf(entry.getQuantity()));
	}
	
	/**
	 * Evaluates the gross price of a single entry, including taxes.
	 * 
	 * @param entry - the entry to evaluate
	 * @return the total price including taxes
	 * @throws OrderEntryException if the entry cannot be evaluated
	 * 
	 */
	public BigDecimal evaluateTotalPrice(OrderEntryModel entry) throws OrderEntryException
	{
		assertEvaluable(entry);
		return evaluateNetPrice(entry).add(evaluateTotalTaxes(entry));
	}
	
	/**
	 * Asserts that an entry contains all required data for evaluation.
	 * 
	 * @param entry - the entry to evaluate
	 * @throws OrderEntryException if the entry cannot be evaluated
	 * 
	 */
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
	
	/**
	 * Rounds tax amount to the nearest amount specified by ROUNDING_FACTOR.
	 * </br></br>
	 * Example: 5% of 11.25 = 0.5625</br>
	 * ROUNDING_FACTOR = 0.05</br>
	 * tax = 0.5625
	 * </br></br>
	 * 0.55 <= 0.5625 <= 0.60
	 * </br></br>
	 * Divide all by 0.05:</br>
	 * 11 <= 11.25 <= 12
	 * </br></br>
	 * Round up to the nearest integer:</br>
	 * 11.25 -> 12
	 * </br></br>
	 * Then multiply again by 0.05</br>
	 * 12 * 0.05 = 0.60
	 * 
	 * @param tax - the tax amount to round
	 * @return the rounded tax amount
	 * 
	 */
	public BigDecimal roundTax(BigDecimal tax)
	{
		return tax.divide(ROUNDING_FACTOR).setScale(0, RoundingMode.CEILING).multiply(ROUNDING_FACTOR).setScale(2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Produces a text description of an order, without any tax calculation.
	 * 
	 * @param order - the order to describe
	 * @return a text description of the order
	 * @throws OrderEntryException if any entry cannot be evaluated
	 * 
	 */
	public String toInputString(OrderModel order) throws OrderEntryException
	{
		StringBuilder builder = new StringBuilder();
		
		for(OrderEntryModel entry : order.getEntries())
		{
			builder.append("\n").append(entry.toString());
		}
		
		return builder.length() > 0 ? builder.substring(1) : "";
	}
	
	/**
	 * Produces a text description of an order, including tax calculation for each entry,
	 * total tax amount and total order amount.
	 * 
	 * @param order - the order to describe
	 * @return a text description of the order
	 * @throws OrderEntryException if any entry cannot be evaluated
	 * 
	 */
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
