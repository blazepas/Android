package com.mareklatuszek.datywaznosci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdapterProductList extends BaseAdapter {
	
	private LayoutInflater inflater=null;
	private Activity mActivity;
	private ArrayList<Product> products;
	
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
        int progress = getProgress(dataOtw, terminWaz);
        
        nazwaTxtList.setText(nazwa);
        dataOtwTxtList.setText(dataOtw);
        okresWazTxtList.setText(okresWaz);
        terminWazTxtList.setText(terminWaz);
        pozostaloPrgsList.setProgress(progress);
        
		return vi;
	}
	
	private int getProgress(String dataOtw, String terminWaz) {
		
		int progress = 100;
		
		try {
			
			double start = parseDate(dataOtw);
			double end = parseDate(terminWaz);
			double current = (new Date()).getTime();
			
			double x = end - start;
			double y = end - current;
			double z = y / x;
			double pr = (((end - current) / (end - start))) * 100;
			
			progress = (int) pr;
			
		} catch (ParseException e) {
			return 100;
		}
		
		
		return progress;		
	}
	
	private long parseDate(String dateToParse) throws ParseException {
		String toParse = dateToParse;
		SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");
		Date date = formatter.parse(toParse);
		long millis = date.getTime();
		
		return millis;
	}

}
