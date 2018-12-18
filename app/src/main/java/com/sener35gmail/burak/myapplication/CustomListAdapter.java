package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Admin on 17.11.2018.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Integer[] imgid;
    private final String[] itemname;


    public CustomListAdapter(@NonNull Context context,String[] itemname, Integer[] imgid) {
        super(context,R.layout.mylisticon,itemname);
        this.context = (Activity) context;
        this.imgid = imgid;
        this.itemname=itemname;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView=inflater.inflate(R.layout.mylisticon, null,true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.listIcon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        imageView.setImageResource(imgid[position]);
        extratxt.setText(itemname[position]);
        return rowView;
    }
}
