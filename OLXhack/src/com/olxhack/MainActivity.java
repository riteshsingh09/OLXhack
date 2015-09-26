package com.olxhack;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.olxhack.adapter.OfflineAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener{
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	private RecyclerView mRecyclerView;
	private OfflineAdapter mOffileAdapter;
	private ImageView postAdd;
	private GoogleApiClient mGoogleApiClient;
	private Location mCurrentLocation;
	private String city;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mOffileAdapter= new OfflineAdapter(getApplicationContext());
		mRecyclerView.setAdapter(mOffileAdapter );
		findViewById(R.id.postAdd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(getApplicationContext(), PostAdsActivity.class);
				intent.putExtra("mCurrentLocation","mCurrentLocation" );

				startActivity(intent);
			}
		});

		
		if (checkPlayServices()) {
			createConnection();
			buildGoogleApiClient();
			if(!LocationUtils.isAnyProviderEnabled(this)){
				buildLocationSettingsRequest();
			}
		}
	}


	private void requestForLocation(){
		if(mGoogleApiClient!=null){
			//findViewById(R.id.location).setVisibility(View.VISIBLE);
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, createLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY), (LocationListener) this);

			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, createLocationRequest(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY), (LocationListener) this);

		}else{
			buildGoogleApiClient();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		try{
			if(mGoogleApiClient!=null)
				mGoogleApiClient.connect();

		}catch(Exception ex){

		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized void onLocationChanged(Location arg0) {

		mCurrentLocation =arg0;
		city=	getCity();
		stopLocationUpdates();

	}

	protected void stopLocationUpdates() {
		if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
	}

	@Override
	public void onConnected(Bundle arg0) {

		if(LocationUtils.isAnyProviderEnabled(getApplicationContext())){
			if (mCurrentLocation == null) {
				mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

			}
			requestForLocation();

		}else{
			buildLocationSettingsRequest();
		}


	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				finish();
			}
			return false;
		}
		return true;
	}




	protected void buildLocationSettingsRequest() {
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
		.addLocationRequest(createLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY))
		.addLocationRequest(createLocationRequest(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY))
		.setAlwaysShow(true);
		PendingResult<LocationSettingsResult> result =
				LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

		result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
			@Override
			public void onResult(LocationSettingsResult result) {
				final Status status = result.getStatus();
				switch (status.getStatusCode()) {
				case LocationSettingsStatusCodes.SUCCESS:
					createConnection();
					buildGoogleApiClient();
					break;

				case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

					break;
				}
			}
		});
	}

	protected LocationRequest createLocationRequest(int type) {
		LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(1000);
		mLocationRequest.setFastestInterval(1000);
		mLocationRequest.setPriority(type);
		return mLocationRequest;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PLAY_SERVICES_RESOLUTION_REQUEST:
			switch (resultCode) {

			case Activity.RESULT_OK:
				createConnection();
				buildGoogleApiClient();
				break;
			case Activity.RESULT_CANCELED:
				if(LocationUtils.isAnyProviderEnabled(getApplicationContext())){
					createConnection();
					buildGoogleApiClient();
				}

				break;
			}
			break;
		}
	}

	private  void createConnection(){
		if(mGoogleApiClient==null){
			mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks((ConnectionCallbacks)this)
			.addOnConnectionFailedListener((OnConnectionFailedListener)this)
			.addApi(LocationServices.API)
			.build();
		}
	}

	protected  void buildGoogleApiClient() {
		if(null!=mGoogleApiClient){

			if(mGoogleApiClient.isConnected()){
				requestForLocation();
			}else{
				mGoogleApiClient.connect();
			}
		}

	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		stopLocationUpdates();


	}
	
	private String getCity(){
		String city = null;
		Geocoder geocoder =
				new Geocoder(getApplicationContext(), Locale.getDefault());
	
		List<Address> addresses = null;
		try {
			/*
			 * Return 1 address.
			 */
			addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude(), 5);

			/*addresses = geocoder.getFromLocation(19.1736815,
					72.8591251, 1);*/
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalArgumentException e2) {
			return null;
		}
		// If the reverse geocode returned an address

		if (addresses != null && addresses.size() > 0) {
			// Get the first address
			city=addresses.get(0).getLocality();
	}
		return city;
}
}
