package pl.jacek.jablonka.android.tpp.atapters;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.popups.PopUpInfo;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import pl.jacek.jablonka.android.tpp.verification.PremiumUtilities;
import android.content.Context;
import android.content.res.TypedArray;
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
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		boolean viewIsNull = convertView == null;
		View vi;
		
		if (clickedPos == position & !viewIsNull){	        
	        vi = (View) convertView.getTag();
	        int pressedColor = context.getResources().getColor(R.color.menu_item_pressed);
	        convertView.setBackgroundColor(pressedColor);

	        return convertView;
        } else if (viewIsNull) {
        	vi = inflater.inflate(R.layout.listview_menu, parent, false);
			convertView = initNormalRows(vi, position);
			convertView.setTag(vi);
			return convertView;	
        } else {
        	vi = inflater.inflate(R.layout.listview_menu, parent, false);
			convertView = initNormalRows(vi, position);
			convertView.setTag(vi);
			return convertView;	                    
        }
	}
	
	private View initNormalRows(View itemView, int position) {
		if (position == 0 | position == 1) {
			return initRowsWithInfo(position);
		}
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		imgIcon = (ImageView) itemView.findViewById(R.id.icon);
		txtTitle = (TextView) itemView.findViewById(R.id.actionBarTitle);
		
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
				String title = context.getString(R.string.tv_have_premium);
				txtTitle.setText(title);
			}
		} 
				
		return itemView;	
	}
	
	private View initRowsWithInfo(int position){
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.listview_menu_info, null, false);
		
		imgIcon = (ImageView) itemView.findViewById(R.id.icon);
		txtTitle = (TextView) itemView.findViewById(R.id.actionBarTitle);
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
