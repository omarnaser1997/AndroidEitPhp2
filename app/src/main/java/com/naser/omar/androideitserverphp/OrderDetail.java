package com.naser.omar.androideitserverphp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity  {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_id_value="";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);


        order_id=(TextView)findViewById(R.id.order_id);
        order_phone=(TextView)findViewById(R.id.order_phone);
        order_address=(TextView)findViewById(R.id.order_address);
        order_total=(TextView)findViewById(R.id.order_total);
        order_comment=(TextView)findViewById(R.id.order_comment);

        lstFoods=(RecyclerView)findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if (getIntent() !=null)
            order_id_value=getIntent().getStringExtra("orderId");



        //set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTooal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());


        OrderDetailAdapter adapter =new OrderDetailAdapter(new Database(getBaseContext()).getCarts());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);


    }
}