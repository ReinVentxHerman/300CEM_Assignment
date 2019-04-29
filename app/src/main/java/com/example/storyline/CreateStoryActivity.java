package com.example.storyline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class CreateStoryActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String id,name;
    Button createStory;
    EditText title;

    CreateStoryTask createStoryTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        createStory=findViewById(R.id.createStory);
        title=findViewById(R.id.editStoryTitle);

        sharedPreferences=getSharedPreferences(getString(R.string.code_prep), Context.MODE_PRIVATE);
        id = sharedPreferences.getString(getString(R.string.code_id), "");
        name = sharedPreferences.getString(getString(R.string.code_name), "");

        createStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createStoryTask=new CreateStoryTask(title.getText().toString());
                createStoryTask.execute();
            }
        });

    }

    class CreateStoryTask extends AsyncTask<String, String, String> {
        String title;
static final String didCreateStory="Your story is ready";

        public CreateStoryTask(String title) {
            this.title = title;
        }

        protected String doInBackground(String... url) {
            return NetworkHelper.createStory(id,title);
        }

        protected void onPostExecute(String s) {
            System.out.println("CreateStoryTask : "+s);
            if (s!=null){
                if(s.equals("true")){
                    Toast.makeText(CreateStoryActivity.this,didCreateStory,Toast.LENGTH_SHORT);
                    CreateStoryActivity.this.finish();

                }

            }
        }
    }
}
