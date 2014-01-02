package pl.mareklatuszek.tpp.dialogs;

import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.premium.PremiumUtilities;
import pl.mareklatuszek.tpp.utilities.FinalVariables;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class DialogPremium extends AlertDialog {
	
	Context context;

	public DialogPremium(Context context) {
		super(context);
		this.context = context;
		
		if(PremiumUtilities.APP_VERSION_PREMIUM) {
			initPremiumContent();
		} else {
			initNonPremiumContent();
		}
		
	}

	private void openInStore() {
		final String appPackageName = FinalVariables.PREMIUM_APP_URI;
		try {
		    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void initNonPremiumContent() {
		String title = context.getString(R.string.dialog_non_premium_title);
		String message = context.getString(R.string.dialog_non_premium_message);
		String positive = context.getString(R.string.possitive_button);
		String negative = context.getString(R.string.cancel_button);
		
		setTitle(title);
		setMessage(message);
		setButton(positive, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openInStore();
				
			}
		});
		setButton2(negative, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void initPremiumContent() {

		String title = context.getString(R.string.dialog_premium_title);
		String message = context.getString(R.string.dialog_premium_message);
		String ok = context.getString(R.string.ok_button);
		
		setTitle(title);
		setMessage(message);
		setButton(ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		
	}

}