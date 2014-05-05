package kz.amikos.android.shoplist.activities;

import java.io.File;
import java.util.ArrayList;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.adapters.ShopListAdapter;
import kz.amikos.android.shoplist.objects.AppContext;
import kz.amikos.android.shoplist.objects.ShopItem;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends Activity {
	private EditText txtNewItem;
	private ArrayList<ShopItem> shopList;
	private ListView listview;
	private ShopListAdapter shopListAdapter;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtNewItem = (EditText) findViewById(R.id.txt_new_item);
		listview = (ListView) findViewById(R.id.listview);
		listview.setEmptyView(findViewById(R.id.empty_view));
		
		listview.setOnItemClickListener(new ListViewClickListener());
		
		listview.setOnItemLongClickListener(new ListViewLongClickListener());
		
		txtNewItem.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	addItem(v);
		        	
		        	//Скрыть клавиатуру после ввода
		        	InputMethodManager inputManager = 
		        	        (InputMethodManager) 
		        	            getSystemService(Context.INPUT_METHOD_SERVICE); 
		        	inputManager.hideSoftInputFromWindow(
		        	        getCurrentFocus().getWindowToken(),
		        	        InputMethodManager.HIDE_NOT_ALWAYS); 
		          return true;
		        }
		        return false;
		    }
		});
		
		intent = new Intent(this, ShopItemActivity.class);
		
		loadItems();
		showItems();
		
		registerForContextMenu(listview);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		showItems();
	}

	private void loadItems() {
		shopList = ((AppContext) getApplicationContext())
				.getShopList();
	}

	private void showItems() {
		((AppContext) getApplicationContext()).sortShopList();
		
		shopListAdapter = new ShopListAdapter(this, shopList);
		listview.setAdapter(shopListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cleareAll: {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.confirm_delete_all);

			builder.setPositiveButton(R.string.delete,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						for (ShopItem shopItem : ((AppContext)getApplicationContext()).getShopList()) {
							String filePath = ((AppContext) getApplicationContext()).getPrefsDir() + "/"
									+ shopItem.getCreatedDate().getTime() + ".xml";
							File file = new File(filePath);
							
							file.delete();
						}
						
						((AppContext) getApplicationContext()).getShopList().clear();
						
						((AppContext) getApplicationContext()).updateAllWidgets();

						showItems();
					}
				});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
			
			return true;
		}
		case R.id.add_by_voice: {
			startVoiceRecognitionActivity();
			
			return true;
		}
		default:
			break;
		}
	
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return true;
	}

	public void addItem(View view) {
		createNewShopItem(txtNewItem.getText().toString());
	}
	
	/**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, AppContext.GOOGLE_VOICE_REQUEST_CODE);
    }
    
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == AppContext.GOOGLE_VOICE_REQUEST_CODE && resultCode == RESULT_OK)
        {
        	// Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	
        	builder.setTitle(R.string.select_dialog);
        	builder.setItems(matches.toArray(new String[matches.size()]), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    createNewShopItem(matches.get(which));
                }
            });
        	
        	AlertDialog dialog = builder.create();
			dialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	private void createNewShopItem(String name){
		if (!name.equals("")) {
			ShopItem shopItem = new ShopItem(name);
			
			saveShopItem(shopItem);
			
			shopList.add(shopItem);
			txtNewItem.setText("");
			showItems();
		}
	}
	
	private void saveShopItem(ShopItem shopItem){
		SharedPreferences sharedPref = getSharedPreferences(
				String.valueOf(shopItem.getCreatedDate().getTime()),
				Context.MODE_PRIVATE);
		
		Editor editor = sharedPref.edit();
		
		editor.putString(AppContext.FIELD_NAME,
				shopItem.getName());
		
		editor.putLong(AppContext.FIELD_CREATED_DATE, shopItem
				.getCreatedDate().getTime());
		
		editor.putBoolean(AppContext.FIELD_IS_BOUGHT, shopItem
				.isBought());
		
		editor.putInt(AppContext.FIELD_PRIORITY_TYPE, shopItem
				.getPriorityType().getIndex());
		
		editor.commit();
		
		((AppContext) getApplicationContext()).updateAllWidgets();
	}
	
	private class ListViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Bundle bundle = new Bundle();
			bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_UPDATE);
			bundle.putInt(AppContext.DOC_INDEX, position);

			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
	private class ListViewLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
			// TODO Auto-generated method stub
			ShopItem shopItem = shopList.get(position);
			shopItem.setBought(!shopItem.isBought());//reverse
			saveShopItem(shopItem);
			showItems();
			return true;
		}
	}
}
