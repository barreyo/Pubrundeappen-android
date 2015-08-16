package se.chalmers.krogkollen.detailed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.BackendHandler;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.map.UserLocation;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.PubUtilities;
import se.chalmers.krogkollen.utils.Preferences;


/**
 * A presenter class for the detailed view of a pub
 */
public class DetailedPresenter implements IDetailedPresenter {

	/** The view connected with the Presenter */
	private DetailedActivity	view;

	/** The pub that the Presenter holds */
	private IPub pub;

	@Override
	public void setView(IView view) {
		this.view = (DetailedActivity) view;
	}

	@Override
	public void setPub(String pubID) throws NoBackendAccessException, NotFoundInBackendException,
            BackendNotInitializedException {
		pub = PubUtilities.getInstance().getPub(pubID);
	}

	/**
	 * Updates the info of a pub
	 * 
	 * @throws NoBackendAccessException
	 * @throws NotFoundInBackendException
	 */
	public void updateInfo() throws NoBackendAccessException, NotFoundInBackendException {
		new UpdateTask().execute();
	}

    // Updates the info about the pub in another thread
	private class UpdateTask extends AsyncTask<Void, Void, Void> {
		protected void onPreExecute() {
			view.showProgressDialog();
		}

		protected Void doInBackground(Void... voids) {
			try {
				BackendHandler.getInstance().updatePubLocally(pub);
			} catch (NoBackendAccessException e) {
				view.showErrorMessage(view.getString(R.string.error_no_backend_access));
			} catch (NotFoundInBackendException e) {
				view.showErrorMessage(view.getString(R.string.error_no_backend_item));
			} catch (BackendNotInitializedException e) {
				view.showErrorMessage(view.getString(R.string.error_backend_not_initialized));
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			view.hideProgressDialog();
			updateMain();
		}
	}

	// Sends the new information to the view for displaying.
	private void updateMain() {
		view.updateQueueIndicator(pub.getQueueTime());
	}
}
