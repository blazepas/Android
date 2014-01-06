package pl.jacek.jablonka.android.tpp;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import android.app.Application;
import android.content.Context;

@ReportsCrashes(
		formKey = "",
        mailTo = "marek.lat@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
        )

public class TPPApp extends Application {
	private static Context mContext;
	private static CommonUtilities utilities;

	@Override
	public void onCreate() {
		super.onCreate();
        ACRA.init(this);
        mContext = this;
        utilities = new CommonUtilities();
	}
	
    public static Context getContext() {
        return mContext;
    }
    
    public static CommonUtilities getUtilities() {
        return utilities;
    }
}
