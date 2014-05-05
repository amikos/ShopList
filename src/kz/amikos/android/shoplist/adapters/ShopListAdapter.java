package kz.amikos.android.shoplist.adapters;

import java.util.ArrayList;

import kz.amikos.android.shoplist.R;
import kz.amikos.android.shoplist.objects.ShopItem;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopListAdapter extends ArrayAdapter<ShopItem> {
	private Context context;
	
	public ShopListAdapter(Context context, ArrayList<ShopItem> objects) {
		super(context, R.id.txt_item_name, objects);
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.custom_listview_row,
					parent, false);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.listRowLayout = (RelativeLayout) convertView.findViewById(R.id.list_row_layout);

			viewHolder.itemName = (TextView) convertView
					.findViewById(R.id.txt_item_name);
			viewHolder.itemImage = (ImageView) convertView
					.findViewById(R.id.image_item);
			viewHolder.itemUnitInfo = (TextView) convertView
					.findViewById(R.id.txt_unit_info);
			viewHolder.itemCreatedDate = (TextView) convertView
					.findViewById(R.id.txt_unit_created_date);

			convertView.setTag(viewHolder);

		}

		ViewHolder holder = (ViewHolder) convertView.getTag();

		ShopItem shopItem = getItem(position);
		
		switch (shopItem.getPriorityType()) {
		case LOW:

			holder.listRowLayout.setBackgroundResource(R.color.light_gray_trans);
			
			break;
			
		case MIDDLE:
			
			holder.listRowLayout.setBackgroundResource(R.color.light_yellow_trans);

			break;
			
		case HIGH:
			
			holder.listRowLayout.setBackgroundResource(R.color.light_red_trans);

			break;

		default:
			break;
		}

		holder.itemName.setText(shopItem.getName());
		
		if (shopItem.getCount()!=0){
			holder.itemUnitInfo.setText(String.valueOf(shopItem.getCount()) + " " + 
					this.context.getResources().getStringArray(R.array.unit_types)[shopItem.getUnitType()]);
		}
		
		holder.itemCreatedDate.setText(DateFormat.format("dd MMMM, yyyy, hh:mm:ss",
				shopItem.getCreatedDate()));
		
		if (shopItem.isBought()){
			holder.itemImage.setImageResource(R.drawable.basket_full);
			
			holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.itemUnitInfo.setPaintFlags(holder.itemUnitInfo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}else{
			holder.itemImage.setImageResource(R.drawable.basket_empty);
			
			holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			holder.itemUnitInfo.setPaintFlags(holder.itemUnitInfo.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
		}
		
		return convertView;
	}

	static class ViewHolder {
		public TextView itemName;
		public ImageView itemImage;
		public TextView itemUnitInfo;
		public TextView itemCreatedDate;
		public RelativeLayout listRowLayout;
	}
	
}
