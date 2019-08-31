package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE = 123;

    String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    //String APP_ID = "";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final String APP_ID = "f6b057af1a67e8587e85a566a17a3fc8";
    final String consumerKey = "dj0yJmk9ZEZBR2x6Mm5IODJpJmQ9WVdrOWFVdENUVEo1TkdzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTI0";
    final String consumerSecret = "fc99494774daa7798ded3f45bb9be41280898d25";

    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    TextView mTemp;
    ImageView mIcon;
    TextView mCity;
    ImageView refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTemp = (TextView) findViewById(R.id.temperatureTV);
        mIcon = (ImageView) findViewById(R.id.iconIV);
        mCity = (TextView) findViewById(R.id.city_nameTV);
        refreshBtn = (ImageView) findViewById(R.id.refreshIV);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChangeCityController.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("clima", "onResume() called");


        Intent myintent = getIntent();
        String city = myintent.getStringExtra("cityname");

        if(city != null){
            getWeatherForCity(city);
        }else{
            Log.d("clima", "Getting weather for current location");
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForCity(String city){
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima:","onLocationChanged() callback received");

                String latitude = String.valueOf(location.getLatitude());
                String longitute = String.valueOf(location.getLongitude());

                Log.d("clima:","Latitude is"+latitude);
                Log.d("clima:","Longitude is"+longitute);

                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitute);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("clima", "onProviderDisabled() callback received");
            }

        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("clima","onRequestPermissionResult():Permission Granted");
                getWeatherForCurrentLocation();
            }else{
                Log.d("clima","Permisssion Denied");
            }
        }
    }

    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers,JSONObject response){
                Log.d("Clima:","Success!"+response.toString());

                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,JSONObject response ){
                Log.e("Clima:","Failure"+e.toString());
                Log.d("Clima:","Status Code"+statusCode);

                Toast.makeText(MainActivity.this,"Request Failed:",Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void updateUI(WeatherDataModel weatherDataModel){
        mTemp.setText(weatherDataModel.getTemperature());
        mCity.setText(weatherDataModel.getCity());

        int resourceID = getResources().getIdentifier(weatherDataModel.getIconName(),"drawable",getPackageName());

        mIcon.setImageResource(resourceID);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
    }
}
