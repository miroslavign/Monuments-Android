/*
 * MonumentListRowHolder.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import novotek.com.monuments.R;

public class MonumentListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView monumentImage;
    protected TextView monumentName;
    protected TextView monumentType;
    protected TextView username;

    public MonumentListRowHolder(View view) {
        super(view);
        this.monumentImage = (ImageView) view.findViewById(R.id.monument_photo);
        this.monumentName = (TextView) view.findViewById(R.id.monument_name);
        this.monumentType = (TextView) view.findViewById(R.id.monument_type);
        this.username = (TextView) view.findViewById(R.id.user_name_added);
    }
}
