package com.mecodroid.firedb_author;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;


public class FormatContentquote extends Fragment {


    SeekBar seekBar_textsize;
    Writecontent activity;
    ImageView vhide, Scent, Sright, Sleft;

    public FormatContentquote() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_format_contentquote, container, false);

        vhide = v.findViewById(R.id.fraghide);
        Sleft = v.findViewById(R.id.imleft2);
        Scent = v.findViewById(R.id.imcenter2);
        Sright = v.findViewById(R.id.imright2);

        seekBar_textsize = v.findViewById(R.id.seekBar_textsize);
        activity = (Writecontent) getActivity();

        vhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(FormatContentquote.this).commit();
            }
        });

        Scent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.coQoute.setGravity(Gravity.CENTER);

            }
        });
        Sleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.coQoute.setGravity(Gravity.LEFT);
            }
        });
        Sright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.coQoute.setGravity(Gravity.RIGHT);

            }
        });
        seekBar_textsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity = (Writecontent) getActivity();
                activity.coQoute.setTextSize(pval);
            }
        });
        return v;
    }


}
