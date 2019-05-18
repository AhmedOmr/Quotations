package com.mecodroid.firedb_author;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.Bidi;
import java.util.List;


class AuthorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    FirebaseDatabase database;
    DatabaseReference authors;
    View v1;
    viewholder holder;
    SharedPreferences sh;
    String nam;
    Dialog builder, vbuilder;
    EditText textname;
    TextView textVi;
    ImageView cancVi;
    ImageView editchild, delchild, canchild;
    private List<String> authorList;
    private CheckInternet checkInternet;


    public AuthorAdapter(Context context, List<String> authorList) {
        this.context = context;
        this.authorList = authorList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        sh = context.getSharedPreferences("Referncename", Context.MODE_PRIVATE);
        nam = sh.getString("nam", "A1");
        database = FirebaseDatabase.getInstance();
        authors = database.getReference(nam);
        checkInternet = new CheckInternet(context);
        v1 = LayoutInflater.from(context).inflate(R.layout.rowitems_author, parent, false);
        return new viewholder(v1);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vholder, final int postion) {

        final String s = authorList.get(postion);
        holder = (viewholder) vholder;
        holder.tex1.setText(s);
        holder.tex1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adding = new Intent(context, ShowContent.class);
                adding.putExtra("child", s);
                SharedPreferences sh = context.getSharedPreferences("fdbase", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("chd", s);
                editor.apply();
                editor.commit();
                context.startActivity(adding);
            }
        });
        holder.auedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setDailogdesign();
                builder.show();
                textname.setText(s);
                editchild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isConnecting = checkInternet.Is_Connecting();
                        if (isConnecting) {
                            // library apache in Gradle (StringUtils)
                            String s1 = StringUtils.capitalize(textname.getText().toString().trim().toLowerCase());
                            RenameAu(authors.child(s), authors.child(s1));
                            // delete if he dont edit because dont create a new child same previous
                            if (!s1.equals(s)) {
                                authors.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            snap.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            builder.dismiss();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                delchild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean is_connecting = checkInternet.Is_Connecting();
                        if (is_connecting) {

                            final AlertDialog.Builder builde = new AlertDialog.Builder(context);
                            builde.setTitle(context.getResources().getString(R.string.delist))
                                    .setMessage(context.getResources().getString(R.string.messagelist))
                                    .setPositiveButton(context.getResources().getString(R.string.ye), new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.O)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            authors.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        snap.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }).setNegativeButton(context.getResources().getString(R.string.n), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builde.show();
                            builder.dismiss();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.check), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                canchild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

            }
        });
        holder.auview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewContent();
                vbuilder.show();
                Bidi bidi2 = new Bidi(authorList.get(postion), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                if (bidi2.getBaseLevel() == 0) {
                    textVi.setGravity(Gravity.LEFT);
                    // holders.tex1.setTypeface(font3);
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) cancVi.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    textVi.setGravity(Gravity.RIGHT);
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) cancVi.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    // holders.tex1.setTypeface(font1);
                }
                textVi.setText(authorList.get(postion));
                cancVi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vbuilder.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorList != null ? authorList.size() : 0;
    }


    public void setDailogdesign() {
        builder = new Dialog(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialoggroup_edit, (ViewGroup) v1.findViewById(R.id.cardviewzkr));
        textname = v.findViewById(R.id.gtextname);
        editchild = v.findViewById(R.id.edit_nam);
        delchild = v.findViewById(R.id.delt_nam);
        canchild = v.findViewById(R.id.cance_nam);
        builder.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setCancelable(false);
        builder.show();
    }

    private void RenameAu(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void viewContent() {
        vbuilder = new Dialog(context);
        View vi = LayoutInflater.from(context).inflate(R.layout.dialogcontent_view, (ViewGroup) v1.findViewById(R.id.cardviewzkr));
        textVi = vi.findViewById(R.id.textvicontent);
        cancVi = vi.findViewById(R.id.cancelview);

        vbuilder.setContentView(vi, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        vbuilder.setCancelable(false);
        vbuilder.show();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView tex1;
        ImageView auedit, auview;
        CardView cName;

        public viewholder(View itemView) {
            super(itemView);
            tex1 = itemView.findViewById(R.id.textview1);
            cName = itemView.findViewById(R.id.cardAuthor);
            auedit = itemView.findViewById(R.id.imageedit1);
            auview = itemView.findViewById(R.id.imageseen1);
        }
    }


}
