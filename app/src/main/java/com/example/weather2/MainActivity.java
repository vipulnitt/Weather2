package com.example.weather2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    final String API_KEY = "3281e272d10e19dc8ff66b787214eea1";
    final String URL = "https://api.openweathermap.org/data/2.5/weather";
    TextView Weathercondition,humidity,speed,location,pressure,temperature;
     ImageView imageView,btnimg;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Weathercondition = findViewById(R.id.Weather);
        humidity = findViewById(R.id.humidity);
        speed = findViewById(R.id.speed);
        location = findViewById(R.id.Location);
        pressure = findViewById(R.id.pressure);
        temperature = findViewById(R.id.temp);
        imageView = findViewById(R.id.imageView2);
        btnimg = findViewById(R.id.imageView4);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        permission();
        btnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity2.class));
            }
        });
    }
    public void permission()
    {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                  getLocation();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
    @SuppressLint("MissingPermission")
    public  void getLocation()
    {
        LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location!=null)
                    {

                        String Latitude = String.valueOf(location.getLatitude());
                        String Longitude = String.valueOf(location.getLongitude());
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("lon",Longitude);
                        requestParams.put("lat",Latitude);
                        requestParams.put("appid",API_KEY);
                        fetch(requestParams);

                    }
                    else
                    {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                String Latitude = String.valueOf(location1.getLatitude());
                                String Longitude = String.valueOf(location1.getLongitude());
                                RequestParams requestParams = new RequestParams();
                                requestParams.put("lon",Longitude);
                                requestParams.put("lat",Latitude);
                                requestParams.put("appid",API_KEY);
                                fetch(requestParams);
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }

                }
            });
        } else
        {
            startActivity(new Intent((Settings.ACTION_LOCATION_SOURCE_SETTINGS)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
    private void fetch(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              Data data = new Data();
                try {
                    data.setCityname(response.optString("name"));
                    JSONArray jsonArray= response.getJSONArray("weather");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    data.setWeatherCondition(jsonObject.optString("main"));
                    data.setMicon("http://openweathermap.org/img/wn/"+jsonObject.optString("icon")+"@2x.png");
                    jsonObject = response.getJSONObject("main");
                    data.setPressure(jsonObject.optString("pressure")+"hpa");
                    data.setTemperature(jsonObject.optString("temp")+"°C");
                    data.setHumidity(jsonObject.optString("humidity")+"%");
                    jsonObject = response.getJSONObject("wind");
                    data.setWindspeed(jsonObject.optString("speed")+"m/s");
                    Weathercondition.append("\n"+data.getWeatherCondition());
                    speed.append("\n"+data.getWindspeed());
                    humidity.append("\n"+data.getHumidity());
                    location.append("\n"+data.getCityname());
                    pressure.append("\n"+data.getPressure());
                    temperature.append("\n"+data.getTemperature());
                    Picasso.get().load(data.getMicon()).into(imageView);
                    Log.d("vipul",data.getMicon());
                } catch (JSONException e) {
                    e.printStackTrace();
                 //   Log.d("vipul",data.);

                }
              data.setCityname(response.optString("name"));


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("vipul","Fail");
            }
        });
    }
}