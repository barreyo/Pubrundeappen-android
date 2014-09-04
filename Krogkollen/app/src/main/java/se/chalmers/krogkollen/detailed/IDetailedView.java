package se.chalmers.krogkollen.detailed;

import com.google.android.gms.maps.model.LatLng;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.pub.IPub;

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
 * Interface for a DetailedView
 * 
 * @author Oskar Karrman
 * 
 */
public interface IDetailedView extends IView {

	/**
	 * Updates all text field in the view
	 * 
	 * @param pubName the pub name
	 * @param description the pub description
	 * @param openingHours the pubs opening hours
	 */
	public void updateText(String pubName, String description, String openingHours, String lastUpdated);

	/**
	 * Updates the queue indicator on the detailed view
	 * 
	 * @param queueTime the queue time used to update
	 */
	public void updateQueueIndicator(int queueTime);
}
