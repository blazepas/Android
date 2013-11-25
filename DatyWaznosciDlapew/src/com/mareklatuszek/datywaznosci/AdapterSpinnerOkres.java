package com.mareklatuszek.datywaznosci;
import com.mareklatuszek.datywaznosci.R;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class AdapterSpinnerOkres extends BaseAdapter implements OnClickListener {
	
	private LayoutInflater inflater = null;
	private FragmentActivity mActivity;
	private int fragPos;
	private int fragId;
	
	String[] okresArr;
	
	public AdapterSpinnerOkres(FragmentActivity mActivity, int fragPos, int fragId) {
		this.mActivity = mActivity;
		this.fragPos = fragPos;
		this.fragId = fragId;
		inflater = (LayoutInflater)mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
		okresArr = mActivity.getResources().getStringArray(R.array.array_date);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return okresArr.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return okresArr[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		vi = inflater.inflate(R.layout.spinner_okres, null);
		
		TextView okresTxt = (TextView) vi.findViewById(R.id.okres);
		okresTxt.setText(okresArr[position]);
		okresTxt.setOnClickListener(this);
		
		return vi;
	}

	@Override
	public void onClick(View v) {
		FragmentManager fm = mActivity.getSupportFragmentManager();
		String selected = ((TextView) v).getText().toString();
		switch (fragPos) {
		case 1:
			FragmentDodaj fragmentDodaj = (FragmentDodaj) fm.findFragmentById(fragId);
			fragmentDodaj.setTerminWazFromAdapter(selected);
			break;
		case 6:
			FragmentEdytuj fragmentEdytuj = (FragmentEdytuj) fm.findFragmentById(fragId);
			fragmentEdytuj.setTerminWazFromAdapter(selected);
			break;
		}
	}

}
