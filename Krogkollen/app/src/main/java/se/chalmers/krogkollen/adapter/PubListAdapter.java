package se.chalmers.krogkollen.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.list.SortedListFragment;
import se.chalmers.krogkollen.map.UserLocation;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.utils.Distance;
import se.chalmers.krogkollen.utils.Preferences;

// TODO why is this in krogkollen.list while TabsPagerAdapter is in krogkollen.adapter?

/**
 * An adapter handling the different items in a list
 */
public class PubListAdapter extends ArrayAdapter<IPub> {

	Context context;
	int layoutResourceId;
	IPub data[] = null;
	View row;
	PubHolder holder;
	SortedListFragment fragment;

	/**
	 * A constructor that creates an PubListAdapter.
	 * @param context
	 * @param layoutResourceId
	 * @param data
	 */
	public PubListAdapter(Context context, int layoutResourceId, IPub[] data, SortedListFragment fragment) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.fragment = fragment;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		row = convertView;
		holder = null;

		if(row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new PubHolder();
			holder.imgIcon = (ImageView)row.findViewById(R.id.listview_image);
			holder.txtTitle = (TextView)row.findViewById(R.id.listview_title);
			holder.distanceText = (TextView)row.findViewById(R.id.listview_distance);

			row.setTag(holder);
		} else {
			holder = (PubHolder)row.getTag();
		}

		IPub pub = data[position];
		DecimalFormat numberFormat = new DecimalFormat("#0.00");
		holder.txtTitle.setText(pub.getName());
		double distance = Distance.calcDistBetweenTwoLatLng(new LatLng(pub.getLatitude(), pub.getLongitude()), UserLocation.getInstance().getCurrentLatLng());
		if(distance < 1){
			DecimalFormat numberFormatMeters = new DecimalFormat("#0");
			holder.distanceText.setText("" + numberFormatMeters.format(distance * 1000) + "m");
		}
		else{
			holder.distanceText.setText(""+(numberFormat.format(Distance.calcDistBetweenTwoLatLng(new LatLng(pub.getLatitude(),pub.getLongitude()), UserLocation.getInstance().getCurrentLatLng())))+" km");
		}

		switch(pub.getQueueTime()){
			case 1:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_green);
				break;
			case 2:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_yellow);
				break;
			case 3:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_red);
				break;
			default :
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_gray);
				break;
		}
		return row;

	}

	/**
	 * Static holder for pubs
	 */
	static class PubHolder
	{
		ImageView imgIcon;
		TextView txtTitle;
		TextView distanceText;
	}
}
