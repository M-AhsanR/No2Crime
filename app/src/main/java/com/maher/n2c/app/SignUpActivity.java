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
import android.widget.LinearLayout;
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

public class SignUpActivity extends AppCompatActivity {

    LinearLayout email_layout, name_layout, password_layout, gender_layout, age_layout;
    ImageView go_forward, male_gender, female_gender;
    TextView age_year;
    EditText email, password, confirm_password, age, f_name, l_name;
    String gender = "Male";
    String timeZone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String prefs = "user_credentials";

    String firebaseToken;

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
            SignupApi();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SignUpActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);
                firebaseToken = newToken;
            }
        });
        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        Initialization();
        Action();
        timeZone = TimeZone.getDefault().getID();
    }

    private void Action(){

        male_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_gender.setImageResource(R.drawable.ic_male_selected);
                female_gender.setImageResource(R.drawable.ic_female_unselected);
                gender = "Male";
            }
        });

        female_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_gender.setImageResource(R.drawable.ic_male_unselected);
                female_gender.setImageResource(R.drawable.ic_female_selected);
                gender = "Female";
            }
        });

        go_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChecksandFinalstep();
            }
        });
    }

    private void ChecksandFinalstep(){
        Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
        if (email_layout.getVisibility() == View.VISIBLE){
            if (email.getText().toString().isEmpty()){
                email.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            }else {
                email_layout.setVisibility(View.GONE);
                name_layout.setVisibility(View.VISIBLE);
            }
        }else if (name_layout.getVisibility() == View.VISIBLE){
            if (f_name.getText().toString().isEmpty()){
                f_name.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "First Name is empty", Toast.LENGTH_SHORT).show();
            }else if (l_name.getText().toString().isEmpty()){
                l_name.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "Last Name is empty", Toast.LENGTH_SHORT).show();
            } else {
                name_layout.setVisibility(View.GONE);
                password_layout.setVisibility(View.VISIBLE);
            }
        }else if (password_layout.getVisibility() == View.VISIBLE){
            if (password.getText().toString().isEmpty()){
                password.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            }else if (confirm_password.getText().toString().isEmpty()){
                confirm_password.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "Confirm Password is empty", Toast.LENGTH_SHORT).show();
            }else if (!password.getText().toString().equals(confirm_password.getText().toString())){
                password.startAnimation(shake);
                confirm_password.startAnimation(shake);
                password.setText("");
                confirm_password.setText("");
                Toast.makeText(SignUpActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            }else {
                password_layout.setVisibility(View.GONE);
                gender_layout.setVisibility(View.VISIBLE);
            }
        }else if (gender_layout.getVisibility() == View.VISIBLE){
            gender_layout.setVisibility(View.GONE);
            age_layout.setVisibility(View.VISIBLE);
        }else if (age_layout.getVisibility() == View.VISIBLE){
            if (age.getText().toString().isEmpty()){
                age.startAnimation(shake);
                age_year.startAnimation(shake);
                Toast.makeText(SignUpActivity.this, "Age is empty", Toast.LENGTH_SHORT).show();
            }else {
                PermissionCheck();
            }
        }
    }

    private void SignupApi(){
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email", email.getText().toString());
        postParam.put("password", password.getText().toString());
        postParam.put("first_name", f_name.getText().toString());
        postParam.put("last_name", l_name.getText().toString());
        postParam.put("gender", gender);
        postParam.put("age", age.getText().toString());
        postParam.put("device_token", firebaseToken);
        postParam.put("time_zone", String.valueOf(timeZone));
        postParam.put("platform", "android");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SIGNUP, SignUpActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Signup_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));

                        String session_id = jsonObject.getString("session_id");
                        String token = jsonObject.getString("token");
                        JSONObject user = jsonObject.getJSONObject("user");

                        mEditor.putString("TOKEN", token);
                        mEditor.putString("SESSION_ID", session_id);
                        mEditor.apply();

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(SignUpActivity.this, ERROR, Toast.LENGTH_SHORT).show();
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
                SignupApi();
            }
        }
    }

    private void Initialization(){
        email_layout = findViewById(R.id.email_layout);
        name_layout = findViewById(R.id.name_layout);
        password_layout = findViewById(R.id.password_layout);
        gender_layout = findViewById(R.id.gender_layout);
        age_layout = findViewById(R.id.age_layout);
        go_forward = findViewById(R.id.go_forward);
        male_gender = findViewById(R.id.male_gender);
        female_gender = findViewById(R.id.female_gender);
        email = findViewById(R.id.email_signup);
        f_name = findViewById(R.id.f_name_signup);
        l_name = findViewById(R.id.l_name_signup);
        password = findViewById(R.id.password_signup);
        confirm_password = findViewById(R.id.confirm_password_signup);
        age = findViewById(R.id.age_signup);
        age_year = findViewById(R.id.age_year);
    }
}
