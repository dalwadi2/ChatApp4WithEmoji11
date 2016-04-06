package com.creative.chatapp4.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creative.chatapp4.R;
import com.creative.chatapp4.core.ChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button btnLinkToReset;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    String email, password;
    public static String USER_LOGIN;
    public static String USER_PASSWORD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);
        USER_LOGIN = sharedPreferences.getString("userId", "");
        USER_PASSWORD = sharedPreferences.getString("password", "");
        Log.e(TAG, "onLogin: " + USER_LOGIN + USER_PASSWORD);
        if (USER_LOGIN.equalsIgnoreCase("") && USER_PASSWORD.equalsIgnoreCase("")) {
            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnLinkToReset = (Button) findViewById(R.id.btnLinkToResetScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLinkToReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResetActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                pDialog.setMessage("Loging in ...");
                showDialog();
                hideKeyboard();
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
//                    checkLogin(email, password);

                    final QBUser user = new QBUser();

                    user.setLogin(email);
                    user.setPassword(password);

                    ChatService.getInstance().login(user, new QBEntityCallback<Void>() {

                        @Override
                        public void onSuccess(Void result, Bundle bundle) {
                            // Go to Dialogs screen
                            //
                            hideDialog();
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("userId", email);
                            editor.putString("password", password);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, DialogsActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(QBResponseException errors) {
                            Toast.makeText(LoginActivity.this, "LoginId or Password is Incorrect", Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                    hideDialog();
                }
            }

        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void hideKeyboard() {
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
