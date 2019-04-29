package com.example.storyline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PhotoPreviewActivity extends AppCompatActivity {

    Button takePhoto, sentPost,buttonPermission,buttonGPS;
    TextView error;
    ImageView imageView;
    EditText editText;//description

    static final int REQUEST_IMAGE_CAPTURE = 1, MY_PERMISSION_ACCESS_FINE_LOCATION = 99;
    Bitmap image;

    String storyId;

    Location location = null;
    LocationManager locationManager;
    LocationListener locationListener;

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_ACCESS_FINE_LOCATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        Bundle extras = getIntent().getExtras();

        takePhoto = findViewById(R.id.takePhotoButton);
        sentPost = findViewById(R.id.sentPostButton);
        imageView = findViewById(R.id.toUploadImage);
        editText = findViewById(R.id.toUploadDescription);
        error = findViewById(R.id.uploadErrorMessage);
        buttonPermission=findViewById(R.id.permissionButton);
        buttonGPS=findViewById(R.id.GPSButton);

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_gary_24dp));

        buttonPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });


        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationListener = new LocationListener() {
                        public void onLocationChanged(Location l) {
                            // Called when a new location is found by the network location provider.
                            location = l;
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }
                    };

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        sentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
                String imageE = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
                String des = editText.getText().toString();
                String lat = "0", lng = "0";
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                } else {
                    System.out.println("no gps");
                }
                new CreateStoryNodeTask(storyId, des, imageE, lat, lng).execute();
            }
        });

        storyId = extras.getString(getString(R.string.code_story_id));

        //takePhoto();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    private void takePhoto(){

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();

            image = (Bitmap) extras.get("data");
            if (isNeedResize(image)){
                image = resize(image);
                System.out.println("resized");
            }
            imageView.setImageBitmap(image);
            sentPost.setVisibility(View.VISIBLE);
            sentPost.setClickable(true);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private static Bitmap resize(Bitmap b){
        int nh = (int) ( b.getHeight() * (1024.0 / b.getWidth()) );
        return Bitmap.createScaledBitmap(b, 1024, nh, true);
    }

    private static Boolean isNeedResize(Bitmap b){
        return b.getHeight()>4096||b.getWidth()>4096;
    }

    class CreateStoryNodeTask extends AsyncTask<String, String, String> {

        String storyId,des,image,lat,lng;

        public CreateStoryNodeTask(String storyId, String des, String image, String lat, String lng) {
            this.storyId = storyId;
            this.des = des;
            this.image = image;
            this.lat = lat;
            this.lng = lng;

        }

        protected String doInBackground(String... url) {
            return NetworkHelper.createStoryNode(storyId,des,image,lat,lng);
        }

        protected void onPostExecute(String s) {
            System.out.println("CreateStoryNodeTask : "+s);
            if (s!=null){
                if(s.equals("true")){
                    PhotoPreviewActivity.this.finish();
                    return;
                }
            }
            Toast.makeText(PhotoPreviewActivity.this,PhotoPreviewActivity.this.getString(R.string.defaultError),Toast.LENGTH_SHORT);
        }
    }

}
