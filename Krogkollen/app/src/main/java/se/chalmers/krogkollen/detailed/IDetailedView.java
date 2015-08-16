package se.chalmers.krogkollen.detailed;

import com.google.android.gms.maps.model.LatLng;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.pub.IPub;

/**
 * Interface for a DetailedView
 * 
 * @author Oskar Karrman
 * 
 */
public interface IDetailedView extends IView {

	/**
	 * Updates all text field in the view
	 * 
	 * @param pubName the pub name
	 * @param description the pub description
	 * @param openingHours the pubs opening hours
	 */
	public void updateText(String pubName, String description, String openingHours, String lastUpdated);

	/**
	 * Updates the queue indicator on the detailed view
	 * 
	 * @param queueTime the queue time used to update
	 */
	public void updateQueueIndicator(int queueTime);
}
