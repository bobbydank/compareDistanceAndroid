/*
 * All code for finding current location taken from: 
 * http://javapapers.com/android/get-current-location-in-android/
 * 
 */
package com.example.comparingdistance;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener { 
	
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	private Location currentLocation; 

	TextView answer;
	TextView currentZip;
	EditText enteredLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Create LocationManager instance as reference to the location service
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Request current location from LocationManager
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		answer = (TextView) findViewById(R.id.answer);
		currentZip = (TextView) findViewById(R.id.currentZip);
		enteredLocation = (EditText) findViewById(R.id.editText1);
		
		if (Geocoder.isPresent()) {
        	Log.i("calculateDistance", "Geocoder is present");
        } else {
        	Log.e("calculateDistance", "Geocoder is not present");
        }
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void calculateDistance(View view) throws IOException {
	    // Do something in response to button
		Log.i("calculateDistance", "YOU PRESSED THE BUTTON");
		
		//hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(enteredLocation.getWindowToken(), 0);
		
		//get location if it hasn't been established
		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		logPostalCode(currentLocation);
		   
		//get enterZip and make a location out of it.
        String locationName = enteredLocation.getText().toString();
        Geocoder gc = new Geocoder(this);
        
        List<Address> list = gc.getFromLocationName(locationName, 1);
        if (list != null && !list.isEmpty()) {
        	Address add = list.get(0);
        	Toast.makeText(this, add.getLatitude()+" "+add.getLongitude(), Toast.LENGTH_LONG).show();
        	
        	Location l = new Location("");
        	l.setLatitude(add.getLatitude());
        	l.setLongitude(add.getLongitude());
        	
        	answer.setText("Distance of "+ (currentLocation.distanceTo(l) * 0.000621371192)); //convert to miles cuz I'm American
        } else {
        	Toast.makeText(this, "Unable to get geodata from "+locationName , Toast.LENGTH_LONG).show();
        }
         
	}

	//-- LocationManager Overrides
	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		//logPostalCode(currentLocation);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("Latitude","disable");
		Toast.makeText(this, "Location services are currently disabled.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude","enable");
		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude","status");
		if (status == 2) {
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
			currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
	}
	
	public void logPostalCode(Location l) {
		Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            //System.out.println(addresses.size());
            if (addresses.size() > 0) {
            	Log.i("Zip Code",""+addresses.get(0).getPostalCode());
            	currentZip.setText("Current Zip Code: "+ addresses.get(0).getPostalCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
