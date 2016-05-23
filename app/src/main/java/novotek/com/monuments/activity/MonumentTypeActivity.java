/*
 * MonumentTypeActivity.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.adapter.MonumentTypeListAdapter;
import novotek.com.monuments.database.MonumentTypeDbHandler;
import novotek.com.monuments.model.MonumentType;

public class MonumentTypeActivity extends AppCompatActivity {

    private static final String TAG = MonumentTypeActivity.class.getSimpleName();

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.input_type_name) EditText typeName;
    @Bind(R.id.btn_add) Button btnAdd;
    @Bind(R.id.typeList) RecyclerView typeListRecycleView;

    private MonumentTypeListAdapter adapter;
    private List<MonumentType> monumentTypeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monument_type);
        ButterKnife.bind(this);

        initRecycleView();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addType();
            }
        });
        getMonumentTypes();
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initRecycleView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        typeListRecycleView.setHasFixedSize(true);
        layoutManager.setStackFromEnd(true);
        typeListRecycleView.setLayoutManager(layoutManager);
    }


    private void gotTypesList(List<MonumentType> alltypes) {
        monumentTypeList = alltypes;
        adapter = new MonumentTypeListAdapter(this, monumentTypeList);
        typeListRecycleView.setAdapter(adapter);
    }

    private void addType() {
        AddMonumentTypeTask saveMonumentTask = new AddMonumentTypeTask();
        String name = typeName.getText().toString();
        if (!TextUtils.isEmpty(name))
            saveMonumentTask.execute(name);
    }

    private class AddMonumentTypeTask extends AsyncTask<String, Void, MonumentType> {
        @Override
        protected MonumentType doInBackground(String... names) {
            String typeName = names[0];
            MonumentTypeDbHandler monumentTypeDbHandler = Monuments.instance.getDb().monumentTypeDbHandler;
            return monumentTypeDbHandler.create(typeName);
        }

        @Override
        protected void onPostExecute(MonumentType result) {
            savedMonumentType(result);
        }
    }

    private void savedMonumentType(MonumentType monumentType) {
        if (monumentType != null) {
            showSnackbar("Saved " + monumentType.typeName);
            typeName.setText("");
            typeName.clearFocus();
            monumentTypeList.add(monumentType);
            //adapter.notifyItemInserted(0);
            adapter.notifyDataSetChanged();
        } else
            showSnackbar("Error saving monument type");
    }

    private void getMonumentTypes() {
        GetMonumentTypesTask getMonumentTypesTask = new GetMonumentTypesTask();
        getMonumentTypesTask.execute();
    }

    private class GetMonumentTypesTask extends AsyncTask<Void, Void, List<MonumentType>> {
        @Override
        protected List<MonumentType> doInBackground(Void... nothing) {
            MonumentTypeDbHandler monumentTypeDbHandler = Monuments.instance.getDb().monumentTypeDbHandler;
            return monumentTypeDbHandler.getAllTypesFull();
        }

        @Override
        protected void onPostExecute(List<MonumentType> result) {
            if (result != null) {
                gotTypesList(result);
            }
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
