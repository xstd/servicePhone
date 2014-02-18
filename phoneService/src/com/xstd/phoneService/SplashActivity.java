package com.xstd.phoneService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.xstd.phoneService.old_code.StartActivity;
import com.xstd.phoneService.setting.SettingManager;

/**
 * Created by michael on 13-12-10.
 */
public class SplashActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.splash);

        int type = SettingManager.getInstance().getServiceType();
        switch (type) {
            case 1:
                Intent i = new Intent();
                i.setClass(getApplicationContext(), StartActivity.class);
                startActivity(i);
                finish();
                break;
            case 2:
                Intent i1 = new Intent();
                i1.setClass(getApplicationContext(), MainServiceInfoActivity.class);
                startActivity(i1);
                finish();
                break;
        }

        View btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.getInstance().setServiceType(1);
                Intent i = new Intent();
                i.setClass(getApplicationContext(), StartActivity.class);
                startActivity(i);
                finish();
            }
        });

        View btn2 = findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SettingManager.getInstance().setServiceType(2);
                Intent i1 = new Intent();
                i1.setClass(getApplicationContext(), MainServiceInfoActivity.class);
                startActivity(i1);
                finish();
            }
        });
    }

}