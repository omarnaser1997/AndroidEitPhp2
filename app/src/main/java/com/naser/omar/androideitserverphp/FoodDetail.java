package com.naser.omar.androideitserverphp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.Model.Rating;
import com.naser.omar.androideitserverphp.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class FoodDetail extends AppCompatActivity implements
        Response.Listener<String>, RatingDialogListener {
    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating,btn_Edit;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    ArrayList<Food> productList;
    ArrayList<Rating> RatingList;
    String foodId="";
    Food food;
    Food currentFood;
    FButton btnSelect,btnUpload;
    RatingBar ratingBar;
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    //for Permissions
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final int GALLERY_REQUEST = 13323;
    final int CAMERA_REQUEST = 22131;

    String selectedPhoto;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    String encodedImage;

    Boolean btnSelectBool=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Init view
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(CounterFab)findViewById(R.id.btnCart);
        food_description=(TextView)findViewById(R.id.food_description);
        food_name=(TextView)findViewById(R.id.food_name);
        food_price=(TextView)findViewById(R.id.food_price);
        food_image=(ImageView)findViewById(R.id.img_food);
        btnRating=(FloatingActionButton)findViewById(R.id.btn_rating);//
        btn_Edit=(FloatingActionButton)findViewById(R.id.btn_Edit);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        galleryPhoto = new GalleryPhoto(getApplicationContext());

        //this listener for Rating Button
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(Integer.parseInt(foodId));

            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()

                ));
                    Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }catch (Exception e){ Toast.makeText(FoodDetail.this," الرجاء الانتظار ليكتمل تحميل البيانات", Toast.LENGTH_SHORT).show();}
            }
        });
        btnCart.setCount(new Database(this).getCountCart());



        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get Food Id from Intent
        if(getIntent() !=null)
            foodId= getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty())
        { if (Common.isConnectedToInternet(getBaseContext()))
                {
                     getDetailFood(foodId);
                    getRatingFood(foodId);

                 }else {
                        Toast.makeText(FoodDetail.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                          return;
                      }
        }
        
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
                Log.d("3423432",selectedPhoto);
                btnSelect.setText(getString(R.string.Image_selected));
                btnSelectBool=true;

            }

        }
    }

    private void getRatingFood(final String foodId) {
        final ProgressDialog mDialog = new ProgressDialog(FoodDetail.this);
        mDialog.setMessage(getString(R.string.please_waiting));
        mDialog.show();



        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/getRating.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mDialog.dismiss();
                        Log.d("3423324",response);
                        RatingList=new JsonConverter<Rating>().toArrayList(response, Rating.class);//تحوي الجيسون الى كلاس
                        Rating rating = null; 
                        if(RatingList!=null)
                         {
                            rating  = RatingList.get(0);
                         }
                        int rat = 0;
                          try{
                             
                              rat=Integer.parseInt(rating.getValue());
                          }catch (NumberFormatException e){}catch (Exception e){}

                       //Toast.makeText(FoodDetail.this, RatingList.get(0).getValue(), Toast.LENGTH_SHORT).show();
                        ratingBar.setRating(rat);
                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string. Please_make_sure_you_are_connected_to_the_network),Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("userphone",Common.currentUser.getNumber());
                params.put("foodid",foodId);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }
    public void showUpdateDialog(final int item) {

        final   String nameFood= currentFood.getName();
        final   String imageFood=currentFood.getImage();
        final   String idFood=currentFood.getFoodID();
        final   String DescriptionFood=currentFood.getDescription();
        final   String DiscountFood=currentFood.getDiscount();
        final   String PriceFood=currentFood.getPrice();

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle(getString(R.string.Edit_Food));
        alertDialog.setMessage(getString(R.string.Please_fill_full_information));

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_food_layout,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        edtDescription=add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice=add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=add_menu_layout.findViewById(R.id.edtDiscount);

        //Set default value for view
        edtName.setText(nameFood);
        edtDescription.setText(DescriptionFood);
        edtPrice.setText(PriceFood);
        edtDiscount.setText(DiscountFood);

        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);

        //Event For Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoadImage();

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //item رقم العنصر في الليستا
                    uploadImage("update",item);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //set button
        alertDialog.setPositiveButton(getString(R.string.YES), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //loadMenu();

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

    private void uploadImage(final String statment, final int item) throws FileNotFoundException {
        //item هذا البرمتر استفيد منه عند عملية التحديث بحيث عند عملية التحديث يتم تمريره على أنه رقم العنصر في القائمه
        //statment هذا البرمتر يمثل التعليمه التي ستم تنفيذها   delete or update

        ///////////////////////////////////////
        final ProgressDialog mDialog =new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.Uploading));
        mDialog.show();
        //////////////////////////////////////

        HashMap<String, String> postData = new HashMap<String, String>();
        try {//في حال تم تحديث دون اختيار الصور مثلا تحديث السعر فقط
            Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(900, 600).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos);
            byte[] byteImage_photo = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
            Log.d("43554654", encodedImage);
        }catch (Exception e){}

        PostResponseAsyncTask task=new PostResponseAsyncTask(FoodDetail.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                mDialog.dismiss();
               // loadListFood(categoryId);
                  // Toast.makeText(FoodDetail.this, s, Toast.LENGTH_SHORT).show();
                // Snackbar.make(rootLayout," Food"+productList.get(item).getName()+"was "+statment, Snackbar.LENGTH_LONG).show();
            }
        });
        if(statment.equals("update"))
        {   //اذا لم يتم اخيار صورة نأتي بالصورة القديمة من قاعدة البيانات
            if(btnSelectBool==false){encodedImage=currentFood.getBase64();}

            postData.put("ImageFood", encodedImage);
            postData.put("NameFood", edtName.getText().toString());
            postData.put("Description",edtDescription.getText().toString());
            postData.put("PriceFood", edtPrice.getText().toString());
            postData.put("Discount", edtDiscount.getText().toString());
            postData.put("FoodID",currentFood.getFoodID());
            task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/updateFood.php");
        }

        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string.Cannot_Connect_to_Server),Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string.URL_Error),Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleProtocolException(ProtocolException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string.Protocol_Error),Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string.Encodnig_Error),Toast.LENGTH_LONG).show();


            }
        });



    }

    void LoadImage() {
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText(getString(R.string.submit))
            .setNegativeButtonText(getString(R.string.CANCEL))
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle(getString(R.string.Rete_this_food))
                .setDescription(getString(R.string.please_select_some_stars_and_give_your_feedback))
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint(getString(R.string.please_write_your_comment_here))
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();

    }

    private void getDetailFood(final String foodId) {


        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/FoodDetail.php", (Response.Listener<String>) this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.Error_while_reading_data),Toast.LENGTH_LONG).show();

            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("foodID",foodId);//typProdect
                return params;
            }

        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public void onResponse(String response) {
         // Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        productList=new JsonConverter<Food>().toArrayList(response, Food.class);//تحوي الجيسون الى كلاس
        currentFood= productList.get(0);
        Log.d("3423325",response);
        //Set Image
        Picasso.with(getBaseContext()).load(currentFood.getImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.user_placeholder_error)
                .into(food_image);


        collapsingToolbarLayout.setTitle(currentFood.getName());
        food_price.setText(currentFood.getPrice());
        food_name.setText(currentFood.getName());
        food_description.setText(currentFood.getDescription());

    }

    //this methode executes when you press in button positive from Rating Dialog
    @Override
    public void onPositiveButtonClicked(int value, String comments) {

        //Get Rating and uplode to firebase//12:20
        final Rating rating =new Rating(Common.currentUser.getNumber(),
                foodId,
                String.valueOf(value),
                comments);

        final ProgressDialog mDialog = new ProgressDialog(FoodDetail.this);
        mDialog.setMessage(getString(R.string.please_waiting));
        mDialog.show();



        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/Rating.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mDialog.dismiss();
                      //  Toast.makeText(FoodDetail.this, getString(R.string.Thank_you_for_submit_rating), Toast.LENGTH_SHORT).show();

                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),getString(R.string.Please_make_sure_you_are_connected_to_the_network),Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("comment",rating.getComments());
                params.put("userphone",Common.currentUser.getNumber());
                params.put("foodid",rating.getFoodnID());
                params.put("ratevalue",rating.getValue());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        int rat = 0;
        try{

            rat=Integer.parseInt(rating.getValue());
        }catch (NumberFormatException e){}

        ratingBar.setRating(rat);
    }

    //this methode executes when you press in button Negative from Rating Dialog
    @Override
    public void onNegativeButtonClicked() {

    }
}
