/*
 * MonumentListAdapter.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.helper.RoundedCornersTransformation;
import novotek.com.monuments.model.Monument;

public class MonumentListAdapter extends RecyclerView.Adapter<MonumentListRowHolder>  {
    private static final String TAG = MonumentListAdapter.class.getSimpleName();

    private List<Monument> monumentList;

    private Context mContext;
    RoundedCornersTransformation roundedCornersTransformation;
    private static final int RESIZE_TO_WIDTH = Monuments.MONUMENT_LIST_RESIZE_WIDTH;
    private static final int RESIZE_TO_HEIGHT = Monuments.MONUMENT_LIST_RESIZE_HEIGHT;
    private Picasso mPicasso;

    public MonumentListAdapter(Context context, List<Monument> conversationList,
                               Picasso picasso) {
        this.monumentList = conversationList;
        this.mContext = context;
        roundedCornersTransformation = new RoundedCornersTransformation(Monuments.ROUND_CORNER_SIZE_PICASSO, 0);
        mPicasso = picasso;
    }

    @Override
    public MonumentListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.monument_list_row, null);
        MonumentListRowHolder mh = new MonumentListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(MonumentListRowHolder feedListRowHolder, int position) {
        Monument oneMonument = monumentList.get(position);

        Uri photo = oneMonument.getMonumentUri();
        if (photo != null)
            mPicasso.load(photo)
                    //.transform(roundedCornersTransformation)
                    .fit()
                    .centerCrop()
                    .into(feedListRowHolder.monumentImage);
        else {
            mPicasso.cancelRequest(feedListRowHolder.monumentImage);
        }

        feedListRowHolder.monumentName.setText(oneMonument.getName());
        feedListRowHolder.monumentType.setText(oneMonument.getMonumentStringType());
        feedListRowHolder.username.setText("by " +oneMonument.getUserNameAdded());
    }

    @Override
    public int getItemCount() {
        return (null != monumentList ? monumentList.size() : 0);
    }


}
