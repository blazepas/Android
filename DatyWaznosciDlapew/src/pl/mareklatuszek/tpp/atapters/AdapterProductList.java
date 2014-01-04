package pl.mareklatuszek.tpp.atapters;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import pl.mareklatuszek.tpp.MainActivity;
import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.Product;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.fragments.FragmentProdukty;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import pl.mareklatuszek.tpp.utilities.ThumbLoader;
import pl.mareklatuszek.tpp.views.TextViewBariol;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class AdapterProductList extends BaseAdapter implements OnClickListener, OnLongClickListener, 
		OnScrollListener{
		
	private int itemHeightInDps = 70;
	
	private LayoutInflater inflater=null;
	private Activity mActivity;
	private ArrayList<Product> products;
	private FragmentManager fragmentManager; 
	private int fragmentId;
	
	private Boolean[] isExpanded;
	private ListView parentListView;
	private int clickedPos = -1;
	private int lastVisible = 0;
	private boolean focusingOnItem = false;
	private CommonUtilities utilities = TPPApp.getUtilities();

	TextViewBariol nazwaTxtList, dataOtwTxtList, terminWazTxtList, kategoriaTxt, estimateTimeTxt;	
	ProgressBar pozostaloPrgsList;
	ImageView obrazekImage, imageInfo;
	LinearLayout detailsLay, basicLay, deleteLay, showProdLay, expandLay;
	
	public AdapterProductList(Activity mActivity, ArrayList<Product> products, 
			FragmentManager fM, int fragmentId, ListView parentListView) {
		
		this.mActivity = mActivity;
		this.products = products;
		this.fragmentManager = fM;
		this.fragmentId = fragmentId;
		this.parentListView = parentListView;
				
		parentListView.setOnScrollListener(this);
		itemHeightInDps = mActivity.getResources().getDimensionPixelSize(R.dimen.products_item_height);
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
		View vi;
		boolean viewIsNull = convertView == null;

        if (clickedPos == position & !viewIsNull){	        
	        vi = (View) convertView.getTag();
	        
	        detailsLay = (LinearLayout) vi.findViewById(R.id.detailsLay);
	        imageInfo = (ImageView) vi.findViewById(R.id.imageInfo);
	        
	        initAnimations(position);	
	        
	        return convertView;
        } else if (viewIsNull) {
        	vi = inflater.inflate(R.layout.listview_products, null);
        	
 	        convertView = initRow(vi, position);
 	        convertView.setTag(vi); 	
 	        
 	        return convertView;
        } else {
        	vi = (View) convertView.getTag();
        	
        	convertView = initRow(vi, position);
        	convertView.setTag(vi);
        	
            return convertView;                    
        }
	}
	
	@Override
	public boolean onLongClick(View v) {
		v.showContextMenu();
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.basicLay:
		case R.id.expandLay:
			int pos = (Integer) v.getTag();
			boolean expanded = isExpanded[pos];
			isExpanded[pos] = !expanded;
			clickedPos = pos;
			AdapterProductList.this.notifyDataSetChanged();
		}		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastVisible = (firstVisibleItem + visibleItemCount) - 1;
		if(visibleItemCount <= totalItemCount) {
			focusingOnItem = true;
		} else {
			focusingOnItem = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}	
	
	private void initAnimations(int position) {
		boolean expanded = isExpanded[position];
		
		if (expanded) {
			imageInfo.setImageResource(R.drawable.image_info_clicked);
	    	if (position == clickedPos){
	    		
	    		expandItem(detailsLay, position);

		    } else {
		    	detailsLay.setVisibility(View.VISIBLE);	
		    }

		} else {    	
			imageInfo.setImageResource(R.drawable.image_info);
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
		
        nazwaTxtList = (TextViewBariol) vi.findViewById(R.id.nazwaTxtList);
        dataOtwTxtList = (TextViewBariol) vi.findViewById(R.id.dataOtwTxtList);
        terminWazTxtList = (TextViewBariol) vi.findViewById(R.id.terminWazTxtList);
        kategoriaTxt = (TextViewBariol) vi.findViewById(R.id.kategoriaTxt);
        estimateTimeTxt = (TextViewBariol) vi.findViewById(R.id.estimateTimeTxt);
        pozostaloPrgsList = (ProgressBar) vi.findViewById(R.id.pozostaloPrgsList);
        expandLay = (LinearLayout) vi.findViewById(R.id.expandLay);
        obrazekImage = (ImageView) vi.findViewById(R.id.obrazekImage);
        detailsLay = (LinearLayout) vi.findViewById(R.id.detailsLay);
        basicLay = (LinearLayout) vi.findViewById(R.id.basicLay);
        deleteLay = (LinearLayout) vi.findViewById(R.id.deleteLay);
        showProdLay = (LinearLayout) vi.findViewById(R.id.showProdLay);
        imageInfo = (ImageView) vi.findViewById(R.id.imageInfo);
        
        final Product product = products.get(pos);
        String nazwa = product.getNazwa();
        String dataOtw = product.getDataOtwarcia();
        String terminWaz = product.getTerminWaznosci();
        String kategoria = product.getKategoria();
        String endDate = product.getEndDate();
        String estimate = getEstimate(endDate);
        String image = product.getImage();
        int progress = utilities.getProgress(dataOtw, endDate);
        Drawable progressDrawable = mActivity.getResources().getDrawable(R.drawable.progress_bar_bg);
        
        vi.setBackgroundResource(rowBackground);
        basicLay.setTag(pos);
        expandLay.setTag(pos);
		expandLay.setBackgroundResource(expandLayBg);
        nazwaTxtList.setText(nazwa);
        dataOtwTxtList.setText(dataOtw);
        terminWazTxtList.setText(terminWaz);
        kategoriaTxt.setText(kategoria);
        estimateTimeTxt.setText(estimate);

        pozostaloPrgsList.setProgress(progress);
        pozostaloPrgsList.setProgressDrawable(progressDrawable);
        
        new ThumbLoader(image, obrazekImage).execute();
 
        //przypisywanie menu kontekstowego
        vi.setOnLongClickListener(this);
        basicLay.setOnLongClickListener(this);
        deleteLay.setOnLongClickListener(this);
        showProdLay.setOnLongClickListener(this);
        
		basicLay.setOnClickListener(this);
        expandLay.setOnClickListener(this);
    
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
        
        initAnimations(pos); // animacje rozsuwania i chowania dodatkowych
                
        return vi;
	}
	
	private String getEstimate(String endDate) {
		long endTime = 0;
		try {
			endTime = utilities.parseDate(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        String estimate = utilities.dateToWords(System.currentTimeMillis(), endTime);
		
		return estimate;
	}
		
	private void deleteProduct(Product product) {
		FragmentProdukty fragmentProdukty = (FragmentProdukty) fragmentManager.findFragmentById(fragmentId);
		fragmentProdukty.deleteProduct(product);
	}
		
	private void showProduct(int position) {		
		Product product = products.get(position);
		((MainActivity) mActivity).selectFragmentToShowProduct(product);
	}
	
	private void showChoiceDialog(final Product product) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
		String message = mActivity.getString(R.string.dialog_delete_default_message);
		String positive = mActivity.getString(R.string.possitive_button);
		String cancel = mActivity.getString(R.string.cancel_button);
		
		dialog.setTitle(R.string.dialog_delete_title);
		dialog.setMessage(message + " " + product.getNazwa() + "?");
		dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteProduct(product);
			}
		});

		dialog.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void expandItem(View v, int position) {
		boolean isLastVisible = position == lastVisible;
		boolean isBeforeLastVisible = position == lastVisible - 1;
		
		if((isLastVisible | isBeforeLastVisible) & focusingOnItem) { // przesuwa listę jeśli produkt niewidoczny
			v.setVisibility(View.VISIBLE);
			v.getLayoutParams().height = itemHeightInDps;
			if (isLastVisible) {
				parentListView.smoothScrollToPosition(lastVisible);
			} else {
				parentListView.smoothScrollToPosition(lastVisible - 1);
			}
			
		} else {
			utilities.expandView(v, itemHeightInDps);
		}
	}
		
	private void collapseItem(View v) {
		utilities.collapseView(v);
	}
}
