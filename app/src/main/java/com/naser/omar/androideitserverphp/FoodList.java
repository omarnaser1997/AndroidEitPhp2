package com.naser.omar.androideitserverphp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Listener.EndlessRecyclerViewScrollListener;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.ViewHolder.FoodAdapter;
import com.naser.omar.androideitserverphp.ViewHolder.FoodViewHolder;
import com.naser.omar.androideitserverphp.ViewHolder.MenuViewHolder;

import com.naser.omar.androideitserverphp.ViewHolder.RecyclerTouchListener;
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
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class FoodList extends Activity  implements Response.Listener<String>{

    //Add new Food
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    FButton btnSelect,btnUpload;
    Food newFood;
    Boolean btnSelectBool=false;

    //for Permissions
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final int GALLERY_REQUEST = 13323;
    final int CAMERA_REQUEST = 22131;

    //select photo
    String selectedPhoto;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    String encodedImage;


    private RecyclerView recyclerView;
    private FoodAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Food> productList =new ArrayList<>();
    ArrayList<Food> productListjson;
    String categoryId="";
    ListResources listResources;
    MaterialSearchBar materialSearchBar;
    ArrayList<Food> searchAdapter=new ArrayList<Food>();
    List<String> suggestList = new ArrayList<>(); //قائمة الاقتراحات لشريط البحث  //هذه القائمة من أجل شريط البجث


    SwipeRefreshLayout swipeRefreshLayout;

    int userpage =2;

    private ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        productListjson=new ArrayList<Food>();
        productList=new ArrayList<Food>();

        recyclerView=(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        //layoutManager=new LinearLayoutManager(this);

        galleryPhoto = new GalleryPhoto(getApplicationContext());

       // mAdapter = new FoodAdapter(productList,getApplicationContext(),this);

        //Get Intent here
        if(getIntent() !=null){
            //جلب فئةcategoryId الطعام من Home
            categoryId=getIntent().getStringExtra("CategoryId");
        }



        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
       // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));




        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new FoodAdapter(productList,getApplicationContext(),this);

        recyclerView.setAdapter(mAdapter);









//////////////////////////////////////////////////////////////////////////////////////

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);


        //اعطاء الألوان لل
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);



        //عند تحديث اللياوت
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                userpage=2;
                //Toast.makeText(FoodList.this, ""+mAdapter.getSize(), Toast.LENGTH_SHORT).show();
                for (int i=0;i<mAdapter.getSize();i++) {
                    mAdapter.delete(i);


                }



               // try{ productList.clear();}catch (Exception e){}

                if(!categoryId.isEmpty() && categoryId !=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext())){

                       loadListFood(categoryId,1);//تحميل قائمة الأطعمة الخاصة بالفئة المطلوبة
                    }else {
                        Toast.makeText(FoodList.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }



            }
        });

        //Defult , load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {


                if(!categoryId.isEmpty() && categoryId !=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext())){
                        loadListFood(categoryId,1);//تحميل قائمة الأطعمة الخاصة بالفئة المطلوبة
                    }else {
                        Toast.makeText(FoodList.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


            }
        });



        /////////////////////////////////////////////////////////////////////////////////////







