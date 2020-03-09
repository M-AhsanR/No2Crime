package com.maher.n2c.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {
    Thread th;
    static Intent i;

    SharedPreferences sharedPreferences;
    String prefs = "user_credentials";
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

        if (token.isEmpty()){
            i = new Intent(this, LoginActivity.class);
        }else {
            i = new Intent(this, MainActivity.class);
        }


        th = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                            Splash.this.finish();
                        }
                    });

                }
            }
        });
        th.start();
    }

}