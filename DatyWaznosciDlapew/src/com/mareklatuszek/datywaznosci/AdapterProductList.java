package com.mareklatuszek.datywaznosci;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;

public class AdapterProductList extends BaseAdapter {
	
	private int detailsHeightInDps = 50;
	float scale;
	
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
		
		scale = mActivity.getResources().getDisplayMetrics().density;
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

		boolean convertViewStatus = (convertView == null);
		
		if (clickedPos == position | convertViewStatus){
			View vi;
			if (convertViewStatus) {
				vi = inflater.inflate(R.layout.listview_products, null);
			} else {
				vi = convertView;
			}
       
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
	          
	        final Product product = products.get(position);
	        String nazwa = product.getNazwa();
	        String dataOtw = product.getDataOtwarcia();
	        String terminWaz = product.getTerminWaznosci();
	        String image = product.getImage();
	        int progress = utilities.getProgress(dataOtw, terminWaz);
	        
	        nazwaTxtList.setText(nazwa);
	        dataOtwTxtList.setText(dataOtw);
	        terminWazTxtList.setText(terminWaz);
	        pozostaloPrgsList.setProgress(progress);
	        
	        if (!image.equals("")) {
	        	File imgFile = new  File(image + "thumb");
	        	if(imgFile.exists()){
	        	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	        	    obrazekImage.setImageBitmap(myBitmap);
	        	}	
			}
	        
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
					showChoiceDialog(product);
				}
			});
	        showProdLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showProduct(position);				
				}
			});

	        return vi;
		} else {
			return convertView;
		}
	}
	
	private void initAnimations(int position) {
		boolean expanded = isExpanded[position];
		
		if (expanded) {
	    	rotateView(expandImage, 0f, 90f, 250); 
	    	
	    	if (position == clickedPos){
	    		expandItem(detailsLay);
		    } else {
		    	detailsLay.setVisibility(View.VISIBLE);	
		    }

		} else {    	
		    rotateView(expandImage, 90f, 0f, 250);
		    if (position == clickedPos){
		    	collapseItem(detailsLay);
		    } else {
			    detailsLay.setVisibility(View.GONE);
		    }
		    
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
	
	private void showChoiceDialog(final Product product) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
		dialog.setTitle("Usuwanie");
		dialog.setMessage("Czy na pewno usunąć produkt?");
		dialog.setPositiveButton("Tak",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteProduct(product);
			}
		});

		dialog.setNegativeButton("Anuluj",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void expandItem(View v) {
		final int targtetHeight = (int) (detailsHeightInDps * scale + 0.5f); //przelicza dps na px
    	utilities.expandView(v, targtetHeight);
	}
	
	private void collapseItem(View v) {
		utilities.collapseView(v);
	}

}
