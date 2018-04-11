package com.naser.omar.androideitserverphp.ViewHolder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.FoodDetail;
import com.naser.omar.androideitserverphp.FoodList;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.MySingleton;
import com.naser.omar.androideitserverphp.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.naser.omar.androideitserverphp.Interface.ItemClickListener;
import com.squareup.picasso.Target;

import info.hoang8f.widget.FButton;

/**
 * Created by OmarNasser on 2/14/2018.
 */

public class FoodAdapter  extends RecyclerView.Adapter<FoodAdapter.myViewHolder> {

    private List<Food> foodList;
    private Context context;
    final int GALLERY_REQUEST = 13323;
    GalleryPhoto galleryPhoto;
    //Favorites
    Database localDB;
    private Activity activity=null;

    //Facebook share
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    //create Target from Picasso
    Target target =new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            //Create Photo from Bitmap
            SharePhoto photo =new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public class  myViewHolder extends RecyclerView.ViewHolder  implements
            View.OnClickListener,
            View.OnCreateContextMenuListener{
        public TextView nameFood;
        public ImageView imageFood,fav_image,optionsFood,share_image,quick_cart;
        private ItemClickListener itemClickListener;

        public void setItemClickListener (ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;

        }

        public myViewHolder(View View) {
            super(View);
            itemView.setOnCreateContextMenuListener(activity);
            //LocalDB
            localDB=new Database(context);

            itemView.setOnClickListener(this);
            nameFood=(TextView) itemView.findViewById(R.id.food_name);
            imageFood=(ImageView) itemView.findViewById(R.id.food_image);
            fav_image=(ImageView) itemView.findViewById(R.id.fav);
            optionsFood=(ImageView) itemView.findViewById(R.id.optionsFood);
            share_image=(ImageView) itemView.findViewById(R.id.btnShare);
            galleryPhoto = new GalleryPhoto(context);
            quick_cart=(ImageView) itemView.findViewById(R.id.btn_quick_cart);





        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the action");
            contextMenu.add(0,0,getAdapterPosition(), Common.UPDATA);
            contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);

        }

    }


    public FoodAdapter(List<Food> foodList, Context context ,Activity activity){
        this.foodList=foodList;
        this.context=context;
        this.activity=activity;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater .from(parent.getContext())
                .inflate(R.layout.food_item,parent,false);




        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
        final Food food =foodList.get(position);
        holder.nameFood.setText(food.getName());
        Picasso.with(context).load(food.getImage())
                .fit()
                .centerCrop()
              //.resize(1400, 900)
               // .placeholder(R.drawable.progress_animation)//تعرض هذه الصورة الى ان يكتمل تحميل الصوره
                .error(R.drawable.user_placeholder_error)
                .into(holder.imageFood);


//set button
////////////////////////////////////////////////////////////////////////////////////////////////////////
        //when you press in food image
        holder.imageFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "BBBBBBBBBBBBBBB", Toast.LENGTH_SHORT).show();
                Food food = foodList.get(position);
               // Toast.makeText(getApplicationContext(), food.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                // FoodIdعند الضغط على الطعام يتم نقل المستخدم الى نافذةمعلومات الطعال FoodDetail حاملا معها رقم الطعام أي المفتاح الأساس للطعام
                // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                //Start new Activity

                Intent foodDetail=new Intent(activity.getBaseContext(),FoodDetail.class);
                foodDetail.putExtra("FoodId",food.getFoodID());// Send Food Id to new Activity
                activity.startActivity(foodDetail);


            }
        });



       // when you press in favorite button
        holder.fav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is the primary key of food    <<  foodList.get(position).getFoodID()  >>

                //Here , I will check if this food Existing in tabel Favorite food at local Database
                if (!localDB.isFavorites(foodList.get(position).getFoodID()))
                {   //if this food not favorite  < in another meaning>  this food not Existing in Foavorite food Tabel

                    //here ,i will add the food as favorite in Database
                    localDB.addToFavorites(foodList.get(position).getFoodID());
                    holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(context, ""+foodList.get(position).getName()+"was added to Favorites", Toast.LENGTH_SHORT).show();
                }else
                {
                    //if this food Existing as favorite in Database

                    //I will delete the food from Tabel favorite in local Database
                    localDB.removeFavorites(foodList.get(position).getFoodID());
                    holder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(context, ""+foodList.get(position).getName()+"was removed from Favorites", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //عند الضغط على زر المشاركة
        holder.share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.with(context)
                        .load(food.getImage())
                        .into(target);

            }
        });

        //this function for check if the food is favorite on not
        searchOfFavoriteFoods(position,holder);

        //when you prass in option image
        holder.optionsFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOption(position);
            }
        });

        //Init Facebook
        callbackManager =CallbackManager.Factory.create();
        shareDialog=new ShareDialog(activity);




        holder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(context).addToCart(new Order(
                        food.getFoodID(),
                        food.getName(),
                        "1",
                        food.getPrice(),
                        food.getDiscount()

                ));
                Toast.makeText(activity, "Added to Cart", Toast.LENGTH_SHORT).show();

            }
        });
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

                    dialog.cancel();

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

