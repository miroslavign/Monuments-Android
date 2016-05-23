
/*
 * MonumentListRowHolder.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 12/26/2015
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import novotek.com.monuments.R;

public class MonumentTypeListRowHolder extends RecyclerView.ViewHolder {
    protected TextView monumentTypeName;

    public MonumentTypeListRowHolder(View view) {
        super(view);
        this.monumentTypeName = (TextView) view.findViewById(R.id.monument_type_name);
    }
}
