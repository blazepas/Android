package com.mareklatuszek.datywaznosci;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class DialogObrazek extends Dialog implements android.view.View.OnClickListener {
	
	ImageView image;
	Bitmap imageToShow;

	public DialogObrazek(FragmentActivity mActivity, Bitmap imageToShow) {
		super(mActivity);
		this.imageToShow = imageToShow;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_obrazek);
		
		image = (ImageView) findViewById(R.id.obrazek);

		image.setImageBitmap(imageToShow);
		

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelButton:
			dismiss();
			break;
		}
		
	}
	


	
}