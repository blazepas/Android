package com.mareklatuszek.datywaznosci;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;
import com.mareklatuszek.utilities.PremiumUtilities;
import com.mareklatuszek.utilities.TextViewBariol;

public class FragmentProdukt extends SherlockFragment implements FinalVariables, OnClickListener {
	
	private Product product = new Product();
	CommonUtilities utilities = new CommonUtilities();
	boolean dodatkoweIsVisible = false;
	
	View rootView;
	LinearLayout layDodatkoweShow, dodatkowe, przypomnieniaLayout;
	TextViewBariol nazwaTxt, okresTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, isScannedTxt, estimateTimeTxt;
	ImageView barcodeImage, obrazekImage, dodatkoweImage;
	ProgressBar pozostaloPrgsList;
	PopupOverflow popupOverflow;
	
	Bitmap codeBmp;
	Bitmap imageBmp;
	
	@Override
	public void onResume() {
		super.onResume();
		utilities.setActionBarTitle("Informacje o produkcie", getSherlockActivity());
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
			dodatkoweIsVisible = savedInstanceState.getBoolean("dodatkowe");
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
			bundle.putBoolean("dodatkowe", true);

		} else {
			bundle.putBoolean("dodatkowe", false);
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
		case R.id.dodatkoweImage:
			if (dodatkoweIsVisible) {
				hideDodatkowe();
			} else {
				showDodatkowe();
			}
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
		dodatkoweImage = (ImageView) rootView.findViewById(R.id.dodatkoweImage);
		pozostaloPrgsList = (ProgressBar) rootView.findViewById(R.id.pozostaloPrgsList);		
		
		String nazwa = product.getNazwa();
		String okres = product.getOkresWaznosci();
		String dataOtw = product.getDataOtwarcia();
		String code = product.getCode();
		String codeFormat = product.getCodeFormat();
		String image = product.getImage();
		String endDate = product.getEndDate();
		codeBmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		int progress = utilities.getProgress(dataOtw, endDate);
		Drawable progressDrawable = getResources().getDrawable(R.drawable.progress_bar_bg);
		String estimate = getEstimate(endDate);
		
		nazwaTxt.setText(nazwa);
		okresTxt.setText(okres);
		estimateTimeTxt.setText(estimate);
		dataOtwTxt.setText(dataOtw);
		barcodeImage.setImageBitmap(codeBmp);
		barcodeImage.setOnClickListener(this);
		if (product.getIsScanned()) {
			isScannedTxt.setText("Zeskanowany");
		} else {
			isScannedTxt.setText("Własny produkt");
		}
		
		if (!image.equals("")) {
			String imagePath = product.getImage();
			imageBmp = BitmapLoader.loadBitmap(imagePath, 100, 100);
			obrazekImage.setImageBitmap(imageBmp);
			obrazekImage.setOnClickListener(this);
		}
		dodatkoweImage.setOnClickListener(this);
		pozostaloPrgsList.setProgress(progress);
        pozostaloPrgsList.setProgressDrawable(progressDrawable);
		
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
	        String date = "za " + utilities.dateToWords(currentTime, notifTime);
	        
	        kiedyPow.setText(date);
	        
	        przypomnieniaLayout.addView(kiedyPow);
		}
 
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
	
	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void switchToProductsFragment() {
		((MainActivity) getActivity()).selectFragment(2);;
	}
	
	private void deleteProduct(Product product) {
		AdapterDB dbAdapter = new AdapterDB(getActivity());
		dbAdapter.open();
		boolean deleteStatus = dbAdapter.deleteProduct(product);
		dbAdapter.close();
		
		if (deleteStatus) {
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String codeId = product.getCode();
			utilities.cancelAlarms(przypomnienia, codeId, getActivity());
			Toast.makeText(getActivity(), "Usunięto " + product.getNazwa(), 2000).show();
			
			switchToProductsFragment();
		} else {
			Toast.makeText(getActivity(), "Usuwanie zakończone niepowodzeniem", 2000).show();
		}
	}
	
	private void onShare() {
		if (PremiumUtilities.APP_VERSION_NONE) {
  		  Toast.makeText(getActivity(), "Aby korzystać z tej funkcji należy wykupic wersję premium", 2000).show();
  	  } else {
  		  DialogShare dialogShare = new DialogShare(getActivity(), product);
      	  dialogShare.show();
  	  } 
	}
	
}
