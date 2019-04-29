package com.example.storyline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

Button createStoryButton;
RecyclerView recyclerView;
StoryAdapter adapter;

ArrayList<Story> storys;

    SharedPreferences sharedPreferences;
    String id,name;

    ListStoryTask listStoryTask=null;

    static final int MY_PERMISSION_ACCESS_FINE_LOCATION=99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        storys=new ArrayList();

        sharedPreferences=getSharedPreferences(getString(R.string.code_prep), Context.MODE_PRIVATE);
        id = sharedPreferences.getString(getString(R.string.code_id), "");
        name = sharedPreferences.getString(getString(R.string.code_name), "");

        recyclerView =findViewById(R.id.storyRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StoryAdapter(recyclerView, this, storys);
        recyclerView.setAdapter(adapter);

        createStoryButton=findViewById(R.id.goCreateStory);
        createStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),CreateStoryActivity.class));
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!id.equals("")){
            listStoryTask=new ListStoryTask();
            listStoryTask.execute();
        }
    }

    class ListStoryTask extends AsyncTask<String, String, String> {


        protected String doInBackground(String... url) {
            return NetworkHelper.listStory(id);
        }

        protected void onPostExecute(String s) {
            System.out.println("ListStoryTask : "+s);
            //[{"id":"1","userId":"1","title":"Some Awesome title","nodeCount":"0"},{"id":"2","userId":"1","title":"Another title","nodeCount":"0"}]
            storys.clear();
            adapter.notifyDataSetChanged();
            if (s!=null){
                JSONArray array;
                try {
                    array =new JSONArray(s);
                    Story story;
                    JSONObject o;
                    for (int i=0;i<array.length();i++){
                        o=array.getJSONObject(i);
                        story=new Story(o.getString(Story.cId),o.getString(Story.cUserId),o.getString(Story.cTitle),o.getString(Story.cNodeCount));
                        storys.add(story);
                        adapter.notifyItemChanged(i);
                    }
                    //adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
    }
}


}
class Story{
    public static final String cId="id",cUserId="userId",cTitle="title",cNodeCount="nodeCount";

    public String id,userId,title,nodeCount;

    public Story(String id, String userId, String title, String nodeCount) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.nodeCount = nodeCount;
    }

    public String getDetail(){

        return nodeCount+" node(s)";
    }

}