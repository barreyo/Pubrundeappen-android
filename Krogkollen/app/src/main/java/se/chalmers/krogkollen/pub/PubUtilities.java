package se.chalmers.krogkollen.pub;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.util.LinkedList;
import java.util.List;

import se.chalmers.krogkollen.backend.BackendHandler;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;

/**
 * A singleton holding a list of all the pubs, and is used to load data from the server.
 * 
 * @author Albin Garpetun
 */
public class PubUtilities {

	private List<IPub>			pubList		= new LinkedList<IPub>();
	private static PubUtilities	instance	= null;

	private PubUtilities() {
		// Exists only to defeat instantiation.
	}

	/**
	 * Creates an instance of this object if there is none, otherwise it simply returns the old one.
	 * 
	 * @return The instance of the object.
	 */
	public static PubUtilities getInstance() {
		if (instance == null) {
			instance = new PubUtilities();
		}
		return instance;
	}

	/**
	 * Loads the pubs from the server and puts them in the list of pubs.
	 * 
	 * @throws BackendNotInitializedException
	 * @throws NoBackendAccessException
	 */
	public synchronized void loadPubList() throws NoBackendAccessException, BackendNotInitializedException {
		pubList = BackendHandler.getInstance().getAllPubs();
	}

	/**
	 * Returns the list of all pubs.
	 * 
	 * @return The list of all pubs.
	 */
	public synchronized List<IPub> getPubList() {
		return pubList;
	}

	/**
	 * Refreshes the list of pubs from the server.
	 * 
	 * @throws BackendNotInitializedException
	 * @throws NoBackendAccessException
	 */
	public synchronized void refreshPubList() throws NoBackendAccessException, BackendNotInitializedException {
        loadPubList();
	}

	/**
	 * Returns a the pub connected with the ID given.
	 * 
	 * @param id The ID of the pub to return
	 * @return The pub according to the ID given
	 */
	public IPub getPub(String id) {
		for (IPub pub : pubList) {
			if (pub.getID().equalsIgnoreCase(id)) {
				return pub;
			}
		}
		throw new Resources.NotFoundException("The ID does not match with any pub");
	}
}
