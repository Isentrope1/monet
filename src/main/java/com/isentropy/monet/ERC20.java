package com.isentropy.monet;

public class ERC20 {

	private String symbol, address,desc;
	private int decimals;
	public ERC20(String id_, String address_,int decimals_) {
		symbol = id_;
		address = address_;
		decimals = decimals_;
	}
	public ERC20(String id_, String address_,int decimals_,String desc_) {
		symbol = id_;
		address = address_;
		decimals = decimals_;
		desc = desc_;
	}
	public String getDescription(){
		return desc;
	}
	public String getSymbol(){
		return symbol;
	}
	public String getAddress(){
		return address;
	}
	public int getDecimals(){
		return decimals;
	}

}
