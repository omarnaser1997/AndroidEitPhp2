package com.naser.omar.androideitserverphp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.ViewHolder.FoodAdapter;
import com.naser.omar.androideitserverphp.ViewHolder.MenuViewHolder;
import com.naser.omar.androideitserverphp.ViewHolder.OrderViewHolder;
import com.naser.omar.androideitserverphp.ViewHolder.RecyclerTouchListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderStatus extends AppCompatActivity {

    RelativeLayout rootLayout;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<com.naser.omar.androideitserverphp.Model.Request> requests;

    private OrderViewHolder mAdapter;

    MaterialSpinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        rootLayout=(RelativeLayout)findViewById(R.id.relativeLayoutRequest);
        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrderViewHolder(requests,getApplicationContext());
        recyclerView.setAdapter(mAdapter);


        //يمكن استخدام هذا التابع عوضا عن التابع في كلاس OrderViewHolder
        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(
                getApplicationContext(), recyclerView, new RecyclerTouchListener
                .ClickListener() {
            //عند الضغط على عنصر في القائمة
            @Override
            public void onClick(View view, int position) {//هذا الحدث يتم تنفيذه عند الضغط على العنصر في القائمة
            //just implement it to fix crash when click to this item
                Intent trackingOrder =new Intent(OrderStatus.this,TrackingOrder.class);
                Common.currentRequest=requests.get(position);//تخذين الطلبية المضغوط عليها في Common
                //Toast.makeText(OrderStatus.this, requests.get(position).getName(), Toast.LENGTH_SHORT).show();
                startActivity(trackingOrder);//يتم نقل المستخدم الى نافذة الخريطة عند الضغط على العنصر في القائمة



            }

            @Override
            public void onLongClick(View view, int position) {

                //Toast.makeText(OrderStatus.this, "long Click", Toast.LENGTH_SHORT).show();
                Intent orderDetail =new Intent(OrderStatus.this,OrderDetail.class);
                Common.currentRequest=requests.get(position);;
                orderDetail.putExtra("orderId",requests.get(position).getRequesteID());
                startActivity(orderDetail);
            }
        }));


        loadOrders(Common.currentUser.getNumber());
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Toast.makeText(this, ""+item.getOrder(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals(Common.UPDATA))
        {
            showUpdateDialog(item.getOrder());
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deletCategory("delete",item.getOrder());

        }

        return super.onContextItemSelected(item);
    }
    void delet(int item) {
        String nameFoodDelete=requests.get(item).getName();
        requests.remove(item);
        mAdapter.notifyItemRemoved(item);
        mAdapter.notifyItemRangeChanged(item,requests.size());
        Snackbar.make(rootLayout,"the Request : "+nameFoodDelete+" was deledted", Snackbar.LENGTH_SHORT).show();
        //uploadImage("delete",item);

        // loadMenu();
    }
    private void deletCategory(String statment, final int item) {

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/deleterequest.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                         //Toast.makeText(OrderStatus.this,response, Toast.LENGTH_SHORT).show();
                        delet(item);
                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error while reading data",Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("requesteID",requests.get(item).getRequesteID().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void showUpdateDialog(final int item) {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater=this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.update_order_layout,null);

        spinner=(MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my way","Shipped");

        alertDialog.setView(view);

       // final String localKey=key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
               String oo=  String.valueOf(spinner.getSelectedIndex());//this retern 0 or 1 or 2
                //تمثل هذه الأرقام 0 1 2 رقم الاندكس في السبينر
                //يجب حفظ هذه القيم في قاعده البيانات وهذه الأرقام تمثل حالة الطلبيه
                // يتم تحويل هذه القيم الى حالة وذلك بعد جلبها من قاعده البيانات عن طريق التابع convertCodeToStatus في كلاس Common

                insertDataInDatabase(oo,item);

            }
        });


//        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });

        alertDialog.show();
    }

    private void insertDataInDatabase(final String state,final int item) {

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/updaterequest.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Snackbar.make(rootLayout,"Okkkk", Snackbar.LENGTH_LONG).show();
                       // Toast.makeText(OrderStatus.this, requests.get(item).getStatus().toString(), Toast.LENGTH_SHORT).show();
                        loadOrders(Common.currentUser.getNumber());
                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error while reading data",Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("state",state);
                params.put("requesteID",requests.get(item).getRequesteID().toString());
                //productList.get(item).getCategoryID().toString()
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void loadOrders(final String phone) {

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/getRequest.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requests=new JsonConverter<com.naser.omar.androideitserverphp.Model.Request>().toArrayList(response, com.naser.omar.androideitserverphp.Model.Request.class);//تحوي الجيسون الى كلاس
                        mAdapter = new OrderViewHolder(requests,getApplicationContext());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);

                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error while reading data",Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("Phone",phone);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }






}
