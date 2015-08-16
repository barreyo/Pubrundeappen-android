package se.chalmers.krogkollen;

import android.os.Bundle;

/**
 * Interface for a View class in the MVP design pattern
 * 
 * @author Oskar Karrman
 */
public interface IView {

	/**
	 * Navigates to another view
	 * 
	 * @param destination the view to navigate to
	 */
	public abstract void navigate(Class<?> destination);

	/**
	 * Navigates to another view
	 * 
	 * @param destination the view to navigate to
	 * @param extras
	 */
	public abstract void navigate(Class<?> destination, Bundle extras);

	/**
	 * Shows an error message
	 * 
	 * @param message the message to show
	 */
	public abstract void showErrorMessage(String message);
}
