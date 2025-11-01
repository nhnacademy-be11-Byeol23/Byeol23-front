package com.nhnacademy.byeol23front.orderset.order.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderSearchCondition {
	private String status;
	private String orderNumber;
	private String receiver;

}