package com.mecodroid.firedb_author;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    FloatingActionButton btn;
    FirebaseDatabase database;
    DatabaseReference authors;
    List<String> au;
    Dialog builder;
    TextView textgroup;
    EditText textname;
    Button addauth, cancelauth;
    RecyclerView Rv;
    AuthorAdapter adapter;
    SharedPreferences sh;
    String nam;
    List<String> searchlist;
    boolean connecting;
    String token = "615186953472-fd3qf63pim192gi27e4cr0l0o6ltp37j.apps.googleusercontent.com";
    int Rec_Sign_In = 0;
    GoogleSignInOptions moInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth mAuth;
    private Query querySearch;
    private CheckInternet checkInternet;
    private ProgressBar barpro1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Rv = findViewById(R.id.rview);
        barpro1 = findViewById(R.id.bar3);
        mAuth = FirebaseAuth.getInstance();
        moInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail().build();
        signInClient = GoogleSignIn.getClient(getApplicationContext(), moInOptions);
        checkInternet = new CheckInternet(this);
        sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
        nam = sh.getString("nam", "A1");

        btn = findViewById(R.id.floatingActionButton);
        au = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        authors = database.getReference(nam);

        authors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barpro1.setVisibility(View.GONE);
                au.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    au.add(snapshot.getKey());
                }
                setuprecyclerView(au);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDailogdesign();
                builder.show();
                addauth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connecting = checkInternet.Is_Connecting();
                        if (connecting) {
                            Bidi bidi2 = new Bidi(textname.getText().toString(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                            if (bidi2.getBaseLevel() == 0) {
                                textname.setGravity(Gravity.LEFT);
                            } else {
                                textname.setGravity(Gravity.RIGHT);
                            }
                            if (!TextUtils.isEmpty(textname.getText().toString().trim())) {
                                authors.child(textname.getText().toString().trim().substring(0, 1).toUpperCase() +
                                        textname.getText().toString().trim().substring(1)).push().setValue("");
                                builder.dismiss();
                            } else {
                                Toast.makeText(Home.this, getResources().getString(R.string.pwgn), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancelauth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchlist = new ArrayList<>();
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.parseColor("#CCE7FC"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchlist.clear();
                querySearch = authors.orderByKey().startAt(StringUtils.capitalize(query)).endAt(query + "\uf8ff");
                querySearch.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(Home.this, getResources().getString(R.string.sresult), Toast.LENGTH_SHORT).show();

                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                searchlist.add(snapshot.getKey());
                            }
                            setuprecyclerView(searchlist);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setuprecyclerView(au);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setuprecyclerView(au);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.sign_out:
                mAuth.signOut();
                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Toast.makeText(Home.this, getResources().getString(R.string.logot), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this, getResources().getString(R.string.logfail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Home.this, getResources().getString(R.string.logfail), Toast.LENGTH_SHORT).show();

                    }
                });

                Intent Sout = new Intent(Home.this, MainActivity.class);
                Sout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Sout);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setuprecyclerView(List<String> ls) {
        LinearLayoutManager lm = new LinearLayoutManager(this);
        Rv.setLayoutManager(lm);
        adapter = new AuthorAdapter(this, ls);
        Rv.setAdapter(adapter);
    }

    public void setDailogdesign() {
        builder = new Dialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialoggroup_add, (ViewGroup) findViewById(R.id.cardviewzkr));
        textgroup = v.findViewById(R.id.textzkradd);
        textname = v.findViewById(R.id.textname);
        addauth = v.findViewById(R.id.addAuth);
        cancelauth = v.findViewById(R.id.cancelAuth);
        builder.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setCancelable(false);
        builder.show();
    }

}