//    private void showUpdateDialog(int item) {
//        MaterialEditText edtName,edtDescription,edtDiscount,edtPrice;
//        FButton btnSelect,btnUpload;
//
//
//        final   String nameFood= foodList.get(item).getName();
//        final   String imageFood=foodList.get(item).getImage();
//        final   String idFood=foodList.get(item).getFoodID();
//        final   String DescriptionFood=foodList.get(item).getDescription();
//        final   String DiscountFood=foodList.get(item).getDiscount();
//        final   String PriceFood=foodList.get(item).getPrice();
//
//        AlertDialog.Builder alertDialog =new AlertDialog.Builder(activity);
//        alertDialog.setTitle("Edit Food");
//        alertDialog.setMessage("Please fill full information");
//
//        LayoutInflater inflater =activity.getLayoutInflater();
//        View add_menu_layout =inflater.inflate(R.layout.add_new_food_layout,null);
//
//        edtName=add_menu_layout.findViewById(R.id.edtName);
//        edtDescription=add_menu_layout.findViewById(R.id.edtDescription);
//        edtPrice=add_menu_layout.findViewById(R.id.edtPrice);
//        edtDiscount=add_menu_layout.findViewById(R.id.edtDiscount);
//
//        //Set default value for view
//        edtName.setText(nameFood);
//        edtDescription.setText(DescriptionFood);
//        edtPrice.setText(PriceFood);
//        edtDiscount.setText(DiscountFood);
//
//        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
//        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);
//
//        //Event For Button
//        btnSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                LoadImage();
//
//            }
//        });
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                try {
////                    //item رقم العنصر في الليستا
////                  //  uploadImage("update",item);
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                }
//                Toast.makeText(context, "uuuuuuuuuuuu", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        alertDialog.setView(add_menu_layout);
//        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
//        //set button
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                //loadMenu();
//
//            }
//        });
////        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                dialogInterface.dismiss();
////            }
////        });
//        alertDialog.show();
//
//    }

    private void LoadImage() {

            activity.startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);

    }



    private void searchOfFavoriteFoods(int position,final myViewHolder holder) {
        if (localDB.isFavorites(foodList.get(position).getFoodID()))
        {
            holder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else
        {
            holder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return (foodList == null) ? 0 : foodList.size();
    }

    private void deleteCategory(final int position) throws FileNotFoundException {
        ///////////////////////////////////////
        final ProgressDialog mDialog =new ProgressDialog(activity);
        mDialog.setMessage("deledting ...");
        mDialog.show();
        //////////////////////////////////////

        String Url="https://omarnaser.000webhostapp.com/AndroidEitServerPHP/deleteFood.php";



        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
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

                params.put("FoodID",foodList.get(position).getFoodID());

                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);



    }

    void deletCategory(int position) {
        foodList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,foodList.size());
    }

    public void delete(int position){
        foodList.remove(position);
        notifyItemRemoved(position);
    }

    public int getSize(){
        return foodList.size();
    }

}
