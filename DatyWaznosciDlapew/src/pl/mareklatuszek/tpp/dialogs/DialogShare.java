package pl.mareklatuszek.tpp.dialogs;

import java.util.ArrayList;

import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.Product;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DialogShare extends Dialog implements android.view.View.OnClickListener {
	CommonUtilities utilities = TPPApp.getUtilities();
	FragmentActivity mActivity;
	ArrayList<Product> products = new ArrayList<Product>();
	Product product;
	
	Button okButton, anulujButton;
	EditText emailTxtBox;
	
	public DialogShare(FragmentActivity mActivity, ArrayList<Product> products) {
		super(mActivity);
		this.products = products;
		this.mActivity = mActivity;
		setTitle(R.string.dialog_share_title_products);
	}
	
	public DialogShare(FragmentActivity mActivity, Product product) {
		super(mActivity);
		this.product = product;
		this.mActivity = mActivity;
		setTitle(R.string.dialog_share_title_product);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);
		
		emailTxtBox = (EditText) findViewById(R.id.emailTxtBox);
		okButton = (Button) findViewById(R.id.okButton);
		anulujButton = (Button) findViewById(R.id.cancelButton);
				
		okButton.setOnClickListener(this);
		anulujButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			new Share().execute();
			break;
		case R.id.cancelButton:
			break;
		default:
			break;
		}
		dismiss();		
	}
		
	private class Share extends AsyncTask<Void, Void, Void> {
		
		boolean emailStatus = false;
		
		@Override
		protected void onPreExecute() {
			String message = mActivity.getString(R.string.dialog_share_toast_sending);
			Toast.makeText(mActivity, message, 1000).show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String email = emailTxtBox.getText().toString();
			
			if (!email.equals("")) {
				
				if (product != null) {
					emailStatus = utilities.sendEmailWithSingleProduct(email, product);
				} else {
					String productsTable = utilities.getProductsTableHTML(products);
					emailStatus = utilities.sendEmailWithProductList(email, productsTable);
				}
			}	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			if (emailStatus) {
				String message = mActivity.getString(R.string.dialog_share_toast_succes);
				Toast.makeText(getContext(), message, 1000).show();
			} else {
				String message = mActivity.getString(R.string.dialog_share_toast_error);
				Toast.makeText(getContext(), message, 2000).show();
			}
		}
	}		
}