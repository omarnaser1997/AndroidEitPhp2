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


        IDnotification= printAllIDNotification();

        Toast.makeText(this, "omarrrrrrrrrrr", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



//        for (Integer item:IDnotification) {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//           try{ createNotification(item);}catch (Exception e){}
//
//        }
        MyThread t1 =new MyThread();
        t1.start();

        printAllNotficationinDB();

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

    public  void createNotification(int id){



        NotificationPicture notificationPicture=new NotificationPicture();
        notificationPicture.notify(getApplicationContext(),new Database(getApplicationContext()).getNotification().get(id).getTextNotification(),
                new Database(getApplicationContext()).getNotification().get(id).getTitle(),id);

        new Database(getApplicationContext()).updateViewNotificsation(new Database(getApplicationContext()).getNotification().get(id),1);

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



//        for (Integer item:IDnotification) {
//            try {
//                //Thread.sleep(1200000 *3);
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            createNotification(item);
//
//        }



        for (int i=0;i<IDnotification.size();i++) {
            if (
                    new Database(getApplicationContext()).getNotification().get(i).getView() == 1) {
                i++;
            } else {
                createNotification(i);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        }

        }
    }

}






//new Database(getApplicationContext()).getUser().get(0).getImage()









//
//







