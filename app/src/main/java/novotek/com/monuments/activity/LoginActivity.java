/*
 * LoginActivity.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.database.MonumentUserDbHandler;
import novotek.com.monuments.events.UserLoginEvent;
import novotek.com.monuments.model.MonumentUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    private static final int PASS_ERROR = 0;
    private static final int ACCOUNT_NOT_EXISTING = 1;
    private static final int FIELD_VALIDATION_ERROR = 2;


    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    private ProgressDialog progressDialog;
    private MonumentUser mLoggeduser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setText(Html.fromHtml("No account yet? <b>Create one?</b>"));
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        // debug purposes only for speed up
        //_emailText.setText("zika@gmail.com");
        //_passwordText.setText("pera");
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

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(FIELD_VALIDATION_ERROR);
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        MonumentUser user = new MonumentUser(null, email, password);
        mLoggeduser = null;
        checkuser(user);
        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        Logger.d("Error log in ! ");
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Logger.d("Logged in ");
        Monuments.instance.setLoggedUser(mLoggeduser);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USER_NAME, mLoggeduser.getUserName());
        intent.putExtra(MainActivity.EXTRA_USER_ID, mLoggeduser.getUserId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(int error) {
        if (error == PASS_ERROR)
            Toast.makeText(getBaseContext(), "Incorrect Password !", Toast.LENGTH_LONG).show();
        else if (error == ACCOUNT_NOT_EXISTING)
            Toast.makeText(getBaseContext(), "Account does not exist !", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getBaseContext(), "Please type in correct data !", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void checkuser(MonumentUser user) {
        CheckUserTask checkUserTask = new CheckUserTask();
        checkUserTask.execute(user);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(final UserLoginEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        // simulate long running network task
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (event.user != null) {
                    // user found in DB, check pass ok
                    if (event.successLogin) {
                        mLoggeduser = event.user;
                        onLoginSuccess();
                    } else
                        onLoginFailed(PASS_ERROR);
                } else
                    onLoginFailed(ACCOUNT_NOT_EXISTING);
            }
        }, 1500);


    }

    private class CheckUserTask extends AsyncTask<MonumentUser, Void, Pair<MonumentUser, Integer>> {
        @Override
        protected Pair<MonumentUser, Integer> doInBackground(MonumentUser... users) {
            MonumentUserDbHandler monumentUserDbHandler  = Monuments.instance.getDb().userDbHandler;
            MonumentUser user = users[0];
            return monumentUserDbHandler.checkUser(user.getEmail(), user.getPassword());
        }

        @Override
        protected void onPostExecute(Pair<MonumentUser, Integer> result) {
            if (result.first != null) {
                if (result.second == 0)
                    EventBus.getDefault().postSticky(new UserLoginEvent(result.first, true));
                else
                    EventBus.getDefault().postSticky(new UserLoginEvent(result.first, false));
            } else
                EventBus.getDefault().postSticky(new UserLoginEvent(null, false));
        }
    }
}
