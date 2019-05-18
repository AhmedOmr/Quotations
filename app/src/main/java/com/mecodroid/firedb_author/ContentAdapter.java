package com.mecodroid.firedb_author;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Bidi;
import java.util.List;

class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    viewholder holders;
    View v1;
    Dialog builder;
    TextView textVi;
    ImageView cancVi;
    CheckInternet internet;
    private List<ContentModel> authorList;

    public ContentAdapter(Context context, List<ContentModel> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        internet = new CheckInternet(context);
        v1 = LayoutInflater.from(context).inflate(R.layout.rowitems_content, parent, false);
        return new viewholder(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vholder, final int postion) {
        holders = (viewholder) vholder;
        final ContentModel model = authorList.get(postion);
        holders.tex1.setText(model.getQuotecontent());
        Bidi bidi = new Bidi(authorList.get(postion).getQuotecontent(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        if (bidi.getBaseLevel() == 0) {
            holders.tex1.setGravity(Gravity.LEFT);
            // holders.tex1.setTypeface(font3);
        } else {
            holders.tex1.setGravity(Gravity.RIGHT);
            // holders.tex1.setTypeface(font1);
        }
        holders.ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ned = new Intent(context, Writecontent.class);
                ned.putExtra("chd", authorList.get(postion).getQuotecontent());
                context.startActivity(ned);

            }
        });
        holders.vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewContent();
                builder.show();
                Bidi bidi2 = new Bidi(authorList.get(postion).getQuotecontent(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
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
                textVi.setText(authorList.get(postion).getQuotecontent());
                cancVi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

            }
        });
        holders.shr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shrd = new Intent(Intent.ACTION_SEND);
                shrd.setType("text/plain");
                shrd.putExtra(Intent.EXTRA_TEXT, authorList.get(postion).getQuotecontent());
                if (shrd.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(shrd);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.tshrd), Toast.LENGTH_SHORT).show();
                }

            }
        });
        holders.cop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(context, authorList.get(postion).getQuotecontent());
                Toast.makeText(context, context.getResources().getString(R.string.tcop), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorList != null ? authorList.size() : 0;
    }

    public void viewContent() {
        builder = new Dialog(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialogcontent_view, (ViewGroup) v1.findViewById(R.id.cardviewzkr));
        textVi = v.findViewById(R.id.textvicontent);
        cancVi = v.findViewById(R.id.cancelview);

        builder.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setCancelable(false);
        builder.show();
    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView tex1;
        ImageView ed, vw, shr, cop;

        public viewholder(View itemView) {
            super(itemView);
            tex1 = itemView.findViewById(R.id.textquotes);
            ed = itemView.findViewById(R.id.imageedit2);
            vw = itemView.findViewById(R.id.imageseen2);
            shr = itemView.findViewById(R.id.imagesend);
            cop = itemView.findViewById(R.id.imagecopy);
        }
    }

}
