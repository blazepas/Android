package com.mareklatuszek.datywaznosci;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterMenu extends BaseAdapter {

	Context context;
	String[] mTitle;
	String[] mSubTitle;
	int[] mIcon;
	LayoutInflater inflater;
	int clickedPos = -1;

	public AdapterMenu(Context context, String[] title, String[] subtitle, int[] icon, int menuPos) {
		this.context = context;
		this.mTitle = title;
		this.mSubTitle = subtitle;
		this.mIcon = icon;
		this.clickedPos = menuPos;
	}

	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			TextView txtTitle;
			TextView txtSubTitle;
			ImageView imgIcon;

			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.listview_menu, parent, false);

			txtTitle = (TextView) itemView.findViewById(R.id.title);
			txtSubTitle = (TextView) itemView.findViewById(R.id.subtitle);

			imgIcon = (ImageView) itemView.findViewById(R.id.icon);

			txtTitle.setText(mTitle[position]);
			txtSubTitle.setText(mSubTitle[position]);

			imgIcon.setImageResource(mIcon[position]);
			
			if (position == clickedPos) {
				itemView.setBackgroundColor(Color.parseColor("#10ffffff"));
			}
			
			return itemView;	
		} else if (position == clickedPos) {
			convertView.setBackgroundColor(Color.parseColor("#10ffffff"));
			return convertView;
		} else {
			convertView.setBackgroundColor(Color.parseColor("#00ffffff"));
			return convertView;
		}
	}
	
	public void setClickedPos(int clickedPos) {
		this.clickedPos = clickedPos;
	}

}
