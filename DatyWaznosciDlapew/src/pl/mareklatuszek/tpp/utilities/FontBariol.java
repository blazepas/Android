package pl.mareklatuszek.tpp.utilities;

import android.content.Context;
import android.graphics.Typeface;

public class FontBariol {
	private Context context;
	private static FontBariol instance;
 
	public FontBariol(Context context) {
		this.context = context;
	}
 
	public static FontBariol getInstance(Context context) {
		synchronized (FontBariol.class) {
			if (instance == null) {
				instance = new FontBariol(context);
			}
			return instance;	
		}
	}
 
	public Typeface getTypeFace() {
		return Typeface.createFromAsset(context.getResources().getAssets(), "Bariol_Regular.otf");
	}
}
