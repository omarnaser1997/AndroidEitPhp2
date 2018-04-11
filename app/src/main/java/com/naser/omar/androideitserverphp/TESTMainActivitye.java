package com.naser.omar.androideitserverphp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TESTMainActivitye extends AppCompatActivity {



   ArrayList<String> listimage64;
    //create Target from Picasso
    Target target =new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

          //  Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(900, 600).getBitmap();

            String encodedImage=null;

            ////////////////////////////////////////////////////////
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos);
            byte[] byteImage_photo = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);

            listimage64.add(encodedImage);



           // Toast.makeText(TESTMainActivitye.this, encodedImage.toString(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmain_activitye);
        listimage64=new ArrayList<String>();
    }

    public void click(View view) {

        Picasso.with(getApplicationContext())
                .load("http://omarnaser.000webhostapp.com/AndroidEitServerPHP/Category/1518399468.jpeg")
                .into(target);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (String item:listimage64) {
            Log.d("4576364",item );
        }

    }
}
