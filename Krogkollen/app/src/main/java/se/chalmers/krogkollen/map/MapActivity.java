package se.chalmers.krogkollen.map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.backend.ParseBackend;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.PubUtilities;
import se.chalmers.krogkollen.utils.Constants;
import se.chalmers.krogkollen.vendor.IVendor;
import se.chalmers.krogkollen.vendor.VendorUtilities;

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
 * The standard implementation of IMapView.
 * 
 * This is a normal map with the user marked on the map, and with a list of pubs marked on the map.
 */
public class MapActivity extends Activity implements IMapView {
	private MapPresenter	presenter;
	private Marker          userMarker;
	private ProgressDialog	progressDialog, progressDialog2, progressDialog3;
    private GoogleMap       googleMap;
    private List<Marker>    pubMarkers;
    private DisplayMetrics  displayMetrics;
    public static boolean   firstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

        // Initiate map
        pubMarkers = new ArrayList<Marker>();
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        displayMetrics = getResources().getDisplayMetrics();

        try {
            this.addPubMarkers(PubUtilities.getInstance().getPubList());
        } catch (NoBackendAccessException e) {
            this.showErrorMessage(this.getString(R.string.error_no_backend_access));
        } catch (NotFoundInBackendException e) {
            this.showErrorMessage(this.getString(R.string.error_no_backend_item));
        }
        try {
            if(!ParseBackend.isPubCrawlActive()) {
                try {
                    this.addVendorMarkers(VendorUtilities.getInstance().getVendorList());
                } catch (NoBackendAccessException e) {
                    this.showErrorMessage(this.getString(R.string.error_no_backend_access));
                } catch (NotFoundInBackendException e) {
                    this.showErrorMessage(this.getString(R.string.error_no_backend_item));
                }
            }
        } catch (NoBackendAccessException e) {
            e.printStackTrace();
        }

        // Create a presenter for this view.
		presenter = new MapPresenter();
		presenter.setView(this);

		googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {

				// Move camera to the clicked marker.
				moveCameraToPosition(marker.getPosition(), googleMap.getCameraPosition().zoom);

				if (marker.getTitle().equalsIgnoreCase(getString(R.string.map_user_name))) {
					return true;        // Suppress user marker click
				} else {
					// Open detailed view.
					presenter.pubMarkerClicked(marker.getTitle());
				}
				return true; // Suppress default behavior; move camera and open info window.
			}
		});

		// Remove the default logo icon and add our list icon.
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.list_icon);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

    // Add markers for all pubs on the server to the map.
    private void addPubMarkers(List<IPub> pubs) throws NoBackendAccessException,
            NotFoundInBackendException {
        IPub[] pubArray = new IPub[pubs.size()];

        for (int i = 0; i < pubs.size(); i++) {
            pubArray[i] = pubs.get(i);
        }
        new CreateMarkerTask().execute(pubArray);
    }

    // Add markers for all vendors on the server to the map.
    private void addVendorMarkers(List<IVendor> vendors) throws NoBackendAccessException,
            NotFoundInBackendException {
        IVendor[] vendorArray = new IVendor[vendors.size()];

        for (int i = 0; i < vendors.size(); i++) {
            vendorArray[i] = vendors.get(i);
        }
        new CreateVendorMarkerTask().execute(vendorArray);
    }



    /**
     * Removes all pub markers, loads and adds them again.
     */
    public synchronized void refreshPubMarkers(HashMap<IPub, Integer> changedPubsHash)
            throws NoBackendAccessException, NotFoundInBackendException {

        List<IPub> changedPubs = new ArrayList<IPub>();

        for (Map.Entry<IPub, Integer> entry : changedPubsHash.entrySet()) {

            IPub pub = entry.getKey();
            Integer integer = entry.getValue();

            // Find added and changed pubs
            if (integer == MapPresenter.PUB_CHANGED || integer == MapPresenter.PUB_ADDED) {
                changedPubs.add(pub);
            }

            // Just remove pub markers for pubs that currently got a marker on the map.
            for (Marker marker : pubMarkers) {
                if (pub.getID().equalsIgnoreCase(marker.getId())) {
                    marker.remove();
                    pubMarkers.remove(marker);
                }
            }
        }
        this.addPubMarkers(changedPubs);
    }


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);

		return true;
	}

	@Override
	public void moveCameraToPosition(LatLng pos, float zoom) {
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pos, zoom, 0, 0)));
	}

	@Override
	public void addUserMarker(LatLng latLng) {
		userMarker = googleMap.addMarker(new MarkerOptions()
				.position(latLng)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
				.title(getString(R.string.map_user_name)));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		presenter.onActionBarClicked(menuItem);
		return true;
	}

	@Override
	public void animateUserMarker(final LatLng toPosition) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = googleMap.getProjection();
		Point startPoint = proj.toScreenLocation(userMarker.getPosition());
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 500;

		final LinearInterpolator interpolator = new LinearInterpolator();

		handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                userMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
	}

	@Override
	public SharedPreferences getPreferences() {
		return this.getPreferences(Context.MODE_PRIVATE);
	}

	@Override
	public void showProgressDialog() {
		progressDialog = ProgressDialog.show(MapActivity.this, "",
				getString(R.string.map_updating), false, false);
        System.out.println("SHOW DIALOG");
    }

	@Override
	public void hideProgressDialog() {
		progressDialog.hide();
        System.out.println("HIDE DIALOG");
    }

	@Override
	public void onPause() {
		super.onPause();
		presenter.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		presenter.onResume();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	@Override
	public void navigate(Class<?> destination) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.MAP_ACTIVITY_NAME);
		startActivity(intent);
	}

	@Override
	public void navigate(Class<?> destination, Bundle extras) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.MARKER_PUB_ID, extras.getString(Constants.MAP_PRESENTER_KEY));
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.MAP_ACTIVITY_NAME);
		startActivity(intent);
	}

	@Override
	public void showErrorMessage(String message) {
		CharSequence text = message;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

	@Override
	public void showAlertDialog(final String msg, final boolean showCheckbox) {
		// Create most of the dialog that will be shown if either wifi or gps are disabled.
		Builder builder = new Builder(this);
		final ArrayList<Integer> selected = new ArrayList<Integer>();

		if (showCheckbox) {
			// Check box, making it possible to chose not to show this dialog again.
			View checkBoxView = View.inflate(this, R.layout.checkbox, null);
			CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						selected.add(0);
					} else if (selected.size() != 0) {
						selected.clear();
					}
				}
			});
			checkBox.setText(this.getString(R.string.alert_dialog_dont_show_again));
			builder.setView(checkBoxView);
		}
		builder.setTitle(R.string.alert_dialog_title);
		builder.setMessage(msg);

		// Set listeners to the buttons in the dialog and chose appropriate consequences for clicks.
		builder.setPositiveButton(R.string.alert_dialog_activate,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (showCheckbox) {
							presenter.saveOption(!selected.isEmpty());
						}
						// Send user to location settings on the phone.
						Intent intent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		builder.setNegativeButton(R.string.alert_dialog_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (showCheckbox) {
							presenter.saveOption(!selected.isEmpty());
						}
					}
				});

		builder.show();
	}

	@Override
	public Resources getResources() {
		return super.getResources();
	}

	@Override
	public void onSearch() {
		this.onSearchRequested();
	}


    // Used to direct workload when creating markers to another thread.
    private class CreateMarkerTask extends AsyncTask<IPub, Void, List<MarkerOptions>> {

        @Override
        protected void onPreExecute()
        {
            progressDialog2 = ProgressDialog.show(MapActivity.this, "",
                    MapActivity.this.getResources().getString(R.string.loading_pubs), false, false);
        }

        @Override
        protected List<MarkerOptions> doInBackground(IPub... pubs) {

            List<MarkerOptions> listMarkerOptions = new ArrayList<MarkerOptions>();

            // Create options for all the markers
            for (int i = 0; i < pubs.length; i++) {
                IPub pub = pubs[i];
                listMarkerOptions.add(MarkerOptionsFactory.createMarkerOptions(displayMetrics, MapActivity.this.getResources(), pub));
            }
            return listMarkerOptions;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptions) {

            // When settings are finished add all the markers to the map
            // This is a GUI process and needs to be run here on the GUI thread.
            for (MarkerOptions markerOption : markerOptions) {
                pubMarkers.add(googleMap.addMarker(markerOption));
            }
            progressDialog2.hide();
        }
    }

    // Used to direct workload when creating markers to another thread.
    private class CreateVendorMarkerTask extends AsyncTask<IVendor, Void, List<MarkerOptions>> {

        @Override
        protected void onPreExecute()
        {
            progressDialog3 = ProgressDialog.show(MapActivity.this, "",
                    MapActivity.this.getResources().getString(R.string.loading_vendors), false, false);
        }

        @Override
        protected List<MarkerOptions> doInBackground(IVendor... vendors) {

            List<MarkerOptions> listMarkerOptions = new ArrayList<MarkerOptions>();

            // Create options for all the markers
            for (int i = 0; i < vendors.length; i++) {
                IVendor vendor = vendors[i];
                MarkerOptions options = new MarkerOptions();
                Bitmap bitmap = BitmapFactory.decodeResource(MapActivity.this.getResources(), Constants.MARKER_VENDOR);
                Bitmap bitmapResult = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmap.recycle();
                options.position(new LatLng(vendor.getLatitude(), vendor.getLongitude()));
                options.icon(BitmapDescriptorFactory.fromBitmap(bitmapResult));
                options.anchor(0.5f,0.3f);
                listMarkerOptions.add(options);
            }
            return listMarkerOptions;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptions) {

            // When settings are finished add all the markers to the map
            // This is a GUI process and needs to be run here on the GUI thread.
            for (MarkerOptions markerOption : markerOptions) {
                googleMap.addMarker(markerOption);
            }
            progressDialog3.hide();
        }
    }
}
