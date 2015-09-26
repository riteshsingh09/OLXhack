package com.olxhack;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;



/**
 * This class is only to hold location related methods. <br/>
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> <br/>
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 */
public class LocationUtils {

	private static final String LOG_TAG = LocationUtils.class.getSimpleName();

	public static final long RELIABLE_LOCATION_AGE_MINUTES = 5;

	/**
	 * @param pContext
	 * @return true if GPS_PROVIDER is available on device
	 */
	public static boolean hasNetworkLocationFeature(Context pContext) {
		return pContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_LOCATION_NETWORK);
	}

	/**
	 * @param pContext
	 * @return true if GPS_PROVIDER is available on device
	 */
	public static boolean hasGpsLocationFeature(Context pContext) {
		return pContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_LOCATION_GPS);
	}

	/**
	 * @param pContext
	 * @return true if NETWORK_PROVIDER is enabled on device
	 */
	public static boolean isNetworkProviderEnabled(Context pContext) {
		LocationManager locationManager = (LocationManager) pContext
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * @param pContext
	 * @return true if GPS_PROVIDER is enabled on device
	 */
	public static boolean isGpsProviderEnabled(Context pContext) {
		LocationManager locationManager = (LocationManager) pContext
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * @param pContext
	 * @return true if any provider is available and enabled on device
	 */
	public static boolean isAnyProviderEnabled(Context pContext) {
		if (hasNetworkLocationFeature(pContext)
				&& isNetworkProviderEnabled(pContext)) {
			return true;
		}
		if (hasGpsLocationFeature(pContext) && isGpsProviderEnabled(pContext)) {
			return true;
		}
		return false;
	}

	/**
	 * Also shows the appropriate dialog
	 * 
	 * @param pActivity
	 * @param pDialogClickListener
	 * 
	 * @return true if location dialog is not shown
	 */
	public static boolean checkLocationSettings(Activity pActivity,
			OnClickListener pDialogClickListener) {
		String message = null;
		if (LocationUtils.hasGpsLocationFeature(pActivity)
				&& !LocationUtils.isGpsProviderEnabled(pActivity)) {
			message = "GPS is turned OFF on your device, do you want to turn it ON?";
		} else if (LocationUtils.hasNetworkLocationFeature(pActivity)
				&& !LocationUtils.isNetworkProviderEnabled(pActivity)) {
			message = "Non-GPS Location is turned OFF on your device, do you want to turn it ON?";
		}
		if (message == null) {
			return true;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(pActivity);
		builder.setMessage(message);
		builder.setTitle("Info");
		builder.setPositiveButton("Yes", pDialogClickListener);
		builder.setNegativeButton("No Thanks", pDialogClickListener);
		builder.show();
		return false;
	}

	/**
	 * Get the most recent location of the user. <br/>
	 * 
	 * @note Requires ACCESS_FINE_LOCATION permission
	 * 
	 * @note use {@link LocationClient} insteat of this method.
	 * 
	 * @param pContext
	 */
	@Deprecated
	public static Location getLastKnownLocation(Context pContext) {
		Location mostRecentLocation = null;
		try {
			LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
			List<String> providersList = locationManager.getAllProviders();
			if (providersList == null || providersList.isEmpty()) {
				return mostRecentLocation;
			}
			for (String provider : providersList) {
				Location location = locationManager.getLastKnownLocation(provider);
				if (location == null) {
					continue;
				}
				if (mostRecentLocation == null) {
					mostRecentLocation = location;
				} else if (mostRecentLocation.getTime() < location.getTime()) {
					mostRecentLocation = location;
				} else if (mostRecentLocation.getTime() == location.getTime()) {
					if (mostRecentLocation.getAccuracy() > location.getAccuracy()) {
						mostRecentLocation = location;
					}
				}
			}
		} catch (Exception e) {
		}
		return mostRecentLocation;
	}

	/**
	 * @param pContext
	 * @return user's Address based on last known location
	 */
	public static Address getAddressByGeoCoder(Context pContext, Location pLocation) {
		try {
			Geocoder geocoder = new Geocoder(pContext);
			List<Address> addressList = geocoder.getFromLocation(pLocation.getLatitude(), pLocation.getLongitude(), 1);
			return addressList.get(0);
		} catch (Exception e) {
			Log.e(LOG_TAG, "getCityByGeoCoder(): " + e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * @param startLat
	 * @param startLong
	 * @param endLat
	 * @param endLong
	 * @return distance in meters or -1 if method fails
	 */
	public static float getDistance(double startLat, double startLong, double endLat, double endLong) {
		try {
			float[] results = new float[3];
			Location.distanceBetween(startLat, startLong, endLat, endLong, results);
			return results[0];
		} catch (Exception e) {
			Log.e(LOG_TAG, "getDistance() " + e.getLocalizedMessage());
			return -1;
		}
	}

	/**
	 * @param pLocation
	 * @return
	 */
	public static boolean isRecentLocation(Location pLocation) {
		return isRecentLocation(pLocation, RELIABLE_LOCATION_AGE_MINUTES);
	}

	/**
	 * @param pLocation
	 * @param pReliableAgeMinutes
	 * @return
	 */
	public static boolean isRecentLocation(Location pLocation, long pReliableAgeMinutes) {
		long locationAgeMillis = getLocationAge(pLocation);
		return locationAgeMillis < pReliableAgeMinutes * DateUtils.MINUTE_IN_MILLIS;
	}

	/**
	 * @param pLocation
	 * @return location age in milliseconds
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static long getLocationAge(Location pLocation) {
		long locationAgeMillis;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			long bootNanos = SystemClock.elapsedRealtimeNanos();
			long locationNanos = pLocation.getElapsedRealtimeNanos();
			locationAgeMillis = (bootNanos - locationNanos) / 1000 / 1000 / DateUtils.MINUTE_IN_MILLIS;
		} else {
			long currentMillis = System.currentTimeMillis();
			long locationMillis = pLocation.getTime();
			locationAgeMillis = (currentMillis - locationMillis) / DateUtils.MINUTE_IN_MILLIS;
		}
		return locationAgeMillis;
	}

	
}