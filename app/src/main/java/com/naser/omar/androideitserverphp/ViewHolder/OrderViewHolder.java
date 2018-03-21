package com.naser.omar.androideitserverphp.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naser.omar.androideitserverphp.Common.Common;
import com.naser.omar.androideitserverphp.Interface.ItemClickListener;
import com.naser.omar.androideitserverphp.Model.Food;
import com.naser.omar.androideitserverphp.Model.Request;
import com.naser.omar.androideitserverphp.OrderStatus;
import com.naser.omar.androideitserverphp.R;
import com.naser.omar.androideitserverphp.TrackingOrder;

import java.util.List;

/**
 * Created by OmarNasser on 1/29/2018.
 */

public class OrderViewHolder extends RecyclerView.Adapter<OrderViewHolder.myViewHolder>{

    private List<Request> requestList;
    private Context context;

    public OrderViewHolder(List<Request> requestList, Context context){
        this.requestList=requestList;
        this.context=context;
    }

    public class  myViewHolder extends RecyclerView.ViewHolder  implements
            View.OnClickListener,
            View.OnCreateContextMenuListener{


        private TextView txtOrderAddress,txtOrderId,txtOrderStatus,txtOrderPhone;
        private ItemClickListener itemClickListener;

        public void setItemClickListener (ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;

        }

        public myViewHolder(View View) {
            super(View);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
            txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
            txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
            txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
            txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);//عند تفعيل هذا لاسطر يحدث خطاء
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the action");
            contextMenu.add(0,0,getAdapterPosition(), Common.UPDATA);
            contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);

        }

    }



    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout,parent,false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        final Request request =requestList.get(position);
        holder.txtOrderAddress.setText(request.getAddress());
        holder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));//request.getStatus()
        holder.txtOrderId.setText(request.getName());
        holder.txtOrderPhone.setText(request.getPhone());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //هذا الحدث يتم تنفيذه عند الضغط على العنصر في الليستة
//                Intent trackingOrder=new Intent(context,TrackingOrder.class);
//                Common.currentRequest=request;
//                startActivity(trackingOrder);

            }
        });



    }


    @Override
    public int getItemCount() {
        return (requestList == null) ? 0 : requestList.size();
    }
}
