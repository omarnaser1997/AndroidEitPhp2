package com.naser.omar.androideitserverphp.Database;

import android.app.Notification;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.naser.omar.androideitserverphp.Model.AppNotification;
import com.naser.omar.androideitserverphp.Model.Category;
import com.naser.omar.androideitserverphp.Model.Image64;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.Model.User;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarNasser on 1/29/2018.
 */

public class Database extends SQLiteAssetHelper{
    private static final String DB_NAME="EatltDB.db";//name of the database in folder assets
    private static final int DB_VER=1;//versions of the database

    public Database(Context context) {
        super(context, DB_NAME, null,DB_VER);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
//<OrderDetail TABLE>
    //this function for get data(CASRTS) from the database
    public List<Order> getCarts(){

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        //name Fields in the Table
        String[] sqlSelect={"ID,ProductName","Productid","Quantity","Price","Discount"};
        //name Table
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<Order> result=new ArrayList<>();
        if(c.moveToFirst()){

            do{
                result.add(new Order(
                          c.getInt(c.getColumnIndex("ID")),
                          c.getString(c.getColumnIndex("Productid")),
                          c.getString(c.getColumnIndex("ProductName")),
                          c.getString(c.getColumnIndex("Quantity")),
                          c.getString(c.getColumnIndex("Price")),
                          c.getString(c.getColumnIndex("Discount"))

                ));

            }while (c.moveToNext());
        }

     return  result;
    }
    //this function for set data(CASRTS) from the database
    public void addToCart(Order order) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO OrderDetail(Productid,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');",

                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount()  );
        db.execSQL(query);
    }
    //this function for delete data(CASRTS) from the database
    public void cleanCart() {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE  FROM OrderDetail");
        db.execSQL(query);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////
//<USER TABLE>

    //this function for get data(User) from the database
    public List<User> getUser(){

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        //name Fields in the Table
        String[] sqlSelect={"Name","Password","Number","secureCode","image"};
        //name Table
        String sqlTable="User";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<User> result=new ArrayList<>();
        if(c.moveToFirst()){

            do{
                result.add(new User (c.getString(c.getColumnIndex("Name")),
                        c.getString(c.getColumnIndex("Password")),
                        c.getString(c.getColumnIndex("Number")),
                        c.getString(c.getColumnIndex("secureCode")),
                        c.getString(c.getColumnIndex("image"))
                ));

            }while (c.moveToNext());
        }

        return  result;
    }

    //this function for set data(CASRTS) from the database
    public void addToUser(User user) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO User(Name,Password,Number,secureCode,image) VALUES ('%s','%s','%s','%s','%s');",

                user.getName(),
                user.getPassword(),
                user.getNumber(),
                user.getSecureCode(),
                user.getImage()
        );
        db.execSQL(query);
    }

    //this function for delete data(CASRTS) from the database
    public void cleanUser() {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE  FROM User");
        db.execSQL(query);
    }

/////////////////////////////////////////////////////////////////////////
//Favorites
    public void addToFavorites(String foodId){
    SQLiteDatabase db=getReadableDatabase();
    String query =String.format("INSERT INTO Favorites(Foodid)VALUES('%s');",foodId);
    db.execSQL(query);

}

    public void removeFavorites(String foodId){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("DELETE  FROM Favorites WHERE Foodid='%s'",foodId);
        db.execSQL(query);

    }

    public boolean isFavorites(String foodId){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("SELECT * FROM Favorites WHERE Foodid='%s'",foodId);
        Cursor cursor=db.rawQuery(query,null);
        final List<Order> result=new ArrayList<>();
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return  true;

    }



    public int getCountCart() {
        int count=0;

        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor=db.rawQuery(query,null);
        final List<Order> result=new ArrayList<>();
        if(cursor.moveToFirst())
        {

            do{
                count=cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        cursor.close();
        return  count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db=getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity=%s WHERE ID = %d",order.getQuantity(),order.getID());
        db.execSQL(query);
    }


    //////////////////////////////////////////////////////////////////////////

    public void addToImage(Image64 image64) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Image(Name,Image64bit,ID) VALUES ('%s','%s','%s');",

                image64.getName(),
                image64.getImage64bit(),
                image64.getId());

        db.execSQL(query);
    }


    public List<Image64> getImage64(){

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        //name Fields in the Table
        String[] sqlSelect={"Name","Image64bit","ID"};
        //name Table
        String sqlTable="Image";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<Image64> result=new ArrayList<>();
        if(c.moveToFirst()){

            do{
                result.add(new Image64 (
                        c.getString(c.getColumnIndex("Name")),
                        c.getString(c.getColumnIndex("Image64bit")),
                        c.getString(c.getColumnIndex("ID"))

                ));

            }while (c.moveToNext());
        }

        return  result;
    }

    public void deleteAllformImage(){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("DELETE  FROM Image");
        db.execSQL(query);

    }

  ///////////////////////////////////////////////////////////////////////////////////
  //Notificaion

    public List<AppNotification> getNotification(){

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        //name Fields in the Table
        String[] sqlSelect={"id,textNotification,view,title,imageURl"};
        //name Table
        String sqlTable="Notification";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<AppNotification> result=new ArrayList<>();
        if(c.moveToFirst()){

            do{
                result.add(new AppNotification(
                        c.getInt(c.getColumnIndex("id")),
                        c.getInt(c.getColumnIndex("view")),
                        c.getString(c.getColumnIndex("title")),
                        c.getString(c.getColumnIndex("textNotification")),
                        c.getString(c.getColumnIndex("imageURl"))


                                              )
                );

            }while (c.moveToNext());
        }

        return  result;
    }

    public void addNotification(AppNotification notification) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Notification(id,textNotification,title,view,imageURl) VALUES ('%s','%s','%s','%s','%s');",
                notification.getId(),
                notification.getTextNotification(),
                notification.getTitle(),
                notification.getView(),
                notification.getImageURL()

        );

            db.execSQL(query);

    }

    public List<Integer> getlistIDnotification(){

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        //name Fields in the Table
        String[] sqlSelect={"id"};
        //name Table
        String sqlTable="Notification";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<Integer> result=new ArrayList<>();
        if(c.moveToFirst()){

            do{
                result.add(c.getInt(c.getColumnIndex("id")));

            }while (c.moveToNext());
        }

        return  result;

    }

    public void updateViewNotificsation(AppNotification notification,int view) {
        SQLiteDatabase db=getReadableDatabase();
        String query = String.format("UPDATE Notification SET view=%s WHERE id = %d",
                view,notification.getId());
        db.execSQL(query);
    }



}
