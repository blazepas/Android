package pl.jacek.jablonka.android.tpp.utilities;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ThumbLoader extends AsyncTask<Void, Void, Void> {
	String uri;
	ImageView view;
	File imgFile;
	boolean exists = false;

	public ThumbLoader(String uri, ImageView view) {
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
