package kz.amikos.android.shoplist.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.RemoteViewsService;

@SuppressLint("NewApi")
public class WidgetService extends RemoteViewsService {
	/*
	 * So pretty simple just defining the Adapter of the listview here Adapter
	 * is ListProvider
	 */

	@SuppressLint("NewApi")
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return (new WidgetFactory(this.getApplicationContext(), intent));
	}

}