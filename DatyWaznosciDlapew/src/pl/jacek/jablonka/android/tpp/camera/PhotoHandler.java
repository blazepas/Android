package pl.jacek.jablonka.android.tpp.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.jacek.jablonka.android.tpp.MainActivity;
import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.fragments.FragmentDodaj;
import pl.jacek.jablonka.android.tpp.fragments.FragmentEdytuj;
import pl.jacek.jablonka.android.tpp.utilities.BitmapLoader;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoHandler implements PictureCallback {

	private final FragmentActivity mActivity;

	public PhotoHandler(FragmentActivity mActivity) {
		this.mActivity = mActivity;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {

		File pictureFileDir = getDir();

		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) { 
			String message = mActivity.getString(R.string.toast_camera_image_no_dir);
			Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "TPP_" + date + ".jpg";

		String filename = pictureFileDir.getPath() + File.separator + photoFile;

		File pictureFile = new File(filename);

		try {
			
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
			new SetCameraResult(pictureFile).execute();
		} catch (Exception error) {
			String message = mActivity.getString(R.string.toast_camera_image_error);
			Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
		}
	}

	private File getDir() {
		File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "TPP");
	}
	
	private class SetCameraResult extends AsyncTask<Void, Void, Void> {
		File pictureFile;
		
		public SetCameraResult(File pictureFile) {
			this.pictureFile = pictureFile;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Uri uri =  Uri.fromFile(pictureFile);
			MainActivity.imageUri = uri;
			String message = mActivity.getString(R.string.toast_camera_image_saved);
			Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			mActivity.finish();
		}
	}
} 
