package se.chalmers.krogkollen.backend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.OpeningHours;
import se.chalmers.krogkollen.pub.Pub;
import se.chalmers.krogkollen.pub.PubCrawl;
import se.chalmers.krogkollen.utils.StringConverter;
import se.chalmers.krogkollen.vendor.IVendor;
import se.chalmers.krogkollen.vendor.Vendor;
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
 * A backend handling the connection between the client and the server. Uses parse.com as backend
 * provider
 * 
 * @author Jonathan Nilsfors
 * @author Oskar Karrman
 * 
 */
public class ParseBackend implements IBackend {

	// This exists to prevent the empty constructor to be called, since all information in the other
	// constructor is required
	@SuppressWarnings("unused")
	private ParseBackend() {
	}

	/**
	 * Initializes the backend to Parse.com, all information is required
	 * 
	 * @param context
	 * @param applicationID
	 * @param clientKey
	 */
	public ParseBackend(Context context, String applicationID, String clientKey) {
		Parse.initialize(context, applicationID, clientKey);
	}

	@Override
	public List<IPub> getAllPubs() throws NoBackendAccessException {

		// Instantiates the list to be returned
		List<IPub> tempPubList = new ArrayList<IPub>();

		// Fetches the requested query from the server
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Pub");

		// Declares a List to be able to handle the query
		List<ParseObject> tempList;
		try {
			// Done to simplify the handling of the query
			// Makes it possible to handle as a java.util.List
			tempList = query.find();
			for (ParseObject object : tempList) {
				tempPubList.add(convertParseObjectToPub(object));
			}
		} catch (com.parse.ParseException e1) {
			throw new NoBackendAccessException(e1.getMessage());
		}
		return tempPubList;
	}

    @Override
    public List<IVendor> getAllVendors() throws NoBackendAccessException {
        List<IVendor> tempVendorList = new ArrayList<IVendor>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vendor");

        List<ParseObject> tempList;
        try {
            tempList = query.find();
            for (ParseObject object : tempList) {
                tempVendorList.add(convertParseObjectToVendor(object));
            }
        }
            catch(com.parse.ParseException e1){
                throw new NoBackendAccessException(e1.getMessage());
            }
        return tempVendorList;
    }

    @Override
	public int getQueueTime(IPub pub) throws NoBackendAccessException, NotFoundInBackendException {
		ParseObject object = new ParseObject("Pub");
        long queueTimeLastUpdatedTimestamp;
        int queueTime;
		try {
			object = ParseQuery.getQuery("Pub").get(pub.getID());
            queueTimeLastUpdatedTimestamp = object.getLong("queueTimeLastUpdated");
            queueTime = object.getInt("queueTime");
		} catch (ParseException e) {
			if (e.getCode() == ParseException.INVALID_KEY_NAME
					|| e.getCode() == ParseException.OBJECT_NOT_FOUND) {
				throw new NotFoundInBackendException(e.getMessage());
			} else {
				throw new NoBackendAccessException(e.getMessage());
			}
		}
		return (!queueTimeIsRecentlyUpdated(queueTimeLastUpdatedTimestamp)) ? 0 : queueTime;
	}

	@Override
	public IPub getPubFromID(String id) throws NoBackendAccessException, NotFoundInBackendException {
		ParseObject object = new ParseObject("Pub");

		try {
			object = ParseQuery.getQuery("Pub").get(id);
		} catch (ParseException e) {
			if (e.getCode() == ParseException.INVALID_KEY_NAME
					|| e.getCode() == ParseException.OBJECT_NOT_FOUND) {
				throw new NotFoundInBackendException(e.getMessage());
			} else {
				throw new NoBackendAccessException(e.getMessage());
			}
		}
		return convertParseObjectToPub(object);
	}

	@Override
	public long getLatestUpdatedTimestamp(IPub pub) throws NoBackendAccessException,
			NotFoundInBackendException {
		ParseObject object = new ParseObject("Pub");

		try {
			object = ParseQuery.getQuery("Pub").get(pub.getID());
		} catch (ParseException e) {
			throw new NotFoundInBackendException(e.getMessage());
		}
		return object.getLong("queueTimeLastUpdated");
	}

    @Override
    public Date getLastUpdated(IPub pub) throws NoBackendAccessException, NotFoundInBackendException {
        ParseObject object = new ParseObject("Pub");

        try {
            object = ParseQuery.getQuery("Pub").get(pub.getID());
        } catch (ParseException e) {
            throw new NotFoundInBackendException(e.getMessage());
        }
        return object.getUpdatedAt();
    }

