package org.epctagcoder.option.SGLN.partitionTable;

import org.epctagcoder.option.TableItem;

import java.util.ArrayList;
import java.util.List;

public class SGLNPartitionTableList {
    static final private List<TableItem> list = new ArrayList<TableItem>();

    static {
        list.add( new TableItem(0, 40, 12,  1, 0) );
        list.add( new TableItem(1, 37, 11,  4, 1) );
        list.add( new TableItem(2, 34, 10, 7, 2) );
        list.add( new TableItem(3, 30,  9, 11, 3) );
        list.add( new TableItem(4, 27,  8, 14, 4) );
        list.add( new TableItem(5, 24,  7, 17, 5) );
        list.add( new TableItem(6, 20,  6, 21, 6) );
    }

    public SGLNPartitionTableList() {

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
