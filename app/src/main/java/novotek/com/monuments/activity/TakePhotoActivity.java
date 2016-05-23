
/*
 * TakePhotoActivity.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 4/5/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.photobyintent.AlbumStorageDirFactory;
import com.example.android.photobyintent.BaseAlbumDirFactory;
import com.example.android.photobyintent.FroyoAlbumDirFactory;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.events.PhotoTakenEvent;
import novotek.com.monuments.helper.Utils;

public class TakePhotoActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 101;
    private static final int REQUEST_APP_SETTINGS = 1001;

    private static final String ACTION_SEND_TO_AMAZON = "send_to_amazon";

    private String mCurrentPhotoPath;
    private String mImageName;
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private boolean mSendToAmazon = false;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.actionBack) TextView backAction;
    @Bind(R.id.caption)  TextView caption;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.needStoragePermissionLayout) RelativeLayout needStoragePermissionLayout;
    @Bind(R.id.permissionSettings) TextView permissionSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                try {
                    mSendToAmazon = Boolean.valueOf(extras.getString(ACTION_SEND_TO_AMAZON));
                } catch (Exception e) {}
            }
        }

        setContentView(R.layout.activity_take_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        init();
        onTakePhoto();
    }

    private void init() {
        caption.setText("Take Photo");
        permissionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().postSticky(new PhotoTakenEvent());
        finish();
    }

    public void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    private void hidePermission() {
        needStoragePermissionLayout.setVisibility(View.GONE);
    }

    private void onTakePhoto() {
        if (Utils.hasPermissionBasedSystem()) {
            if (askForStoragePermission())
                startTakingPhoto();
        } else {
            startTakingPhoto();
            hidePermission();
        }
    }

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Logger.e("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile(String uuid) throws IOException {
        File albumF = getAlbumDir();
        File imageF = new File(albumF, mImageName + JPEG_FILE_SUFFIX);
        return imageF;
    }

    /** creates image file in album by setname (uuid) **/
    public File setUpPhotoFile(String uuid) throws IOException {
        return createImageFile(uuid);
    }

    private void startTakingPhoto() {
        CreateFileTask createFileTask = new CreateFileTask();
        String uuid = Monuments.instance.generateUuid();
        mImageName = uuid;
        createFileTask.execute(mImageName);
    }

    private void takePhotoWithPermission(File f) {
        if (f != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
        } else {

        }
    }

    private class CreateFileTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... uuid) {
            File file = null;
            try {
                file = setUpPhotoFile(uuid[0]);
            } catch (IOException e) {
                mCurrentPhotoPath = null;
                e.printStackTrace();
                file = null;
            }
            return file;
        }
        @Override
        protected void onPostExecute(File result) {
            takePhotoWithPermission(result);
        }
    }

    private long findMediaId(String imageName){
        final String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE
        };
        imageName += Monuments.IMAGE_TYPE_SUFFIX;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                MediaStore.Images.Media.DISPLAY_NAME + "=\"" + imageName+"\"",
                null,
                null);
        cursor.moveToFirst();
        long id = -1;
        if(cursor.getCount()>0){
            id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
        }
        cursor.close();
        return id;
    }

    public boolean hasPermission(@NonNull String permission) {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission))
            return false;
        return true;
    }

    private void showResultNeedPermissionSnackbar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "In order to send a photo, we need permission to save it.",
                        Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /** adding to media gallery **/
    private void galleryAddPic(String path, final String imageName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        long id = findMediaId(imageName);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (mCurrentPhotoPath != null) {
                    // index it -> save to gallery
                    galleryAddPic(mCurrentPhotoPath, mImageName);
                    String amazonUrl = null;
                    EventBus.getDefault().postSticky(new PhotoTakenEvent(mImageName, mCurrentPhotoPath, amazonUrl, mSendToAmazon));
                }
            } else {
                EventBus.getDefault().postSticky(new PhotoTakenEvent());
            }
            finish();
        } else if (requestCode == REQUEST_APP_SETTINGS) {
            if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // got permission WRITE_EXTERNAL_STORAGE
                hidePermission();
                onTakePhoto();
            } else {
                // permission DENIED
                showResultNeedPermissionSnackbar();
            }
        }
    }

    private boolean askForStoragePermission() {
        // ask for permission
        // Here, thisActivity is the current activity
        if (ContextCompat.
                checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showNeedPermissionSnackbar();
            } else {
                startStoragePermission();
            }
            return false;
        } else { // we have permission without asking
            hidePermission();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hidePermission();
                    startTakingPhoto();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void showNeedPermissionSnackbar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "We need permission to take a photo.",
                        Snackbar.LENGTH_LONG)
                .setAction("Add", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startStoragePermission();
                    }
                });
        snackbar.show();
    }

    private void startStoragePermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_STORAGE);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
