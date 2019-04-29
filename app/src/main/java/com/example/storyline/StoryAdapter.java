package com.example.storyline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Activity activity;
    ArrayList<Story> storys;
    RecyclerView recyclerView;

    public StoryAdapter(RecyclerView recyclerView, Activity activity, ArrayList<Story> storys){
        this.activity=activity;
        this.recyclerView=recyclerView;
        this.storys=storys;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.story_item,viewGroup,false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final StoryViewHolder v= (StoryViewHolder) viewHolder;
        final Story story=storys.get(i);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity,StoryActivity.class);
                i.putExtra(v.getContext().getString(R.string.code_story_id),story.id);
                i.putExtra(v.getContext().getString(R.string.code_story_title),story.title);
                activity.startActivity(i);

            }
        };

        v.container.setOnClickListener(l);
        //v.coverImage.setImageDrawable();
        v.title.setText(story.title);
        v.detail.setText(story.getDetail());
    }

    @Override
    public int getItemCount() {
        return storys.size();
    }
}

