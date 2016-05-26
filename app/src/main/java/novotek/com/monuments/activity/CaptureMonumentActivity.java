/*
 * CaptureMonumentActivity.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import novotek.com.monuments.database.MonumentDbHandler;
import novotek.com.monuments.database.MonumentTypeDbHandler;
import novotek.com.monuments.events.MonumentCreatedEvent;
import novotek.com.monuments.events.MonumentTypeCreatedEvent;
import novotek.com.monuments.events.PhotoTakenEvent;
import novotek.com.monuments.model.Monument;
import novotek.com.monuments.model.MonumentType;
import novotek.com.monuments.model.MonumentUser;


public class CaptureMonumentActivity extends AppCompatActivity {

    private static final String TAG = CaptureMonumentActivity.class.getSimpleName();

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.monument_photo) ImageView monumentPoto;
    @Bind(R.id.monument_name) EditText monumentName;
    @Bind(R.id.monument_description) EditText monumentDescription;
    @Bind(R.id.type) Spinner typeSpinner;
    @Bind(R.id.type_settings) ImageView typeSettings;
    @Bind(R.id.btn_save) Button btnSave;

    private ProgressDialog progressDialog;
    private MonumentUser mLoggeduser;
    private Uri mPhotoUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_monument);
        ButterKnife.bind(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMonument();
            }
        });

        typeSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // start add / remove monument type activity
                Intent intent = new Intent(CaptureMonumentActivity.this, MonumentTypeActivity.class);
                startActivity(intent);
            }
        });
        getMonumentTypes();
        onTakePhoto();
        monumentName.requestFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void gotTypesList(List<String> alltypes) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, alltypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(dataAdapter);
    }

    private void saveMonument() {
        String name = monumentName.getText().toString();
        String description = monumentDescription.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name should be entered",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Monument monument = new Monument(Monuments.instance.generateUuid(), name, description, mPhotoUri);
        String type = (String)typeSpinner.getSelectedItem();
        MonumentType monumentType = new MonumentType(type);
        monumentType.id = 0l;
        monument.setMonumentType(monumentType);
        monument.setUser(Monuments.instance.getLoggedUser());
        SaveMonumentTask saveMonumentTask = new SaveMonumentTask();
        saveMonumentTask.execute(monument);
    }


    private class SaveMonumentTask extends AsyncTask<Monument, Void, Monument> {
        @Override
        protected Monument doInBackground(Monument... monuments) {
            Monument monument = monuments[0];
            MonumentDbHandler monumentDbHandler = Monuments.instance.getDb().monumentDbHandler;
            if (monument != null) {
                MonumentTypeDbHandler monumentTypeDbHandler = Monuments.instance.getDb().monumentTypeDbHandler;
                MonumentType monumentType = monumentTypeDbHandler.getFromName(monument.getMonumentStringType());
                if (monumentType != null) {
                    monument.setMonumentType(monumentType);
                }
            }
            return monumentDbHandler.save(monument);
        }

        @Override
        protected void onPostExecute(Monument result) {
            if (result != null) {
                EventBus.getDefault().postSticky(new MonumentCreatedEvent(result));
                finish();
            }
        }
    }

    private void getMonumentTypes() {
        GetMonumentTypesTask getMonumentTypesTask = new GetMonumentTypesTask();
        getMonumentTypesTask.execute();
    }

    private class GetMonumentTypesTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... nothing) {
            MonumentTypeDbHandler monumentTypeDbHandler = Monuments.instance.getDb().monumentTypeDbHandler;
            return monumentTypeDbHandler.getAllTypes();
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                gotTypesList(result);
            }
        }
    }

    private void onTakePhoto() {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PhotoTakenEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        // reAct
        String currentPhotoPath = event.currentPhotoPath;
        if (TextUtils.isEmpty(currentPhotoPath))
            finish();

        mPhotoUri = Uri.fromFile(new File(currentPhotoPath));
        if (mPhotoUri != null)
            Picasso.with(this)
                    .load(mPhotoUri)
                    .centerCrop()
                    .fit()
                    .into(monumentPoto);
        else
            Picasso.with(this)
                    .cancelRequest(monumentPoto);
    }

    private void showSoftKeyboard() {
        monumentName.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(monumentName, InputMethodManager.SHOW_FORCED);
    }

    @Subscribe
    public void onEvent(MonumentTypeCreatedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        getMonumentTypes();
    }

}
