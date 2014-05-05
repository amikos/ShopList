package kz.amikos.android.shoplist.objects;

import java.util.Date;

public class ShopItem {
	private String name;
	private Integer number;
	private boolean bought;
	private int count;
	private int unitType;
	private Date createdDate;

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getUnitType() {
		return unitType;
	}

	public void setUnitType(int unitType) {
		this.unitType = unitType;
	}

	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ShopItem(String name) {
		super();
		this.name = name;
		this.createdDate = new Date();
	}

	public ShopItem() {
		super();
		this.createdDate = new Date();
	}

}