    /**
	 * A method for converting a ParseObject to an IPub
	 * 
	 * @param object the ParseObject
	 * @return the IPub representation of the ParseObject
	 */
	public static IPub convertParseObjectToPub(ParseObject object) {

		long queueTimeLastUpdatedTimestamp = object.getLong("queueTimeLastUpdated");
		int queueTime = object.getInt("queueTime");
        Date opening = object.getDate("opens");
        Date closing = object.getDate("closes");
        Date lastUpdated = object.getUpdatedAt();

		if (!queueTimeIsRecentlyUpdated(queueTimeLastUpdatedTimestamp)) {
			queueTime = 0;
		}

        ParseFile imageFile = (ParseFile) object.get("poster");

		return new Pub(object.getString("name"), object.getString("description"),
				object.getDouble("latitude"), object.getDouble("longitude"),opening, closing,
                queueTime, queueTimeLastUpdatedTimestamp, lastUpdated,
                imageFile, object.getString("arrangedBy"), object.getObjectId());
	}

    public static IVendor convertParseObjectToVendor(ParseObject object){
        return new Vendor(object.getString("name"), object.getDouble("latitude"),
                object.getDouble("longitude"));
    }

	@Override
	public void updatePubLocally(IPub pub) throws NoBackendAccessException,
			NotFoundInBackendException {
		ParseObject object = new ParseObject("Pub");

		try {
			object = ParseQuery.getQuery("Pub").get(pub.getID());
		} catch (ParseException e) {
			if (e.getCode() == ParseException.INVALID_KEY_NAME
					|| e.getCode() == ParseException.OBJECT_NOT_FOUND) {
				throw new NotFoundInBackendException(e.getMessage());
			} else {
				throw new NoBackendAccessException(e.getMessage());
			}
		}
		long lastUpdate = object.getLong("queueTimeLastUpdated");

		if (queueTimeIsRecentlyUpdated(lastUpdate)) {
			pub.setQueueTime(object.getInt("queueTime"));
		} else {
			pub.setQueueTime(0);
		}

		pub.setQueueTimeLastUpdatedTimestamp(lastUpdate);
	}

	// checks if the queue time was recently updated
	private static boolean queueTimeIsRecentlyUpdated(long queueTimeLastUpdatedTimestamp) {
		long epochTime = System.currentTimeMillis() / 1000;

        return (epochTime - queueTimeLastUpdatedTimestamp) < 3200;
	}

    private static List<ParseObject> getAllPubCrawls() throws NoBackendAccessException{
        List<Date> pubCrawls = new ArrayList<Date>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PubCrawl");
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
        }
        catch(com.parse.ParseException e1){
            throw new NoBackendAccessException(e1.getMessage());
        }
        return parseObjects;
    }

    public static PubCrawl getNextPubcrawl() throws NoBackendAccessException{
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PubCrawl");
        Calendar presentDate = Calendar.getInstance();
        Date date = presentDate.getTime();
        query.whereGreaterThanOrEqualTo("date", date);
        query.orderByAscending("date");
        ParseObject object = null;
        try {
            object = query.getFirst();
        } catch (ParseException e) {
            throw new NoBackendAccessException(e.getMessage());
        }
        PubCrawl nextPubCrawl = new PubCrawl(object.getString("name"), object.getDate("date"),
                object.getString("textForTextView"));
        return nextPubCrawl;
    }

    public static boolean isPubCrawlActive() throws NoBackendAccessException{
        Calendar presentDate = Calendar.getInstance();
        TimeZone localTimeZone = presentDate.getTimeZone();
        Date pubCrawlDate;
        List<ParseObject> pubCrawls = getAllPubCrawls();
        for(ParseObject pubCrawl : pubCrawls) {
            pubCrawlDate = pubCrawl.getDate("date");
            if ((pubCrawlDate.getYear() + 1900 == presentDate.get(Calendar.YEAR) &&
                    pubCrawlDate.getMonth() == presentDate.get(Calendar.MONTH) &&
                    pubCrawlDate.getDay() == presentDate.get(Calendar.DAY_OF_WEEK) - 1 &&
                    presentDate.get(Calendar.HOUR_OF_DAY) >= 15) ||
                    (pubCrawlDate.getYear() + 1900 == presentDate.get(Calendar.YEAR) &&
                    pubCrawlDate.getMonth() == presentDate.get(Calendar.MONTH) &&
                    pubCrawlDate.getDay()+1 == presentDate.get(Calendar.DAY_OF_WEEK) - 1 &&
                    presentDate.get(Calendar.HOUR_OF_DAY) < 4) ) {
                return true;
            }
        }
        return false;
    }
}
