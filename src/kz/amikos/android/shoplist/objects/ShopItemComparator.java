package kz.amikos.android.shoplist.objects;

import java.util.Comparator;

public class ShopItemComparator {
	
	private static DateComparator dateComparator;
	private static NameComparator nameComparator;
	private static BoughtComparator boughtComparator;
	private static CustomComparator customComparator;
	
	public static Comparator<ShopItem> getDateComparator(){
		if (dateComparator == null){
			dateComparator = new DateComparator();
		}
		
		return dateComparator;
	}

	
	public static Comparator<ShopItem> getNameComparator(){
		if (nameComparator == null){
			nameComparator = new NameComparator();
		}
		
		return nameComparator;
	}
	
	public static Comparator<ShopItem> getBoughtComparator(){
		if (boughtComparator == null){
			boughtComparator = new BoughtComparator();
		}
		
		return boughtComparator;
	}
	
	public static Comparator<ShopItem> getCustomComparator(){
		if (customComparator == null){
			customComparator = new CustomComparator();
		}
		
		return customComparator;
	}
	
	private static class NameComparator implements Comparator<ShopItem> {

		@Override
		public int compare(ShopItem shopItem1, ShopItem shopItem2) {
			return shopItem1.getName().compareTo(shopItem2.getName());
		}

	}

	private static class DateComparator implements Comparator<ShopItem> {

		@Override
		public int compare(ShopItem shopItem1, ShopItem shopItem2) {
			return shopItem1.getCreatedDate().compareTo(shopItem2.getCreatedDate());
		}

	}
	
	private static class BoughtComparator implements Comparator<ShopItem> {

		@Override
		public int compare(ShopItem shopItem1, ShopItem shopItem2) {
			if (shopItem1.isBought()==shopItem2.isBought()){
				return shopItem1.getCreatedDate().compareTo(shopItem2.getCreatedDate());
			}else{
				return Boolean.valueOf(shopItem1.isBought()).compareTo(Boolean.valueOf(shopItem2.isBought()));
			}
		}

	}
	
	private static class CustomComparator implements Comparator<ShopItem> {

		@Override
		public int compare(ShopItem shopItem1, ShopItem shopItem2) {
			if (shopItem1.isBought()==shopItem2.isBought()){
				return shopItem2.getPriorityType().compareTo(shopItem1.getPriorityType());
			}else{
				return Boolean.valueOf(shopItem1.isBought()).compareTo(Boolean.valueOf(shopItem2.isBought()));
			}
		}

	}

}
