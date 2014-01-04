package pl.mareklatuszek.tpp;

import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.atapters.AdapterDB;
import pl.mareklatuszek.tpp.atapters.AdapterMenu;
import pl.mareklatuszek.tpp.dialogs.DialogPremium;
import pl.mareklatuszek.tpp.dialogs.DialogPrzypomnienie;
import pl.mareklatuszek.tpp.fragments.FragmentDodaj;
import pl.mareklatuszek.tpp.fragments.FragmentEdytuj;
import pl.mareklatuszek.tpp.fragments.FragmentKategorie;
import pl.mareklatuszek.tpp.fragments.FragmentOAplikacji;
import pl.mareklatuszek.tpp.fragments.FragmentProdukt;
import pl.mareklatuszek.tpp.fragments.FragmentProdukty;
import pl.mareklatuszek.tpp.fragments.FragmentPrzypomnienia;
import pl.mareklatuszek.tpp.fragments.FragmentPrzypomnienie;
import pl.mareklatuszek.tpp.premium.InitPremiumStatus;
import pl.mareklatuszek.tpp.premium.PremiumUtilities;
import pl.mareklatuszek.tpp.utilities.FinalVariables;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;


public class MainActivity extends SherlockFragmentActivity implements FinalVariables, OnClickListener {
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

	ListView menuList;
	AdapterMenu menuAdapter;
	AdapterDB adapterDb;
	SlidingMenu menu;
	
	boolean backIsDoublePressed = false;
	public static int currentFragmentPos = 2;
	public static int currentFragmentId;
	int menuPos = -1;
	public static Uri imageUri = new Uri.Builder().build();
	public static boolean notification = false;
	boolean menuClicked = false;
	
	
	Fragment fragmentDodaj = new FragmentDodaj();
	Fragment fragmentProdukty = new FragmentProdukty();
	Fragment fragmentKategorie = new FragmentKategorie();
	Fragment fragmentPrzypomnienia = new FragmentPrzypomnienia();
	Fragment fragmentProdukt = new FragmentProdukt();
	Fragment fragmentPrzypomnienie = new FragmentPrzypomnienie();
	Fragment fragmentOAplikacji = new FragmentOAplikacji();
	Fragment fragmentEdytuj;
	
