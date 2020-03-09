package com.maher.n2c.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.maher.n2c.app.ApiStructure.ApiModelClass;
import com.maher.n2c.app.ApiStructure.Constants;
import com.maher.n2c.app.ApiStructure.ServerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    GPSTracker gpsTracker;
    Location location;
    double latitude;
    double longitude;
    LatLng latLng;
    ImageView settings, alarm_btn;

    ArrayList<ContactsModel> contactsArray = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        GetFvrtContacts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(this);
        setContentView(R.layout.activity_main);

        Initializations();
        settings.setImageResource(R.drawable.setting);
        alarm_btn.setImageResource(R.drawable.alarm);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetFvrtContacts();
        Actions();
    }

    private void Actions(){
        alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (contactsArray.isEmpty()){
//                    Toast.makeText(MainActivity.this, "Select Contacts first in Settings", Toast.LENGTH_SHORT).show();
//                }else {
//                    String toNumbers = "";
//                    for (int i = 0; i < contactsArray.size(); i++) {
//                        toNumbers = toNumbers + contactsArray.get(i).getNumber() + ";";
//                    }
//                    toNumbers = toNumbers.substring(0, toNumbers.length() - 1);
//                    String message= "Help me! \n" +
//                            "\n" +
//                            " Tap on the following link to see my Location. \n" +
//                            "\n" +
//                            " https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
//
//                    Uri sendSmsTo = Uri.parse("smsto:" + toNumbers);
//                    Intent intent = new Intent(
//                            Intent.ACTION_SENDTO, sendSmsTo);
//                    intent.putExtra("sms_body", message);
//                    startActivity(intent);
//                }

                GetNotify();

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Initializations(){
        settings = findViewById(R.id.settings_btn);
        alarm_btn = findViewById(R.id.alarm_image);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap = mMap;
        googleMap.setMyLocationEnabled(true);

        location = gpsTracker.getLocation();

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitude, longitude);

        // Creating a marker
//        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
//        markerOptions.position(latLng);

//        googleMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)        // Sets the center of the map to Mountain View
                .zoom(17)              // Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(0)               // Sets the tilt of the camera to 30 degrees
                .build();              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        UpdateLocation(longitude, latitude);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                googleMap.clear();

                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)        // Sets the center of the map to Mountain View
                        .zoom(17)              // Sets the zoom
                        .bearing(90)           // Sets the orientation of the camera to east
                        .tilt(0)               // Sets the tilt of the camera to 30 degrees
                        .build();              // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                UpdateLocation(longitude, latitude);
                // Creating a marker
//                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
//                markerOptions.position(latLng);
//                googleMap.addMarker(markerOptions);
                return true;
            }
        });
    }

    private void GetFvrtContacts(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, List<ContactsModel>> postParam = new HashMap<String, List<ContactsModel>>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponseArray(Request.Method.GET, Constants.URL.GET_FVRT, MainActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){

                            JSONArray favourite_contacts = jsonObject.getJSONArray("favourite_contacts");
                            for (int i = 0; i < favourite_contacts.length(); i++){
                                JSONObject object = favourite_contacts.getJSONObject(i);
                                ContactsModel contactsModel = new ContactsModel();

                                String name = object.getString("name");
                                String number = object.getString("number");

                                contactsModel.setName(name);
                                contactsModel.setNumber(number);

                                contactsArray.add(contactsModel);
                            }



                        }else {
                            Toast.makeText(MainActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void GetNotify(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, List<ContactsModel>> postParam = new HashMap<String, List<ContactsModel>>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponseArray(Request.Method.GET, Constants.URL.NOTIFY, MainActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){

                            Toast.makeText(MainActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(MainActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void UpdateLocation(double longitude, double latitude){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, Double> postParam = new HashMap<String, Double>();
        postParam.put("longitude", longitude);
        postParam.put("latitude", latitude);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));
        ApiModelClass.GetApiResponseLong(Request.Method.POST, Constants.URL.UPDATE_LOCATION, MainActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){
                            String message = jsonObject.getString("message");

                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(MainActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
