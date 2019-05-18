package com.mecodroid.firedb_author;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowContent extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference authors;
    List<ContentModel> auChildList;
    RecyclerView Rq;
    ContentAdapter adapter;
    Intent n;
    String ch, chd;
    SharedPreferences sh, sh2;
    String nam;
    TextView texheader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        texheader = findViewById(R.id.textheader);

        sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
        nam = sh.getString("nam", "A1");

        auChildList = new ArrayList<>();

        n = getIntent();
        ch = n.getExtras().getString("child", "not found");

        database = FirebaseDatabase.getInstance();
        authors = database.getReference(nam);

        texheader.setText(ch);

        authors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                auChildList.clear();
                for (DataSnapshot snapshot : dataSnapshot.child(ch).getChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        ContentModel value = snapshot.getValue(ContentModel.class);
                        auChildList.add(value);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        setuprecycler();

    }

    public void setuprecycler() {
        Rq = findViewById(R.id.recquotes);
        LinearLayoutManager ln = new LinearLayoutManager(ShowContent.this);
        Rq.setLayoutManager(ln);
        adapter = new ContentAdapter(this, auChildList);
        Rq.setAdapter(adapter);
    }

    public void add_quotes(View view) {
        Intent sen = new Intent(ShowContent.this, Writecontent.class);
        sen.putExtra("chld", ch);
        startActivity(sen);
    }


}