//        // row click listener
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(
//                getApplicationContext(), recyclerView, new RecyclerTouchListener
//                .ClickListener() {
//
//            @Override
//            public void onClick(View view, int position) {
//                Food food = productList.get(position);
//               // Toast.makeText(getApplicationContext(), food.getName() + " is selected!", Toast.LENGTH_SHORT).show();
//                // FoodIdعند الضغط على الطعام يتم نقل المستخدم الى نافذةمعلومات الطعال FoodDetail حاملا معها رقم الطعام أي المفتاح الأساس للطعام
//                // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
//                //Start new Activity
//                Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
//                foodDetail.putExtra("FoodId",food.getFoodID());// Send Food Id to new Activity
//                startActivity(foodDetail);
//
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));


       // rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);



        /////////////////////////////////////////////////////////////////// progress_bar
        progress_bar=(ProgressBar)findViewById(R.id.progress_bar_Foood);

        /////////////////////////////////////////////////////////////////////////////////////////////


        //Search
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint(getString(R.string.Enter_your_food));//العنوان الظاهري لشريط البحث
        //loadSuggest();//تحميل اقتراحات البحث
        materialSearchBar.setLastSuggestions(suggestList);//اضافة قائمة الاقتراحات لشريط البحث
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //When user type their text , we will change suggest list

                List<String> suggest =new ArrayList<>();
                for(String search:suggestList)  //loop in Suggest list
                {
                    //مقارنة النص المدخل في شريط البحث مع قائمة الاقتراحات
                    //suggestفاذا كان النص مطابق يتم اضافة النص الىى القائمة الجديده
                    //حيث القائمة الجديده suggestتمثل طلب المستخدم
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });//عند الضغط على زر البحث يتم جلب نتيجه البحث
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when Search Bar is close
                //Restory original adapter
                if(!enabled)
                {
                   // recyclerView.setAdapter(listResources);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //Show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });





        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

               // userpage=page;
                //Toast.makeText(FoodList.this, userpage+"", Toast.LENGTH_SHORT).show();


                    //التأكد من وجود النت
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadListFood(categoryId, userpage);//تحميل قائمة الأطعمة الخاصة بالفئة المطلوبة
                    } else {
                        Toast.makeText(FoodList.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                        return;
                    }

                userpage++;

                //loadMenu(page);
                //Toast.makeText(Home.this, ""+page, Toast.LENGTH_SHORT).show();


                progress_bar.setVisibility(View.VISIBLE);

            }
        });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        loadListFood(categoryId,userpage);

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
                btnSelect.setText("Image selected !");
                btnSelectBool=true;

            }

        }
    }

    private void loadSuggest() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/getFood.php", (Response.Listener<String>) this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.Error_while_reading_data),Toast.LENGTH_LONG).show();

            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                //params.put("categoryID",categoryId);//typProdect
                return params;
            }

        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void loadListFood(final String categoryId, final int page) {


        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/getFood.php", (Response.Listener<String>) this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.Error_while_reading_data),Toast.LENGTH_LONG).show();

            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("categoryID",categoryId);//typProdect
                params.put("page", String.valueOf(page));//typProdect
                return params;
            }

        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        swipeRefreshLayout.setRefreshing(false);//لايقاف التحديث
    }

    @Override
    public void onResponse(String response) {

        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        Log.d("3564543",response);

        //the list of page is fineshed
        if (response.contains("Array")){
            //when the page is fineshed i will hide last item in Recycler view
            Toast.makeText(this, getString(R.string.End_of_list), Toast.LENGTH_SHORT).show();
        }




        //  Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        try {
            productListjson = new JsonConverter<Food>().toArrayList(response, Food.class);//تحوي الجيسون الى كلاس

            productList.addAll(productListjson);

            for (Food object : productList) {
                Food item = object;
                suggestList.add(item.getName());//Add name of food to suggest
            }
            // listResources = new ListResources(productList, this, this);
            // recyclerView.setAdapter(listResources);


        }catch (Exception e){}

        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(),productList.size());



        swipeRefreshLayout.setRefreshing(false);//لايقاف التحديث

        progress_bar.setVisibility(View.GONE);



