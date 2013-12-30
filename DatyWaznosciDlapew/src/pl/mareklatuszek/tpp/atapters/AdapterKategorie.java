package pl.mareklatuszek.tpp.atapters;

import java.util.ArrayList;

import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.fragments.FragmentKategorie;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import pl.mareklatuszek.tpp.views.TextViewBariol;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class AdapterKategorie extends BaseAdapter {
	
	private Activity mActivity;
	private LayoutInflater inflater=null;
	private FragmentManager fragmentManager;
	private int fragmentId;
	ArrayList<String> categories = new ArrayList<String>();
	CommonUtilities utilites = new CommonUtilities();
	
	TextViewBariol catNameTxt;
	LinearLayout deleteCat;
	
	public AdapterKategorie(Activity mActivity, ArrayList<String> categories, FragmentManager fragmentManager, int fragmentId) {
		this.mActivity = mActivity;
		this.categories = categories;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = initRow(position);
        
		return vi;
	}
	
	private View initRow(int position) {
		View vi = inflater.inflate(R.layout.listview_kategorie, null);
        
        int rowBackground;
		int deleteButtonBg;
        
        if((position % 2) == 0) { // produkt parzysty lub nie
			rowBackground = R.color.categories_even;
			deleteButtonBg = R.color.categories_delete_even_bg;
		} else {
			rowBackground = R.color.categories_odd;
			deleteButtonBg = R.color.categories_delete_odd_bg;
		}
        
        catNameTxt = (TextViewBariol) vi.findViewById(R.id.catNameTxt);
        deleteCat = (LinearLayout) vi.findViewById(R.id.deleteCat);
        
        vi.setBackgroundResource(rowBackground);
        deleteCat.setBackgroundResource(deleteButtonBg);
         
        final String category = categories.get(position);
        catNameTxt.setText(category);
        
        deleteCat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(category);
				
			}
		});
        
        return vi;
	}
	
	private void showDialog(final String category) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
		String message = mActivity.getString(R.string.dialog_delete_kategorie_message);
		String positive = mActivity.getString(R.string.possitive_button);
		String cancel = mActivity.getString(R.string.cancel_button);
		
		dialog.setTitle(R.string.dialog_delete_title);
		dialog.setMessage(message + " " + category + "?");
		dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteCategory(category);
			}
		});

		dialog.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void deleteCategory(String category) {		
		FragmentKategorie fragmentKategorie = (FragmentKategorie) fragmentManager.findFragmentById(fragmentId);
		fragmentKategorie.removeCategory(category);
	}

}
