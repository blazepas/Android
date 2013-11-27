package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

public class AdapterProductList extends BaseAdapter {
	
	private LayoutInflater inflater=null;
	private Activity mActivity;
	private ArrayList<Product> products;
	private CommonUtilities utilities = new CommonUtilities();
	
	TextView nazwaTxtList, dataOtwTxtList, okresWazTxtList, terminWazTxtList;	
	ProgressBar pozostaloPrgsList;
	
	public AdapterProductList(Activity mActivity, ArrayList<Product> products) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.products = products;
	}

	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_products, null);
        
        Product product = products.get(position);
        
        nazwaTxtList = (TextView) vi.findViewById(R.id.nazwaTxtList);
        dataOtwTxtList = (TextView) vi.findViewById(R.id.dataOtwTxtList);
        okresWazTxtList = (TextView) vi.findViewById(R.id.okresWazTxtList);
        terminWazTxtList = (TextView) vi.findViewById(R.id.terminWazTxtList);
        pozostaloPrgsList = (ProgressBar) vi.findViewById(R.id.pozostaloPrgsList);
        
        String nazwa = product.getNazwa();
        String dataOtw = product.getDataOtwarcia();
        String okresWaz = product.getOkresWaznosci();
        String terminWaz = product.getTerminWaznosci();
        int progress = utilities.getProgress(dataOtw, terminWaz);
        
        nazwaTxtList.setText(nazwa);
        dataOtwTxtList.setText(dataOtw);
        okresWazTxtList.setText(okresWaz);
        terminWazTxtList.setText(terminWaz);
        pozostaloPrgsList.setProgress(progress);
        
		return vi;
	}	

}
