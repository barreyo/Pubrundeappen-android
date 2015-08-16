package se.chalmers.krogkollen.utils;

import se.chalmers.krogkollen.R;

/**
 * A class containing constant values
 * 
 * @author Jonathan Nilsfors
 * @author Oskar Karrman
 */
public class Constants {

    public static final String MAIN_ACTIVITY_NAME       = "MAIN_ACTIVITY";
    public static int			MAP_SHORT_QUEUE			= R.drawable.green_marker_bg;
	public static int			MAP_MEDIUM_QUEUE		= R.drawable.yellow_marker_bg;
	public static int			MAP_LONG_QUEUE			= R.drawable.red_marker_bg;
	public static int			MAP_NO_INFO_QUEUE		= R.drawable.gray_marker_bg;

	public static final String	ACTIVITY_FROM			= "FROM";

	public static final String	MAP_ACTIVITY_NAME		= "MapActivity";
	public static final String	HELP_ACTIVITY_NAME		= "HelpActivity";
	public static final String	LIST_ACTIVITY_NAME		= "ListActivity";
	public static final String	DETAILED_ACTIVITY_NAME	= "DetailedActivity";
	public static final String	SEARCH_ACTIVITY_NAME	= "SearchActivity";

	public static final String	SORT_MODE				= "SORT_MODE";

	public static final int		LIST_NUMBER_OF_TABS		= 2;

	/** Identifier for the intent used to start the activity for detailed view. */
	public static final String	MARKER_PUB_ID			= "se.chalmers.krogkollen.MARKER_PUB_ID";

	/** Key value used when sending intents */
	public static final String	MAP_PRESENTER_KEY		= "se.chalmers.krogkollen.MAP_PRESENTER_KEY";

    //Marker for vendor
    public static final int MARKER_VENDOR = R.drawable.vendor_marker;
}
