package com.creative.chatapp4.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.creative.chatapp4.ApplicationSingleton;
import com.creative.chatapp4.R;
import com.creative.chatapp4.core.ChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Login to REST API
        //
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("userId", "").equalsIgnoreCase("") && sharedPreferences.getString("password", "").equalsIgnoreCase("")) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
        final QBUser user = new QBUser();
        user.setLogin(ApplicationSingleton.USER_LOGIN);
        user.setPassword(ApplicationSingleton.USER_PASSWORD);

        ChatService.getInstance().login(user, new QBEntityCallback<Void>() {

            @Override
            public void onSuccess(Void result, Bundle bundle) {
                // Go to Dialogs screen
                //
                Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }
}