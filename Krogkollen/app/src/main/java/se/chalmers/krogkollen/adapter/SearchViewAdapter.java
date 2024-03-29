package se.chalmers.krogkollen.adapter;

/*
 * This file is part of Krogkollen.
 *
 * Krogkollen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krogkollen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krogkollen.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.pub.IPub;

/**
 * An adapter handling the different items in a search list
 */
public class SearchViewAdapter extends ArrayAdapter<IPub> {

	private Context		context;
	private int			layoutResourceId;
	private IPub		data[]	= null;
	private View		row;
	private PubHolder	holder;

	/**
	 * A constructor that creates an SearchViewAdapter.
	 * 
	 * @param context
	 * @param layoutResourceId
	 * @param data
	 */
	public SearchViewAdapter(Context context, int layoutResourceId, IPub[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		row = convertView;
		holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new PubHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.listview_image);
			holder.txtTitle = (TextView) row.findViewById(R.id.listview_title);
			holder.distanceText = (TextView) row.findViewById(R.id.listview_distance);

			row.setTag(holder);
		} else {
			holder = (PubHolder) row.getTag();
		}

		IPub pub = data[position];
		holder.txtTitle.setText(pub.getName());
		holder.distanceText.setText(pub.getBranch() != null ? pub.getBranch() : "");

		switch (pub.getQueueTime()) {
			case 1:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_green);
				break;
			case 2:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_yellow);
				break;
			case 3:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_red);
				break;
			default:
				holder.imgIcon.setImageResource(R.drawable.detailed_queue_gray);
				break;
		}

		row.setBackgroundColor(Color.WHITE);

		return row;
	}

	/**
	 * Static holder for pubs
	 */
	static class PubHolder
	{
		ImageView	imgIcon;
		TextView	txtTitle;
		TextView	distanceText;
	}
}