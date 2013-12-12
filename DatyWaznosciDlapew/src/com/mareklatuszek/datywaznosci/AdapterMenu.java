package com.mareklatuszek.datywaznosci;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract.Colors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterMenu extends BaseAdapter {

	Context context;
	String[] titles;
	TypedArray icons;
	TypedArray colors;
	int clickedPos = -1;
	
	LayoutInflater inflater;
	TextView txtTitle;
	ImageView imgIcon;

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
	
	public void setClickedPos(int clickedPos) {
		this.clickedPos = clickedPos;
	}
	
	private int getItemBackgroundColor(int position) {
		int color = colors.getColor(position, R.color.menu_background);
		return color;
	}

}
