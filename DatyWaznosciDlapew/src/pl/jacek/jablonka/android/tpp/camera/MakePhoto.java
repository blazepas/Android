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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MakePhoto extends FragmentActivity {
 	private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_photo);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);        
        camera = getCameraInstance();
        cameraPreview = new CameraPreview(this, camera);
        
        preview.addView(cameraPreview, 0);
        preview.addView(getButton(), 1);
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
    
    private Button getButton() {
    	 int wrapContent = WindowManager.LayoutParams.WRAP_CONTENT;
    	 LayoutParams params = new LinearLayout.LayoutParams(wrapContent, wrapContent);
    	 Button button = new Button(this);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 camera.takePicture(null, null, new PhotoHandler(MakePhoto.this));
             }
         });
         String buttonTxt = getString(R.string.context_take_photo);
         button.setText(buttonTxt);
         button.setLayoutParams(params);
         
         return button;
    }
    
}
