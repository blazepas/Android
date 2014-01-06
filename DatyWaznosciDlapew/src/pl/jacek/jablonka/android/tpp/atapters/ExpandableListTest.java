package pl.jacek.jablonka.android.tpp.atapters;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import pl.jacek.jablonka.android.tpp.MainActivity;
import pl.jacek.jablonka.android.tpp.Product;
import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.fragments.FragmentProdukty;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.views.TextViewBariol;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ExpandableListTest extends BaseExpandableListAdapter{
	
	private ArrayList<Product> products;
	private Fragment mFragment;
	private Activity mActivity;
	private LayoutInflater inflater;
	private View[] views;
	private View[] viewsChild;
	
	private CommonUtilities utilities = TPPApp.getUtilities();
	
	private TextViewBariol nazwaTxtList;
	private TextViewBariol kategoriaTxt;
	private TextViewBariol estimateTimeTxt;
	private ProgressBar pozostaloPrgsList;
	private LinearLayout expandLay;
	private ImageView imageInfo;
	private TextViewBariol dataOtwTxtList;
	private TextViewBariol terminWazTxtList;
	private LinearLayout deleteLay;
	private LinearLayout showProdLay;

	public ExpandableListTest(Fragment mFragment, ExpandableListView listView, ArrayList<Product> products) {
		this.products = products;
		this.mFragment = mFragment;
		this.mActivity = mFragment.getActivity();
		
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		views = new View[products.size()];
		viewsChild = new View[products.size()];
		
		listView.setOnGroupExpandListener(new ExpandListener());
		listView.setOnGroupCollapseListener(new ColapseListener());
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return products.get(arg0);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (viewsChild[groupPosition] == null) {
			convertView = initChild(groupPosition);
			viewsChild[groupPosition] = convertView;
		} else {
			convertView = viewsChild[groupPosition];
		}
				
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return views[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return products.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View vi = views[groupPosition];
		if (vi == null) {
			parent = initParent(groupPosition);
			views[groupPosition] = parent;
		} else {
			parent = (ViewGroup) vi;
		}
		
		return parent;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private class ExpandListener implements OnGroupExpandListener {

		@Override
		public void onGroupExpand(int groupPosition) {
			View v = (View) getGroup(groupPosition);
			imageInfo = (ImageView) v.findViewById(R.id.imageInfo);
			imageInfo.setImageResource(R.drawable.image_info_clicked);
		}
	}
	
	private class ColapseListener implements OnGroupCollapseListener {

		@Override
		public void onGroupCollapse(int groupPosition) {
			View v = (View) getGroup(groupPosition);
			imageInfo = (ImageView) v.findViewById(R.id.imageInfo);
			imageInfo.setImageResource(R.drawable.image_info);			
		}
		
	}
	
	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Product product = new Product();
			Object tag = v.getTag();
			int pos = 0;
			
			if (tag != null) {
				pos = (Integer) tag;
				product = products.get(pos);
			}

			switch (v.getId()) {
			case R.id.deleteLay:
				showChoiceDialog(product);
				break;
			case R.id.showProdLay:
				showProduct(pos);
				break;
			}
		}
	}
	
	private ViewGroup initParent(int groupPosition) {
		Product product = products.get(groupPosition);
		ViewGroup vi = (ViewGroup) inflater.inflate(R.layout.listview_products_parent, null);

		nazwaTxtList = (TextViewBariol) vi.findViewById(R.id.nazwaTxtList);
		kategoriaTxt = (TextViewBariol) vi.findViewById(R.id.kategoriaTxt);
		estimateTimeTxt = (TextViewBariol) vi.findViewById(R.id.estimateTimeTxt);
		pozostaloPrgsList = (ProgressBar) vi.findViewById(R.id.pozostaloPrgsList);
		expandLay = (LinearLayout) vi.findViewById(R.id.expandLay);
		ImageView obrazekImage = (ImageView) vi.findViewById(R.id.obrazekImage);
		imageInfo = (ImageView) vi.findViewById(R.id.imageInfo);
		
		String nazwa = product.getNazwa();
		String kategoria = product.getKategoria();
		String dataOtw = product.getDataOtwarcia();
		String endDate = product.getEndDate();
        String estimate = getEstimate(endDate);
        String image = product.getImage();
        int progress = utilities.getProgress(dataOtw, endDate);
        Drawable progressDrawable = mActivity.getResources().getDrawable(R.drawable.progress_bar_bg);
        
        nazwaTxtList.setText(nazwa);
        kategoriaTxt.setText(kategoria);
        estimateTimeTxt.setText(estimate);
		
        pozostaloPrgsList.setProgress(progress);
        pozostaloPrgsList.setProgressDrawable(progressDrawable);
                
        new LoadImage(image, obrazekImage).execute();
        
        int rowBackground;
		int expandLayBg;
		
		if((groupPosition % 2) == 0) { // produkt parzysty lub nie
			rowBackground = R.color.products_even;
			expandLayBg = R.color.products_expand_even_bg;
		} else {
			rowBackground = R.color.products_odd;
			expandLayBg = R.color.products_expand_odd_bg;
		}

		vi.setBackgroundResource(rowBackground);
		expandLay.setBackgroundResource(expandLayBg);

        return vi;
	}
	
	private View initChild(final int position) {
		final Product product = products.get(position);
		View vi = (View) inflater.inflate(R.layout.listview_products_child, null);
		
		dataOtwTxtList = (TextViewBariol) vi.findViewById(R.id.dataOtwTxtList);
        terminWazTxtList = (TextViewBariol) vi.findViewById(R.id.terminWazTxtList);
        deleteLay = (LinearLayout) vi.findViewById(R.id.deleteLay);
        showProdLay = (LinearLayout) vi.findViewById(R.id.showProdLay);
        
        String dataOtw = product.getDataOtwarcia();
        String terminWaz = product.getTerminWaznosci();
        
        dataOtwTxtList.setText(dataOtw);
        terminWazTxtList.setText(terminWaz);
        
//        deleteLay.setTag(position);
//        showProdLay.setTag(position);
        deleteLay.setOnClickListener(new ButtonListener()); 
        showProdLay.setOnClickListener(new ButtonListener());
        
        return vi;
	}
		
	private void showProduct(int position) {		
		Product product = products.get(position);
		((MainActivity) mActivity).selectFragmentToShowProduct(product);
	}
	
	private void deleteProduct(Product product) {
		FragmentProdukty fragmentProdukty = (FragmentProdukty) mFragment;
		fragmentProdukty.deleteProduct(product);
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
	
	private class LoadImage extends AsyncTask<Void, Void, Void> {
		String uri;
		ImageView view;
		File imgFile;
		boolean exists = false;

		public LoadImage(String uri, ImageView view) {
			this.uri = uri;
			this.view = view;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if (!uri.equals("")) {
	        	imgFile = new  File(uri + "thumb");
	        	exists = imgFile.exists();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			if (exists) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        	    view.setImageBitmap(myBitmap);    
			}    	    
		}
	}

}
