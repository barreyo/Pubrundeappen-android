package se.chalmers.krogkollen.map;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.detailed.DetailedActivity;
import se.chalmers.krogkollen.help.HelpActivity;
import se.chalmers.krogkollen.list.ListActivity;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.PubUtilities;
import se.chalmers.krogkollen.utils.Constants;
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
 * A Map presenter, doing the logic for the map view
 *
 * @author Oskar Karrman
 *
 */
public class MapPresenter implements IMapPresenter {

    public static final int		PUB_REMOVED			= -1;
    public static final int		PUB_CHANGED			= 0;
    public static final int		PUB_ADDED			= 1;

    public static final int		USER_ZOOM			= 16;
    public static final int		DEFAULT_ZOOM		= 12;

    /**
     * Default location for map (SWEDEN).
     */
    public static final LatLng DEFAULT_LOCATION	= new LatLng(57.70887, 11.974613);

    private IMapView			mapView;
    private UserLocation		userLocation;
    private Resources			resources;

    private SharedPreferences	sharedPref;
    private boolean				haveShownDialog		= false;
    private boolean				dontShowDialogAgain;

    @Override
    public void setView(IView view) {
        mapView = (IMapView) view;
        this.resources = this.mapView.getResources();
        this.sharedPref = mapView.getPreferences();
        this.dontShowDialogAgain = sharedPref.getBoolean(
                resources.getString(R.string.dont_show_again_key),
                resources.getBoolean(R.bool.dont_show_again_default));
        this.userLocation = UserLocation.getInstance();
        this.userLocation.addObserver(this);
        this.userLocation.startTrackingUser();

        // Use a default zoom if no position is found.
        if (userLocation.getCurrentLatLng() == null) {
            mapView.moveCameraToPosition(DEFAULT_LOCATION, DEFAULT_ZOOM);
        }
    }

    @Override
    public void onActionBarClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_info:

                if (!MapActivity.updating) {
                    new RefreshTask().execute();
                }
                break;
            case R.id.search:
                mapView.onSearch();
                break;
            case R.id.go_to_my_location:
                // If no position has been found, show corresponding dialog, otherwise move the
                // camera to the users location.
                if (userLocation.getCurrentLatLng() == null) {
                    showDialog(userLocation.getProviderStatus(), false);
                } else {
                    mapView.moveCameraToPosition(userLocation.getCurrentLatLng(), USER_ZOOM);
                }
                break;
            case R.id.action_help:
                mapView.navigate(HelpActivity.class);
                break;
            case android.R.id.home:
                mapView.navigate(ListActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        this.userLocation.onPause();
    }

    @Override
    public void onResume() {
        if (!MapActivity.firstLoad && !MapActivity.updating) {
            new RefreshTask().execute();
        }
        MapActivity.firstLoad = false;
        this.userLocation.onResume();
    }

    @Override
    public void pubMarkerClicked(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MAP_PRESENTER_KEY, title);
        mapView.navigate(DetailedActivity.class, bundle);
    }

    @Override
    public void update(Status status) {
        // Update accordingly to what has happened in user location.
        if (status == Status.FIRST_LOCATION) {
            // User location has received a first location so a user marker is added and
            // map is centered on user.
            this.mapView.addUserMarker(this.userLocation.getCurrentLatLng());
            this.mapView.moveCameraToPosition(userLocation.getCurrentLatLng(), USER_ZOOM);
        } else if (status == Status.NORMAL_UPDATE) {
            // The location has been updated, move the marker accordingly.
            this.mapView.animateUserMarker(this.userLocation.getCurrentLatLng());
        } else if ((status == Status.GPS_DISABLED || status == Status.NET_DISABLED || status == Status.ALL_DISABLED)
                &&
                !(this.haveShownDialog || this.dontShowDialogAgain)) {
            showDialog(status, true);
        }
    }

    // Showing the correct dialog for GPS and NET status.
    private void showDialog(Status status, boolean showCheckbox) {
        String baseMessage = resources.getString(R.string.alert_dialog_base_message);
        String additionalMessage = status == Status.NET_DISABLED || status == Status.ALL_DISABLED ?
                resources.getString(R.string.alert_dialog_net) : "";

        additionalMessage += status == Status.GPS_DISABLED || status == Status.ALL_DISABLED ?
                resources.getString(R.string.alert_dialog_gps) : "";

        this.mapView.showAlertDialog(baseMessage + additionalMessage, showCheckbox);
        this.haveShownDialog = true;
    }

    /**
     * Save information in the key values of Android.
     *
     * @param dontShowAgain true, don't show dialogs again; false, show dialogs again.
     */
    public void saveOption(boolean dontShowAgain) {
        // Save the don't show again option.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(resources.getString(R.string.dont_show_again_key), dontShowAgain);
        editor.commit();
    }

    // Send the heavy work of recreating the markers to another thread.
    // Only changed pubs/markers will be altered.
    private class RefreshTask extends AsyncTask<Void, Void, Void>
    {
        // Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            mapView.showProgressDialog();
            System.out.println("--- UPDATE START ---");
        }

        // The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            // Refresh with new pubs from the server.
            try {
                PubUtilities.getInstance().refreshPubList();
            } catch (NoBackendAccessException e) {
                System.out.println("REFRESH ERROR");
            } catch (BackendNotInitializedException e) {
                System.out.println("REFRESH ERROR");
            }

            final List<IPub> refreshedList = PubUtilities.getInstance().getPubList();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        mapView.refreshPubMarkers(refreshedList);
                    } catch (NoBackendAccessException e) {
                        mapView.showErrorMessage(mapView.getResources().getString(R.string.error_no_backend_access));
                    } catch (NotFoundInBackendException e) {
                        mapView.showErrorMessage(mapView.getResources().getString(R.string.error_no_backend_item));
                    }
                }
            });

            return null; // Nothing to return to the post execute.
        }

        // After executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            System.out.println("--- UPDATE FINISHED ---");
            mapView.hideProgressDialog();
        }
    }
}
