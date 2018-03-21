package com.naser.omar.androideitserverphp.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.Model.Request;
import com.naser.omar.androideitserverphp.Model.User;
import com.naser.omar.androideitserverphp.Remote.IGeoCoordinates;
import com.naser.omar.androideitserverphp.Remote.RetrofitClient;
import com.naser.omar.androideitserverphp.Service.ListenOrder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

/**
 * Created by OmarNasser on 2/8/2018.
 */

public class Common {
    public static User currentUser;
    public static final String UPDATA = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";
    public static final int PICK_IMAGE_REQUEST=71;
    public static final String baseUrl="https://maps.googleapis.com";
    public static Request currentRequest;


    public static String convertCodeToStatus(String status) {

        if (
                status.equals("0"))
            return "placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getRetrofit(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap =Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX =0,pivotY=0;

        Matrix scaleMatrix =new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas =new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    //هذا التابع للتأكد من وجود الانترنيت
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }


}
