package com.naser.omar.androideitserverphp.Test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.FoodList;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Model.Item;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.R;
import com.rengwuxian.materialedittext.MaterialEditText;
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

import info.hoang8f.widget.FButton;

public class imageSlider extends Activity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private SliderLayout mDemoSlider;
    ArrayList<Category> productListjson;
    ArrayList<Item> requests;
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    FButton btnSelect,btnUpload;
    final int GALLERY_REQUEST = 13323;
    final int CAMERA_REQUEST = 22131;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    String selectedPhoto;
    String encodedImage;
    Boolean btnSelectBool=false;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    GridView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
         getData();
       list =(GridView)findViewById(R.id.GridView);

        getDataforGridView();



    }

    private void sliderimageLoude(ArrayList<Category> productListjson) {
        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put(productListjson.get(0).getName(), productListjson.get(0).getImage());
        url_maps.put(productListjson.get(1).getName(), productListjson.get(1).getImage());
        url_maps.put(productListjson.get(2).getName(), productListjson.get(2).getImage());

        for(Category item : productListjson){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(item.getName())
                    .image(item.getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("id",item.getCategoryID());


            mDemoSlider.addSlider(textSliderView);
        }




        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Toast.makeText(this,slider.getBundle().get("id") + "",Toast.LENGTH_SHORT).show();
        Intent foodList = new Intent(this, FoodList.class);
        //Because CategoryId is Key , so we just get Key of this item
        foodList.putExtra("CategoryId",slider.getBundle().get("id")+"");
        startActivity(foodList);


    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    //جلب البيانات من أجل LyoutSlider
    private HashMap<String, String> getData(){
        final HashMap<String,String> url_mapsr = new HashMap<String, String>();
        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/getCategory.php?page=1";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productListjson = new JsonConverter<Category>().toArrayList(response, Category.class);//تحوي الجيسون الى كلاس
                        productListjson.addAll(productListjson);
                        //Toast.makeText(imageSlider.this, productListjson.get(0).getImage(), Toast.LENGTH_SHORT).show();

                        sliderimageLoude(productListjson);


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
//                params.put("Phone",edtPhone.getText().toString());
//                params.put("Password",edtPassword.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    return url_mapsr;
    }

    public void uploadfood(View view) {
        showAddFoodDialog();

    }

    private void showAddFoodDialog() {
        //this code like code Add category in home class

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(imageSlider.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_item,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);

        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);

        //Event For Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUserPermsions(GALLERY_REQUEST);
                //chooseImage();// let user select image from Gallery and save URL of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadImage("insert",0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });



        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertDialog.show();

    }

    void LoadImage() {
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK ) {

            if (requestCode == CAMERA_REQUEST) {


                String photoPath = cameraPhoto.getPhotoPath();
                selectedPhoto=photoPath;
                Bitmap bitmap=null;

            } else if (requestCode == GALLERY_REQUEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto=photoPath;
              //  Log.d("3423432",selectedPhoto);
                btnSelect.setText("Image selected !");
                btnSelectBool=true;

            }

        }
    }

    void CheckUserPermsions(int permission) {
        if (Build.VERSION.SDK_INT >= 23) {

            if(permission==GALLERY_REQUEST){
                //permission Storage
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }

                LoadImage();
            }

            if(permission==CAMERA_REQUEST){
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    return;
                }
                openCamera();
            }

        }


    }

    private void uploadImage(final String statment, final int item) throws FileNotFoundException {
        //item هذا البرمتر استفيد منه عند عملية التحديث بحيث عند عملية التحديث يتم تمريره على أنه رقم العنصر في القائمه
        //statment هذا البرمتر يمثل التعليمه التي ستم تنفيذها   delete or update

        ///////////////////////////////////////
        final ProgressDialog mDialog =new ProgressDialog(this);
        mDialog.setMessage("Uploading ...");
        mDialog.show();

        //////////////////////////////////////

        HashMap<String, String> postData = new HashMap<String, String>();
        try {//في حال تم تحديث دون اختيار الصور مثلا تحديث السعر فقط
            Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(400, 200).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 10, baos);
            byte[] byteImage_photo = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
            Log.d("43554654", encodedImage);
        }catch (Exception e){}

        PostResponseAsyncTask task=new PostResponseAsyncTask(imageSlider.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                mDialog.dismiss();
               // loadListFood(categoryId,userpage);
                Toast.makeText(imageSlider.this, s, Toast.LENGTH_SHORT).show();
                // Snackbar.make(rootLayout," Food"+productList.get(item).getName()+"was "+statment, Snackbar.LENGTH_LONG).show();
            }
        });

            try{
                postData.put("ImageFood", encodedImage);
               postData.put("NameFood", edtName.getText().toString());
                task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/Additem.php");
            }catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Cannot Connect to Server",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),"URL Error",Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleProtocolException(ProtocolException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Protocol Error",Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Encodnig Error",Toast.LENGTH_LONG).show();


            }
        });



    }


    void openCamera(){

        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            cameraPhoto.addToGallery();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "Something Wrong while taking photos",Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Item> getDataforGridView() {



        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/getitem.php?page=1";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requests=new JsonConverter<Item>().toArrayList(response, Item.class);//تحوي الجيسون الى كلاس

                        list.setAdapter(new ListtResource(getApplicationContext(),requests));
                        list.setNestedScrollingEnabled(true);


                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.Error_while_reading_data),Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        return requests;
    }

    class  ListtResource extends BaseAdapter {
        ArrayList<Item> mydata;
        String name=null;


        Context context;
        ListtResource(Context context,ArrayList<Item> items){
            this.context=context;
            mydata=new ArrayList<Item>();
            mydata=items;
           // Toast.makeText(context, items.get(0).getImage(), Toast.LENGTH_SHORT).show();
            ;

        }



        @Override
        public int getCount() {
            return (mydata == null) ? 0 : mydata.size();
        }

        @Override
        public Object getItem(int position) {
            return mydata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            final View row=inflater.inflate(R.layout.listrowgridlist,parent,false);

            ImageView imageView=(ImageView) row.findViewById(R.id.imageView2);
            TextView textView=(TextView)row.findViewById(R.id.textView);
            textView.setText(mydata.get(position).getName());
            //imageView.setImageResource(R.drawable.ic_audiotrack_black_24dp);
            Picasso.with(context).load(mydata.get(position).getImage()).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent foodList = new Intent(context, FoodList.class);
                    //Because CategoryId is Key , so we just get Key of this item
                    foodList.putExtra("CategoryId", mydata.get(position).getId());
                    startActivity(foodList);

                }
            });

          //  Toast.makeText(context, mydata.get(0).getImage(), Toast.LENGTH_SHORT).show();

            return row;
        }
    }





}