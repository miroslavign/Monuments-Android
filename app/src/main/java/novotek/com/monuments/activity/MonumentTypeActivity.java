package novotek.com.monuments.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.adapter.MonumentListAdapter;
import novotek.com.monuments.adapter.MonumentTypeListAdapter;
import novotek.com.monuments.database.MonumentDbHandler;
import novotek.com.monuments.database.MonumentTypeDbHandler;
import novotek.com.monuments.events.MonumentCreatedEvent;
import novotek.com.monuments.events.PhotoTakenEvent;
import novotek.com.monuments.model.Monument;
import novotek.com.monuments.model.MonumentType;
import novotek.com.monuments.model.MonumentUser;

/**
 * Created by BX on 5/23/2016.
 */
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
