package com.mareklatuszek.datywaznosci;

import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;


public class MainActivity extends SherlockFragmentActivity {

	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	AdapterMenu mMenuAdapter;
	
	String[] title;
	String[] subtitle;
	int[] icon;
	public static int currentFragmentPos = 2;
	public static int currentFragmentId;
	
	Fragment fragmentDodaj = new FragmentDodaj();
	Fragment fragmentProdukty = new FragmentProdukty();
	Fragment fragmentKategorie = new FragmentKategorie();
	Fragment fragmentPrzypomnienia = new FragmentPrzypomnienia();
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Layout wysuwanego menu
		setContentView(R.layout.drawer_main);

		// Tytul
		mTitle = mDrawerTitle = getTitle();

		// Nag³ówki elementów menu
		title = new String[] { "Skanowanie produktu", "W³asny produkt",
				"Lista produktów", "Kategorie produktów", "Przypomnienia" };

		// Opisy nag³ówkó
		subtitle = new String[] { "Zeskanuj kod", "Dodaj w³asny",
				"Przegl¹daj dodane", "Przegl¹daj kategorie", "Przegl¹daj przypomnienia" };

		// przypisywanie ikon elementom menu
		icon = new int[] { R.drawable.collections_cloud, R.drawable.collections_cloud,
				R.drawable.collections_cloud, R.drawable.collections_cloud, R.drawable.collections_cloud };

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.listview_drawer);

		// cieñ menu
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
				// TODO Auto-generated method stub
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
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
		// Locate Position
		switch (position) {
		case 0:
			startScanner();
			break;
		case 1:
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
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
        	String code = scanResult.getContents();
        	String codeFormat = scanResult.getFormatName();
        	if (code != null & codeFormat != null) {
        		selectFragmentToStoreCode(code, codeFormat);
        	}
        	
        } else {
        	//TODO wyœwietliæ informacjê o nieudanym skanowaniu
        }
       
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
	    // TODO Auto-generated method stub
	    super.onAttachFragment(fragment);

	    currentFragmentId = fragment.getId();
	}
	
	public void startScanner() {
		IntentIntegrator.initiateScan(MainActivity.this);
	}
	
	private void selectFragmentToStoreCode(String code, String codeFormat) {
		//TODO sprawdziæ czy w kodzie s¹ jakieœ dane i je przekazaæ
		Log.i("kod", code);
	
		Bundle data = new Bundle();
        data.putString("scanResultCode", code);
        data.putString("scanResultCodeFormat", codeFormat);
  
        if (currentFragmentPos == 1) {
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	FragmentDodaj actualFragment = (FragmentDodaj) fragmentManager.findFragmentById(currentFragmentId);
        	actualFragment.setDataFromScan(code, codeFormat);
        } else {
        	fragmentDodaj.setArguments(data);
            selectFragment(1);
        }
        
		
	}
	
	
}
