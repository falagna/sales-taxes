package it.alagna.salestaxes.service.impl;

import it.alagna.salextaxes.exception.OrderEntryException;
import it.alagna.salextaxes.model.OrderEntryModel;
import it.alagna.salextaxes.model.OrderModel;
import it.alagna.salextaxes.service.impl.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class OrderServiceIntegrationTest {

	private OrderService orderService;
	private OrderEntryModel entry1, entry2, entry3, entry4;
	private OrderModel order;
	
	@Before
	public void setup() throws Exception
	{
		orderService = new OrderService();
		entry1 = new OrderEntryModel();
		entry2 = new OrderEntryModel();
		entry3 = new OrderEntryModel();
		entry4 = new OrderEntryModel();
		order = new OrderModel();
	}

	@Test
	public void test1() throws OrderEntryException {
		entry1.setDescription("book");
		entry1.setPricePerUnit(BigDecimal.valueOf(12.49).setScale(2));
		entry1.setQuantity(1);
		entry1.setTaxExempt(true);
		entry1.setImported(false);
		
		entry2.setDescription("music CD");
		entry2.setPricePerUnit(BigDecimal.valueOf(14.99).setScale(2));
		entry2.setQuantity(1);
		entry2.setTaxExempt(false);
		entry2.setImported(false);
		
		entry3.setDescription("chocolate bar");
		entry3.setPricePerUnit(BigDecimal.valueOf(0.85).setScale(2));
		entry3.setQuantity(1);
		entry3.setTaxExempt(true);
		entry3.setImported(false);
		
		order.setEntries(Arrays.asList(entry1, entry2, entry3));
		
		System.out.println("***** Input 1 *****");
		System.out.println(orderService.toInputString(order));
		
		System.out.println("***** Output 1 *****");
		System.out.println(orderService.toOutputString(order));
		
		System.out.println();
	}
	
	@Test
	public void test2() throws OrderEntryException {
		entry1.setDescription("box of chocolates");
		entry1.setPricePerUnit(BigDecimal.valueOf(10.00).setScale(2));
		entry1.setQuantity(1);
		entry1.setTaxExempt(true);
		entry1.setImported(true);
		
		entry2.setDescription("bottle of perfume");
		entry2.setPricePerUnit(BigDecimal.valueOf(47.50).setScale(2));
		entry2.setQuantity(1);
		entry2.setTaxExempt(false);
		entry2.setImported(true);
		
		order.setEntries(Arrays.asList(entry1, entry2));
		
		System.out.println("***** Input 2 *****");
		System.out.println(orderService.toInputString(order));
		
		System.out.println("***** Output 2 *****");
		System.out.println(orderService.toOutputString(order));
		
		System.out.println();
	}
	
	@Test
	public void test3() throws OrderEntryException {
		entry1.setDescription("bottle of perfume");
		entry1.setPricePerUnit(BigDecimal.valueOf(27.99).setScale(2));
		entry1.setQuantity(1);
		entry1.setTaxExempt(false);
		entry1.setImported(true);
		
		entry2.setDescription("bottle of perfume");
		entry2.setPricePerUnit(BigDecimal.valueOf(18.99).setScale(2));
		entry2.setQuantity(1);
		entry2.setTaxExempt(false);
		entry2.setImported(false);
		
		entry3.setDescription("packet of headache pills");
		entry3.setPricePerUnit(BigDecimal.valueOf(9.75).setScale(2));
		entry3.setQuantity(1);
		entry3.setTaxExempt(true);
		entry3.setImported(false);
		
		entry4.setDescription("box of chocolates");
		entry4.setPricePerUnit(BigDecimal.valueOf(11.25).setScale(2));
		entry4.setQuantity(1);
		entry4.setTaxExempt(true);
		entry4.setImported(true);
		
		order.setEntries(Arrays.asList(entry1, entry2, entry3, entry4));
		
		System.out.println("***** Input 3 *****");
		System.out.println(orderService.toInputString(order));
		
		System.out.println("***** Output 3 *****");
		System.out.println(orderService.toOutputString(order));
		
		System.out.println();
	}

}
