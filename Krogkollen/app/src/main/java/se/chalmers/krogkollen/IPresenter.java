package se.chalmers.krogkollen;

/**
 * Interface for a Presenter class in the MVP design pattern.
 * 
 * @author Oskar Karrman
 */
public interface IPresenter {

	/**
	 * Sets the active view of the Presenter
	 * 
	 * @param view the view to set as active
	 */
	public abstract void setView(IView view);
}
