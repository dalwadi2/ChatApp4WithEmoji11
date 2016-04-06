package com.creative.chatapp4.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.chatapp4.ApplicationSingleton;
import com.creative.chatapp4.R;
import com.creative.chatapp4.utils.AppConfig;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.HashMap;
import java.util.Map;

public class ResetActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private TextView Messageee;
    private ProgressDialog pDialog;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();

        inputEmail = (EditText) findViewById(R.id.resetemail);
        btnLogin = (Button) findViewById(R.id.btnForgot);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToLOGINScreen);
        Messageee = (TextView) findViewById(R.id.customMessege);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        Messageee.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                pDialog.setMessage("Sending Request ...");
                showDialog();
                hideKeyboard();
                email = inputEmail.getText().toString().trim();

                if (!email.isEmpty()) {
//                    checkLogin(email, password);
                    resetUser(email);
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
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void resetUser(final String email) {
        String tag_string_req = "req_reset";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RESET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                if (response.equalsIgnoreCase(" ")) {
                    Messageee.setVisibility(View.VISIBLE);
                    Messageee.setText("Password Reset link is send to your Registered MailID.");
                    btnLogin.setEnabled(false);
                    btnLogin.setBackgroundColor(R.color.gray_bg);
                } else {
                    Messageee.setVisibility(View.VISIBLE);
                    Messageee.setText("Please make sure your EmailId is correct.");
                    inputEmail.requestFocus();
                }
               /* Log.e(TAG, "onResponse: sadgasjk jasgdksadjk sagjhasgd");
                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.e("harsh", response);
                    if (!jObj.has("errors")) {
                        Messageee.setVisibility(View.VISIBLE);
                        Messageee.setText("Password Reset link is send to your Registered MailID.");
                        btnLogin.setEnabled(false);
                    } else {
                        Messageee.setVisibility(View.VISIBLE);
                        Messageee.setText("Please make sure your EmailId is correct.");
                        inputEmail.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Reset Error: " + error.getMessage());
                ErrorSnackBar();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                return params;
            }

        };
        ApplicationSingleton.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void hideKeyboard() {
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void ErrorSnackBar() {
        Snackbar.with(ResetActivity.this)
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
                .swipeToDismiss(true)
                .show(ResetActivity.this);
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
