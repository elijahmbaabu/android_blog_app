package com.example.farmersblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farmersblog.Adapter.PostsAdapter;
import com.example.farmersblog.Model.PostsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView recyclerView;
    PostsAdapter postsAdapter;
    List<PostsModel> postsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.posts_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        postsModelList = new ArrayList<>();

        loadPost();

    }

    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             postsModelList.clear();
             for (DataSnapshot ds: snapshot.getChildren()){
                 PostsModel postsModel = ds.getValue(PostsModel.class);
                 postsModelList.add(postsModel);
                 postsAdapter = new PostsAdapter(HomeActivity.this, postsModelList);
                 recyclerView.setAdapter(postsAdapter);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logOut){
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
        if (item.getItemId() == R.id.action_add_post){
            startActivity(new Intent(HomeActivity.this, AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}