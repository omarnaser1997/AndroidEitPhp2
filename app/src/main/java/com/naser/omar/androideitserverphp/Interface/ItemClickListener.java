package com.naser.omar.androideitserverphp.Interface;

import android.view.View;

import com.naser.omar.androideitserverphp.ViewHolder.MoviesAdapter;

/**
 * Created by OmarNasser on 2/8/2018.
 */

public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
