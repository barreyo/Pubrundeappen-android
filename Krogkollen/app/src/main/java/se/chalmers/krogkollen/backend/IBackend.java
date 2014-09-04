package se.chalmers.krogkollen.backend;

import android.text.format.Time;

import java.util.Date;
import java.util.List;

import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.vendor.IVendor;



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
 * Interface describing methods for a backend
 * 
 * @author Oskar Karrman
 * 
 */
public interface IBackend {

	/**
	 * @return a list of all pubs contained in the backend
	 * @throws NoBackendAccessException
	 */
	public List<IPub> getAllPubs() throws NoBackendAccessException;

    /**
     * @return a list of all vendors contained in the backend
     * @throws NoBackendAccessException
     */
    public List<IVendor> getAllVendors() throws NoBackendAccessException;

	/**
	 * Returns the queue time for the specified IPub object
	 * 
	 * @param pub the pub which queue time should be fetched from
	 * @return the current queue time for the specified pub
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 */
	public int getQueueTime(IPub pub) throws NoBackendAccessException, NotFoundInBackendException;

	/**
	 * Returns an IPub object with fields matching a pub in the backend with the specified ID
	 * 
	 * @param id the ID of the pub in the backend
	 * @return the pub matching the ID
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 */
	public IPub getPubFromID(String id) throws NoBackendAccessException, NotFoundInBackendException;


	/**
	 * Returns the timestamp in seconds from The Epoch when the queue time was last updated
	 * 
	 * @param pub the requested pub
	 * @return the timestamp
	 * @throws NotFoundInBackendException
	 * @throws NoBackendAccessException
	 */
	public long getLatestUpdatedTimestamp(IPub pub) throws NotFoundInBackendException, NoBackendAccessException;


    public Date getLastUpdated(IPub pub) throws NoBackendAccessException, NotFoundInBackendException;

	/**
	 * Updates the pub object with the current info about the queue time, queue timestamp, positive
	 * and negative rating
	 * 
	 * @param pub the pub for which values should be updated
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 */
	public void updatePubLocally(IPub pub) throws NoBackendAccessException, NotFoundInBackendException;
}