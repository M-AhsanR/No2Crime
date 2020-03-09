package com.maher.n2c.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.maher.n2c.app.ApiStructure.ApiModelClass;
import com.maher.n2c.app.ApiStructure.Constants;
import com.maher.n2c.app.ApiStructure.ServerCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    ImageView go_forward;
    TextView sign_up_btn;
    EditText email, password;
    String timeZone;

    String firebaseToken;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String prefs = "user_credentials";

    private void PermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS},
                    123);
        }else {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            if (email.getText().toString().isEmpty()){
                email.startAnimation(shake);
                Toast.makeText(LoginActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            }else if (password.getText().toString().isEmpty()){
                password.startAnimation(shake);
                Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            }else {
                LoginApi();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

                firebaseToken = newToken;

            }
        });

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        go_forward = findViewById(R.id.go_forward);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        go_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionCheck();
            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        timeZone = TimeZone.getDefault().getID();

    }

    private void LoginApi(){
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email", email.getText().toString());
        postParam.put("password", password.getText().toString());
        postParam.put("device_token", firebaseToken);
        postParam.put("time_zone", String.valueOf(timeZone));
        postParam.put("platform", "android");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LOGIN, LoginActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){
                            String session_id = jsonObject.getString("session_id");
                            String token = jsonObject.getString("token");
                            JSONObject user = jsonObject.getJSONObject("user");

                            mEditor.putString("TOKEN", token);
                            mEditor.putString("SESSION_ID", session_id);
                            mEditor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            Toast.makeText(LoginActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
                                Manifest.permission.SEND_SMS},
                        123);
            }else {
                Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                if (email.getText().toString().isEmpty()){
                    email.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                }else if (password.getText().toString().isEmpty()){
                    password.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }else {
                    LoginApi();
                }
            }
        }
    }
}
