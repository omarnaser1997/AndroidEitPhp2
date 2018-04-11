package com.naser.omar.androideitserverphp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.FoodList;
import com.naser.omar.androideitserverphp.Home;
import com.naser.omar.androideitserverphp.MainActivity;
import com.naser.omar.androideitserverphp.Model.User;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListenOrder extends Service implements Response.Listener<String> {



    @Override
    public void onCreate() {
        super.onCreate();


        //Toast.makeText(this, "omarrrrrrrrrrr", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            new Thread(new Runnable() {
                @Override
                public void run() {


//                    PostResponseAsyncTask task = new PostResponseAsyncTask(getApplicationContext(), new AsyncResponse() {
//            @Override
//            public void processFinish(String s) {
//                Toast.makeText(ListenOrder.this, s, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/test.php");




                    String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/test.php";
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                     Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();



                                }
                            },     new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(),getString(R.string.Please_make_sure_you_are_connected_to_the_network),Toast.LENGTH_LONG).show();

                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params =new HashMap<>();

                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);




                }
            });



       return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onResponse(String response) {
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
    }
}


















