package se.chalmers.krogkollen.pub;

import android.graphics.Bitmap;
import android.text.format.Time;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;

import java.util.Date;
import java.util.TimeZone;

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

/**
 * 
 * A class representing a pub
 * 
 * @author Jonathan Nilsfors
 * @author Albin Garpetun
 * @author Oskar Karrman
 * 
 */
public class Pub implements IPub {

	private String			name;
	private String			description;
	private double			latitude;
	private double			longitude;
	private int				queueTime;
    private Date            openTime, closeTime, lastUpdated;
	private final String	ID;
	private long			queueTimeLastUpdatedTimestamp;
    private String          branch;
    private ParseFile       backgroundImage;

	/**
	 * Create a new Pub object with default values
	 */
	public Pub() {
		this("Name", "Description", 51, 11, new Date(), new Date(), 3, 1, new Date(), null, "Informationsteknik", "ID");
	}

	/**
	 * Create a new Pub object
	 * 
	 * @param name the name
	 * @param description the description
	 * @param latitude the latitude position
	 * @param longitude the longitude position
	 * @param queueTime the current queue time of the pub
	 * @param ID the ID of the pub
	 */
	public Pub(String name,
		String description,
		double latitude,
		double longitude,
		Date openTime,
        Date closeTime,
		int queueTime,
		long queueTimeLastUpdatedTimestamp,
        Date lastUpdated,
        ParseFile backgroundImage,
        String branch,
		String ID)
	{
		this.name = name;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
        this.openTime = openTime;
        this.closeTime = closeTime;
		this.queueTime = queueTime;
		this.queueTimeLastUpdatedTimestamp = queueTimeLastUpdatedTimestamp;
        this.backgroundImage = backgroundImage;
        this.lastUpdated = lastUpdated;
        this.branch = branch;
		this.ID = ID;
	}

	@Override
	public LatLng getCoordinates() {
		return new LatLng(this.latitude, this.longitude);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public int getQueueTime() {
		return isOpen() ? this.queueTime : 0;
	}
	@Override
	public void setQueueTime(int queueTime) {
		// No negative time allowed
		if (queueTime < 0) {
			this.queueTime = 0;
		}
		else {
			this.queueTime = queueTime;
		}
	}

	@Override
	public String getID() {
		return this.ID;
	}

    @Override
    public Date getOpeningTime() {
        return this.openTime;
    }

    @Override
    public Date getClosingTime() {
        return this.closeTime;
    }

    @Override
    public ParseFile getBackground() {
        return this.backgroundImage;
    }

    @Override
    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public boolean isOpen() {

        Date fixedOpening = convertTimeZone(this.getOpeningTime(), TimeZone.getDefault(), TimeZone.getTimeZone("Coordinated Universal Time"));
        Date fixedClosing = convertTimeZone(this.getClosingTime(), TimeZone.getDefault(), TimeZone.getTimeZone("Coordinated Universal Time"));

        Time currentTime = new Time();
        currentTime.setToNow();

        Date currentTimeDate = new Date(currentTime.year - 1900, currentTime.month, currentTime.monthDay, currentTime.hour, currentTime.minute, currentTime.second);

        return currentTimeDate.after(fixedOpening) && currentTimeDate.before(fixedClosing);
    }

    private java.util.Date convertTimeZone(java.util.Date date, TimeZone fromTZ , TimeZone toTZ)
    {
        long fromTZDst = 0;
        if(fromTZ.inDaylightTime(date))
        {
            fromTZDst = fromTZ.getDSTSavings();
        }

        long fromTZOffset = fromTZ.getRawOffset() + fromTZDst;

        long toTZDst = 0;
        if(toTZ.inDaylightTime(date))
        {
            toTZDst = toTZ.getDSTSavings();
        }
        long toTZOffset = toTZ.getRawOffset() + toTZDst;

        return new java.util.Date(date.getTime() + (toTZOffset - fromTZOffset));
    }

    @Override
	public double getLatitude() {
		return this.latitude;
	}

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public void setQueueTimeLastUpdatedTimestamp(long queueTimeLastUpdatedTimestamp) {
		this.queueTimeLastUpdatedTimestamp = queueTimeLastUpdatedTimestamp;
	}

	@Override
	public long getQueueTimeLastUpdatedTimestamp() {
		return this.queueTimeLastUpdatedTimestamp;
	}
}
