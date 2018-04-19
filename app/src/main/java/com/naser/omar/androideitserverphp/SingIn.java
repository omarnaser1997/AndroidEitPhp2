package com.naser.omar.androideitserverphp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.json.JsonConverter;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.Model.Product;
import com.naser.omar.androideitserverphp.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class SingIn extends Activity {
    EditText edtPhone,edtPassword;
    Button btnSignIn;
    com.rey.material.widget.CheckBox ckbRemember;
    TextView txtForgotPwd;
    TextView txtslogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        ckbRemember=(com.rey.material.widget.CheckBox)findViewById(R.id.ckbRemember);
        txtForgotPwd=(TextView)findViewById(R.id.txtForgotpwd);
        txtslogan=(TextView)findViewById(R.id.txtSlogan1);
        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/ToyorAljanahFat.otf");
        txtslogan.setTypeface(face);
        //Init Paper from library PAPER
        Paper.init(this);


        //when you press the txtForgotPassword
        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotpwdDialog();
            }

        });

    }

    private void showForgotpwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Forgot_Password));
        builder.setMessage(getString(R.string.Enter_your_secure_code));

        LayoutInflater inflater=this.getLayoutInflater();
        View forgot_view=inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone=(MaterialEditText)forgot_view.findViewById(R.id.edtphone);
        final MaterialEditText edtSecureCode =(MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);


        builder.setPositiveButton(getString(R.string.YES), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Check if User available
               //when you press the yes button ,Go Application to Database and get password using SecureCode for user


                final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
                mDialog.setMessage(getString(R.string.please_waiting));
                mDialog.show();


                String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/forgotpassword.php";
                StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                User user = null;
                                if(response != null) {
                                    mDialog.dismiss();

                                    ArrayList<User> listUser = new JsonConverter<User>().toArrayList(response, User.class);



                                    try {
                                        user = listUser.get(0);
                                    } catch (Exception e) {
                                    }
                                   // Toast.makeText(SingIn.this, user.getSecureCode(), Toast.LENGTH_SHORT).show();

                                    if (user.getSecureCode().equals(edtSecureCode.getText().toString())) {
                                        Toast.makeText(SingIn.this, getString(R.string.your_password) + user.getPassword(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SingIn.this, getString(R.string.wrong_secure_code), Toast.LENGTH_LONG).show();

                                   }
                                }
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
                        params.put("Phone",edtPhone.getText().toString());
                        params.put("SecureCode",edtSecureCode.getText().toString());
                        return params;
                    }
                };
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            }
        });

        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //when you press th No button , the application exits from  AlertDialog

            }
        });
        builder.show();

    }


    public void login(View view) {

        //التأكد من وجود انترنيت
        if (Common.isConnectedToInternet(getBaseContext())) {

            //Save User and Password
            saveUserandPasswor();


        final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
        mDialog.setMessage(getString(R.string.please_waiting));
        mDialog.show();

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/SingIn.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        User user = null;
                        if(response != null) {
                            mDialog.dismiss();

                            ArrayList<User> listUser = new JsonConverter<User>().toArrayList(response, User.class);
                            try {
                                new Database(getBaseContext()).addToUser(listUser.get(0));
                                user = listUser.get(0);
                            } catch (Exception e) {
                            }
                            try {
                            if (user.getPassword().equals(edtPassword.getText().toString())) {
                                Intent homeintent = new Intent(SingIn.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeintent);
                                finish();
                            } else {
                                Toast.makeText(SingIn.this, getString(R.string.Wrong_password), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                                Toast.makeText(SingIn.this, getString(R.string.Wrong_password), Toast.LENGTH_SHORT).show();

                            }
                        }else
                        {
                            mDialog.dismiss();
                            Toast.makeText(SingIn.this, getString(R.string.User_not_exist_in_Database), Toast.LENGTH_SHORT).show();

                        }

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
                params.put("Phone",edtPhone.getText().toString());
                params.put("Password",edtPassword.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        }else {
            Toast.makeText(SingIn.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
            return;
        }

    }


    //this function to save user & password if checkbox is checked
    private void saveUserandPasswor() {
        //Paper help you write key_value to Android memory

        if (ckbRemember.isChecked())
        {
            Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
            Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
        }
    }
}
