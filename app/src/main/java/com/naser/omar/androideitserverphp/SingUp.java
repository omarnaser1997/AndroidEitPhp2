package com.naser.omar.androideitserverphp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.naser.omar.androideitserverphp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingUp extends AppCompatActivity {
    EditText edtPhone,edtPassword,edtName;
    Button btnSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        edtName=(EditText)findViewById(R.id.edtName);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        btnSignup=(Button)findViewById(R.id.btnSignup);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SingUp.this);
                    mDialog.setMessage(getString(R.string.please_waiting));
                    mDialog.show();


                    String Url = "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/SingUp.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                    User user = null;
                                    if (response != null) {
                                        mDialog.dismiss();
                                        Toast.makeText(SingUp.this, response, Toast.LENGTH_SHORT).show();

                                    } else {
                                        mDialog.dismiss();
                                        // user=new User(edtName.getText().toString(),edtPassword.getText().toString(),edtPhone.getText().toString());
                                        Toast.makeText(SingUp.this, getString(R.string.Sing_up_successfuly), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }


                                }
                            }
                            ,
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getString(R.string.Please_make_sure_you_are_connected_to_the_network), Toast.LENGTH_LONG).show();

                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Name", edtName.getText().toString());
                            params.put("Password", edtPassword.getText().toString());
                            params.put("Number", edtPhone.getText().toString());
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                }else {
                    Toast.makeText(SingUp.this, getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                    return;
                }

            }



        });
    }

}
