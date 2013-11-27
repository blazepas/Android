package com.mareklatuszek.datywaznosci;

import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.datywznosci.utilities.FinalVariables;


public class MainActivity extends SherlockFragmentActivity implements FinalVariables {

	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	AdapterMenu mMenuAdapter;
	AdapterDB adapterDb;
	
	String[] title;
	String[] subtitle;
	int[] icon;
	boolean backIsDoublePressed = false;
	public static int currentFragmentPos = 2;
	public static int currentFragmentId;
	public static Uri imageUri = new Uri.Builder().build();
	public static boolean notification = false;
	
	Fragment fragmentDodaj = new FragmentDodaj();
	Fragment fragmentProdukty = new FragmentProdukty();
	Fragment fragmentKategorie = new FragmentKategorie();
	Fragment fragmentPrzypomnienia = new FragmentPrzypomnienia();
	Fragment fragmentProdukt = new FragmentProdukt();
	Fragment fragmentEdytuj;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
    @Override   
    public void onResume()
    {
    	super.onResume();
    	
    	if(notification)
    	{
    		
    		String productCode = getIntent().getStringExtra("productCode");
            String timeInMillis = getIntent().getStringExtra("timeInMillis");
            notification = false;
            if (productCode != null & timeInMillis != null) {
            	
        		onNotification(productCode, timeInMillis);
            } else {
            	//TODO
            }
    	}
    	
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		adapterDb = new AdapterDB(this);
				
		// Layout wysuwanego menu
		setContentView(R.layout.drawer_main);

		// Tytul
		mTitle = mDrawerTitle = getTitle();

		// Nag��wki element�w menu
		title = new String[] { "Skanowanie produktu", "Własny produkt",
				"Lista produktów", "Kategorie produktów", "Przypomnienia" };

		// Opisy nag��wk�
		subtitle = new String[] { "Zeskanuj kod", "Dodaj własny",
				"Przeglądaj dodane", "Przeglądaj kategorie", "Przeglądaj przypomnienia" };

		// przypisywanie ikon elementom menu
		icon = new int[] { R.drawable.collections_cloud, R.drawable.collections_cloud,
				R.drawable.collections_cloud, R.drawable.collections_cloud, R.drawable.collections_cloud };

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.listview_drawer);

