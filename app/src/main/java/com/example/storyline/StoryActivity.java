package com.example.storyline;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity implements SensorEventListener {

    String storyId, storyNodeCount, storyTitle;
    RecyclerView recyclerView;
    StoryNodeAdapter adapter;

    ArrayList<StoryNode> nodes;

    ListStoryNodeTask listStoryNodeTask;

    Button buttonTakePhoto,buttonGoMap;
    TextView textViewStoryTitle;

    SensorManager sensorManager;
    Sensor sensor;



    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private static final float MAX_ACCEL=10;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > MAX_ACCEL) {
            Intent i =new Intent(this,MapsActivity.class);
            i.putExtra(getString(R.string.code_story_id),storyId);
            i.putExtra(getString(R.string.code_story_title), storyTitle);
            startActivity(i);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        nodes = new ArrayList<>();

        storyId = getIntent().getExtras().getString(getString(R.string.code_story_id));
        storyTitle = getIntent().getExtras().getString(getString(R.string.code_story_title));

        buttonTakePhoto = findViewById(R.id.goTakePhoto);
        textViewStoryTitle = findViewById(R.id.storyTitleTop);
        buttonGoMap=findViewById(R.id.goMap);

        textViewStoryTitle.setText(storyTitle);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), PhotoPreviewActivity.class);
                i.putExtra(view.getContext().getString(R.string.code_story_id), storyId);
                i.putExtra(view.getContext().getString(R.string.code_story_node_count), storyNodeCount);
                i.putExtra(view.getContext().getString(R.string.code_story_title), storyTitle);
                startActivity(i);
            }
        });
        buttonGoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  Intent i =new Intent(view.getContext(),MapsActivity.class);
                  i.putExtra(view.getContext().getString(R.string.code_story_id),storyId);
                  i.putExtra(view.getContext().getString(R.string.code_story_title), storyTitle);
                  startActivity(i);
            }
        });

        recyclerView = findViewById(R.id.nodeRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StoryNodeAdapter(recyclerView, this, nodes);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void refresh() {
        nodes.clear();
        adapter.notifyDataSetChanged();
        listStoryNodeTask = new ListStoryNodeTask(storyId);
        listStoryNodeTask.execute();
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
                            adapter.notifyItemChanged(i);
                            new LoadImageTask(image,node,adapter).execute();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}

class LoadImageTask extends AsyncTask<String, String, Drawable> {

    private String fileName;
    private StoryNode node;
    private StoryNodeAdapter adapter;

    public LoadImageTask(String fileName,StoryNode node,StoryNodeAdapter adapter) {
        this.fileName = fileName;
        this.node=node;
        this.adapter=adapter;
    }

    protected Drawable doInBackground(String... url) {
        return NetworkHelper.downloadImage(fileName);
    }

    protected void onPostExecute(Drawable s) {
        node.drawable=s;
        adapter.notifyDataSetChanged();
    }
}





class StoryNode {
    public static final String cId = "id", cDes = "des", cLat = "lat", cLng = "lng", cImage = "image", cTime = "datetime", cStoryId = "storyId";
    public String id, des, lat, lng, image, time, storyId;
    public Drawable drawable;

    public StoryNode(String id, String des, String lat, String lng, String image, String time, String storyId) {
        this.id = id;
        this.des = des;
        this.lat = lat;
        this.lng = lng;
        this.image = image;
        this.time = time;
        this.storyId = storyId;
    }
}