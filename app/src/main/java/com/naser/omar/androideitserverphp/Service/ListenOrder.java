package com.naser.omar.androideitserverphp.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
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
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.FoodList;
import com.naser.omar.androideitserverphp.Home;
import com.naser.omar.androideitserverphp.MainActivity;
import com.naser.omar.androideitserverphp.Model.AppNotification;
import com.naser.omar.androideitserverphp.Model.User;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.Notificaton.NotificationPicture;
import com.naser.omar.androideitserverphp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenOrder extends Service implements Response.Listener<String> {

    List<Integer> IDnotification;



   int  numItemInList=0;
    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        MyThread t1 =new MyThread();
        t1.start();

      //  printAllNotficationinDB();

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

    public  void createNotification(){



        NotificationPicture notificationPicture=new NotificationPicture(getApplicationContext());
      //  notificationPicture.notify(getApplicationContext(),new Database(getApplicationContext()).getNotification().get(id).getTextNotification(),
       //         new Database(getApplicationContext()).getNotification().get(id).getTitle(),id);

     //   notificationPicture.notify(getApplicationContext());
       // new Database(getApplicationContext()).updateViewNotificsation(new Database(getApplicationContext()).getNotification().get(id),1);

    }

    private List<Integer> printAllIDNotification(){

        List<Integer> notificationID =  new Database(getApplicationContext()).getlistIDnotification();

        for (Integer notification:notificationID) {
            Log.d("2222222", String.valueOf(notification));
        }

        return notificationID;

    }

    void printAllNotficationinDB(){

        List<AppNotification> notificationDB =  new Database(getApplicationContext()).getNotification();

        for (AppNotification notification:notificationDB) {
            Log.d("2245622546547222", String.valueOf(notification.getView()));
        }
    }

    void printAllIDNotification1(){

        List<Integer> notificationDB =  new Database(getApplicationContext()).getlistIDnotification();

        for (Integer notification:notificationDB) {
            Log.d("2222222", String.valueOf(notification));
        }

    }


    class MyThread extends Thread{
        @Override
        public void run() {
            while (true){
            createNotification();
            try {
                Thread.sleep(10000);
            }catch (Exception e){}
            }


        }
    }

}







