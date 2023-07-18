package com.crihexe.japi.response;

import java.util.Objects;

public class Sneaker {
	
	public Long id;
	
	public Integer status;
	
	public String sku;
	
	public String slug;
	
	@Override
	public int hashCode() {
		return id.intValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sneaker other = (Sneaker) obj;
		return id.equals(other.id);
	}
	
	public int setStatusBit(boolean value, int bitIndex) {
		if(extractStatusBit(bitIndex) != value) status ^= (1 << bitIndex);
		return status;
	}
	
	public boolean extractStatusBit(int bitIndex) {
		return (status & (1 << bitIndex)) >> bitIndex == 1;
	}
	
}
