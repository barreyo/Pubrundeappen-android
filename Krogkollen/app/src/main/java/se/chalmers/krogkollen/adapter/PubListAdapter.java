package se.chalmers.krogkollen.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.pub.IPub;

/**
 * An adapter handling the different items in a list
 */
public class PubListAdapter extends ArrayAdapter<IPub> {

	Context context;
	int layoutResourceId;
	List<IPub> data;
	View row;
	PubHolder holder;

	/**
	 * A constructor that creates an PubListAdapter.
	 * @param context
	 * @param layoutResourceId
	 * @param data
	 */
	public PubListAdapter(Context context, int layoutResourceId, List<IPub> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
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
			holder.subText = (TextView)row.findViewById(R.id.listview_subtext);

			row.setTag(holder);
		} else {
			holder = (PubHolder)row.getTag();
		}

		IPub pub = data.get(position);
        holder.txtTitle.setText(pub.getName());

        if (pub.getBranch() != null) {
            holder.subText.setText(pub.getBranch());
        } else {
            holder.subText.setText("");
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
		TextView subText;
	}
}
