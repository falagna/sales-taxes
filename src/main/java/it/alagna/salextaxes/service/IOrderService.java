package it.alagna.salextaxes.service;

import it.alagna.salextaxes.exception.OrderEntryException;
import it.alagna.salextaxes.model.OrderEntryModel;
import it.alagna.salextaxes.model.OrderModel;

import java.math.BigDecimal;

public interface IOrderService {
		
	BigDecimal evaluateTotalTaxes(OrderModel order) throws OrderEntryException;
	BigDecimal evaluateTotalPrice(OrderModel order) throws OrderEntryException;
	BigDecimal evaluateTotalTaxes(OrderEntryModel entry) throws OrderEntryException;
	BigDecimal evaluateNetPrice(OrderEntryModel entry) throws OrderEntryException;
	BigDecimal evaluateTotalPrice(OrderEntryModel entry) throws OrderEntryException;
	BigDecimal roundTax(BigDecimal tax);
	String toInputString(OrderModel order) throws OrderEntryException;
	String toOutputString(OrderModel order) throws OrderEntryException;
}
