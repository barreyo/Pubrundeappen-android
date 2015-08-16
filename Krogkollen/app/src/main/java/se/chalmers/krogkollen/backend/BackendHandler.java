package se.chalmers.krogkollen.backend;

import java.util.List;

import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.vendor.IVendor;


/**
 * A singleton class that handles the current active backend.
 */
public class BackendHandler {
	private static BackendHandler	instance		= null;
	private static IBackend			backendInstance	= null;

	// Private constructor to prevent accessibility
	private BackendHandler() {}

	/**
	 * Returns the instance for this singleton
	 * 
	 * @return the instance
	 */
	public static BackendHandler getInstance() {
		if (instance == null) {
			instance = new BackendHandler();
		}
		return instance;
	}

	/**
	 * Set the current backend
	 * 
	 * @param backend
	 */
	public void setBackend(IBackend backend) {
		backendInstance = backend;
	}

	/**
	 * @return the current initialized backend
	 */
	public IBackend getBackend() {
		return backendInstance;
	}

	/**
	 * @return a list containing all pubs in the current backend
	 * @throws NoBackendAccessException
	 * @throws BackendNotInitializedException
	 */
	public List<IPub> getAllPubs() throws NoBackendAccessException, BackendNotInitializedException {
		this.checkBackendInstance();
		return backendInstance.getAllPubs();
	}

    /**
     *
     * @return a list containg all vendors in the current backend
     * @throws NoBackendAccessException
     * @throws BackendNotInitializedException
     */
    public List<IVendor> getAllVendors() throws NoBackendAccessException, BackendNotInitializedException {
        this.checkBackendInstance();
        return backendInstance.getAllVendors();
    }

	/**
	 * @param pub
	 * @return the current queue time for the specified pub
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 * @throws BackendNotInitializedException
	 */
	public int getQueueTime(IPub pub) throws NoBackendAccessException, NotFoundInBackendException, BackendNotInitializedException {
		this.checkBackendInstance();
		return !pub.isOpen() ? 0 : backendInstance.getQueueTime(pub);
	}

	/**
	 * 
	 * @param id the pub ID
	 * @return the IPub object for the specified ID
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 * @throws BackendNotInitializedException
	 */
	public IPub getPubFromID(String id) throws NoBackendAccessException, NotFoundInBackendException, BackendNotInitializedException {
		this.checkBackendInstance();
		return backendInstance.getPubFromID(id);
	}


	/**
	 * 
	 * @param pub
	 * @return the timestamp for when the specified pub was last updated in the current backend
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 * @throws BackendNotInitializedException
	 */
	public long getLatestUpdatedTimestamp(IPub pub) throws NoBackendAccessException, NotFoundInBackendException, BackendNotInitializedException {
		this.checkBackendInstance();
		return backendInstance.getLatestUpdatedTimestamp(pub);
	}

	/**
	 * Updates queue time and ratings in the specified IPub object to match the fields in the
	 * current backend
	 * 
	 * @param pub
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 * @throws BackendNotInitializedException
	 */
	public void updatePubLocally(IPub pub) throws NoBackendAccessException, NotFoundInBackendException, BackendNotInitializedException {
		this.checkBackendInstance();
		backendInstance.updatePubLocally(pub);
	}

	// Check if there is a backend
	private void checkBackendInstance() throws BackendNotInitializedException {
		if (backendInstance == null) {
			instance = new BackendHandler();
		}
	}
}
