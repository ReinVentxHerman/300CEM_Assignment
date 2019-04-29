package com.example.storyline;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Boolean isMapReady,isNodesReady;

    ArrayList<StoryNode> nodes ;
    String storyId,storyTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        isMapReady=false;
        isNodesReady=false;

        nodes=new ArrayList();

        storyId=getIntent().getExtras().getString(getString(R.string.code_story_id));
        storyTitle=getIntent().getExtras().getString(getString(R.string.code_story_title));

        new ListStoryNodeTask(storyId).execute();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady=true;
        System.out.println("onMapReady");
        mMap = googleMap;
        if (isMapReady&&isNodesReady){
            drawMap();
        }


        // Add a marker in Sydney and move the camera



    }

    public void drawMap(){
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/System.out.println("drawMap");
        ArrayList<LatLng> locations=new ArrayList<>();
        for (int i =0;i<nodes.size();i++){
            locations.add(new LatLng(Double.parseDouble(nodes.get(i).lat),Double.parseDouble(nodes.get(i).lng)));
            mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(nodes.get(i).des));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations.get(i)));
        }



    }

    class ListStoryNodeTask extends AsyncTask<String, String, String> {

        private String storyId;

        public ListStoryNodeTask(String storyId) {
            this.storyId = storyId;
        }

        protected String doInBackground(String... url) {
            return NetworkHelper.listNode(storyId);
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                System.out.println("ListStoryNodeTask : " + s);
                nodes.clear();
                //[{"id":"1","des":"node 1 des","lat":"0","lng":"0","image":"","datetime":"2019-04-18 03:23:00","storyId":"1"}]
                if (!s.equals("false")) {
                    JSONArray array;
                    JSONObject o;
                    String id, des, lat, lng, image, time, storyId;
                    StoryNode node;
                    try {
                        array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            o = array.getJSONObject(i);
                            id = o.getString(StoryNode.cId);
                            des = o.getString(StoryNode.cDes);
                            lat = o.getString(StoryNode.cLat);
                            lng = o.getString(StoryNode.cLng);
                            image = o.getString(StoryNode.cImage);
                            time = o.getString(StoryNode.cTime);
                            storyId = o.getString(StoryNode.cStoryId);
                            node = new StoryNode(id, des, lat, lng, image, time, storyId);
                            nodes.add(node);
                        }
                        isNodesReady=true;
                        System.out.println("onPostExecute");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isNodesReady&&isMapReady){
                        drawMap();
                    }
                }
            }
        }
    }
}
