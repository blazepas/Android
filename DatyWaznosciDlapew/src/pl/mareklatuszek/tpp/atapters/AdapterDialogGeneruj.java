package pl.mareklatuszek.tpp.atapters;

import java.util.ArrayList;
import java.util.HashMap;

import pl.mareklatuszek.tpp.R;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterDialogGeneruj extends BaseAdapter{
	
	private FragmentActivity mActivity;
	private ArrayList<HashMap<String,String>> pozostale;
	private LayoutInflater inflater = null;
	
	private TextView firstTxt, secondTxt;
	
	public AdapterDialogGeneruj(FragmentActivity mActivity, ArrayList<HashMap<String,String>> pozostale) {
		this.mActivity = mActivity;
		this.pozostale = pozostale;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return pozostale.size();
	}

	@Override
	public Object getItem(int position) {
		return pozostale.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_dialog_generuj, null);
        
        firstTxt = (TextView) vi.findViewById(R.id.firstTxt);
        secondTxt = (TextView) vi.findViewById(R.id.secondTxt);  
        
        HashMap<String, String> map = pozostale.get(position);
        String first = map.get("first");
        String second = map.get("second");
        
        firstTxt.setText(first);
        secondTxt.setText(second);
        
		return vi;
	}

}
