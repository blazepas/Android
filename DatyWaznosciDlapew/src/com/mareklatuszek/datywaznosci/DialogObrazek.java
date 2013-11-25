package com.mareklatuszek.datywaznosci;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashMap;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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