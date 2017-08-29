package com.example.franklong.madyikyak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView javaRecyclerView;
    private ArrayList<String> posts, urls, usernames, timestamps;
    private PostAdapter mAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference("data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posts = new ArrayList<>();
        urls = new ArrayList<>();
        usernames= new ArrayList<>();
        timestamps = new ArrayList<>();




        javaRecyclerView = (RecyclerView) findViewById(R.id.feed);
        mAdapter = new PostAdapter(posts, usernames, timestamps, urls, getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        javaRecyclerView.setLayoutManager(linearLayoutManager);

        javaRecyclerView.setAdapter(mAdapter);
    }

    private ValueEventListener eventListener = new ValueEventListener() { //auto update each time there is post
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            refresh();
            for (DataSnapshot timeData : dataSnapshot.getChildren()) {
                String time = timeData.getKey();
                timestamps.add(time);
                for (DataSnapshot urlData : timeData.getChildren()) {
                    String url = "http://goo.gl/" + urlData.getKey();
                    urls.add(url);
                    for (DataSnapshot userData : urlData.getChildren()) {
                        String username = userData.getKey();
                        usernames.add(username);
                        String post = userData.getValue(String.class);
                        posts.add(post);
                    }
                }

            }
            Collections.reverse(posts);
            Collections.reverse(usernames);
            Collections.reverse(urls);
            Collections.reverse(timestamps);
            mAdapter.notifyDataSetChanged(); //so adapter can update
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("Error", "Attempt unsuccessful");
        }
    };

    public void refresh() {
        posts.clear();
        urls.clear();
        usernames.clear();
        timestamps.clear();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        refresh();
        mRef.addValueEventListener(eventListener); //add eventlistner to data reference

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRef.removeEventListener(eventListener); //make sure doesn't continuously update
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.compose) {
            //do some stuff
            Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
