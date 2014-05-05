package kz.amikos.android.shoplist.activities;

import java.io.File;
import java.util.ArrayList;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.R.array;
import kz.amikos.android.shoplist.R.id;
import kz.amikos.android.shoplist.R.layout;
import kz.amikos.android.shoplist.R.menu;
import kz.amikos.android.shoplist.R.string;
import kz.amikos.android.shoplist.objects.AppContext;
import kz.amikos.android.shoplist.objects.ShopItem;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ShopItemActivity extends Activity {
	private int actionType;
	private int docIndex;
	
	private ShopItem shopItem;
	private ArrayList<ShopItem> shopList;
	
	private EditText edTxtItemName;
	private Spinner spinner;
	
	private EditText edTxtUnitCount;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_item);
		
		actionType = getIntent().getExtras().getInt(AppContext.ACTION_TYPE);
		
		edTxtItemName = (EditText) findViewById(R.id.itemName);
		
		shopList = ((AppContext) getApplicationContext())
				.getShopList();
		
		edTxtUnitCount = (EditText) findViewById(R.id.unit_count);
		
		showSpinner();
		
		prepareItem(actionType);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void showSpinner(){
		String[] unit_types = getResources().getStringArray(R.array.unit_types);
		 
		// адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unit_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinner = (Spinner) findViewById(R.id.spnr_unit);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            	shopItem.setUnitType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
          });
	}
        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_item, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			saveShopItem();

			return true;
		}

		case R.id.save: {

			saveShopItem();

			return true;
		}

		case R.id.delete: {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.confirm_delete);

			builder.setPositiveButton(R.string.delete,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleteShopItem();
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
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void saveShopItem(){
		if (actionType == AppContext.ACTION_UPDATE) {

			boolean edited = false;

			SharedPreferences sharedPref = getSharedPreferences(
					String.valueOf(shopItem.getCreatedDate().getTime()),
					Context.MODE_PRIVATE);

			Editor editor = sharedPref.edit();

			// если документ старый и текст изменился
			if (!edTxtItemName.getText().toString().trim()
					.equals(shopItem.getName())) {

				shopItem.setName(edTxtItemName.getText().toString());
				editor.putString(AppContext.FIELD_NAME,
						shopItem.getName());
				edited = true;
			}
			
			// если документ старый и текст изменился
			if (!edTxtUnitCount.getText().toString().trim()
					.equals(shopItem.getCount())) {

				shopItem.setCount(Integer.parseInt(edTxtUnitCount.getText().toString()));
				editor.putInt(AppContext.FIELD_COUNT,
						shopItem.getCount());
				edited = true;
			}
			
			// если документ старый и текст изменился
			if (spinner.getSelectedItemPosition()!=shopItem.getUnitType()) {

				shopItem.setUnitType(spinner.getSelectedItemPosition());
				editor.putInt(AppContext.FIELD_UNIT_TYPE,
						shopItem.getUnitType());
				edited = true;
			}
			if (edited) {
				
				editor.putLong(AppContext.FIELD_CREATED_DATE, shopItem
						.getCreatedDate().getTime());
				editor.putBoolean(AppContext.FIELD_IS_BOUGHT, shopItem
						.isBought());
				editor.commit();
				
				((AppContext) getApplicationContext()).updateAllWidgets();
			}
		}

		finish();
	}
	
	private void deleteShopItem(){
		String filePath = ((AppContext) getApplicationContext()).getPrefsDir() + "/"
				+ shopItem.getCreatedDate().getTime() + ".xml";
		File file = new File(filePath);
		
		file.delete();
		
		((AppContext) getApplicationContext()).getShopList().remove(shopItem);
		
		finish();
		
		((AppContext) getApplicationContext()).updateAllWidgets();
	}
	
	private void prepareItem(int actionType) {
		switch (actionType) {
		case AppContext.ACTION_NEW_TASK:
			shopItem = new ShopItem();
			break;

		case AppContext.ACTION_UPDATE:
			docIndex = getIntent().getExtras().getInt(AppContext.DOC_INDEX);
			shopItem = shopList.get(docIndex);
			
			edTxtItemName.setText(shopItem.getName());
			
			edTxtUnitCount.setText(String.valueOf(shopItem.getCount()));
			
			break;

		default:
			break;
		}
		
		// выделяем элемент 
        spinner.setSelection(shopItem.getUnitType());
	}

}
