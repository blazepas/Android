package pl.jacek.jablonka.android.tpp.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
	SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;
	private Camera camera;

	// Constructor that obtains context and camera
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
		super(context);
		this.camera = camera;
		this.surfaceHolder = this.getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
		    camera.setPreviewDisplay(surfaceHolder);
		    camera.startPreview();
		} catch (IOException e) {
			//TODO
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		camera.stopPreview();
		camera.release();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
		    int width, int height) {
		try {
		    camera.setPreviewDisplay(surfaceHolder);
		    camera.startPreview();
		} catch (Exception e) {
		    //TODO
		}
	}
}