package com.naser.omar.androideitserverphp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.json.JsonConverter;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSingin;
    TextView txtslogan;
    LoginButton login_button;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //just init Facebook into MainActivity
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        btnSingin=(Button)findViewById(R.id.btnSingnUp);
        txtslogan=(TextView)findViewById(R.id.txtSlogan);

        printKeyHash();

        loginWithFB();


        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtslogan.setTypeface(face);

        //Init Paper
        Paper.init(this); // using paper to help you write key_value To Android memory

        //Check remember
        checkRemeber();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //The CallbackManager manages
        // the callbacks into the FacebookSdk from an Activity's
        // or Fragment's onActivityResult() method.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //this function for Checked if user has already logged in
    private void checkRemeber() {
        String user = Paper.book().read(Common.USER_KEY);
        String pwd =Paper.book().read(Common.PWD_KEY);
        if (user !=null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
            {
                try{
                if(new Database(getBaseContext()).getUser()!=null) {
                    //login(user,pwd);
                    Intent homeintent = new Intent(MainActivity.this, Home.class);
                    Common.currentUser = new Database(getBaseContext()).getUser().get(0);
                    startActivity(homeintent);
                    finish();
                }}catch (Exception e){}
            }
        }
    }

    private void login(final String phone, final String pwd) {
        // i just copied login code from Signin.Class



        //التأكد من وجود انترنيت
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("please waiting ...");
            mDialog.show();


            String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/SingIn.php";
            StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            User user = null;
                            if(response != null)
                            {
                                mDialog.dismiss();

                                ArrayList<User> listUser = new JsonConverter<User>().toArrayList(response, User.class);
                                try{ user=listUser.get(0);}catch (Exception e){}

                                if(user.getPassword().equals(pwd))
                                {
                                    Intent homeintent=new Intent(MainActivity.this,Home.class);
                                    Common.currentUser=user;
                                    startActivity(homeintent);
                                    finish();
                                }else {
                                    Toast.makeText(MainActivity.this, "Wrong password !!!", Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                mDialog.dismiss();
                                Toast.makeText(MainActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();

                            }

                        }
                    },     new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext()," Please make sure you are connected to the network",Toast.LENGTH_LONG).show();

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params =new HashMap<>();
                    params.put("Phone",phone);
                    params.put("Password",pwd);
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);




        }else {
            Toast.makeText(MainActivity.this, "please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public void Singin(View view) {
        Intent Singin =new Intent(this,SingIn.class);
        startActivity(Singin);
    }

    public void Singup(View view) {
        Intent SingUp =new Intent(this,SingUp.class);
        startActivity(SingUp);
    }

    private void printKeyHash() {
        try{
            PackageInfo info =getPackageManager().getPackageInfo("com.naser.omar.androideitserver",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md =MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void loginWithFB(){
        callbackManager= CallbackManager.Factory.create();
        login_button=(LoginButton)findViewById(R.id.login_button);
        login_button.setReadPermissions("public_profile","email","user_birthday","user_friends");

        //callbackManager معلومات الحساب بعد تنفيذ onActivityResult
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //اذا نجح تسجيل الدخول
            @Override
            public void onSuccess(LoginResult loginResult) {






                GraphRequest request =GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {





                        ArrayList<String> arrayList =getData(object);
//                        Intent in =new Intent(MainActivity.this,Main2Activity.class);
//                        in.putExtra("url", arrayList.get(0));
//                        try{ in.putExtra("name", arrayList.get(1));}catch (Exception e){Log.d("eeeeh",e.toString());}
//                        in.putExtra("email", arrayList.get(2));
//                        startActivity(in);

                      //  Toast.makeText(MainActivity.this,  arrayList.get(1), Toast.LENGTH_SHORT).show();

                       // setDataInDBserver(arrayList.get(1),"facebook","096805489");

                        showPhoneNumberDialog(arrayList.get(1),arrayList.get(0));


                    }
                });




                //Request Graph API
                Bundle parameters=new Bundle();
                parameters.putString("fields","id,name,email,birthday,friends");

                request.setParameters(parameters);
                //  تنفيذ الطلب بشكل غير متزامن
                //  ستعود هذه الوظيفة على الفور و وسيتم معالجة الطلب على مؤشر ترابط منفصل من أجل معالجه نتيجه الطلب
                //  و تحديد ما إذا كان الطلب قد نجح أو فشل
                //  وهذا التابع يعيد الوظيفة المطلوبه منه
                request.executeAsync();



            }

            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {


            }
        });

    }

    private void setDataInDBserver(final String name, final String password, final String phone, final String image) {

        String Url = "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/singwithfacebook.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        if (response.contains("success")) {

                            //Save User and Password
                            saveUserandPasswor(phone,password);


                             User user= new User(name,"facebook",phone,"facebook",image);
                             Common.currentUser=user;
                             new Database(getBaseContext()).addToUser(user);


                            Intent homeintent = new Intent(MainActivity.this, Home.class);

                            startActivity(homeintent);
                            finish();

                        }


                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), " Please make sure you are connected to the network", Toast.LENGTH_LONG).show();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Name", name);
                params.put("Password", password);
                params.put("Number", phone);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void saveUserandPasswor(String phone,String Password) {
        //Paper help you write key_value to Android memory



            Paper.book().write(Common.USER_KEY,phone);
            Paper.book().write(Common.PWD_KEY,Password);

    }

    private ArrayList<String> getData(JSONObject object) {
        ArrayList arrayList =new ArrayList<String>();
        URL profile_Picture = null;
        try {
            profile_Picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");

            //Toast.makeText(myActivity, object.getString("password"), Toast.LENGTH_SHORT).show();

            arrayList.add(0,profile_Picture.toString());//object.getString("email")
            arrayList.add(1,object.getString("name"));//object.getString("name")
            arrayList.add(2,object.getString("email"));//profile_Picture.toString()

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();


        }



        return arrayList;
    }


    private void showPhoneNumberDialog(final String name , final String image) {
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Welcome "+name);
        alertDialog.setMessage("Please Enter Phone number !");

        LayoutInflater inflater =LayoutInflater.from(this);
        View layout_pwd =inflater.inflate(R.layout.setphonenumber,null);

        final MaterialEditText phone =(MaterialEditText)layout_pwd.findViewById(R.id.phoneNumber);


        alertDialog.setView(layout_pwd);


        //Butoon
        alertDialog.setPositiveButton("Oky !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Change Password here

                //For use SportDialog , please use AlertDialog from android.app  , not from v7 like above AlertDialog
                final android.app.AlertDialog waitingDialog=new SpotsDialog(MainActivity.this);
               // waitingDialog.show();



                setDataInDBserver(name,"facebook",phone.getText().toString(),image);


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

}