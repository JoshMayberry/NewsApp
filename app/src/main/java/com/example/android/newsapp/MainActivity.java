package com.example.android.newsapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.newsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#4
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }
}
