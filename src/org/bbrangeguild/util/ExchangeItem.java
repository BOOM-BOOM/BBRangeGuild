package org.bbrangeguild.util;

/**
 * @author BOOM BOOM
 */
public class ExchangeItem {

    private int childId, value, itemId;

    public ExchangeItem (final int childId, final int value, final int itemId) {
        this.childId = childId;
        this.value = value;
        this.itemId = itemId;
    }

    public int getChildId() {
        return childId;
    }

    public int getValue() {
        return value;
    }

    public int getItemId() {
        return itemId;
    }

}
