package com.brucetoo.annotationbase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.brucetoo.annotation.InjectActivity;
import com.brucetoo.annotation.InjectView;
import com.brucetoo.annotation.PoetTest;

@PoetTest
@InjectActivity
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv_hello)
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bindView(this);
        hello.setText("Hello,Mother Fucker..");
    }
}
