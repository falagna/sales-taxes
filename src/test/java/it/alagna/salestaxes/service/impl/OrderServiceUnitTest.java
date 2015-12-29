package it.alagna.salestaxes.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import it.alagna.salextaxes.exception.OrderEntryException;
import it.alagna.salextaxes.model.OrderEntryModel;
import it.alagna.salextaxes.model.OrderModel;
import it.alagna.salextaxes.service.impl.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class OrderServiceUnitTest {

	private static final BigDecimal TAX1 = BigDecimal.valueOf(5.00).setScale(2);
	private static final BigDecimal TAX2 = BigDecimal.valueOf(7.00).setScale(2);
	private static final BigDecimal TAX3 = BigDecimal.valueOf(9.00).setScale(2);
	private static final BigDecimal TOTAL_TAXES = BigDecimal.valueOf(21.00).setScale(2);

	private static final BigDecimal PRICE1 = BigDecimal.valueOf(15.00).setScale(2);
	private static final BigDecimal PRICE2 = BigDecimal.valueOf(27.00).setScale(2);
	private static final BigDecimal PRICE3 = BigDecimal.valueOf(39.00).setScale(2);
	private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(81.00).setScale(2);
	
	private static final String DESCRIPTION = "Some description";
	private static final BigDecimal PRICE_PER_UNIT = BigDecimal.valueOf(10.00).setScale(2);
	private static final int QUANTITY = 2;
	private static final BigDecimal NET_PRICE = PRICE_PER_UNIT.multiply(BigDecimal.valueOf(QUANTITY));
	
	private static final BigDecimal NO_TAXES = BigDecimal.ZERO.setScale(2);
	private static final BigDecimal ONLY_IMPORT_TAX = NET_PRICE.multiply(OrderEntryModel.IMPORT_TAX_RATE).setScale(2);
	private static final BigDecimal ONLY_BASIC_TAX = NET_PRICE.multiply(OrderEntryModel.BASIC_TAX_RATE).setScale(2);
	private static final BigDecimal BOTH_TAXES = ONLY_IMPORT_TAX.add(ONLY_BASIC_TAX).setScale(2);

	private static final BigDecimal GROSS_PRICE = NET_PRICE.add(BOTH_TAXES);
	
	private static final BigDecimal NEGATIVE_PRICE = BigDecimal.valueOf(-10.00).setScale(2);
	private static final int NEGATIVE_QUANTITY = -2;


	@Spy
	private OrderService orderService;

	private OrderEntryModel entry1, entry2, entry3;
	private OrderModel order;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		entry1 = new OrderEntryModel();
		entry2 = new OrderEntryModel();
		entry3 = new OrderEntryModel();
		order = new OrderModel();
	}

	@Test
	public void testEvaluateTotalTaxes_OrderModel() throws OrderEntryException
	{
		// GIVEN
		order.setEntries(Arrays.asList(entry1, entry2, entry3));

		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(TAX1).given(orderService).evaluateTotalTaxes(entry1);
		willReturn(TAX2).given(orderService).evaluateTotalTaxes(entry2);
		willReturn(TAX3).given(orderService).evaluateTotalTaxes(entry3);

		// WHEN
		BigDecimal result = orderService.evaluateTotalTaxes(order);

		// THEN
		assertEquals(TOTAL_TAXES, result);
	}
	
	@Test
	public void testEvaluateTotalTaxes_OrderModel_Exception() throws OrderEntryException
	{
		// GIVEN
		order.setEntries(Arrays.asList(entry1, entry2, entry3));
		willThrow(OrderEntryException.class).given(orderService).assertEvaluable(entry2);
		
		// WHEN
		try
		{
			orderService.evaluateTotalTaxes(order);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}

	@Test
	public void testEvaluateTotalPrice_OrderModel() throws OrderEntryException
	{
		// GIVEN
		order.setEntries(Arrays.asList(entry1, entry2, entry3));

		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(PRICE1).given(orderService).evaluateTotalPrice(entry1);
		willReturn(PRICE2).given(orderService).evaluateTotalPrice(entry2);
		willReturn(PRICE3).given(orderService).evaluateTotalPrice(entry3);

		// WHEN
		BigDecimal result = orderService.evaluateTotalPrice(order);

		// THEN
		assertEquals(TOTAL_PRICE, result);
	}
	
	@Test
	public void testEvaluateTotalPrice_OrderModel_Exception() throws OrderEntryException
	{
		// GIVEN
		order.setEntries(Arrays.asList(entry1, entry2, entry3));
		willThrow(OrderEntryException.class).given(orderService).assertEvaluable(entry2);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(order);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}

	@Test
	public void testEvaluateTotalTaxes_OrderEntryModel_NoTaxes() throws OrderEntryException
	{
		// GIVEN
		entry1.setTaxExempt(true);
		entry1.setImported(false);
	
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(NET_PRICE).given(orderService).evaluateNetPrice(entry1);
	
		// WHEN
		BigDecimal result = orderService.evaluateTotalTaxes(entry1);
	
		// THEN
		assertEquals(NO_TAXES, result);
	}
	
	@Test
	public void testEvaluateTotalTaxes_OrderEntryModel_OnlyImportTax() throws OrderEntryException
	{
		// GIVEN
		entry1.setTaxExempt(true);
		entry1.setImported(true);
	
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(NET_PRICE).given(orderService).evaluateNetPrice(entry1);
	
		// WHEN
		BigDecimal result = orderService.evaluateTotalTaxes(entry1);
	
		// THEN
		assertEquals(ONLY_IMPORT_TAX, result);
	}
	
	@Test
	public void testEvaluateTotalTaxes_OrderEntryModel_OnlyBasicTax() throws OrderEntryException
	{
		// GIVEN
		entry1.setTaxExempt(false);
		entry1.setImported(false);
	
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(NET_PRICE).given(orderService).evaluateNetPrice(entry1);
	
		// WHEN
		BigDecimal result = orderService.evaluateTotalTaxes(entry1);
	
		// THEN
		assertEquals(ONLY_BASIC_TAX, result);
	}
	
	@Test
	public void testEvaluateTotalTaxes_OrderEntryModel_BothTaxes() throws OrderEntryException
	{
		// GIVEN
		entry1.setTaxExempt(false);
		entry1.setImported(true);
	
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(NET_PRICE).given(orderService).evaluateNetPrice(entry1);
	
		// WHEN
		BigDecimal result = orderService.evaluateTotalTaxes(entry1);
	
		// THEN
		assertEquals(BOTH_TAXES, result);
	}
	
	@Test
	public void testEvaluateTotalTaxes_OrderEntryModel_Exception() throws OrderEntryException
	{
		// GIVEN
		willThrow(OrderEntryException.class).given(orderService).assertEvaluable(entry1);
		
		// WHEN
		try
		{
			orderService.evaluateTotalTaxes(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}

	@Test
	public void testEvaluateNetPrice() throws OrderEntryException
	{
		// GIVEN
		entry1.setPricePerUnit(PRICE_PER_UNIT);
		entry1.setQuantity(QUANTITY);
	
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
	
		// WHEN
		BigDecimal result = orderService.evaluateNetPrice(entry1);
	
		// THEN
		assertEquals(NET_PRICE, result);
	}
	
	@Test
	public void testEvaluateNetPrice_Exception() throws OrderEntryException
	{
		// GIVEN
		willThrow(OrderEntryException.class).given(orderService).assertEvaluable(entry1);
		
		// WHEN
		try
		{
			orderService.evaluateNetPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}

	@Test
	public void testEvaluateTotalPrice_OrderEntryModel() throws OrderEntryException
	{
		// GIVEN
		willDoNothing().given(orderService).assertEvaluable(any(OrderEntryModel.class));
		willReturn(NET_PRICE).given(orderService).evaluateNetPrice(entry1);
		willReturn(BOTH_TAXES).given(orderService).evaluateTotalTaxes(entry1);
	
		// WHEN
		BigDecimal result = orderService.evaluateTotalPrice(entry1);
	
		// THEN
		assertEquals(GROSS_PRICE, result);
	}
	
	@Test
	public void testEvaluateTotalPrice_Exception() throws OrderEntryException
	{
		// GIVEN
		willThrow(OrderEntryException.class).given(orderService).assertEvaluable(entry1);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}

	@Test
	public void testAssertEvaluable_OK() throws OrderEntryException
	{
		// GIVEN
		entry1.setDescription(DESCRIPTION);
		entry1.setPricePerUnit(PRICE_PER_UNIT);
		entry1.setQuantity(QUANTITY);
		
		// WHEN
		orderService.assertEvaluable(entry1);
	
		// THEN OK
	}
	
	@Test
	public void testAssertEvaluable_MissingPricePerUnit()
	{
		// GIVEN
		entry1.setDescription(DESCRIPTION);
		entry1.setPricePerUnit(null);
		entry1.setQuantity(QUANTITY);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}
	
	@Test
	public void testAssertEvaluable_NegativePricePerUnit()
	{
		// GIVEN
		entry1.setDescription(DESCRIPTION);
		entry1.setPricePerUnit(NEGATIVE_PRICE);
		entry1.setQuantity(QUANTITY);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}
	
	@Test
	public void testAssertEvaluable_NegativeQuantity()
	{
		// GIVEN
		entry1.setDescription(DESCRIPTION);
		entry1.setPricePerUnit(PRICE_PER_UNIT);
		entry1.setQuantity(NEGATIVE_QUANTITY);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}
	
	@Test
	public void testAssertEvaluable_MissingDescription()
	{
		// GIVEN
		entry1.setDescription(null);
		entry1.setPricePerUnit(PRICE_PER_UNIT);
		entry1.setQuantity(QUANTITY);
		
		// WHEN
		try
		{
			orderService.evaluateTotalPrice(entry1);
		}
		// THEN
		catch (OrderEntryException e)
		{
			return;
		}
	
		fail("Should have thrown an OrderEntryException");
	}
}
