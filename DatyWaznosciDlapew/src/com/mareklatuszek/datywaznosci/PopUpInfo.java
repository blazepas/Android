package com.mareklatuszek.datywaznosci;

import com.mareklatuszek.utilities.FinalVariables;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopUpInfo extends PopupWindow implements FinalVariables{
	
	View clickedView;
	Activity mActivity;
	
    private int posX = -265;
    private int posY = -35;
	
	public PopUpInfo(Activity mActivity, View clickedView) {
		this.mActivity = mActivity;
		this.clickedView = clickedView;
		initWindow();
	}
	
	public void initWindow() {
		
        int layoutId = R.layout.popup_info_right;
        int wrapContent = WindowManager.LayoutParams.WRAP_CONTENT;

        boolean leftSide = false;
        String message = "";

        switch (clickedView.getId()) {
        case R.id.okresInfoImage:
        	leftSide = false;
        	message = INFO_OKRES;
        	break;
        case R.id.terminWazInfoImage:
        	leftSide = true;
        	message = INFO_TERMIN_WAZNOSCI;
        	break;
        case R.id.dataZuzInfoImage:
        	leftSide = true;
        	message = INFO_DATA_ZUZYCIA;
        case R.id.scanInfoImage:
        	leftSide = false;
        	message = INFO_OKRES;
        	break;
        case R.id.customInfoImage:
        	leftSide = false;
        	message = INFO_OKRES;
        	break;
        }
        
        if (leftSide) {
            layoutId = R.layout.popup_info_left;
            posX = -5;
            posY = -35;
        }
        
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        Drawable background = mActivity.getResources().getDrawable(R.drawable.back_info_popup);
        RelativeLayout popupLay = (RelativeLayout) inflater.inflate(layoutId, null);
        
        TextView popupTxt = (TextView) popupLay.findViewById(R.id.popupTxt);
        popupTxt.setText(message);
        
        setFocusable(true); 
        setBackgroundDrawable(background); //TODO
        setHeight(wrapContent);
        setWidth(300);
        setContentView(popupLay);
	}
	
	public void showPopUp() {
        showAsDropDown(clickedView, posX, posY);
	}
}
