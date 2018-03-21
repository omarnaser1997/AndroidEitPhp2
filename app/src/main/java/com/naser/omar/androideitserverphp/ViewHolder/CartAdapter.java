package com.naser.omar.androideitserverphp.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.naser.omar.androideitserverphp.Cart;
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Database.Database;
import com.naser.omar.androideitserverphp.Interface.ItemClickListener;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by OmarNasser on 1/29/2018.
 */


class CartviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        ,View.OnCreateContextMenuListener{


    public TextView txt_cart_name,txt_Price;
   // public ImageView img_cart_count;
    public ElegantNumberButton btn_quantity;
    private ItemClickListener itemClickListener;


    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }
    public CartviewHolder(View itemView) {
        super(itemView);
        txt_cart_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        txt_Price=(TextView)itemView.findViewById(R.id.cart_item_price);
        btn_quantity=(ElegantNumberButton)itemView.findViewById(R.id.btn_quantity);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);

    }
}


public class CartAdapter extends RecyclerView.Adapter<CartviewHolder> {

    private List<Order> listData =new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(cart);
        View itemView =inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartviewHolder holder,final int position) {
        TextDrawable drawable= TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.RED);

      //  holder.img_cart_count.setImageDrawable(drawable);


        holder.btn_quantity.setNumber(listData.get(position).getQuantity());

        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);


                //Update txtTOTAl
                //calculate total price
                int total=0;
                List <Order> orders =new Database(cart).getCarts();
               try{ for (Order item:orders) {
                    total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                }}catch (Exception e){}
                Locale locale =new Locale("en","US");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

                cart.txtTotalPrice.setText(fmt.format(total));



            }
        });


        Locale locale =new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        int price = 0;
        try {
            price = (Integer.parseInt(listData.get(position).getPrice()) * (Integer.parseInt(listData.get(position).getQuantity())));
        }catch (Exception e){}
        holder.txt_Price.setText(fmt.format(price));

        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
