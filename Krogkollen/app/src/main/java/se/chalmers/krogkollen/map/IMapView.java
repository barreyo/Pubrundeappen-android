package se.chalmers.krogkollen.map;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.search.ICanSearch;

/**
 * Interface for a MapView object
 * 
 * @author Oskar Karrman
 * 
 */
public interface IMapView extends IView, ICanSearch {

	/**
	 * Adds the GPS-location of the phone as a marker in the map view.
	 * 
	 * @param latLng The location to be added.
	 */
	public void addUserMarker(LatLng latLng);

	/**
	 * @return resources.
	 */
	public Resources getResources();

	/**
	 * Shows an alert dialog with the given message and the option of adding a checkbox.
	 * 
	 * @param msg message to be shown.
	 * @param showCheckbox show a checkbox or not? :P
	 */
	public void showAlertDialog(final String msg, final boolean showCheckbox);

	/**
	 * Move the camera to the given position and zoom the given amount.
	 * 
	 * @param pos the position to move to.
	 * @param zoom zoom level.
	 */
	public void moveCameraToPosition(LatLng pos, float zoom);

	/**
	 * ** Method written by Google, found on stackoverflow.com ** ** **
	 * http://stackoverflow.com/questions/13728041/move-markers-in-google-map-v2-android ** Moves
	 * the user marker smoothly to a new position.
	 * 
	 * @param toPosition position to animate to.
	 */
	public void animateUserMarker(final LatLng toPosition);

	/**
	 * @return shared preferences of the activity.
	 */
	public SharedPreferences getPreferences();

	/**
	 * Shows loading progress in a dialog.
	 */
	public void showProgressDialog();

	/**
	 * Hide the loading dialog if it exists.
	 */
	public void hideProgressDialog();

    public void refreshPubMarkers(List<IPub> pubs)
            throws NoBackendAccessException, NotFoundInBackendException;
}
