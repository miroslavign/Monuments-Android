/*
 * MonumentTypeListAdapter.java
 * Monuments-AndroidMonuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import novotek.com.monuments.R;
import novotek.com.monuments.model.MonumentType;

public class MonumentTypeListAdapter extends RecyclerView.Adapter<MonumentTypeListRowHolder>  {
    private static final String TAG = MonumentTypeListAdapter.class.getSimpleName();

    private List<MonumentType> monumentTypeList;

    private Context mContext;

    public MonumentTypeListAdapter(Context context, List<MonumentType> monumentTypeList) {
        this.monumentTypeList = monumentTypeList;
        this.mContext = context;
    }

    @Override
    public MonumentTypeListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.monument_type_list_row, null);
        MonumentTypeListRowHolder mh = new MonumentTypeListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(MonumentTypeListRowHolder feedListRowHolder, int position) {
        MonumentType oneMonumentType = monumentTypeList.get(position);
        feedListRowHolder.monumentTypeName.setText(oneMonumentType.typeName);
    }

    @Override
    public int getItemCount() {
        return (null != monumentTypeList ? monumentTypeList.size() : 0);
    }


}
