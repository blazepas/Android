package com.mareklatuszek.datywaznosci;

import com.mareklatuszek.utilities.FinalVariables;
import com.mareklatuszek.utilities.PremiumUtilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract.Colors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterMenu extends BaseAdapter implements FinalVariables{

	Context context;
	String[] titles;
	TypedArray icons;
	TypedArray colors;
	int clickedPos = -1;
	
	LayoutInflater inflater;
	TextView txtTitle;
	ImageView imgIcon;
	LinearLayout info;

	public AdapterMenu(Context context, String[] titles, TypedArray icons, TypedArray colors, int menuPos) {
		this.context = context;
		this.titles = titles;
		this.icons = icons;
		this.colors = colors;
		this.clickedPos = menuPos;
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return titles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (position == 0 | position == 1) {
				return initRowsWithInfo(position);
			}
			
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.listview_menu, parent, false);
			
			imgIcon = (ImageView) itemView.findViewById(R.id.icon);
			txtTitle = (TextView) itemView.findViewById(R.id.title);
			
			imgIcon.setImageResource(icons.getResourceId(position, R.drawable.collections_cloud));
			txtTitle.setText(titles[position]);
			
			
			if (position == clickedPos) {
				int pressedColor = context.getResources().getColor(R.color.menu_item_pressed);
				itemView.setBackgroundColor(pressedColor);
			} else {
				int backgroundColor = getItemBackgroundColor(position);
				itemView.setBackgroundColor(backgroundColor);
			}
			
			

			
			if (position == getCount() - 1) {
				// premium item
				
				ImageView arrow = (ImageView) itemView.findViewById(R.id.arrow);
				
				int iconSize = context.getResources().getDimensionPixelSize(R.dimen.menu_icon_premium);
				int starSize = context.getResources().getDimensionPixelSize(R.dimen.menu_star);
				
				imgIcon.getLayoutParams().height = iconSize;
				imgIcon.getLayoutParams().width = iconSize;
				arrow.getLayoutParams().height = starSize;
				arrow.getLayoutParams().width = starSize;
				arrow.setImageResource(R.drawable.image_star);
				
				if(PremiumUtilities.APP_VERSION_PREMIUM) {
					txtTitle.setText(R.string.menu_premium_active);
				}
			} 
					
			return itemView;	
		} else if (position == clickedPos) {
			int pressedColor = context.getResources().getColor(R.color.menu_item_pressed);
			convertView.setBackgroundColor(pressedColor);
			return convertView;
		} else {
			int backgroundColor = getItemBackgroundColor(position);
			convertView.setBackgroundColor(backgroundColor);
			return convertView;
		}
	}
	
	private View initRowsWithInfo(int position){
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.listview_menu_info, null, false);
		
		imgIcon = (ImageView) itemView.findViewById(R.id.icon);
		txtTitle = (TextView) itemView.findViewById(R.id.title);
		info = (LinearLayout) itemView.findViewById(R.id.info);
		
		
		
		imgIcon.setImageResource(icons.getResourceId(position, R.drawable.collections_cloud));
		txtTitle.setText(titles[position]);	
		
		if (position == clickedPos) {
			int pressedColor = context.getResources().getColor(R.color.menu_item_pressed);
			itemView.setBackgroundColor(pressedColor);
		} else {
			int backgroundColor = getItemBackgroundColor(position);
			itemView.setBackgroundColor(backgroundColor);
		}
		
		switch (position) { // tak samo jest w dialog dodaj produkt
		case 0:
			info.setId(INFO_MENU_0_ITEM_ID);
			break;
		case 1:
			info.setId(INFO_MENU_1_ITEM_ID);
			break;
		}
		
		info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PopUpInfo popup = new PopUpInfo(context, v);
				popup.showPopUp();
			}
		});
		
		
		return itemView;
	}
	
	public void setClickedPos(int clickedPos) {
		this.clickedPos = clickedPos;
	}
	
	private int getItemBackgroundColor(int position) {
		int color = colors.getColor(position, R.color.menu_background);
		return color;
	}

}
