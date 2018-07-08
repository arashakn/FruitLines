package com.adapter;


public class ItemShare {
    
    private int itemIdIcon;
    private String text;
    public ItemShare(String text,int idIcon) {
        this.itemIdIcon = idIcon;
        this.text = text;
    }

	public int getItemIdIcon() {
		return itemIdIcon;
	}

	public void setItemIdIcon(int itemIdIcon) {
		this.itemIdIcon = itemIdIcon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
    
    

}