package com.isentropy.monet;

public class Contact {
	private String address;
	private String displayName;
	
	public String getDisplayName(){
		return displayName;
	}
	public String getAddress(){
		return address;
	}
	
	public Contact(String dname,String addr){
		displayName = dname;
		address = addr;
	}
}
