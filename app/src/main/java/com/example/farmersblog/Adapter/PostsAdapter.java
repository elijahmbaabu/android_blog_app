package com.example.farmersblog.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.farmersblog.Model.PostsModel;
import com.example.farmersblog.R;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyHolder> {
    Context context;
    List<PostsModel> postsModelList;

    public PostsAdapter(Context context, List<PostsModel> postsModelList) {
        this.context = context;
        this.postsModelList = postsModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String title = postsModelList.get(position).getpTitle();
        String description = postsModelList.get(position).getpDescription();
        String image = postsModelList.get(position).getpImage();

        holder.titlePost.setText(title);
        holder.descriptionPost.setText(description);

        Glide.with(context).load(image).into(holder.imagePost);


    }

    @Override
    public int getItemCount() {
        return postsModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView imagePost;
        TextView titlePost, descriptionPost;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imagePost = itemView.findViewById(R.id.post_image);
            titlePost = itemView.findViewById(R.id.postTitle);
            descriptionPost = itemView.findViewById(R.id.postDescription);
        }
    }
}