		// cie� menu
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);

		mMenuAdapter = new AdapterMenu(MainActivity.this, title, subtitle,
				icon);

		// Adapter menu
		mDrawerList.setAdapter(mMenuAdapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectFragment(2);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
			selectFragment(position);
		}
	}

	public void selectFragment(int position) {
		
		currentFragmentPos = position;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (position) {
		case 0:
			startScanner();
			break;
		case 1:
			fragmentDodaj = new FragmentDodaj();
			ft.replace(R.id.content_frame, fragmentDodaj);
			break;
		case 2:
			ft.replace(R.id.content_frame, fragmentProdukty);
			break;
		case 3:
			ft.replace(R.id.content_frame, fragmentKategorie);
			break;
		case 4:
			ft.replace(R.id.content_frame, fragmentPrzypomnienia);
			break;
		case 5:
			ft.replace(R.id.content_frame, fragmentProdukt);
			ft.commit();
			return;
		case 6:
			ft.replace(R.id.content_frame, fragmentEdytuj);
			ft.commit();
			return;
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);

		setTitle(title[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {   
		mDrawerLayout.closeDrawer(mDrawerList);
		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_ADD_RQ_CODE) {
				imageUri = intent.getData();
				setPictureInFragmentDodaj();
			} else if (requestCode == GALLERY_ADD_RQ_CODE) {
				imageUri = intent.getData();
				setPictureInFragmentDodaj();
			} else if (requestCode == CAMERA_EDIT_RQ_CODE) {
				imageUri = intent.getData();
				setPictureInFragmentEdytuj();
			} else if (requestCode == GALLERY_EDIT_RQ_CODE) {
				imageUri = intent.getData();
				setPictureInFragmentEdytuj();
	        } else {
	        	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		        if (scanResult != null) {
		        	String code = scanResult.getContents();
		        	String codeFormat = scanResult.getFormatName();
		        	if(code != null & codeFormat != null) {
		        		selectFragmentToStoreCode(code, codeFormat);  
		        	}     	       	
		        }
	        }
		}
		
		
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
	    super.onAttachFragment(fragment);
	    currentFragmentId = fragment.getId();
	}
	
	@Override
    public void onBackPressed() {		
		if (currentFragmentPos == 6) { // jesli fragment edytuj			
			Bundle extras = fragmentEdytuj.getArguments();
			
			if (extras != null) {
				Product product = (Product) extras.getSerializable("product");
				selectFragmentToShowProduct(product);
			}			
		} else if (currentFragmentPos == 5) { //jesli fragment produkt			
			selectFragment(2);			
		} else {
			
	        if (backIsDoublePressed) {
	            super.onBackPressed();
	            return;
	        }
	        
	        backIsDoublePressed = true;
	        Toast.makeText(this, "Naciśnij wstecz drugi raz aby wyjść", 1500).show();
	        new Handler().postDelayed(new Runnable() {

	            @Override
	            public void run() {
	             backIsDoublePressed = false;   
	            }
	        }, 2000);
		}
    } 
	
	private void onNotification(String productId, String alarmTime) {	
		adapterDb.open();
		Product product = adapterDb.getProduct(productId);
		adapterDb.close();
		
		DialogPrzypomnienie dialogPrzypomnienie = new DialogPrzypomnienie(this, product);
		dialogPrzypomnienie.show();
		
//		removePrzypomnienie(productId, alarmTime);
	}
	
	public void startScanner() {
		if (currentFragmentPos != 1) {
            selectFragment(1);
        }		
		IntentIntegrator.initiateScan(MainActivity.this);
	}
	
	private void selectFragmentToStoreCode(String code, String codeFormat) {
		Log.i("kod", code);
		Log.i("fromat kodu", codeFormat);
		       
        if (currentFragmentPos == 1) {
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	FragmentDodaj actualFragment = (FragmentDodaj) fragmentManager.findFragmentById(currentFragmentId);
        	actualFragment.setDataFromScan(code, codeFormat);

        } else {
        	Bundle data = new Bundle();
            data.putString("scanResultCode", code);
            data.putString("scanResultCodeFormat", codeFormat);
            
        	fragmentDodaj.setArguments(data);
            selectFragment(1);
        }		
	}
	
	private void setPictureInFragmentDodaj() {
		Log.i("picture", "taken");
	       
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentDodaj actualFragment = (FragmentDodaj) fragmentManager.findFragmentById(currentFragmentId);
        actualFragment.setCameraResult();      
	}
	
	private void setPictureInFragmentEdytuj() {
		Log.i("picture", "taken");
	       
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentEdytuj actualFragment = (FragmentEdytuj) fragmentManager.findFragmentById(currentFragmentId);
        actualFragment.setCameraResult();      
	}
		
	public void selectFragmentToShowProduct(Product product) {
		Bundle data = new Bundle();
        data.putSerializable("product", product);

        fragmentProdukt.setArguments(data);
        selectFragment(5);		
	}
	
	public void selectFragmentToEditProduct(Product product) {
		Bundle data = new Bundle();
        data.putSerializable("product", product);
        fragmentEdytuj = new FragmentEdytuj();
        fragmentEdytuj.setArguments(data);
        selectFragment(6);		
	}
	
	public void removeFragmentEdytuj() {
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.remove(fragmentEdytuj).commit();
	}
	
	private void removePrzypomnienie(String productId, String alarmTime) {
		adapterDb.open();
		boolean removeStatus = adapterDb.deletePrzypomnienie(productId, alarmTime);
		adapterDb.close();	
	}
	
	private boolean chcekIfCodeIsInDB(String code) {
		adapterDb.open();
		boolean status = adapterDb.chckIfProductIsInDB(code);
		adapterDb.close();
		return status;
	}
	
}
