package com.sener35gmail.burak.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ExampleCaregivers extends AppCompatActivity {

    private RecyclerView recycler_view;
    private List<GraveCareGivers> graveCareGiversList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_caregivers);
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);


        recycler_view.setLayoutManager(layoutManager);

        graveCareGiversList = new ArrayList<GraveCareGivers>();
        graveCareGiversList.add(new GraveCareGivers("burak ","Şener","gsm: 5549094353"));
        graveCareGiversList.add(new GraveCareGivers("mehmet ","Şener","gsm:5453012474"));
        GraveCareGivers graveCareGivers=new GraveCareGivers();



            SimpleRecyclerAdapter adapter_items = new SimpleRecyclerAdapter(graveCareGiversList, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.d("position", "Tıklanan Pozisyon:" + position);
                    GraveCareGivers graveCareGivers = graveCareGiversList.get(position);
                    Toast.makeText(getApplicationContext(),"pozisyon:"+" "+position+" "+"Ad:"+graveCareGivers.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            recycler_view.setHasFixedSize(true);

            recycler_view.setAdapter(adapter_items);

            recycler_view.setItemAnimator(new DefaultItemAnimator());











    }


}
