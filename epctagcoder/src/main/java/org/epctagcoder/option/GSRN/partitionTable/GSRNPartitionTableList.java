package org.epctagcoder.option.GSRN.partitionTable;

import java.util.ArrayList;
import java.util.List;

import org.epctagcoder.option.TableItem;


public class GSRNPartitionTableList {
	static final private List<TableItem> list = new ArrayList<TableItem>();

	static {
		list.add( new TableItem(0, 40, 12, 18, 5) );
		list.add( new TableItem(1, 37, 11, 21, 6) );
		list.add( new TableItem(2, 34, 10, 24, 7) );
		list.add( new TableItem(3, 30,  9, 28, 8) );
		list.add( new TableItem(4, 27,  8, 31, 9) );
		list.add( new TableItem(5, 24,  7, 34, 10) );
		list.add( new TableItem(6, 20,  6, 38, 11) );
	}
	
	public GSRNPartitionTableList() {

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
