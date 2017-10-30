package com.pomelo.drools.model;

/**
 * @author zxz
 *
 */
public class Users {
	public String name;
	public int status;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(name);
		sb.append(",");
		sb.append(status);
		
		return sb.toString();
	}
}
