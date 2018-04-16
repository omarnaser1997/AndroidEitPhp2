package com.naser.omar.androideitserverphp.Notificaton;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.kosalgeek.android.json.JsonConverter;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Home;
import com.naser.omar.androideitserverphp.Model.AppNotification;
import com.naser.omar.androideitserverphp.Model.ViewNotification;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for showing and canceling picture
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NotificationPicture {
    /**
     * The unique identifier for this type of notification.
     */

    Context context;

    private static final String NOTIFICATION_TAG = "Title";
    ArrayList<AppNotification> notificationList;

    public NotificationPicture(final Context context) {
        this.context=context;




        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/getNotification.php";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // Toast.makeText(context,response, Toast.LENGTH_SHORT).show();


                        notificationList = new JsonConverter<AppNotification>().toArrayList(response, AppNotification.class);//تحوي الجيسون الى كلاس
                        // Toast.makeText(Home.this, notificationList.get(0).getImageURL(), Toast.LENGTH_SHORT).show();
                        // Log.d("25436678443",notificationList.get(0).getImageURL().toString());
                        // Log.d("254336778443",notificationList.get(0).getView()+"");

                        if (notificationList!=null) {
                            for (AppNotification notification : notificationList) {

                                if (!searchID(notification.getId())) {
                                    try {
                                        new Database(context).addNotification(notification);
                                    } catch (Exception e) {
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d("4376375", "sucsses Add to DB");
                                }
                            }
                        }




                            List<AppNotification> listnotification =new Database(context).getNotification();

                            if (listnotification!=null) {
                                for (AppNotification notification : listnotification) {

                                    createNotification(notification);
                                }
                            }


                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("NumberUser",new Database(context).getUser().get(0).getNumber());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);

       // printAllNotficationinDB();
      // printAllNotficationinDBfff();

    }

    private void createNotification(final AppNotification notification){

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/checkNotification.php";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ViewNotification view = new JsonConverter<ViewNotification>().toArrayList(response, ViewNotification.class).get(0);
                        //Toast.makeText(context,view.getView()+"", Toast.LENGTH_SHORT).show();

                        if(view.getView()==0){
                            createNewNotification(notification);
                        }

                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();

                params.put("NotificationID",String.valueOf(notification.getId()));
                params.put("NumberUser",new Database(context).getUser().get(0).getNumber());


                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    private void createNewNotification(AppNotification notification){

        try{
             notifyy(context,notification.getTextNotification(),notification.getTitle(),notification.getId());
            }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    boolean searchID(Integer id){

        List<Integer> notificationID =getIDnotification();
        if (notificationID!=null){
        for (Integer item
                : notificationID){

            if (id == item) {
                return true;
            }
        }
        }
        return false;
    }

    void printAllNotficationinDBfff(){

        List<AppNotification> notificationDB =  new Database(context).getNotification();

        for (AppNotification notification:notificationDB) {
            Log.d("2245622546547222", String.valueOf(notification.getView()));
        }
    }
    void printAllNotficationinDB(){

        List<AppNotification> notificationDB =  new Database(context).getNotification();

        for (AppNotification notification:notificationDB) {
            Log.d("22222453454322", String.valueOf(notification.getTextNotification()));
        }
    }


    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of picture notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public  void notifyy(final Context context,final String description,final String title,final int number){




        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail) when
        // the notification is collapsed, and as the big picture to show when
        // the notification is expanded.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);


        final String ticker = title;


//                res.getString(
//                R.string.picture_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.picture_notification_placeholder_text_template, title);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_picture)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show an expanded photo on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(picture)
                        .setBigContentTitle(title)
                        .setSummaryText(description))

                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.
                .addAction(
                        R.drawable.ic_action_stat_share,
                        res.getString(R.string.action_share),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_action_stat_reply,
                        res.getString(R.string.action_reply),
                        null)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }

    public List getIDnotification() {
        List<Integer> IDnotification=null;
           try {
               IDnotification =  new Database(context).getlistIDnotification();
           }catch (Exception e){}

        return IDnotification;
    }
}

















//                        int i=0;
//                        for (AppNotification notificationn:listnotification) {
//                           try{
//                            if (new Database(context).getNotification().get(i).getView() == 1) {
//
//
//                            } else {
//                                id=new Database(context).getNotification().get(i).getId();
//                                    new Database(context).updateViewNotificsation(id,1);
//
//                                AppNotification notification= new Database(context).getNotification().get(i);
//                                try{
//                                notifyy(context,notification.getTextNotification(),notification.getTitle(),notification.getId());
//                                }catch (Exception e){}
//                            }
//
//
//                        }catch (Exception e){
//                               Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
//                           }
//                           i++;
//                        }
