package com.example.storyline;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StoryNodeViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        public TextView des;

    public StoryNodeViewHolder(View itemView){

            super(itemView);
        image=itemView.findViewById(R.id.storyNodeImage);
        des=itemView.findViewById(R.id.storyNodeDes);

        }

    }
