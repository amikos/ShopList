package kz.amikos.android.shoplist.widget;

import java.util.Arrays;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.activities.MainActivity;
import kz.amikos.android.shoplist.objects.AppContext;
import kz.amikos.android.shoplist.objects.ShopItem;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class WidgetProvider extends AppWidgetProvider {

	public static final String TOAST_ACTION = "kz.amikos.android.shoplist.TOAST_ACTION";
    public static final String EXTRA_ITEM = "kz.amikos.android.shoplist.EXTRA_ITEM";
    public static final String RUN_ACTIVITY_ACTION = "kz.amikos.android.shoplist.RUN_ACTIVITY_ACTION";
	
	private static String LOG_TAG = "widget log";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(LOG_TAG, "onEnabled");
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive");
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            
            
            setBought(context, viewIndex);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
            int[] appWidgetIds = mgr.getAppWidgetIds(thisAppWidget);
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
            
        }else if (intent.getAction().equals(RUN_ACTIVITY_ACTION)) {
        	Intent intentMainAvtivity = new Intent(context , MainActivity.class);
        	intentMainAvtivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(intentMainAvtivity);
        }
        super.onReceive(context, intent);
    }
	
	protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
	
	private void setBought(Context context, int viewIndex){
		ShopItem shopItem = ((AppContext) context.getApplicationContext()).getShopList().get(viewIndex);
		
		shopItem.setBought(!shopItem.isBought());//reverse
		
		saveShopItem(shopItem, context);
		
	}
	
	private void saveShopItem(ShopItem shopItem, Context context){
		SharedPreferences sharedPref = context.getSharedPreferences(
				String.valueOf(shopItem.getCreatedDate().getTime()),
				Context.MODE_PRIVATE);
		
		Editor editor = sharedPref.edit();
		
		editor.putString(AppContext.FIELD_NAME,
				shopItem.getName());
		
		editor.putLong(AppContext.FIELD_CREATED_DATE, shopItem
				.getCreatedDate().getTime());
		
		editor.putBoolean(AppContext.FIELD_IS_BOUGHT, shopItem
				.isBought());
		
		editor.commit();
	}


	@Override
	public void onUpdate(Context context, AppWidgetManager
	                appWidgetManager,int[] appWidgetIds) {
	 
		// update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
        	updateWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

		Log.d(LOG_TAG, "onUpdate");
	}
	
	private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
		// Here we setup the intent which points to the StackViewService which will
        // provide the views for this collection.
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        rv.setRemoteAdapter(R.id.widget_list_view, intent);

        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
        rv.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        // Here we setup the a pending intent template. Individuals items of a collection
        // cannot setup their own pending intents, instead, the collection as a whole can
        // setup a pending intent template, and the individual items can set a fillInIntent
        // to create unique before on an item to item basis.
        Intent toastIntent = new Intent(context, WidgetProvider.class);
        toastIntent.setAction(WidgetProvider.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.widget_list_view, toastPendingIntent);

        rv.setOnClickPendingIntent(R.id.layout_widget_app_icon, getPendingSelfIntent(context, RUN_ACTIVITY_ACTION));
        
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        
        System.out.println("updateWidget");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d(LOG_TAG, "onDisabled");
	}

}
