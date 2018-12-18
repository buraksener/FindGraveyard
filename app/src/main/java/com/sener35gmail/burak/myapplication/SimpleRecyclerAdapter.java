package com.sener35gmail.burak.myapplication;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 12.11.2018.
 */

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>{
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
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
        holder.person_name.setText(list_caregivers.get(position).getName());
        holder.person_surname.setText(list_caregivers.get(position).getSurname());
        holder.person_phone.setText(list_caregivers.get(position).getPhoneNumber());


    }

    @Override
    public int getItemCount() {
        return list_caregivers.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView person_name;
        public TextView person_surname;
        public TextView person_phone;

        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = (CardView)view.findViewById(R.id.cardView);
            person_name = (TextView)view.findViewById(R.id.person_name);
            person_surname = (TextView)view.findViewById(R.id.person_surname);
            person_phone = (TextView)view.findViewById(R.id.phone_number);


        }
    }
    List<GraveCareGivers> list_caregivers;
    CustomItemClickListener listener;
    public SimpleRecyclerAdapter(List<GraveCareGivers> list_person, CustomItemClickListener listener) {

        this.list_caregivers = list_person;
        this.listener = listener;
    }
}
