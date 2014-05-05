package kz.amikos.android.shoplist.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.enums.PriorityType;
import kz.amikos.android.shoplist.widget.WidgetProvider;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

public class AppContext extends Application {

	public static final String ACTION_TYPE = "kz.amikos.android.shoplist.ActionType";
	
	public static final String DOC_INDEX = "kz.amikos.android.shoplist.DocIndex";

	public static final int ACTION_NEW_TASK = 0;
	public static final int ACTION_UPDATE = 1;

	public static final String FIELD_NAME = "name";
	public static final String FIELD_COUNT = "count";
	public static final String FIELD_UNIT_TYPE = "unitType";
	public static final String FIELD_CREATED_DATE = "createdDate";
	public static final String FIELD_IS_BOUGHT = "isBought";
	public static final String FIELD_PRIORITY_TYPE = "priorityType";
	
	public static final int GOOGLE_VOICE_REQUEST_CODE = 1234;
	
	public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "kz.amikos.android.shoplist.widget.ListViewRowNumer";
	
	private ArrayList<ShopItem> shopList = new ArrayList<ShopItem>();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		populateList();
	}
	
	/**
	 * Заполнение листа из shared_prefs
	 */
	private void populateList(){
		File prefsDir = new File(
				((AppContext) getApplicationContext()).getPrefsDir());

		if (prefsDir.exists() && prefsDir.isDirectory()) {
			String[] list = prefsDir.list();
			for (int i = 0; i < list.length; i++) {
				SharedPreferences sharedPref = getSharedPreferences(
						list[i].replace(".xml", ""), Context.MODE_PRIVATE);
				
				ShopItem shopItem = new ShopItem();
				shopItem.setName(sharedPref.getString(
						FIELD_NAME, null));
				shopItem.setCreatedDate(new Date(sharedPref.getLong(
						FIELD_CREATED_DATE, 0)));
				shopItem.setCount(sharedPref.getInt(
						FIELD_COUNT, 0));
				shopItem.setUnitType(sharedPref.getInt(
						FIELD_UNIT_TYPE, 0));
				shopItem.setBought(sharedPref.getBoolean(
						FIELD_IS_BOUGHT, false));
				shopItem.setPriorityType(PriorityType.values()[sharedPref.getInt(FIELD_PRIORITY_TYPE, 0)]);
				shopList.add(shopItem);
			}
		}
	}
	
	public void setShopList(ArrayList<ShopItem> shopList) {
		this.shopList = shopList;
	}

	public ArrayList<ShopItem> getShopList() {
		return shopList;
	}
	
	/**
	 * Сортировка листа
	 */
	public void sortShopList(){
		Collections.sort(shopList, ShopItemComparator.getCustomComparator());
	}
	
	/**
	 * Получение пути к папке с настройками
	 */
	public String getPrefsDir() {
		return getApplicationInfo().dataDir + "/" + "shared_prefs";
	}
	
	/**
	 * Обновление свех виджетов
	 * Вызывать аккуратно!
	 */
	public void updateAllWidgets(){
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
	    if (appWidgetIds.length > 0) {
	    	appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
	    }
	}

}
