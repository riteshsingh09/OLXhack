package com.olxhack;


import com.olxhack.offlinedata.OfflineData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class PostAdsActivity extends ActionBarActivity implements OnClickListener{

	private EditText  mAdDescription, mAdTitel,mUserName,mPrice;
	private ImageView imageview;
	private Spinner mSpinnerCity, mSpinnercat; 
	private Location mCurrentLocation ;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitypostads);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setCustomView(R.layout.actionbar_layout);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#2f2f2f")));
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.getCustomView().findViewById(R.id.actionbar_post).setOnClickListener(this);
		mSpinnerCity = (Spinner) findViewById(R.id.spinnerCity);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.city_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinnerCity.setAdapter(adapter);

		mSpinnercat = (Spinner) findViewById(R.id.catagory);
		// Create an ArrayAdapter using the string array and a default spinner layout

		ArrayAdapter<CharSequence> adapterCat = ArrayAdapter.createFromResource(this,
				R.array.catagory_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinnercat.setAdapter(adapterCat);


		imageview= (ImageView) findViewById(R.id.itemImage);
		imageview.setOnClickListener(this);

		mAdDescription = (EditText) findViewById(R.id.description);
		mAdTitel = (EditText) findViewById(R.id.adsTitel);
		mUserName =  (EditText) findViewById(R.id.name);
		mPrice =  (EditText) findViewById(R.id.price);
		mCurrentLocation=(Location)getIntent().getParcelableExtra("mCurrentLocation");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.actionbar_post:
			if(validate()){
				showToast("Ad Posted");
			}
			break;
		case R.id.itemImage:
			showImagePicker();
			break;
		default:
			break;
		}

	}

	private boolean validate(){
		if(mAdDescription.getText().toString().length()<20){
			showToast("Description should be of 20 or more char");
		}else if(mAdTitel.getText().toString().length()<15){
			showToast("Description should be of 15 or more char");
		}else if(mUserName.getText().toString().length()<2){
			showToast("Enter valid name");
		}
		return true;
	}


	private void showToast(String msg){
		Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
	}

	private void showImagePicker(){


		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");

		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent i = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						
						startActivityForResult(i, 100);
			}
		});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        startActivityForResult(takePictureIntent, 110);
			    }

			}
		});
		myAlertDialog.show();
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// our BitmapDrawable for the thumbnail
					 Bundle extras = data.getExtras();
				        Bitmap imageBitmap = (Bitmap) extras.get("data");
				        imageview.setImageBitmap(imageBitmap);
				}
			}
		} else if (requestCode == 110) {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra("data")) {

					// retrieve the bitmap from the intent
					Bitmap    bitmap = (Bitmap) data.getExtras().get("data");


					Cursor cursor = getContentResolver()
							.query(Media.EXTERNAL_CONTENT_URI,
									new String[] {
									Media.DATA,
									Media.DATE_ADDED,
									MediaStore.Images.ImageColumns.ORIENTATION },
									Media.DATE_ADDED, null, "date_added ASC");
					String selectedImagePath;
					if (cursor != null && cursor.moveToFirst()) {
						do {
							Uri uri = Uri.parse(cursor.getString(cursor
									.getColumnIndex(Media.DATA)));
							selectedImagePath = uri.toString();
						} while (cursor.moveToNext());
						cursor.close();
					}



					bitmap = Bitmap.createScaledBitmap(bitmap, 100,
							100, false);
					// update the image view with the bitmap
					imageview.setImageBitmap(bitmap);
				} else if (data.getExtras() == null) {

					Toast.makeText(getApplicationContext(),
							"No extras to retrieve!", Toast.LENGTH_SHORT)
							.show();

					BitmapDrawable thumbnail = new BitmapDrawable(
							getResources(), data.getData().getPath());

					// update the image view with the newly created drawable
					imageview.setImageDrawable(thumbnail);

				}

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Cancelled",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
