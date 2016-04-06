package com.creative.chatapp4.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.chatapp4.ApplicationSingleton;
import com.creative.chatapp4.R;
import com.creative.chatapp4.core.ChatService;
import com.creative.chatapp4.utils.AppConfig;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

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
                            editor.putString("userId", email);
                            editor.putString("password", password);
                            editor.commit();
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

    /* private void checkLogin(final String email, final String password) {
         String tag_string_req = "req_login";

         pDialog.setMessage("Logging in ...");
         showDialog();

         StringRequest strReq = new StringRequest(Request.Method.POST,
                 AppConfig.URL_LOGIN, new Response.Listener<String>() {

             @Override
             public void onResponse(String response) {
 //                Log.d(TAG, "Login Response: " + response.toString());
                 hideDialog();

                 try {
                     JSONObject jObj = new JSONObject(response);
                     boolean error = jObj.getBoolean("error");

                     if (!error) {
                         // user successfully logged in
                         // Create login session
                         // Launch main activity
                         SharedPreferences sharedPreferences = getSharedPreferences("MyAppData", Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor = sharedPreferences.edit();
                         editor.putString("userId", email);
                         editor.putString("password", password);
                         editor.apply();
 //                        Log.e(TAG, "onResponse: " + jObj.getString("uid"));
                         Intent intent = new Intent(LoginActivity.this,
                                 SplashActivity.class);
                         startActivity(intent);
                         finish();
                     } else {
                         // Error in login. Get the error message
                         String errorMsg = jObj.getString("error_msg");
                         Toast.makeText(getApplicationContext(),
                                 errorMsg, Toast.LENGTH_LONG).show();
                     }
                 } catch (JSONException e) {
                     // JSON error
                     e.printStackTrace();
                     Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                 }

             }
         }, new Response.ErrorListener() {

             @Override
             public void onErrorResponse(VolleyError error) {
 //                Log.e(TAG, "Login Error: " + error.getMessage());
                 ErrorSnackBar();
                 hideDialog();
             }
         }) {

             @Override
             protected Map<String, String> getParams() {
                 // Posting parameters to login url
                 Map<String, String> params = new HashMap<String, String>();

                 params.put("email", email);
                 params.put("password", password);

                 return params;
             }

         };
         // Adding request to request queue
         ApplicationSingleton.getInstance().addToRequestQueue(strReq, tag_string_req);
     }

     private void ErrorSnackBar() {
         Snackbar.with(LoginActivity.this)
                 .type(SnackbarType.MULTI_LINE)
                 .text("Check Internet Connection")
                 .actionLabel("Done")
                 .actionColor(Color.CYAN)
                 .actionListener(new ActionClickListener() {
                     @Override
                     public void onActionClicked(Snackbar snackbar) {
                         startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                     }
                 })
                 .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                 .swipeToDismiss(false)
                 .show(LoginActivity.this);
     }

 */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
