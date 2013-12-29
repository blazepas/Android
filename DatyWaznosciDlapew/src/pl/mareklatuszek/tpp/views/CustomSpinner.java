package pl.mareklatuszek.tpp.views;



import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.atapters.AdapterCustomSpinner;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CustomSpinner extends LinearLayout implements OnClickListener {
	private Context context;
	private AdapterCustomSpinner adapter;
	private PopupWindow popupWindow;
	private OnItemSelectedListener listener;
	
	private LinearLayout spinner;
	private TextView spinnerTxt;
	private ColorDrawable popupBackground;
	private ListView contentList;

	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		
		this.context = context;
		this.adapter = new AdapterCustomSpinner();
		this.popupWindow = new PopupWindow(context);
		this.listener = new OnItemSelectedListener();
		this.popupBackground = new ColorDrawable(getResources().getColor(R.color.dropdown));
		
		this.spinner = (LinearLayout) inflater.inflate(R.layout.spinner_button, null);
		this.spinnerTxt = (TextView) spinner.findViewById(R.id.spinnerTxt);
		
		spinner.setOnClickListener(this);
		
		addView(spinner);
	}
	
	@Override
	public void onClick(View v) {
		if (adapter.getCount() > 0) {
			popupWindow.showAsDropDown(v, 9, -10);	
		}
	
	}

	public void setAdapter(AdapterCustomSpinner adapter) {
		this.adapter = adapter;
		initPopupDropDown();
	}
	
	public void setBackground(int drawableResource) {
		spinner.setBackgroundResource(drawableResource);
	}
	
	public void setBackground(Drawable drawable) {
		spinner.setBackground(drawable);
	}
	
	public void setBackgroundColor(int color) {
		spinner.setBackgroundColor(color);
	}
	
	public void setDropDownBackgroundColor(ColorDrawable color) {
		this.popupBackground = color;
		popupWindow.setBackgroundDrawable(popupBackground);
	}
	
	public void setOnItemSelectedListener(OnItemClickListener onItemClickListener) {
		contentList.setOnItemClickListener(onItemClickListener);
	}
	
	public void setText(String text) {
		spinnerTxt.setText(text);
	}
	
	public AdapterCustomSpinner getAdapter() {
		return adapter;
	}
	
	public String getText() {
		String text = spinnerTxt.getText().toString();
		return text;
	}
	
	private void initPopupDropDown() {
		contentList = new ListView(context);
		contentList.setAdapter(adapter);
		contentList.setOnItemClickListener(listener);
		contentList.setDivider(null);
		
		popupWindow.setFocusable(true); 
	    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	    popupWindow.setWidth(this.getLayoutParams().width - 18);
	    popupWindow.setBackgroundDrawable(popupBackground);
	    popupWindow.setContentView(contentList);
	}
	
	public void callOnItemClickListener(AdapterView<?> arg0, View view, int arg2, long arg3) {
		listener.onItemClick(arg0, view, arg2, arg3);
	}
	
	private class OnItemSelectedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
			String choice = (String) view.getTag();
			setText(choice);
			popupWindow.dismiss();				
		}
	}

}
