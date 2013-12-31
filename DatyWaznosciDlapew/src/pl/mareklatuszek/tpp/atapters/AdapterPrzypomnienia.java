package pl.mareklatuszek.tpp.atapters;

import java.util.ArrayList;
import java.util.HashMap;

import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import pl.mareklatuszek.tpp.utilities.FinalVariables;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdapterPrzypomnienia extends BaseAdapter implements FinalVariables {
	private Activity mActivity;
	ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
	private LayoutInflater inflater = null;
	private CommonUtilities utilities = TPPApp.getUtilities();
	
	TextView nazwaPow, pozostaloPow;
	LinearLayout peroid, show;
	
	public AdapterPrzypomnienia(Activity mActivity, ArrayList<HashMap<String, String>> przypomnienia) {
		this.mActivity = mActivity;
		this.przypomnienia = przypomnienia;		
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return przypomnienia.size();
	}

	@Override
	public Object getItem(int position) {
		return przypomnienia.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_powiadomienia, null);
        
        nazwaPow = (TextView) vi.findViewById(R.id.nazwaPow);  
        pozostaloPow = (TextView) vi.findViewById(R.id.pozostaloPow);
        peroid = (LinearLayout) vi.findViewById(R.id.peroid);
        show = (LinearLayout) vi.findViewById(R.id.show);

        HashMap<String, String> przypomnienie = przypomnienia.get(position);
        
        String nazwa = przypomnienie.get(DB_NAZWA);
        long powiadomienieDate = Long.parseLong(przypomnienie.get(PRZYP_DATE));
        long currentTime = System.currentTimeMillis();
        String pozostaloText = makeEstimateText(currentTime, powiadomienieDate);
        
        nazwaPow.setText(nazwa);
        pozostaloPow.setText(pozostaloText);
        
        int rowBackground;
        int peroidBackground;
        int showBackground;
        
        if((position % 2) == 0) { // przypomnienie parzyste lub nie
			rowBackground = R.color.alarms_even;
			peroidBackground = R.color.alarms_peroid_even;
			showBackground = R.color.alarms_show_even;
		} else {
			rowBackground = R.color.alarms_odd;
			peroidBackground = R.color.alarms_peroid_odd;
			showBackground = R.color.alarms_show_odd;
		}
        
        vi.setBackgroundResource(rowBackground);
        peroid.setBackgroundResource(peroidBackground);
        show.setBackgroundResource(showBackground);
        
		return vi;
	}
	
	private String makeEstimateText(long currentTime, long powiadomienieDate) {
		String text = utilities.dateToWords(currentTime, powiadomienieDate);
		if (text.equals("Powiadomiono")) { //TODO strings
			return text;
		} else {
			String forTime = mActivity.getString(R.string.date_for);
			return forTime + " " + text;
		}
	}
}
