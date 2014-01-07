package pl.jacek.jablonka.android.tpp.fragments;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import pl.jacek.jablonka.android.tpp.MainActivity;
import pl.jacek.jablonka.android.tpp.Product;
import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.atapters.AdapterDB;
import pl.jacek.jablonka.android.tpp.dialogs.DialogGeneruj;
import pl.jacek.jablonka.android.tpp.dialogs.DialogObrazek;
import pl.jacek.jablonka.android.tpp.dialogs.DialogShare;
import pl.jacek.jablonka.android.tpp.popups.PopupOverflow;
import pl.jacek.jablonka.android.tpp.utilities.BitmapLoader;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import pl.jacek.jablonka.android.tpp.verification.PremiumUtilities;
import pl.jacek.jablonka.android.tpp.views.TextViewBariol;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FragmentProdukt extends SherlockFragment implements FinalVariables, OnClickListener {
	
	private static final String DODATKOWE = "dodatkowe";
	
	private Product product = new Product();
	CommonUtilities utilities = TPPApp.getUtilities();
	boolean dodatkoweIsVisible = false;
	
	View rootView;
	LinearLayout layDodatkoweShow, toggle, dodatkowe, przypomnieniaLayout;
	TextViewBariol nazwaTxt, okresTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, isScannedTxt, estimateTimeTxt;
	ImageView barcodeImage, obrazekImage, pickGenerate, dodatkoweImage;
	PopupOverflow popupOverflow;
	Bitmap codeBmp,imageBmp;
	RelativeLayout progressLay;
	
	@Override
	public void onResume() {
		super.onResume();
		String title = getString(R.string.frag_product_title);
		utilities.setActionBarTitle(title, getSherlockActivity());
	}
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_produkt, container, false);
		
		Bundle extras = getArguments();
		if (extras != null) {
			product = (Product) extras.getSerializable("product");	
			
	        setHasOptionsMenu(true);
			
			initPodstawowe();
			initDodatkowe();
		} else {
			switchToProductsFragment();
		}
		
		if(savedInstanceState != null) {
			dodatkoweIsVisible = savedInstanceState.getBoolean(DODATKOWE);
		}
		
		if (dodatkoweIsVisible) { 
			showDodatkowe();
		}
		
		return rootView;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.items_product, menu);
      try {
    	  boolean deviceHasMenuButton = ViewConfiguration.get(getActivity()).hasPermanentMenuKey();
          if (!deviceHasMenuButton) {
        	  menu.removeItem(R.id.editMenuButton);
        	  menu.removeItem(R.id.shareMenuButton);
        	  menu.removeItem(R.id.deleteMenuButton);
          }
      } catch (NoSuchMethodError e) {
    	  Log.i("Fragment Produkt", "onCreateOptionsMenu ERROR");
      }
      

      super.onCreateOptionsMenu(menu, inflater);
    }
       
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		
		if (dodatkoweIsVisible) { 
			bundle.putBoolean(DODATKOWE, true);

		} else {
			bundle.putBoolean(DODATKOWE, false);
		}
		
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) {
          case R.id.editMenuButton:
        	switchToEditFragment(product);
            break;
          case R.id.shareMenuButton:
        	  onShare();
            break;
          case R.id.deleteMenuButton:
        	 showChoiceDeleteDialog(product);
            break;
          case R.id.overflow_product:
         	 popupOverflow = new PopupOverflow(getActivity(), item, new OverflowListener());
         	 popupOverflow.showPopup();
             break;
       }
       return true;
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barcodeImage:
			DialogObrazek showBarcode = new DialogObrazek(getActivity(), codeBmp);
			showBarcode.show();
			break;
		case R.id.obrazekImage:
			DialogObrazek showImage = new DialogObrazek(getActivity(), imageBmp);
			showImage.show();
			break;
		case R.id.toggle:
			toggleDodatkowe();
			break;
		case R.id.pickGenerate:
			getDialogGeneruj();		
			break;
		}
	}
    
    private class OverflowListener implements OnClickListener {
		// listener menu actionbara
		@Override
		public void onClick(View v) {
			popupOverflow.dismiss();
			
			switch (v.getId()) {
			case 0:
				 onShare();
				break;
			case 1:
				switchToEditFragment(product);
				break;
			case 2:
				showChoiceDeleteDialog(product);
				break;	
			}
		}
	};
	
	private void initPodstawowe() {		
		isScannedTxt = (TextViewBariol) rootView.findViewById(R.id.isScannedTxt);
		nazwaTxt = (TextViewBariol) rootView.findViewById(R.id.nazwaTxt);
		dataOtwTxt = (TextViewBariol) rootView.findViewById(R.id.dataOtwTxt);
		okresTxt = (TextViewBariol) rootView.findViewById(R.id.okresTxt);
		estimateTimeTxt = (TextViewBariol) rootView.findViewById(R.id.estimateTimeTxt);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		obrazekImage = (ImageView) rootView.findViewById(R.id.obrazekImage);
		toggle = (LinearLayout) rootView.findViewById(R.id.toggle);
		dodatkoweImage = (ImageView) rootView.findViewById(R.id.dodatkoweImage);
		pickGenerate = (ImageView) rootView.findViewById(R.id.pickGenerate);
		progressLay = (RelativeLayout) rootView.findViewById(R.id.progressLay);		
		
		String nazwa = product.getNazwa();
		String okres = product.getOkresWaznosci();
		String dataOtw = product.getDataOtwarcia();
		String code = product.getCode();
		String codeFormat = product.getCodeFormat();
		String image = product.getImage();
		String endDate = product.getEndDate();
		final int progress = utilities.getProgress(dataOtw, endDate);
		Drawable progressDrawable = getResources().getDrawable(R.drawable.progress_bar_bg);
		String estimate = getEstimate(endDate);
		String productStatus = "";
		
		if (product.getIsScanned()) {
			productStatus = getString(R.string.tv_product_is_scaned);
		} else {
			productStatus = getString(R.string.tv_product_is_own);
		}
		
		nazwaTxt.setText(nazwa);
		okresTxt.setText(okres);
		estimateTimeTxt.setText(estimate);
		dataOtwTxt.setText(dataOtw);
		isScannedTxt.setText(productStatus);
		setImage(image);
		setBarcode(code, codeFormat);
		setProggres(progress, progressDrawable);
		
		toggle.setOnClickListener(this);
		pickGenerate.setOnClickListener(this);
	}
	
	private void initDodatkowe() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		layDodatkoweShow = (LinearLayout) inflater.inflate(R.layout.lay_dodatkowe_show, null);		
		dodatkowe = (LinearLayout) rootView.findViewById(R.id.dodatkowe);	
		dodatkowe.addView(layDodatkoweShow);
		
		terminWazTxt = (TextViewBariol) dodatkowe.findViewById(R.id.terminWazTxt);
		kategoriaTxt = (TextViewBariol) dodatkowe.findViewById(R.id.kategoriaTxt);
		przypomnieniaLayout = (LinearLayout) dodatkowe.findViewById(R.id.przypomnieniaLayout);
		
		String terminWaz = product.getTerminWaznosci();
		String kategoria = product.getKategoria();

		terminWazTxt.setText(terminWaz);
		kategoriaTxt.setText(kategoria);
		initPrzypomnienia();
	}
	
	private void initPrzypomnienia() {
		ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
		przypomnienia = utilities.sortPrzypomnieniaAll(przypomnienia);
		
		for (int i = 0; i < przypomnienia.size(); i++) {
			TextViewBariol kiedyPow = new TextViewBariol(getActivity());
			kiedyPow.setTextAppearance(getActivity(), R.style.TPP_Normal_TextAppearance);
			kiedyPow.setTextColor(getResources().getColor(R.color.dark_text_view));
	        
	        String przypDate = przypomnienia.get(i).get(PRZYP_DATE);
	        long notifTime = Long.parseLong(przypDate);
	        long currentTime = System.currentTimeMillis();
	        String date = makeEstimateText(currentTime, notifTime);
	        
	        kiedyPow.setText(date);
	        
	        przypomnieniaLayout.addView(kiedyPow);
		}
 
	}
	
	private void setImage(String image) {
		if (!image.equals("")) {
			String imagePath = product.getImage();
			imageBmp = BitmapLoader.loadBitmap(imagePath, 100, 100);
			obrazekImage.setImageBitmap(imageBmp);
			obrazekImage.setOnClickListener(this);
		}
	}
	
	private void setBarcode(String code, String codeFormat) {
		if (!code.equals("")) {
			codeBmp = utilities.encodeCodeToBitmap(code, codeFormat);
			barcodeImage.setImageBitmap(codeBmp);
			barcodeImage.setOnClickListener(this);
		}
	}
	
	private void setProggres(int progress, Drawable progressDrawable) {
		// ominiecie buga androida
		int fillParent = LayoutParams.FILL_PARENT;
		LayoutParams params = new LayoutParams(fillParent, fillParent);
		
		ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setProgress(progress);
		progressBar.setProgressDrawable(progressDrawable);
		progressBar.setLayoutParams(params);
		
		progressLay.addView(progressBar, 0);
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
	
	private void getDialogGeneruj() {
		boolean isScanned = product.getIsScanned();
		
		if(!isScanned) {
			if (PremiumUtilities.APP_VERSION_NONE) {
				String message = getString(R.string.toast_must_have_premium);
	  		  	Toast.makeText(getActivity(), message, 2000).show();
	  	  	} else {
	      	  	DialogGeneruj dialogGen = new DialogGeneruj(this, product, barcodeImage);
				dialogGen.show();
	  	  	} 	
		}
	
	}
	
	private void toggleDodatkowe() {
		if (dodatkoweIsVisible) {
			hideDodatkowe();
		} else {
			showDodatkowe();
		}
	}
	
	private void showDodatkowe() {
		dodatkoweIsVisible = true;
		Drawable backg = getResources().getDrawable(R.drawable.collapse_dodatkowe);
		dodatkoweImage.setImageDrawable(backg);
        utilities.expandLinearLayout(dodatkowe);
	}
	
	private void hideDodatkowe() {		
		dodatkoweIsVisible = false;
		Drawable backg = getResources().getDrawable(R.drawable.expand_dodatkowe);		
		dodatkoweImage.setImageDrawable(backg);
        dodatkowe.setVisibility(View.GONE);
	}
	
	private void showChoiceDeleteDialog(final Product product) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		String message = getString(R.string.dialog_delete_default_message);
		String positive = getString(R.string.possitive_button);
		String cancel = getString(R.string.cancel_button);
		
		dialog.setTitle(R.string.dialog_delete_title);
		dialog.setMessage(message + product.getNazwa() + "?");
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
	
	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void switchToProductsFragment() {
		((MainActivity) getActivity()).selectFragment(SELECTION_PRODUCTS);
	}
	
	private void deleteProduct(Product product) {
		AdapterDB dbAdapter = new AdapterDB(getActivity());
		dbAdapter.open();
		boolean deleteStatus = dbAdapter.deleteProduct(product);
		dbAdapter.close();
		
		if (deleteStatus) {
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String productId = product.getProductId();
			utilities.cancelAlarms(przypomnienia, productId);
			
			String message = getString(R.string.toast_delete_succes);
			Toast.makeText(getActivity(), message + " " + product.getNazwa(), 2000).show();
			
			switchToProductsFragment();
		} else {
			String message = getString(R.string.toast_delete_error);
			Toast.makeText(getActivity(), message, 2000).show();
		}
	}
	
	private void onShare() {
		if (PremiumUtilities.APP_VERSION_NONE) {
			String message = getString(R.string.toast_must_have_premium);
  		  	Toast.makeText(getActivity(), message, 2000).show();
  	  	} else {
  	  		DialogShare dialogShare = new DialogShare(getActivity(), product);
  	  		dialogShare.show();
  	  	}	 
	}
	
	private String makeEstimateText(long currentTime, long powiadomienieDate) {
		String text = utilities.dateToWords(currentTime, powiadomienieDate);
		if (currentTime >= powiadomienieDate) {
			return text;
		} else {
			String forTime = getString(R.string.date_for);
			return forTime + " " + text;
		}
	}
}
