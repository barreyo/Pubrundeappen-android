package se.chalmers.krogkollen.pub;

import android.graphics.Bitmap;
import android.text.format.Time;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;

import java.util.Date;

/**
 * 
 * Interface for a Pub object
 * 
 * @author Oskar Karrman
 * 
 */
public interface IPub {

	/**
	 * @return the name of the pub
	 */
	public String getName();

	/**
	 * @return the description of the pub
	 */
	public String getDescription();

	/**
	 * @return the current queue time of the pub
	 */
	public int getQueueTime();

	/**
	 * Sets the queuetime for the pub
	 * 
	 * @param queueTime the queue time of the pub
	 */
	public void setQueueTime(int queueTime);

	/**
	 * @return the latitude of this pub
	 */
	public double getLatitude();

	/**
	 * @return the longitude of this pub
	 */
	public double getLongitude();

	/**
	 * @return the coordinates of this pub as a LatLng object
	 */
	public LatLng getCoordinates();

	/**
	 * @return the unique ID for the pub
	 */
	public String getID();

	/**
	 * @return the OpeningHours of the pub
	 */
	public Date getOpeningTime();

    public Date getClosingTime();

    public ParseFile getBackground();

    public Date getLastUpdated();

    public String getBranch();

    public boolean isOpen();

	/**
	 * Updates the timestamp for when the queue time was last updated
	 * 
	 * @param queueTimeLastUpdatedTimestamp the time the queue time was last updated
	 */
	public void setQueueTimeLastUpdatedTimestamp(long queueTimeLastUpdatedTimestamp);

	/**
	 * @return the time the queue time was last updated
	 */
	public long getQueueTimeLastUpdatedTimestamp();
}
