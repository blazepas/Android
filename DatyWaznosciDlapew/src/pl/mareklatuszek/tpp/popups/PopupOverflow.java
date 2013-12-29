package pl.mareklatuszek.tpp.popups;

import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.R.array;
import pl.mareklatuszek.tpp.R.id;
import pl.mareklatuszek.tpp.R.layout;
import pl.mareklatuszek.tpp.views.TextViewBariol;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.actionbarsherlock.view.MenuItem;

public class PopupOverflow extends PopupWindow {

	private Activity mActivity;
	private View clickedView;
	private OnClickListener listener;

	public PopupOverflow(Activity mActivity, MenuItem item, OnClickListener listener) {
		super(mActivity);
		this.mActivity = mActivity;
		this.clickedView = mActivity.findViewById(item.getItemId());
		this.listener = listener;
		initWindow();
	}
	
	private void initWindow() {
		int wrapContent = WindowManager.LayoutParams.WRAP_CONTENT;
		View contentView = getCustomContentView();
		
		setFocusable(true); 
        setBackgroundDrawable(new BitmapDrawable());
        setHeight(wrapContent);
        setWidth(wrapContent);
        setContentView(contentView);
		
	}

	private View getCustomContentView() {
		View root = null;

		switch (clickedView.getId()) {
		case R.id.overflow_product_list:
			root = createView(R.array.array_popup_products_list_title);
			break;	
		case R.id.overflow_product:
			root = createView(R.array.array_popup_product_title);
			break;
		}

		return root;
	}
	
	private View createView(int resArrayTitles) {
		Resources res = mActivity.getResources();
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		LinearLayout root = new LinearLayout(mActivity);
		root.setOrientation(LinearLayout.VERTICAL);
		
		String[] titles = res.getStringArray(resArrayTitles);

		for (int i = 0; i < titles.length; i++) {
			View item = inflater.inflate(R.layout.item_overflow_popup, null);
			item.setId(i);
			item.setOnClickListener(listener);
			
			TextViewBariol text = (TextViewBariol) item.findViewById(R.id.actionBarTitle);
			text.setText(titles[i]);
			
			root.addView(item);
		}
		
		return root;
	}

	public void showPopup() {
		showAsDropDown(clickedView);
	}
}
