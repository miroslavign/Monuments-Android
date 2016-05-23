/*
 * RegisterActivity.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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
import novotek.com.monuments.events.UserCreatedEvent;
import novotek.com.monuments.model.MonumentUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password_confirm) EditText _passwordConfirmText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setText(Html.fromHtml("Already a member? <b>Login</b>"));
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        MonumentUser user = new MonumentUser(name, email, password);
        createUser(user);
        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Logger.d("Saving OK");
        //setResult(RESULT_OK, null);
        gotoLogin();
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Logger.d("Saving NOK");

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConfirm = _passwordConfirmText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

        if (!passwordConfirm.equals(password)) {
            _passwordConfirmText.setError("password and confirm must be the same");
            valid = false;
        } else {
            _passwordConfirmText.setError(null);
        }

        return valid;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(UserCreatedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (progressDialog != null)
            progressDialog.dismiss();
        if (event.user != null) {
            // user created ok, now auto login ?
            onSignupSuccess();
        } else
            onSignupFailed();
    }

    private void createUser(MonumentUser user) {
        CreateUserTask createUserTask = new CreateUserTask();
        createUserTask.execute(user);
    }

    private class CreateUserTask extends AsyncTask<MonumentUser, Void, MonumentUser> {
        @Override
        protected MonumentUser doInBackground(MonumentUser... users) {
            MonumentUserDbHandler monumentUserDbHandler  = Monuments.instance.getDb().userDbHandler;
            return monumentUserDbHandler.create(users[0]);
        }

        @Override
        protected void onPostExecute(MonumentUser result) {
            EventBus.getDefault().postSticky(new UserCreatedEvent(result));
        }
    }
}
