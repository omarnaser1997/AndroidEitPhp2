package com.naser.omar.androideitserverphp;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.naser.omar.androideitserverphp.Model.Request;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.ViewHolder.CartAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;



public class Cart extends Activity implements Response.Listener<String> {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public TextView txtTotalPrice;
    FButton btnPlace;
    List<Order> cart =new ArrayList<>();
    CartAdapter adapter;
    RelativeLayout rootLayout;
    String part="1";//part of request الفرع الخاص بالطلبية
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);
        //init
        recyclerView=(RecyclerView)findViewById(R.id.list_Cart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout=(RelativeLayout)findViewById(R.id.rootLayoutcart);
        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cart.size() >0) {
                    ShowAlertDialog();
                }else {
                    Toast.makeText(Cart.this, getString(R.string.Your_cart_is_empty), Toast.LENGTH_SHORT).show();
                }


            }
        });
        loadListFood();


    }


    //when you select one of Redio Button عند اختيارك لأحد الفرلاوع
    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    // Pirates are the best
                    Toast.makeText(this, "تم اختيار الفرع الخاص باللاذقية", Toast.LENGTH_SHORT).show();
                part="1";
                break;
            case R.id.radio_ninjas:
                if (checked)
                    // Ninjas rule
                    part="2";
                Toast.makeText(this, "تم اختيار الفرع الخاص بمدينة حلب", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void ShowAlertDialog(){

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle(getString(R.string.One_more_step));
        alertDialog.setMessage(getString(R.string.Enter_your_address));

        LayoutInflater inflater =this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);
        final MaterialEditText edtAddress=(MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment=(MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertDialog.setPositiveButton(getString(R.string.YES),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Create new Request
                Request request=new Request(
                        Common.currentUser.getNumber(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                //submit to php
                //we will using System.currentMill to key
                UplodeRequestToServer(request,String.valueOf(System.currentTimeMillis()));


                //Delete cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, getString(R.string.Thank_you_Order_Place), Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        alertDialog.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }



    private void UplodeRequestToServer(final Request request,final String dataRequest) {


        if(part==null){
            Toast.makeText(this, "الرجاء ادخال الفرع الذي تريد ارسال الطلب له", Toast.LENGTH_SHORT).show();
        }

        StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.POST, "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/SetRequest.php", (Response.Listener<String>) this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.Error_while_reading_data),Toast.LENGTH_LONG).show();

            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
               Log.d("dataRequest514654165",dataRequest);
                params.put("Phone",request.getPhone());//typProdect

                params.put("part",part);//part of request

                params.put("Name",request.getName());//typProdect

                params.put("Addres",request.getAddress());//typProdect

                params.put("Tooal",request.getTooal());//typProdect

                //params.put("Status",request.getStatus());//typProdect

                params.put("Requests",dataRequest);//typProdect
               // params.put("FK_Number",categoryId);//typProdect

                return params;
            }

        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
    private void loadListFood() {
        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total=0;
        try {
            for (Order order : cart) {
                total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
            }
        }catch (Exception e){}
        Locale locale =new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public void onResponse(String response) {

        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();

    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
        {
            deleteCart(item.getOrder());
        }


        return true;
    }


    private void deleteCart(int position) {
        String nameFoodDelete=cart.get(position).getProductName();
        //we will remove item at List<Order> by Position
        cart.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,cart.size());
        Snackbar.make(rootLayout,getString(R.string.the_Food)+nameFoodDelete+getString(R.string.was_deleted), Snackbar.LENGTH_SHORT).show();
        //After that ,we will delete all old data from SQLite
        new Database(this).cleanCart();
        //And final ,we will update new data from List<Order> to SQlite
        for (Order item:cart) {
            new Database(this).addToCart(item);
        }


    }
}
