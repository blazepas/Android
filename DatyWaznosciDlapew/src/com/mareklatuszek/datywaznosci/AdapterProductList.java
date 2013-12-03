package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;

public class AdapterProductList extends BaseAdapter {
	
	private LayoutInflater inflater=null;
	private Activity mActivity;
	private ArrayList<Product> products;
	FragmentManager fragmentManager; 
	int fragmentId;
	
	private Boolean[] isExpanded;
	private int clickedPos = -1;
	private CommonUtilities utilities = new CommonUtilities();
	
	TextView nazwaTxtList, dataOtwTxtList, terminWazTxtList;	
	ProgressBar pozostaloPrgsList;
	ImageView expandImage, obrazekImage;
	LinearLayout detailsLay, basicLay, deleteLay, showProdLay;
	
	public AdapterProductList(Activity mActivity, ArrayList<Product> products, FragmentManager fragmentManager, int fragmentId) {
		this.mActivity = mActivity;
		this.products = products;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isExpanded = new Boolean[products.size()];
		Arrays.fill(isExpanded, false);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_products, null);
        
        final Product product = products.get(position);
        
        nazwaTxtList = (TextView) vi.findViewById(R.id.nazwaTxtList);
        dataOtwTxtList = (TextView) vi.findViewById(R.id.dataOtwTxtList);
        terminWazTxtList = (TextView) vi.findViewById(R.id.terminWazTxtList);
        pozostaloPrgsList = (ProgressBar) vi.findViewById(R.id.pozostaloPrgsList);
        expandImage = (ImageView) vi.findViewById(R.id.expandImage);
        obrazekImage = (ImageView) vi.findViewById(R.id.obrazekImage);
        detailsLay = (LinearLayout) vi.findViewById(R.id.detailsLay);
        basicLay = (LinearLayout) vi.findViewById(R.id.basicLay);
        deleteLay = (LinearLayout) vi.findViewById(R.id.deleteLay);
        showProdLay = (LinearLayout) vi.findViewById(R.id.showProdLay);
        
        
        String nazwa = product.getNazwa();
        String dataOtw = product.getDataOtwarcia();
        String terminWaz = product.getTerminWaznosci();
        String image = product.getImage();
        int progress = utilities.getProgress(dataOtw, terminWaz);
        
        nazwaTxtList.setText(nazwa);
        dataOtwTxtList.setText(dataOtw);
        terminWazTxtList.setText(terminWaz);
        pozostaloPrgsList.setProgress(progress);
        
//        if (!image.equals("")) {
//			String imagePath = product.getImage();
//			Bitmap imageBmp = BitmapLoader.loadBitmap(imagePath, 50, 50);
//			obrazekImage.setImageBitmap(imageBmp);
//		}
        
        initAnimations(position);
                
        basicLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean expanded = isExpanded[position];
				isExpanded[position] = !expanded;
				clickedPos = position;
				
				AdapterProductList.this.notifyDataSetChanged();
			}
		});
        
        deleteLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
        showProdLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProduct(position);				
			}
		});

		return vi;
	}
	
	private void initAnimations(int position) {
		boolean expanded = isExpanded[position];
		
		if (expanded) {
			if (clickedPos == position) {
	    		rotateView(expandImage, 0f, 90f, 250);   		
	    	} else {
	    		rotateView(expandImage, 0f, 90f, 0);
	    	}
			
			detailsLay.setVisibility(View.VISIBLE);	
			
		    } else {    	
		    	if (clickedPos == position) {
		    		rotateView(expandImage, 90f, 0f, 250);
		    	} 
		    	
		    	detailsLay.setVisibility(View.GONE);	
		    }
	}
	
	private void rotateView(View view, float fromDegree, float toDegree, int duration) {
	    final RotateAnimation rotateAnim = new RotateAnimation(fromDegree, toDegree,
	            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

	    rotateAnim.setDuration(duration);
	    rotateAnim.setFillAfter(true);
	    view.startAnimation(rotateAnim);
	}
	
	private void deleteProduct(Product product) {
		FragmentProdukty fragmentProdukty = (FragmentProdukty) fragmentManager.findFragmentById(fragmentId);
		fragmentProdukty.deleteProduct(product);
	}
	
	private void showProduct(int position) {
		FragmentProdukty fragmentProdukty = (FragmentProdukty) fragmentManager.findFragmentById(fragmentId);
		fragmentProdukty.switchToProductFragment(position);
	}
		
}