    @Override   
    public void onResume() {
    	super.onResume();    	
    	if(notification) {
    		String productId = getIntent().getStringExtra("productId");
            String timeInMillis = getIntent().getStringExtra("timeInMillis");
            notification = false;
            if (productId != null & timeInMillis != null) {
        		onNotification(productId, timeInMillis);
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
		
		initActionBar();
		setContentView(R.layout.main_frame);

		if (savedInstanceState == null) {
			initMenu();
			selectFragment(SELECTION_PRODUCTS);	
			menu.toggle();
		} else {
			menuPos = savedInstanceState.getInt("menuPos");
			initMenu();
		}		
		new InitPremiumStatus(this, menuAdapter).execute();
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt("menuPos", menuPos);    
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			menu.toggle();
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {   
		if (resultCode == RESULT_OK) {
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					MainActivity.imageUri));
			if (requestCode == CAMERA_ADD_RQ_CODE) {
				if (intent != null) {
					imageUri = intent.getData();
				}
				setPictureInFragmentDodaj(imageUri);
			} else if (requestCode == GALLERY_ADD_RQ_CODE) {
				if (intent != null) {
					imageUri = intent.getData();
				}
				setPictureInFragmentDodaj(imageUri);
			} else if (requestCode == CAMERA_EDIT_RQ_CODE) {
				if (intent != null) {
					imageUri = intent.getData();
				}
				setPictureInFragmentEdytuj(imageUri);
			} else if (requestCode == GALLERY_EDIT_RQ_CODE) {
				if (intent != null) {
					imageUri = intent.getData();
				}
				setPictureInFragmentEdytuj(imageUri);
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
		if (currentFragmentPos == SELECTION_EDIT) { // jesli fragment edytuj			
			if (fragmentEdytuj != null) {
				Bundle extras = fragmentEdytuj.getArguments();// TODO
				Product product = (Product) extras.getSerializable("product");
				selectFragmentToShowProduct(product);
			} else {
				selectFragment(SELECTION_PRODUCTS);	
			}
		} else if (currentFragmentPos == SELECTION_PRODUCT) { //jesli fragment produkt			
			selectFragment(SELECTION_PRODUCTS);	
		} else if (currentFragmentPos == SELECTION_ALARM) { //jesli fragment przypomnienie	
			selectFragment(SELECTION_ALARMS);
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
	

	@Override
	public void onClick(View v) {}
	
	private void initMenu() {
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setFadeEnabled(true);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(getMenuList());
		menu.setOnClosedListener(new MenuCloseListener());
	}
	
	private void initActionBar() {	
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setCustomView(R.layout.actionbar_title);
	}

	private class MenuItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
			// po wybraniu z menu chowa je i zapisuje wybraną pozycje
			menuClicked = true;
			selectItemMenu(position);
			menu.toggle();
		}
	}
	
	private class MenuCloseListener implements OnClosedListener {
		// po schowaniu menu ładuje fragment
		@Override
		public void onClosed() {
			
			if(currentFragmentPos != menuPos & menuClicked) {
				selectFragment(menuPos);
			}
			menuClicked = false;
		}
	}

	public void selectFragment(int position) {	
		currentFragmentPos = position;
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		switch (position) {
		case SELECTION_SCAN:
			startScanner();
			break;
		case SELECTION_ADD:
			selectItemMenu(position);
			fragmentDodaj = new FragmentDodaj();
			ft.replace(R.id.content_frame, fragmentDodaj);
			break;
		case SELECTION_PRODUCTS:
			selectItemMenu(position);
			ft.replace(R.id.content_frame, fragmentProdukty);
			break;
		case SELECTION_CATEGORIES:
			selectItemMenu(position);
			ft.replace(R.id.content_frame, fragmentKategorie);
			break;
		case SELECTION_ALARMS:
			selectItemMenu(position);
			ft.replace(R.id.content_frame, fragmentPrzypomnienia);
			break;
		case SELECTION_PREMIUM:
			DialogPremium dialog = new DialogPremium(this);
			dialog.show();
			return;	
		case SELECTION_PRODUCT:
			ft.replace(R.id.content_frame, fragmentProdukt);
			ft.commit();
			return;
		case SELECTION_EDIT:
			ft.replace(R.id.content_frame, fragmentEdytuj);
			ft.commit();
			return;
		case SELECTION_ALARM:
			ft.replace(R.id.content_frame, fragmentPrzypomnienie);
			ft.commit();
		case SELECTION_ABOUT:
			ft.replace(R.id.content_frame, fragmentOAplikacji);
			ft.commit();
			return;
		}
		ft.commit();	
	}
	
	private void selectItemMenu(int position) {
		if(menuPos != position) {
			menuPos = position;
			menuAdapter.setClickedPos(position);
			menuAdapter.notifyDataSetChanged();
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
		if (currentFragmentPos != SELECTION_ADD) {
            selectFragment(SELECTION_ADD);
        }
		selectItemMenu(SELECTION_SCAN);
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
            selectFragment(SELECTION_ADD);
        }		
	}
	
	private void setPictureInFragmentDodaj(Uri selectedImage) {	       
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentDodaj actualFragment = (FragmentDodaj) fragmentManager.findFragmentById(currentFragmentId);
        actualFragment.setCameraResult(selectedImage);      
	}
	
	private void setPictureInFragmentEdytuj(Uri selectedImage) {	       
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentEdytuj actualFragment = (FragmentEdytuj) fragmentManager.findFragmentById(currentFragmentId);
        actualFragment.setCameraResult(selectedImage);      
	}

	private FrameLayout getMenuList() {
		//reakcja na przesunięcie palcem menu
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
		
		String[] titles = getResources().getStringArray(R.array.array_menu_titles);
		TypedArray icons = getResources().obtainTypedArray(R.array.array_menu_icons);
		TypedArray colors = getResources().obtainTypedArray(R.array.array_menu_background_colors);
		
		LayoutInflater li = getLayoutInflater();
		FrameLayout menuFrame = (FrameLayout) li.inflate(R.layout.menu_frame, null);
		
		menuFrame.setOnClickListener(this); 
		menuFrame.setOnTouchListener(gestureListener);
		
		menuAdapter = new AdapterMenu(MainActivity.this, titles, icons, colors, menuPos);
		menuList = (ListView) menuFrame.findViewById(R.id.listview_drawer);
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new MenuItemClickListener());
		menuList.setDivider(null);
				
		return menuFrame;
	}
		
	public void selectFragmentToShowProduct(Product product) {
		Bundle data = new Bundle();
        data.putSerializable("product", product);

        fragmentProdukt.setArguments(data);
        selectFragment(SELECTION_PRODUCT);		
	}
	
	public void selectFragmentToShowPrzypomienie(Product product) {
		Bundle data = new Bundle();
        data.putSerializable("product", product);

        fragmentPrzypomnienie.setArguments(data);
        selectFragment(SELECTION_ALARM);		
	}
	
	public void selectFragmentToEditProduct(Product product) {
		Bundle data = new Bundle();
        data.putSerializable("product", product);
        fragmentEdytuj = new FragmentEdytuj();
        fragmentEdytuj.setArguments(data);
        selectFragment(SELECTION_EDIT);		
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
		boolean status = adapterDb.chckIfCodeIsInDB(code);
		adapterDb.close();
		return status;
	}
	
	private class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                		&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { // w lewo
                    menu.toggle();
                   
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE 
                		&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { // w prawo
                  
                }
            } catch (Exception e) {
            	Log.i("menu gesture detector", "error");
            }
            return false;
        }
    }
			
}
