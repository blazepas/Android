package pl.jacek.jablonka.android.tpp.dialogs;

import pl.jacek.jablonka.android.tpp.R;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class DialogRegulamin extends AlertDialog.Builder {

	Context context;
	
	String title;
	String dialogMessage;
	String close;
	String copy;
	String toastMessage;
	
	public DialogRegulamin(Context context) {
		super(context);
		this.context = context;
		
		makeDialog();
	}
	
	private void makeDialog() {
		title = context.getString(R.string.dialog_regulations_title);
		dialogMessage = context.getString(R.string.dialog_regulations_message);
		close = context.getString(R.string.close_button);
		copy = context.getString(R.string.copy_button);
		
		setTitle(title);
		setMessage(dialogMessage);
		setNegativeButton(close, null);
		setPositiveButton(copy, new CopyListener());	
	}
		
	private class CopyListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			toastMessage = context.getString(R.string.toast_copy_regulations);

			ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE); 
			ClipData clip = ClipData.newPlainText("label", dialogMessage);
			clipboard.setPrimaryClip(clip);

			Toast.makeText(getContext(), toastMessage, 2000).show();
		}
	}
	

	
			
}