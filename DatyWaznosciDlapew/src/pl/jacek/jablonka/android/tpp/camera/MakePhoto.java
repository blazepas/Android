package pl.jacek.jablonka.android.tpp.camera;


import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.R.id;
import pl.jacek.jablonka.android.tpp.R.layout;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MakePhoto extends FragmentActivity {
 	private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_photo);
        camera = getCameraInstance();
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new PhotoHandler(MakePhoto.this));
            }
        });
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
        	String message = getString(R.string.toast_pick_camera_error);
            Toast.makeText(this, message, 2000);
        }
        return camera;
    }
}
