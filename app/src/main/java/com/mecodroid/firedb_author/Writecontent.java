package com.mecodroid.firedb_author;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Writecontent extends AppCompatActivity {
    Intent n, m;
    String ch;
    SharedPreferences sh;
    String nam;
    EditText coQoute;
    DatabaseReference authors;
    FirebaseDatabase database;
    boolean fromAdapter = false;
    String c;
    ImageView delt;
    String idformat;
    private CheckInternet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecontent);
        coQoute = findViewById(R.id.text_quote_content);
        internet = new CheckInternet(this);
        delt = findViewById(R.id.imdelet);
        sh = getSharedPreferences("Referncename", Context.MODE_PRIVATE);
        nam = sh.getString("nam", "A1");
        database = FirebaseDatabase.getInstance();
        authors = database.getReference(nam);
        n = getIntent();
        m = getIntent();
        ch = n.getExtras().getString("chld", "not found");
        if (m != null && m.hasExtra("chd")) {
            fromAdapter = true;
            c = m.getExtras().getString("chd", "not found");
            database = FirebaseDatabase.getInstance();
            authors = database.getReference(nam);
            coQoute.setText(c);
        }
        coQoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.conf);
                if (currentFragment instanceof FormatContentquote) {
                    getSupportFragmentManager().beginTransaction().
                            hide(getSupportFragmentManager().findFragmentById(R.id.conf)).commit();

                }
            }
        });

        coQoute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.conf);
                if (currentFragment instanceof FormatContentquote) {
                    getSupportFragmentManager().beginTransaction().
                            hide(getSupportFragmentManager().findFragmentById(R.id.conf)).commit();
                }
                return false;
            }
        });
    }

    public void save_Coqoute(View view) {
        if (fromAdapter) {
            if (internet.Is_Connecting()) {
                if (!TextUtils.isEmpty(coQoute.getText().toString().trim())) {
                    final ContentModel cm = new ContentModel();
                    cm.setQuotecontent(coQoute.getText().toString().trim());
                    SharedPreferences shd = getSharedPreferences("fdbase", Context.MODE_PRIVATE);
                    final String chf = shd.getString("chd", "l");
                    Query quotecontent = authors.child(chf).orderByChild("quotecontent").equalTo(c);
                    quotecontent.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                dataSnapshot1.getRef().setValue(cm);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("canceled", "onCancelled", databaseError.toException());
                        }
                    });
                    finish();

                } else {
                    Toast.makeText(Writecontent.this, getResources().getString(R.string.plwrconame), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Writecontent.this, getResources().getString(R.string.plwrconame), Toast.LENGTH_SHORT).show();

            }
        } else {
            if (internet.Is_Connecting()) {
                if (!TextUtils.isEmpty(coQoute.getText().toString().trim())) {
                    ContentModel mo = new ContentModel();
                    mo.setQuotecontent(coQoute.getText().toString().trim());
                    authors.child(ch).push().setValue(mo);
                    finish();
                } else {
                    Toast.makeText(Writecontent.this, getResources().getString(R.string.plwrconame), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Writecontent.this, getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void delet_Coqoute(View view) {
        if (fromAdapter) {
            if (internet.Is_Connecting()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.deq))
                        .setMessage(getResources().getString(R.string.qm))
                        .setPositiveButton(getResources().getString(R.string.ye), new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences shd = getSharedPreferences("fdbase", Context.MODE_PRIVATE);
                                final String chf = shd.getString("chd", "l");
                                Query quotecontent = authors.child(chf).orderByChild("quotecontent").equalTo(c);

                                quotecontent.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            dataSnapshot1.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("canceled", "onCancelled", databaseError.toException());
                                    }
                                });
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.n), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                Toast.makeText(Writecontent.this, getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();

            }
        } else {
            if (internet.Is_Connecting()) {
                delt.setEnabled(false);
            } else {
                Toast.makeText(Writecontent.this, getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();

            }
        }

    }

    public void cancel_Coqoute(View view) {
        finish();

    }

    public void formats_Coqoutes(View view) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.conf, new FormatContentquote()).addToBackStack(null).commit();

    }
}
