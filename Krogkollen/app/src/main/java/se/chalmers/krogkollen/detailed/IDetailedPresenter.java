package se.chalmers.krogkollen.detailed;

import android.view.View.OnClickListener;

import se.chalmers.krogkollen.IPresenter;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;

/**
 * Interface for a DetailedPresenter
 * 
 * @author Oskar Karrman
 * 
 */
public interface IDetailedPresenter extends IPresenter {

	/**
	 * Sets the pub which the presenter is connected to.
	 * 
	 * @param pubID the pub
	 * @throws NotFoundInBackendException
	 * @throws NoBackendAccessException
	 * @throws BackendNotInitializedException
	 */
	public void setPub(String pubID) throws NotFoundInBackendException, NoBackendAccessException, BackendNotInitializedException;

	/**
	 * Gets new information from the server
	 * 
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 * @throws BackendNotInitializedException
	 */
	public void updateInfo() throws NoBackendAccessException, NotFoundInBackendException, BackendNotInitializedException;
}
