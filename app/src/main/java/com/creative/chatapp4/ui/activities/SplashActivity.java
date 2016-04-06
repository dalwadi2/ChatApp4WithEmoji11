package com.creative.chatapp4.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.creative.chatapp4.ApplicationSingleton;
import com.creative.chatapp4.R;
import com.creative.chatapp4.core.ChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;


public class SplashActivity extends Activity {


    public static String USER_LOGIN;
    public static String USER_PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Login to REST API
        //
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);

        USER_LOGIN = sharedPreferences.getString("userId", "");
        USER_PASSWORD = sharedPreferences.getString("password", "");

        if (USER_LOGIN.equalsIgnoreCase("") && USER_PASSWORD.equalsIgnoreCase("")) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
        else {
            final QBUser user = new QBUser();

            user.setLogin(USER_LOGIN);
            user.setPassword(USER_PASSWORD);

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
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
    }
}