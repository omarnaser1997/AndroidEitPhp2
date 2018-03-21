package com.naser.omar.androideitserverphp;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.net.Uri;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Listener.EndlessRecyclerViewScrollListener;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Service.ListenOrder;
import com.naser.omar.androideitserverphp.ViewHolder.FoodViewHolder;
import com.naser.omar.androideitserverphp.ViewHolder.MenuViewHolder;
import com.naser.omar.androideitserverphp.ViewHolder.MoviesAdapter;
import com.naser.omar.androideitserverphp.ViewHolder.RecyclerTouchListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,Response.Listener<String>

{

    // private List<Category> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    private ProgressBar progress_bar;
    private Boolean btnSelectBool=false;

    TextView txtFullName;
    GalleryPhoto galleryPhoto;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final int GALLERY_REQUEST = 13323;
    final int CAMERA_REQUEST = 22131;
    //View
    ListView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    //Add New Menu layout
    MaterialEditText edtName;
    FButton btnUpload,btnSelect;

    Uri saveUri;//this URI for select image
    private final int PICK_IMAGE_REQUEST=71;

    Category newCategory;
    DrawerLayout drawer;
    ImageView menu_image;
    TextView menu_name;
    String selectedPhoto;
    String encodedImage;
    CameraPhoto cameraPhoto;
    ArrayList<Category> productList;
    ArrayList<Category> productListjson;

    SwipeRefreshLayout swipeRefreshLayout;

    CounterFab fab;

    int userpage =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        galleryPhoto = new GalleryPhoto(getApplicationContext());

       // I written this Function for using Paper Library
        Paper.init(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent=new Intent(Home.this,Cart.class);//نافذه المشتريات
                startActivity(cartIntent);



            }
        });
        fab.setCount(new Database(this).getCountCart());//count item in tabel OrderDetail



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set Name for User
        View headerView = navigationView.getHeaderView(0);
        txtFullName=(TextView)headerView.findViewById(R.id.txtFullName2);
        txtFullName.setText(Common.currentUser.getName());


        ImageView drawerImage = (ImageView) headerView.findViewById(R.id.drawer_image);



try {
    Picasso.with(this).load(new Database(getApplicationContext()).getUser().get(0).getImage()).into(drawerImage);
}catch (Exception e){}
        //Load menu
        // recycler_menu=(ListView) findViewById(R.id.recycler_menu);
        // recycler_menu.setHasFixedSize(true);
        // layoutManager=new LinearLayoutManager(this);
        // recycler_menu.setLayoutManager(layoutManager);
        //menu_image=(ImageView) findViewById(R.id.menu_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
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
        //recyclerView.setItemAnimator(new DefaultItemAnimator());



        ///////////////////////////////////////////////////////////////////////
        //View
        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        //اعطاء الألوان لل
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //عند تحديث اللياوت
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectedToInternet(getBaseContext())){
                    loadMenu(userpage-2);
                }else {
                    Toast.makeText(getBaseContext(), "please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Defult , load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())){
                   // loadMenu(1);
                }else {
                    Toast.makeText(getBaseContext(), "please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        ///////////////////////////////////////////////////////////////////






//        loadMenu();


        // Register Service
       // Intent service= new Intent(Home.this, ListenOrder.class);
       // startService(service);



//        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getBaseContext());
//        recyclerView.setLayoutManager(linearLayoutManager);



        /////////////////////////////////////////////////////////////////// progress_bar
        progress_bar=(ProgressBar)findViewById(R.id.progress_bar);


        LinearLayoutManager mLayoutManagerr=new LinearLayoutManager(getApplicationContext());

        productList=new ArrayList<Category>();
        productListjson=new ArrayList<Category>();

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MoviesAdapter(productList,getApplicationContext(),this,selectedPhoto);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                userpage=page;
                loadMenu(page);
                //Toast.makeText(Home.this, ""+page, Toast.LENGTH_SHORT).show();


                progress_bar.setVisibility(View.VISIBLE);

            }
        });






    }




    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());//count item in tabel OrderDetail




    }

    public void fab2(View view) {
        showDialog();//this function for upload picture  ;)

    }

    private void showDialog(){
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_menu_layout,null);

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

                //   uploadImage();
                try {
                    uploadImage("insert",0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });



        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                loadMenu(userpage-1);
                //Hare , just create new category
                if (newCategory !=null)
                {// Snackbar.make(drawer,"New category"+newCategory.getName()+"was added",Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(Home.this, "New category"+newCategory.getName()+"was added", Toast.LENGTH_SHORT).show();
                }

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
                btnSelect.setText("SELECTED");

            }

        }
    }




    private void uploadImage(String statment,int item) throws FileNotFoundException {
        //item هذا البرمتر استفيد منه عند عملية التحديث بحيث عند عملية التحديث يتم تمريره على أنه رقم العنصر في القائمه
        //statment هذا البرمتر يمثل التعليمه التي ستم تنفيذها   delete or update
        HashMap<String, String> postData = new HashMap<String, String>();
        if(btnSelectBool==false){encodedImage=productList.get(item).getBase64();}
        if(!statment.equals("delete")) {
            try {
                Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(900, 600).getBitmap();


                ////////////////////////////////////////////////////////
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos);
                byte[] byteImage_photo = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);

                Log.d("43554654", encodedImage);
                ///////////////////////////////////////
            }catch (Exception e){}
            //if(btnSelectBool==false){encodedImage=productList.get(item).getBase64();}
            postData.put("image", encodedImage);
            postData.put("NameCategory", edtName.getText().toString());//productList.get(item).getCategoryID();

        }

        PostResponseAsyncTask task=new PostResponseAsyncTask(Home.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        });
        if(statment.equals("insert")) {
            // Toast.makeText(this, "insert", Toast.LENGTH_SHORT).show();
            task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/uplodeCategory.php");
        }else if(statment.equals("update")){
            // Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();

            postData.put("idCategory",productList.get(item).getCategoryID().toString());
            task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/updateCategory.php");
        }else if(statment.equals("delete")){
            Toast.makeText(this, productList.get(item).getName().toString(), Toast.LENGTH_SHORT).show();
            postData.put("idCategory",productList.get(item).getCategoryID().toString());
            task.execute("https://omarnaser.000webhostapp.com/AndroidEitServerPHP/deleteCategory.php");
            deletCategory(item);
        }

        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                Toast.makeText(getApplicationContext(),"Cannot Connect to Server",Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {
                Toast.makeText(getApplicationContext(),"URL Error",Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleProtocolException(ProtocolException e) {
                Toast.makeText(getApplicationContext(),"Protocol Error",Toast.LENGTH_LONG).show();

            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                Toast.makeText(getApplicationContext(),"Encodnig Error",Toast.LENGTH_LONG).show();


            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),PICK_IMAGE_REQUEST);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText(this, "لا تتوفر صلاحية لهذه العملية", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            loadMenu(userpage-1);//تحديث القائمة
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartintent =new Intent(Home.this,Cart.class);
            startActivity(cartintent);

        } else if (id == R.id.nav_orders) {
            Intent orderintent =new Intent(Home.this,OrderStatus.class);
            startActivity(orderintent);

        } else if (id == R.id.nav_log_out) {
            //Delete Remember user & Password
            //this destroy fu for delete All key value Saved after press logout(Exit app)
            Paper.book().destroy();


            //Logout
            Intent signIn =new Intent(Home.this,MainActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }
        else if (id == R.id.nav_change_pwd) {
            showChangePasswordDialog();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater =LayoutInflater.from(this);
        View layout_pwd =inflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText edtPassword =(MaterialEditText)layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = (MaterialEditText)layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword =(MaterialEditText)layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);


        //Butoon
        alertDialog.setPositiveButton("CHANE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Change Password here

                //For use SportDialog , please use AlertDialog from android.app  , not from v7 like above AlertDialog
                final android.app.AlertDialog waitingDialog=new SpotsDialog(Home.this);
                waitingDialog.show();

                //Check old password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    //Check new password and repeat password
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {


                        //Make update

                        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/ChangePassword.php";

                         StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                         new Response.Listener<String>() {
                             @Override
                             public void onResponse(String response) {
                                 waitingDialog.dismiss();
                                 Toast.makeText(Home.this, response, Toast.LENGTH_SHORT).show();

                          }
                    },     new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params =new HashMap<>();


                    params.put("Password",edtNewPassword.getText().toString());

                    params.put("Number",Common.currentUser.getNumber());
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "wrong old password", Toast.LENGTH_SHORT).show();
                }



            }
        });


        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    public void onResponse(String response) {

        //the list of page is fineshed
        if (response.contains("Array")){
            //when the page is fineshed i will hide last item in Recycler view
            Toast.makeText(this, "End of list (^_^)", Toast.LENGTH_SHORT).show();
        }


    try {
    productListjson = new JsonConverter<Category>().toArrayList(response, Category.class);//تحوي الجيسون الى كلاس
    productList.addAll(productListjson);

    }catch (Exception e){}



        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(),productList.size());

        swipeRefreshLayout.setRefreshing(false);//لايقاف التحديث

        progress_bar.setVisibility(View.GONE);


