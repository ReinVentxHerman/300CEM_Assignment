
package com.example.storyline;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class

StoryNodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<StoryNode> nodes;
    RecyclerView recyclerView;

    public StoryNodeAdapter(RecyclerView recyclerView, Activity activity, ArrayList<StoryNode> nodes) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.nodes = nodes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.node_item, viewGroup, false);
        return new StoryNodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final StoryNodeViewHolder v = (StoryNodeViewHolder) viewHolder;
        final StoryNode node = nodes.get(i);

        v.image.setImageDrawable(node.drawable);
        v.des.setText(node.des);

    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }
}

