package com.naser.omar.androideitserverphp.ViewHolder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Movie;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.FoodList;
import com.naser.omar.androideitserverphp.Home;
import com.naser.omar.androideitserverphp.Interface.ItemClickListener;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    int i=0;
    private List<Category> moviesList;
    private Context context;
    private Activity activity;
    private String selectedPhoto;
    MyViewHolder holder;


    public class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnCreateContextMenuListener{
        public TextView nameCategory;
        public ImageView imageCategory,imgoptions;
        public FloatingActionButton btnEndList;


        private ItemClickListener itemClickListener;

        void editNameCategory(String nameCatecory){

        }

        // int i=0;

        public MyViewHolder(View view,int i) {
            super(view);
            nameCategory = (TextView) view.findViewById(R.id.menu_name);
            imageCategory = (ImageView) view.findViewById(R.id.menu_image);
            imgoptions = (ImageView) view.findViewById(R.id.options);
//            btnEndList= (FloatingActionButton) view.findViewById(R.id.btnEndList);
            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(this);
            MoviesAdapter.this.i=i;

        }

//        public void setItemClickListener(ItemClickListener itemClickListener){
//            this.itemClickListener=itemClickListener;
//        }
//
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the action");
            contextMenu.add(0,0,getAdapterPosition(), Common.UPDATA);
            contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
        }


//        void removeAt(int position){
//            notifyItemRemoved(getAdapterPosition());
//            notifyItemRangeChanged(getAdapterPosition(),);
//        }
    }


    public MoviesAdapter(List<Category> moviesList, Context context, Activity activity,String selectedPhoto) {
        this.moviesList = moviesList;
        this.context=context;
        this.activity=activity;
        this.selectedPhoto=selectedPhoto;//this is path of selected photo from user
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //this method checks if the passed viewType is the item layout or the button layout
        //and then inflates the view occordingly and sends the inflated view to <<onBindViewHolder>> method

        View itemView;
        if(viewType == R.layout.layoutbutton){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layoutbutton, parent, false);
        }else {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_item, parent, false);
        }

        return new MyViewHolder(itemView,i);
    }

    // it will check if the current position is past the item in your list
    @Override
    public int getItemViewType(int position) {
        //if it is then it will return the button layout value to <<onCreateViewHolder>>  method
        return (position == moviesList.size())? R.layout.layoutbutton :R.layout.menu_item;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        this.holder=holder;
        //this checks if we are past the last item in the list
        //and if we are then it sets the onClick method to our button
        if(position ==moviesList.size()){
//            holder.btnEndList.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {//عند الضعط عل الزر الذي يظهر في أخر القائمة
//                    Toast.makeText(context, "END", Toast.LENGTH_SHORT).show();
//
//                }
//            });
        }else {

            Category category = moviesList.get(position);
            holder.nameCategory.setText(category.getName());

            Picasso.with(context).load(category.getImage())
                    //.resize(1400, 900)
                    .fit()
                    .centerCrop()
                    // .placeholder(R.drawable.progress_animation)//تعرض هذه الصورة الى ان يكتمل تحميل الصوره
                    .error(R.drawable.user_placeholder_error)
                    .into(holder.imageCategory);

          /////////////////////////////////////////////////////////////////////////////////
          //when you prassed on image option button
           holder.imgoptions.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   showDialogOption(position);//show Dialog for let the user choose one of the options in Dialog ((Delete / updata))
               }
           });

           //when you prassed on the image Category ,move to food List
           holder.imageCategory.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   Intent foodList = new Intent(context, FoodList.class);
                    //Because CategoryId is Key , so we just get Key of this item
                    foodList.putExtra("CategoryId", moviesList.get(position).getCategoryID());
                    activity.startActivity(foodList);
               }
           });
        }
    }

    private void showDialogOption(final int position) {

        FButton btnBack;
        final TextView Delete,updata;

        //Buid an AlertDialog
        AlertDialog.Builder builder =new AlertDialog.Builder(activity);
        builder.setTitle("select option");


        LayoutInflater inflater =activity.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.layout_option,null);

        // Specify alert dialog is not cancelable/not ignorable
        builder.setCancelable(false);

        //Set the Custom layout as alert dialog view
        builder.setView(add_menu_layout);


        //set Button
        //Get the custom alert dialog view widgets reference
        Delete=add_menu_layout.findViewById(R.id.Delete);
        updata=add_menu_layout.findViewById(R.id.updata);
        btnBack=add_menu_layout.findViewById(R.id.btnBack);

        //Create the alert dialog
        final AlertDialog dialog =builder.create();

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.cancel();
                    deleteCategory(position);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(position);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.setIcon(R.drawable.ic_menu_black_24dp);

        dialog.show();
    }
    void showUpdateDialog(final int position) {
        final MaterialEditText edtName;
        FButton btnBack,btnUpload;


        //Buid an AlertDialog
        AlertDialog.Builder builder =new AlertDialog.Builder(activity);
        builder.setTitle("Update Category");
        builder.setMessage("Please enter name category !");

        LayoutInflater inflater =activity.getLayoutInflater();
        View add_menu_layout =inflater.inflate(R.layout.add_new_menu_layout,null);

        // Specify alert dialog is not cancelable/not ignorable
        builder.setCancelable(false);
        //Set the Custom layout as alert dialog view
        builder.setView(add_menu_layout);
        //Get the custom alert dialog view widgets reference
        edtName=add_menu_layout.findViewById(R.id.edtName);
        btnBack=add_menu_layout.findViewById(R.id.btnBack);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);

        //Create the alert dialog
        final AlertDialog dialog =builder.create();

        //set default name
        edtName.setText(moviesList.get(position).getName().toString());

        //Event For Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updataCategory(position,edtName.getText().toString());
            }
        });

        dialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        dialog.show();


    }


    private void updataCategory(final int position,final String NameCategory){
        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/updateCategory.php";



        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();


                    }
                },     new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();

                params.put("idCategory",moviesList.get(position).getCategoryID().toString());
                params.put("NameCategory",NameCategory);

                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }

    private void deleteCategory(final int position) throws FileNotFoundException {


     String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/deleteCategory.php";



      StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            deletCategory(position);
                        }
                    },     new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params =new HashMap<>();

                    params.put("idCategory",moviesList.get(position).getCategoryID().toString());


                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);



    }

    void deletCategory(int position) {
        moviesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,moviesList.size());
        //uploadImage("delete",item);

        // loadMenu();
    }


    @Override
    public int getItemCount() {
        //ading a button at the end ,the + 1 is our button
        //نحن قمنا بوضع +1 لكي يتم اضافة عنصر الزر للقائمة والا لن يتم عرض الزر
        return (moviesList == null) ? 0 : moviesList.size() +1;
    }


}
