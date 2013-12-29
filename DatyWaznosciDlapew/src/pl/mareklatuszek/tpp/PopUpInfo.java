package pl.mareklatuszek.tpp;

import utilities.FinalVariables;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopUpInfo extends PopupWindow implements FinalVariables{
	
	View clickedView;
	Context mActivity;
	
    private int posX = -265;
    private int posY = -35;
    
	
	public PopUpInfo(Context mActivity, View clickedView) {
		super(mActivity);
		this.mActivity = mActivity;
		this.clickedView = clickedView;
		initWindow();
	}
	
	public void initWindow() {
		
        int layoutId = R.layout.popup_info_right;
        int infoRes = 0;
        int wrapContent = WindowManager.LayoutParams.WRAP_CONTENT;

        boolean leftSide = false;
        String message = "";

        switch (clickedView.getId()) {
        case R.id.okresInfoImage:
        	leftSide = false;
        	infoRes = R.drawable.ic_okres_info;
        	message = INFO_OKRES;
        	break;
        case R.id.terminWazInfoImage:
        	leftSide = false;
        	infoRes = R.drawable.ic_termin_info;
        	message = INFO_TERMIN_WAZNOSCI;
        	break;
        case R.id.dataZuzInfoImage:
        	leftSide = true;
        	infoRes = R.drawable.ic_datazuz_info;
        	message = INFO_DATA_ZUZYCIA;
        	break;
        case R.id.scanInfoArea:	
        case INFO_MENU_0_ITEM_ID:
        	leftSide = true;
        	infoRes = R.drawable.ic_scan_info;
        	clickedView = clickedView.findViewById(R.id.infoIcon); //okno względem ikonki
        	message = INFO_SKANOWANIE;
        	break;
        case R.id.customInfoArea:	
        case INFO_MENU_1_ITEM_ID:
        	leftSide = true;
        	infoRes = R.drawable.ic_add_item_info;
        	clickedView = clickedView.findViewById(R.id.infoIcon); //okno względem ikonki
        	message = INFO_WLASNY_PRODUKT;
        	break;
        }
        
        if (leftSide) {
            layoutId = R.layout.popup_info_left;
            posX = -5;
            posY = -35;
        }
        
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        Drawable background = mActivity.getResources().getDrawable(R.drawable.back_info_popup);
        Drawable info = mActivity.getResources().getDrawable(infoRes);
        RelativeLayout popupLay = (RelativeLayout) inflater.inflate(layoutId, null);
        
        ImageView infoImage = (ImageView) popupLay.findViewById(R.id.infoImage);
        infoImage.setImageDrawable(info);
        TextView popupTxt = (TextView) popupLay.findViewById(R.id.popupTxt);
        popupTxt.setText(message);
        
        setFocusable(true); 
        setBackgroundDrawable(background); //TODO
        setHeight(wrapContent);
        setWidth(300);
        setContentView(popupLay);
	}
	
	public void showPopUp() {
		Log.i("popup", "show");
        showAsDropDown(clickedView, posX, posY);
	}
}
