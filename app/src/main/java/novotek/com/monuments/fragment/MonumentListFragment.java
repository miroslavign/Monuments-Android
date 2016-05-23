
/*
 * MonumentListFragment.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 12/26/2015
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.adapter.MonumentListAdapter;
import novotek.com.monuments.adapter.RecyclerItemClickListener;
import novotek.com.monuments.database.MonumentDbHandler;
import novotek.com.monuments.model.Monument;

public class MonumentListFragment extends Fragment {
    public static final String TAG = MonumentListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private List<Monument> monumentsList;
    private TextView listEmpty;
    LinearLayoutManager layoutManager;
    MonumentListAdapter adapter;
    Picasso mPicasso;
    Long mSelectedUserId;

    public MonumentListFragment() {
    }

    public static MonumentListFragment newInstance(int page, String title, Long userId) {
        MonumentListFragment fragment = new MonumentListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        fragment.setParams(userId);
        return fragment;
    }

    public void setParams(Long userId) {
        mSelectedUserId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Let this fragment contribute menu items
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mPicasso = Picasso.with(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.monument_list_fragment, container, false);
        mRecyclerView = (RecyclerView)inflatedView.findViewById(R.id.monument_list);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        mRecyclerView.setHasFixedSize(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Monument monument = monumentsList.get(position);
                // @TODO: 5/23/2016
            }
            @Override
            public void onItemLongClick(View view, int position) {
                // do not need it here, but will need it elsewhere
            }
        }));

        listEmpty = (TextView) inflatedView.findViewById(R.id.listEmpty);
        return inflatedView;
    }


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        initializeListAdapter(mSelectedUserId);
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
        if (mPicasso != null)
            mPicasso.resumeTag(TAG);
    }

    @Override
    public void onStop() {
        if (mPicasso != null)
            mPicasso.pauseTag(TAG);
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void initializeListAdapter(Monument monument) {
        if (monumentsList == null) {
            monumentsList = new ArrayList<>();
            listEmpty.setVisibility(View.GONE);
            adapter = new MonumentListAdapter(getActivity(), monumentsList, mPicasso);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.scrollToPosition(monumentsList.size());
            layoutManager.scrollToPosition(monumentsList.size());
        }

        monumentsList.add(monumentsList.size(), monument);
        adapter.notifyItemInserted(monumentsList.size());
        //adapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutManager.scrollToPosition(monumentsList.size());
                mRecyclerView.scrollToPosition(monumentsList.size());
            }
        }, 1250);

        listEmpty.setVisibility(View.GONE);
    }


    public void initializeListAdapter(Long userId) {
        new GetMonumentListTask().execute(userId);
    }

    public class GetMonumentListTask extends AsyncTask<Long, Void, List<Monument>> {
        @Override
        protected List<Monument> doInBackground(Long... params) {
            MonumentDbHandler monumentsDBHandler = Monuments.instance.getDb().monumentDbHandler;
            List<Monument> monuments = null;
            if (params != null && params.length > 0) {
                Long oneParam = params[0];
                if (oneParam != null)
                    monuments = monumentsDBHandler.getByUserId(oneParam);
                else
                    monuments = monumentsDBHandler.getByUserId(null);
            } else
                monuments = monumentsDBHandler.getByUserId(null);

            return monuments;
        }

        @Override
        protected void onPostExecute(List<Monument> result) {
            gotMonumentList(result);
        }
    }

    private void gotMonumentList(List<Monument> list) {
        if (list != null && list.size() > 0) {
            monumentsList = list;
            listEmpty.setVisibility(View.GONE);
            adapter = new MonumentListAdapter(getActivity(), monumentsList, mPicasso);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.scrollToPosition(monumentsList.size());
            layoutManager.scrollToPosition(monumentsList.size());
        } else {
            if (monumentsList != null) {
                monumentsList.clear();
                adapter.notifyDataSetChanged();
            }
            listEmpty.setVisibility(View.VISIBLE);
            Logger.d("no Monuments");
        }
    }

}
