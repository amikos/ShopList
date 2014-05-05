package kz.amikos.android.shoplist.widget;

import java.util.ArrayList;
import java.util.Collections;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.objects.AppContext;
import kz.amikos.android.shoplist.objects.ShopItem;
import kz.amikos.android.shoplist.objects.ShopItemComparator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

@SuppressLint("NewApi")
public class WidgetFactory implements RemoteViewsFactory {
	private ArrayList<ShopItem> listItemList = new ArrayList<ShopItem>();
	private Context context = null;
	
	public WidgetFactory(Context context, Intent intent) {
		this.context = context;

		listItemList = ((AppContext) context.getApplicationContext()).getShopList();
		
		Collections.sort(listItemList, ShopItemComparator.getBoughtComparator());
	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * Similar to getView of Adapter where instead of Viewwe return RemoteViews
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.custom_listview_row);
		
		//Определение итема листа
		ShopItem shopItem = listItemList.get(position);
		
		//Установка названия
		remoteViews.setTextViewText(R.id.txt_item_name, shopItem.getName());
		
		//Если указано кол-во
		if (shopItem.getCount()!=0){
			remoteViews.setTextViewText(R.id.txt_unit_info, String.valueOf(shopItem.getCount()) + " " + 
				this.context.getResources().getStringArray(R.array.unit_types)[shopItem.getUnitType()]);
		}
		
		//Дата создания
		remoteViews.setTextViewText(R.id.txt_unit_created_date, DateFormat.format("dd MMMM, yyyy, hh:mm:ss",
				shopItem.getCreatedDate()));
		
		//Стиль для купленного итема
		if (shopItem.isBought()){
			remoteViews.setImageViewResource(R.id.image_item, R.drawable.basket_full);
			
			//Установка подчеркнутого стиля
			remoteViews.setInt(R.id.txt_item_name, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			remoteViews.setInt(R.id.txt_unit_info, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		}else{
			remoteViews.setImageViewResource(R.id.image_item, R.drawable.basket_empty);
			
			//Снятие подчеркнутого стиля
			remoteViews.setInt(R.id.txt_item_name, "setPaintFlags", (~ Paint.STRIKE_THRU_TEXT_FLAG));
			remoteViews.setInt(R.id.txt_unit_info, "setPaintFlags", (~ Paint.STRIKE_THRU_TEXT_FLAG));
		}
		
		//Передача параметра OnClick позиции итема листа
		Bundle extras = new Bundle();
        extras.putInt(WidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.list_row_layout, fillInIntent);
		
		return remoteViews;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataSetChanged() {
		// Вызывается в случае обновления виджета
		listItemList = ((AppContext) context.getApplicationContext()).getShopList();
		
		System.out.println("onDataSetChanged");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}
}
