package org.epctagcoder.option.CPI.partitionTable;

import java.util.ArrayList;
import java.util.List;

import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.CPI.CPITagSize;


public class CPIPartitionTableList {
	static final private List<TableItem> list = new ArrayList<TableItem>();
	


	public CPIPartitionTableList(CPITagSize tagSize) {
		if ( tagSize.getValue()==96 ) {
			list.clear();			
			list.add( new TableItem(0, 40, 12, 11, 3) );
			list.add( new TableItem(1, 37, 11, 14, 4) );
			list.add( new TableItem(2, 34, 10, 17, 5) );
			list.add( new TableItem(3, 30,  9, 21, 6) );
			list.add( new TableItem(4, 27,  8, 24, 7) );
			list.add( new TableItem(5, 24,  7, 27, 8) );
			list.add( new TableItem(6, 20,  6, 31, 9) );
		} else { //if ( tagSize.getValue()==202 ) {  // variable
			list.clear();
			list.add( new TableItem(0, 40, 12, 114, 18) );
			list.add( new TableItem(1, 37, 11, 120, 19) );
			list.add( new TableItem(2, 34, 10, 126, 20) );
			list.add( new TableItem(3, 30,  9, 132, 21) );
			list.add( new TableItem(4, 27,  8, 138, 22) );
			list.add( new TableItem(5, 24,  7, 144, 23) );
			list.add( new TableItem(6, 20,  6, 150, 24) );			
		}
	}
	

	public TableItem getPartitionByL(Integer index) {
		TableItem tableItem = null;
		for (TableItem item : list) {
			if (item.getL()==index) {
				tableItem = item;
				break;
			}
		}
		return tableItem;
	}
	
	public TableItem getPartitionByValue(Integer index) {
		TableItem tableItem = null;
		for (TableItem item : list) {
			if (item.getPartitionValue()==index) {
				tableItem = item;
				break;
			}
		}
		return tableItem;
	}	
	

}
