package org.epctagcoder.option.GIAI.partitionTable;

import java.util.ArrayList;
import java.util.List;

import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.GIAI.GIAITagSize;


public class GIAIPartitionTableList {
	static final private List<TableItem> list = new ArrayList<TableItem>();


	public GIAIPartitionTableList(GIAITagSize tagSize) {
		if ( tagSize.getValue()==96 ) {
			list.clear();			
			list.add( new TableItem(0, 40, 12, 42, 13) );
			list.add( new TableItem(1, 37, 11, 45, 14) );
			list.add( new TableItem(2, 34, 10, 48, 15) );
			list.add( new TableItem(3, 30,  9, 52, 16) );
			list.add( new TableItem(4, 27,  8, 55, 17) );
			list.add( new TableItem(5, 24,  7, 58, 18) );
			list.add( new TableItem(6, 20,  6, 62, 19) );
		} else if ( tagSize.getValue()==202 ) {
			list.clear();
			list.add( new TableItem(0, 40, 12, 148, 18) );
			list.add( new TableItem(1, 37, 11, 151, 19) );
			list.add( new TableItem(2, 34, 10, 154, 20) );
			list.add( new TableItem(3, 30,  9, 158, 21) );
			list.add( new TableItem(4, 27,  8, 161, 22) );
			list.add( new TableItem(5, 24,  7, 164, 23) );
			list.add( new TableItem(6, 20,  6, 168, 24) );			
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