//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext()
//                , recyclerView, new RecyclerTouchListener .ClickListener() {
//            @Override
//            public void onClick(View view, int position) {//هذا التابع يتم تنفيذه عند الضعط على العنصر في القائمة
//
//                if ( position !=productList.size()) {
//                    Category category = productList.get(position);
//                    //  Toast.makeText(getApplicationContext(), category.getName() + " is selected!", Toast.LENGTH_SHORT).show();
//
//                    Intent foodList = new Intent(Home.this, FoodList.class);
//                    //Because CategoryId is Key , so we just get Key of this item
//                    foodList.putExtra("CategoryId", productList.get(position).getCategoryID());
//                    startActivity(foodList);
//
//                }
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));


    }

    private void loadMenu(int page) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/DBClass/getCategory.php?page="+page, (Response.Listener<String>) this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error while reading data",Toast.LENGTH_LONG).show();

            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return null;
            }

        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this, " "+item.getOrder(), Toast.LENGTH_SHORT).show();
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

    void deletCategory(int item) {
        productList.remove(item);
        mAdapter.notifyItemRemoved(item);
        mAdapter.notifyItemRangeChanged(item,productList.size());
        //uploadImage("delete",item);

        // loadMenu();
    }
    void showUpdateDialog(final int item) {
        //itemi هو عبارة عن رقم الالعنصر المضغوط في list View

        final   String nameCategory= productList.get(item).getName();
        final   String imageCategory=productList.get(item).getImage();
        final   String idCategory=productList.get(item).getCategoryID();

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);

        //set default name
        edtName.setText(nameCategory);

        //Event For Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSelectBool=true;
                LoadImage();

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadImage("update",item);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                loadMenu(userpage-1);

            }
        });
//        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        alertDialog.show();



//     String Url="https://omarnaser.000webhostapp.com/AppHrmosh/loginfromfacebook.php";
//
//      StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//
//                        }
//                    },     new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(Home.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }){
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> params =new HashMap<>();
//
//
//                    params.put("email",nameCategory);
//                    params.put("birthday",imageCategory);
//                    params.put("friends",idCategory);
//
//
//                    return params;
//                }
//            };
//            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//
//
//
//
//
//
    }




}