//        mAdapter.notifyDataSetChanged();
      //  Log.d("87678",productList.get(0).getName());



    }
    private void startSearch(CharSequence text) {
        for (Food object : productList) {
            Food item = object;
            if (object.getName().equals(text.toString()))
            {
                searchAdapter.add(item);//Add name of food to suggest
            }
            mAdapter = new FoodAdapter(searchAdapter,getApplicationContext(),this);
            //StartSearch startSearch = new StartSearch(searchAdapter, getBaseContext(), this);
            recyclerView.setAdapter(mAdapter);
        }

    }

    public void uploadfood(View view) {
        showAddFoodDialog();

    }

    private void showAddFoodDialog() {
        //this code like code Add category in home class

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_food_layout,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        edtDescription=add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice=add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=add_menu_layout.findViewById(R.id.edtDiscount);

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

//        //set button
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//
//
//            }
//        });
//        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        alertDialog.show();

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
    void openCamera(){

        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            cameraPhoto.addToGallery();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "Something Wrong while taking photos",Toast.LENGTH_LONG).show();
        }
    }

    void LoadImage() {
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
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
                Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(900, 600).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos);
                byte[] byteImage_photo = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
                Log.d("43554654", encodedImage);
            }catch (Exception e){}

                 PostResponseAsyncTask task=new PostResponseAsyncTask(FoodList.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                      mDialog.dismiss();
                      loadListFood(categoryId,userpage);
                Toast.makeText(FoodList.this, s, Toast.LENGTH_SHORT).show();
               // Snackbar.make(rootLayout," Food"+productList.get(item).getName()+"was "+statment, Snackbar.LENGTH_LONG).show();
                     }
        });
           if(statment.equals("insert")) {
            try{
               postData.put("ImageFood", encodedImage);
               postData.put("CategoryID", categoryId);
               postData.put("NameFood", edtName.getText().toString());
               postData.put("Description", edtDescription.getText().toString());
               postData.put("PriceFood", edtPrice.getText().toString());
               postData.put("Discount", edtDiscount.getText().toString());//categoryId
               task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/uploadFood.php");
           }catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
           } else if(statment.equals("update"))
           {        if(btnSelectBool==false){encodedImage=productList.get(item).getBase64();}
               postData.put("ImageFood", encodedImage);
               postData.put("CategoryID", categoryId);
               postData.put("NameFood", edtName.getText().toString());
               postData.put("Description",edtDescription.getText().toString());
               postData.put("PriceFood", edtPrice.getText().toString());
               postData.put("Discount", edtDiscount.getText().toString());//categoryId
               postData.put("FoodID",productList.get(item).getFoodID());
               task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/updateFood.php");
           }else if(statment.equals("delete"))
           {       // if(btnSelectBool==false){encodedImage=productList.get(item).getBase64();}
              // postData.put("ImageFood", encodedImage);
             //  postData.put("CategoryID", categoryId);
              // postData.put("NameFood", edtName.getText().toString());
             //  postData.put("Description",edtDescription.getText().toString());
              // postData.put("PriceFood", edtPrice.getText().toString());
              // postData.put("Discount", edtDiscount.getText().toString());//categoryId
               postData.put("FoodID",productList.get(item).getFoodID());
               task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/deleteFood.php");
               deletCategory(item);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Toast.makeText(this, ""+item.getOrder(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals(Common.UPDATA))
        {
            showUpdateDialog(item.getOrder());
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            try {
                uploadImage("delete",item.getOrder());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


        return super.onContextItemSelected(item);
    }



    public void showUpdateDialog(final int item) {

        final   String nameFood= productList.get(item).getName();
        final   String imageFood=productList.get(item).getImage();
        final   String idFood=productList.get(item).getFoodID();
        final   String DescriptionFood=productList.get(item).getDescription();
        final   String DiscountFood=productList.get(item).getDiscount();
        final   String PriceFood=productList.get(item).getPrice();

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(FoodList.this);
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

    void deletCategory(int item) {
        String nameFoodDelete=productList.get(item).getName();
        productList.remove(item);
        mAdapter.notifyItemRemoved(item);
        mAdapter.notifyItemRangeChanged(item,productList.size());
        Snackbar.make(swipeRefreshLayout,getString(R.string.the_Food)+nameFoodDelete+getString(R.string.was_deledted), Snackbar.LENGTH_SHORT).show();
        //uploadImage("delete",item);

        // loadMenu();
    }
//    public static void intentTo(){
//        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
//        foodDetail.putExtra("FoodId",food.getFoodID());// Send Food Id to new Activity
//        startActivity(foodDetail);
//    }

    public class ListResources extends BaseAdapter {

        ArrayList<Food> listFood=new ArrayList<Food>();
        Context context;
        Activity activity;

        ListResources(ArrayList<Food> foods,Context context,Activity activity){
            Log.d("listCategory2",listFood.toString());
            this.listFood=foods;
            this.context=context;
            this.activity=activity;
        }
        @Override
        public int getCount() {
            return (listFood == null) ? 0 : listFood.size();

        }

        @Override
        public Object getItem(int i) {
            return listFood.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater=activity.getLayoutInflater();
            View row=inflater.inflate(R.layout.menu_item,viewGroup,false);

            //  menu_image=(ImageView)row.findViewById(R.id.menu_image);
            // menu_name=(TextView)row.findViewById(R.id.menu_name);
            final Food food=listFood.get(i);


            MenuViewHolder viewHolder=new MenuViewHolder(row,i);
            viewHolder.txtMenuName.setText(food.getName());
            Picasso.with(context).load(food.getImage()).into(viewHolder.imageView);
            viewHolder.setItemClickListener(new com.naser.omar.androideitserverphp.Interface.ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    // FoodIdعند الضغط على الطعام يتم نقل المستخدم الى نافذةمعلومات الطعال FoodDetail حاملا معها رقم الطعام أي المفتاح الأساس للطعام
                    // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    //Start new Activity
                    Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                    foodDetail.putExtra("FoodId",food.getFoodID());// Send Food Id to new Activity
                    startActivity(foodDetail);

                }
            });


            return row;
        }

    }



}
