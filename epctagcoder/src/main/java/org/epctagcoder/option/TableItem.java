package org.epctagcoder.option;

public class TableItem {
	private int partitionValue;
	private int l;
	private int m;
	private int n;
	private int digits;
	
	
	public TableItem(int partitionValue, int m, int l, int n, int digits) {
		this.partitionValue = partitionValue;
		this.m = m;		
		this.l = l;
		this.n = n;
		this.digits = digits;		
	}


	public int getPartitionValue() {
		return partitionValue;
	}


	public void setPartitionValue(int partitionValue) {
		this.partitionValue = partitionValue;
	}


	public int getL() {
		return l;
	}


	public void setL(int l) {
		this.l = l;
	}


	public int getM() {
		return m;
	}


	public void setM(int m) {
		this.m = m;
	}


	public int getN() {
		return n;
	}


	public void setN(int n) {
		this.n = n;
	}


	public int getDigits() {
		return digits;
	}


	public void setDigits(int digits) {
		this.digits = digits;
	}



	

	
	
}
