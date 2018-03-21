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
import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Interface.ItemClickListener;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.Model.Order;
import com.naser.omar.androideitserverphp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by OmarNasser on 1/24/2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
                    ,View.OnCreateContextMenuListener

{

    public TextView txtMenuName;
    public ImageView imageView;
    int i=0;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView,int i) {
        super(itemView);
        txtMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);
        this.i=i;
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }


    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,getAdapterPosition(),i, Common.UPDATA);
        contextMenu.add(0,getAdapterPosition(),i, Common.DELETE);
    }
}


