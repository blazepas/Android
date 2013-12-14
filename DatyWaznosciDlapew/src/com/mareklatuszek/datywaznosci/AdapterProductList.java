package com.mareklatuszek.datywaznosci;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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

public class AdapterProductList extends BaseAdapter implements OnLongClickListener {
		
	private int detailsHeightInDps = 70;
	float scale;
	
	private LayoutInflater inflater=null;
	private Activity mActivity;
	private ArrayList<Product> products;
	FragmentManager fragmentManager; 
	int fragmentId;
	
	private Boolean[] isExpanded;
	private View[] views;
	private int clickedPos = -1;
	private CommonUtilities utilities = new CommonUtilities();
	
	TextView nazwaTxtList, dataOtwTxtList, terminWazTxtList;	
	ProgressBar pozostaloPrgsList;
	ImageView obrazekImage;
	LinearLayout detailsLay, basicLay, deleteLay, showProdLay, expandLay;
	
	public AdapterProductList(Activity mActivity, ArrayList<Product> products, FragmentManager fragmentManager, int fragmentId) {
		this.mActivity = mActivity;
		this.products = products;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		
		scale = mActivity.getResources().getDisplayMetrics().density; //oblicza skalę px do dps
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isExpanded = new Boolean[products.size()];
		views = new View[products.size()];//TODO
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
		View vi = views[position];
        boolean convertViewStatus = (vi == null); 

        if (clickedPos == position){
	        
	        vi = views[position];
	        detailsLay = (LinearLayout) vi.findViewById(R.id.detailsLay);
	        initAnimations(position);
        	        
	        return vi;
        } else if (convertViewStatus) {
        	vi = inflater.inflate(R.layout.listview_products, null);
 	        vi = initRow(vi, position);
 	        views[position] = vi;
 	        return vi;
        } else {
            return views[position];                    
        }
	}
	
	@Override
	public boolean onLongClick(View v) {
		v.showContextMenu();
		return true;
	}
	
	
	private void initAnimations(int position) {
		boolean expanded = isExpanded[position];
		
		if (expanded) {
//	    	rotateView(expandImage, 0f, 90f, 250); 
	    	
	    	if (position == clickedPos){
	    		expandItem(detailsLay);
		    } else {
		    	detailsLay.setVisibility(View.VISIBLE);	
		    }

		} else {    	
//		    rotateView(expandImage, 90f, 0f, 250);
		    if (position == clickedPos){
		    	collapseItem(detailsLay);
		    } else {
			    detailsLay.setVisibility(View.GONE);
		    }
		    
		}
	}
	
	private View initRow(View vi, final int pos) {
		int rowBackground;
		int expandLayBg;
		
		if((pos % 2) == 0) { // produkt parzysty lub nie
			rowBackground = R.color.products_even;
			expandLayBg = R.color.products_expand_even_bg;
		} else {
			rowBackground = R.color.products_odd;
			expandLayBg = R.color.products_expand_odd_bg;
		}
		
        nazwaTxtList = (TextView) vi.findViewById(R.id.nazwaTxtList);
        dataOtwTxtList = (TextView) vi.findViewById(R.id.dataOtwTxtList);
        terminWazTxtList = (TextView) vi.findViewById(R.id.terminWazTxtList);
        pozostaloPrgsList = (ProgressBar) vi.findViewById(R.id.pozostaloPrgsList);
        expandLay = (LinearLayout) vi.findViewById(R.id.expandLay);
        obrazekImage = (ImageView) vi.findViewById(R.id.obrazekImage);
        detailsLay = (LinearLayout) vi.findViewById(R.id.detailsLay);
        basicLay = (LinearLayout) vi.findViewById(R.id.basicLay);
        deleteLay = (LinearLayout) vi.findViewById(R.id.deleteLay);
        showProdLay = (LinearLayout) vi.findViewById(R.id.showProdLay);
          
        final Product product = products.get(pos);
        String nazwa = product.getNazwa();
        String dataOtw = product.getDataOtwarcia();
        String terminWaz = product.getTerminWaznosci();
        String image = product.getImage();
        int progress = utilities.getProgress(dataOtw, terminWaz);
        Drawable progressDrawable = mActivity.getResources().getDrawable(R.drawable.progress_bar_bg);
        
        vi.setTag(pos);
        vi.setBackgroundResource(rowBackground);
		expandLay.setBackgroundResource(expandLayBg);
        nazwaTxtList.setText(nazwa);
        dataOtwTxtList.setText(dataOtw);
        terminWazTxtList.setText(terminWaz);
        pozostaloPrgsList.setProgress(progress);
        pozostaloPrgsList.setProgressDrawable(progressDrawable);
        
        if (!image.equals("")) {
        	File imgFile = new  File(image + "thumb");
        	if(imgFile.exists()){
        	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        	    obrazekImage.setImageBitmap(myBitmap);
        	}	
		}
        
        initAnimations(pos); // animacje rozsuwania i chowania dodatkowych
         
        //przypisywanie menu kontekstowego
        vi.setOnLongClickListener(this);
        basicLay.setOnLongClickListener(this);
        deleteLay.setOnLongClickListener(this);
        showProdLay.setOnLongClickListener(this);
        
        expandLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean expanded = isExpanded[pos];
				isExpanded[pos] = !expanded;
				clickedPos = pos;
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
				showProduct(pos);				
			}
		});
        
        return vi;
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
		Log.i("show product", position+"");
		FragmentProdukty fragmentProdukty = (FragmentProdukty) fragmentManager.findFragmentById(fragmentId);
		fragmentProdukty.switchToProductFragment(position);
	}
	
	private void showChoiceDialog(final Product product) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
		dialog.setTitle("Usuwanie");
		dialog.setMessage("Czy na pewno usunąć " + product.getNazwa() + "?");
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
		final int targtetHeight = (int) (detailsHeightInDps * scale + 0.5f);
    	utilities.expandView(v, targtetHeight);
	}
	
	private void collapseItem(View v) {
		utilities.collapseView(v);
	}
}
