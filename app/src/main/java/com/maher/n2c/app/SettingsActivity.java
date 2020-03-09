package com.maher.n2c.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.maher.n2c.app.ApiStructure.ApiModelClass;
import com.maher.n2c.app.ApiStructure.Constants;
import com.maher.n2c.app.ApiStructure.ServerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    ImageView back_btn, add_number;
    TextView first, second, third, fourth, fifth, name1, name2, name3, name4, name5, alarmTab, profileTab;
    LinearLayout profileLayout, alarmLayout;
    EditText f_name, l_name, address, city, post, age;
    TextView male, female, save_btn;

    ArrayList<ContactsModel> contactsArray = new ArrayList<>();
    String gender;
    public static final int PICK_CONTACT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Initialization();
        GetProfileData();
        Action();
        GetFvrtContacts();

    }

    private void GetProfileData(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, List<ContactsModel>> postParam = new HashMap<String, List<ContactsModel>>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponseArray(Request.Method.GET, Constants.URL.GETPROFILE, SettingsActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));

                        if (jsonObject != null && !String.valueOf(jsonObject).isEmpty()){
                            JSONObject user = jsonObject.getJSONObject("user");

                            gender = user.getString("gender");
                            int ageInt = user.getInt("age");
                            String first_name = user.getString("first_name");
                            String last_name = user.getString("last_name");
                            String addressString = user.getString("address");
                            String cityString = user.getString("city");
                            String postString = user.getString("post");

                            f_name.setText(first_name);
                            l_name.setText(last_name);
                            address.setText(addressString);
                            city.setText(cityString);
                            post.setText(postString);
                            age.setText(String.valueOf(ageInt));

                            if (gender.equals("male")){
                                male.setBackground(getResources().getDrawable(R.drawable.settings_male_background_selected));
                                male.setTextColor(getResources().getColor(R.color.white));
                                female.setBackgroundColor(getResources().getColor(R.color.transparent));
                                female.setTextColor(getResources().getColor(R.color.black));
                            }else {
                                female.setBackground(getResources().getDrawable(R.drawable.settings_female_background));
                                female.setTextColor(getResources().getColor(R.color.white));
                                male.setBackgroundColor(getResources().getColor(R.color.transparent));
                                male.setTextColor(getResources().getColor(R.color.black));
                            }
                        }else {
                            Toast.makeText(SettingsActivity.this, "Something wrong happened!!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateProfileData(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String,String> postParam = new HashMap<String, String>();
        postParam.put("age", age.getText().toString());
        postParam.put("gender", gender);
        postParam.put("first_name", f_name.getText().toString());
        postParam.put("last_name", l_name.getText().toString());
        postParam.put("address", address.getText().toString());
        postParam.put("city", city.getText().toString());
        postParam.put("post", post.getText().toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEPROFILE, SettingsActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));

                        int code = jsonObject.getInt("code");

                        if (code == 200){
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SettingsActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Action(){

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfileData();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setBackground(getResources().getDrawable(R.drawable.settings_male_background_selected));
                male.setTextColor(getResources().getColor(R.color.white));
                female.setBackgroundColor(getResources().getColor(R.color.transparent));
                female.setTextColor(getResources().getColor(R.color.black));
                gender = "male";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.setBackground(getResources().getDrawable(R.drawable.settings_female_background));
                female.setTextColor(getResources().getColor(R.color.white));
                male.setBackgroundColor(getResources().getColor(R.color.transparent));
                male.setTextColor(getResources().getColor(R.color.black));
                gender = "female";
            }
        });

        alarmTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmTab.setTextColor(getResources().getColor(R.color.black));
                profileTab.setTextColor(getResources().getColor(R.color.gray));
                profileLayout.setVisibility(View.GONE);
                alarmLayout.setVisibility(View.VISIBLE);
            }
        });

        profileTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmTab.setTextColor(getResources().getColor(R.color.gray));
                profileTab.setTextColor(getResources().getColor(R.color.black));
                profileLayout.setVisibility(View.VISIBLE);
                alarmLayout.setVisibility(View.GONE);
            }
        });

        add_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fifth.getText().toString().isEmpty()){
                    launchMultiplePhonePicker();
                }else {
                    Toast.makeText(SettingsActivity.this, "You have reached the maximum limit of contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void launchMultiplePhonePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                if (cursor.moveToFirst()) {

                    ContactsModel contactsModel = new ContactsModel();
                    int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String number = cursor.getString(numberColumn);
                    String name = cursor.getString(nameColumn);
                    if (first.getText().toString().isEmpty()) {
                        first.setText(number);
                        name1.setText(name);
                        contactsModel.setNumber(number);
                        contactsModel.setName(name);
                        contactsArray.add(contactsModel);
                    } else if (second.getText().toString().isEmpty()) {
                        second.setText(number);
                        name2.setText(name);
                        contactsModel.setNumber(number);
                        contactsModel.setName(name);
                        contactsArray.add(contactsModel);
                    } else if (third.getText().toString().isEmpty()) {
                        third.setText(number);
                        name3.setText(name);
                        contactsModel.setNumber(number);
                        contactsModel.setName(name);
                        contactsArray.add(contactsModel);
                    } else if (fourth.getText().toString().isEmpty()) {
                        fourth.setText(number);
                        name4.setText(name);
                        contactsModel.setNumber(number);
                        contactsModel.setName(name);
                        contactsArray.add(contactsModel);
                    } else if (fifth.getText().toString().isEmpty()) {
                        fifth.setText(number);
                        name5.setText(name);
                        contactsModel.setNumber(number);
                        contactsModel.setName(name);
                        contactsArray.add(contactsModel);

                        add_number.setVisibility(View.GONE);
                    }
                    // TODO Fetch other Contact details as you want to use

                    AddFvrtContacts();

                }
            }
        }
    }

    private void GetFvrtContacts(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, List<ContactsModel>> postParam = new HashMap<String, List<ContactsModel>>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponseArray(Request.Method.GET, Constants.URL.GET_FVRT, SettingsActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){

                            JSONArray favourite_contacts = jsonObject.getJSONArray("favourite_contacts");
                            for (int i = 0; i < favourite_contacts.length(); i++){
                                JSONObject object = favourite_contacts.getJSONObject(i);
                                ContactsModel contactsModel = new ContactsModel();

                                String name = object.getString("name");
                                String number = object.getString("number");

                                contactsModel.setName(name);
                                contactsModel.setNumber(number);

                                contactsArray.add(contactsModel);
                            }

                            if (contactsArray.size() == 1){
                                name1.setText(contactsArray.get(0).getName());
                                first.setText(contactsArray.get(0).getNumber());
                            }else if (contactsArray.size() == 2){
                                name1.setText(contactsArray.get(0).getName());
                                first.setText(contactsArray.get(0).getNumber());
                                name2.setText(contactsArray.get(1).getName());
                                second.setText(contactsArray.get(1).getNumber());
                            }else if (contactsArray.size() == 3){
                                name1.setText(contactsArray.get(0).getName());
                                first.setText(contactsArray.get(0).getNumber());
                                name2.setText(contactsArray.get(1).getName());
                                second.setText(contactsArray.get(1).getNumber());
                                name3.setText(contactsArray.get(2).getName());
                                third.setText(contactsArray.get(2).getNumber());
                            }else if (contactsArray.size() == 4){
                                name1.setText(contactsArray.get(0).getName());
                                first.setText(contactsArray.get(0).getNumber());
                                name2.setText(contactsArray.get(1).getName());
                                second.setText(contactsArray.get(1).getNumber());
                                name3.setText(contactsArray.get(2).getName());
                                third.setText(contactsArray.get(2).getNumber());
                                name4.setText(contactsArray.get(3).getName());
                                fourth.setText(contactsArray.get(3).getNumber());
                            }else if (contactsArray.size() == 5){
                                name1.setText(contactsArray.get(0).getName());
                                first.setText(contactsArray.get(0).getNumber());
                                name2.setText(contactsArray.get(1).getName());
                                second.setText(contactsArray.get(1).getNumber());
                                name3.setText(contactsArray.get(2).getName());
                                third.setText(contactsArray.get(2).getNumber());
                                name4.setText(contactsArray.get(3).getName());
                                fourth.setText(contactsArray.get(3).getNumber());
                                name5.setText(contactsArray.get(4).getName());
                                fifth.setText(contactsArray.get(4).getNumber());
                            }

                        }else {
                            Toast.makeText(SettingsActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AddFvrtContacts(){

        SharedPreferences sp = getSharedPreferences("user_credentials", MODE_PRIVATE);

        Map<String, List<ContactsModel>> postParam = new HashMap<String, List<ContactsModel>>();
        postParam.put("contacts", contactsArray);


        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("x-sh-auth", sp.getString("TOKEN", ""));

        ApiModelClass.GetApiResponseArray(Request.Method.POST, Constants.URL.ADD_FVRT, SettingsActivity.this, postParam, headers, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.d("Login_result", String.valueOf(result));
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 200){
                            String message = jsonObject.getString("message");

                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(SettingsActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Initialization(){
        first = findViewById(R.id.first_number);
        second = findViewById(R.id.second_number);
        third = findViewById(R.id.third_number);
        fourth = findViewById(R.id.fourth_number);
        fifth = findViewById(R.id.fifth_number);
        name1 = findViewById(R.id.first_name);
        name2 = findViewById(R.id.second_name);
        name3 = findViewById(R.id.third_name);
        name4 = findViewById(R.id.fourth_name);
        name5 = findViewById(R.id.fifth_name);
        back_btn = findViewById(R.id.back_btn);
        add_number = findViewById(R.id.add_number);
        profileLayout = findViewById(R.id.profile_layout);
        profileTab = findViewById(R.id.profile_tab);
        alarmLayout = findViewById(R.id.alarm_layout);
        alarmTab = findViewById(R.id.alarm_tab);
        f_name = findViewById(R.id.f_name);
        l_name = findViewById(R.id.l_name);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        post = findViewById(R.id.post);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        age = findViewById(R.id.age);
        save_btn = findViewById(R.id.save_btn);

    }
}
