package se.chalmers.krogkollen.map;

import android.view.MenuItem;

import se.chalmers.krogkollen.IPresenter;
import se.chalmers.krogkollen.utils.IObserver;

/**
 * Interface for a MapPresenter
 * 
 * @author Oskar Karrman
 * 
 */
public interface IMapPresenter extends IPresenter, IObserver {

	/**
	 * Determine what will happen when an action bar item is clicked.
	 * 
	 * @param item the menu item that was clicked.
	 */
	public void onActionBarClicked(MenuItem item);

	/**
	 * When the corresponding activity is paused this method gets called.
	 */
	public void onPause();

	/**
	 * When the corresponding activity is resumed this method gets called.
	 */
	public void onResume();

	/**
	 * Indicates that a pub marker has been clicked
	 * 
	 * @param id the id of the clicked pub
	 */
	void pubMarkerClicked(String id);
}
