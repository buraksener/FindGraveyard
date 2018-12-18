package com.sener35gmail.burak.myapplication;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sener35gmail.burak.myapplication.Model.Deceased;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Admin on 14.11.2018.
 */

public class SimpleRecyclerAdapterForDeceased extends RecyclerView.Adapter<SimpleRecyclerAdapterForDeceased.ViewHolder> {

    private Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView deceased_name;
        public TextView deceased_surname;
        public TextView deceased_gender;
        public TextView deceased_job;
        public TextView deceased_bd;
        public TextView deceased_dd;
        public TextView deceased_bio;
        public ImageView person_img;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = (CardView)view.findViewById(R.id.cardView_for_deceased);
            deceased_name = (TextView)view.findViewById(R.id.tw_deceased_name);
            deceased_surname = (TextView)view.findViewById(R.id.tw_deceased_surname);
            deceased_bd=(TextView) view.findViewById(R.id.tw_deceased_birthdate);
            deceased_dd=(TextView) view.findViewById(R.id.tw_deceased_deathDate);

            person_img = (ImageView)view.findViewById(R.id.deceased_photo);

        }
    }
    List<Deceased> list_deceased;
    CustomItemClickListener listener;
    public SimpleRecyclerAdapterForDeceased(List<Deceased> list_deceased,CustomItemClickListener listener,Context context)
    {
        this.list_deceased=list_deceased;
        this.listener=listener;
        this.context=context;


    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_for_deceaseds, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });

        return view_holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.deceased_name.setText(list_deceased.get(position).getDeceasedName());
        holder.deceased_surname.setText(list_deceased.get(position).getDeceasedSurname());
        holder.deceased_bd.setText(list_deceased.get(position).getDeceasedBirthDate());
        holder.deceased_dd.setText(list_deceased.get(position).getDeceasedDeathDate());
       Picasso.with(context).load(list_deceased.get(position).getImageUrl()).into(holder.person_img);
       //Glide.with(context).load(list_deceased.get(position).getImageUrl()).into(holder.person_img);
    }

    @Override
    public int getItemCount() {
        return list_deceased.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

