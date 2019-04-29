package com.example.storyline;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class StoryViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout container;
    public ImageView coverImage;
    public TextView title,detail;

    public StoryViewHolder(View itemView){

        super(itemView);
        container=itemView.findViewById(R.id.storyContainer);
        coverImage=itemView.findViewById(R.id.storyCoverImage);
        title=itemView.findViewById(R.id.storyTitle);
        detail=itemView.findViewById(R.id.storyDetail);

    }

}
