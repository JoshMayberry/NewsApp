package com.example.android.newsapp;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.newsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

//To Emulate on an AMD Processor
//Use: https://android-developers.googleblog.com/2018/07/android-emulator-amd-processor-hyper-v.html
//Use: https://forum.level1techs.com/t/solved-no-virtualization-support-with-gigabyte-ga-ab350-gaming-3/114171/8

public class MainActivity extends AppCompatActivity {
    String LOG_TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;
    NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#4
        //Bug: https://stackoverflow.com/questions/34368329/data-binding-android-type-parameter-t-has-incompatible-upper-bounds-viewdata/37364609#37364609
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#viewmodel
        binding.setNewsViewModel(ViewModelProviders.of(this).get(New_NewsViewModel.class));

        //LiveData needs the binding to know who the lifecycle owner is
        //See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#a2ea
        binding.setLifecycleOwner(this);

//        loginViewModel.getUser().observe(this, new Observer() {
//            @Override
//            public void onChanged(@Nullable User user) {
//                if (user.getEmail().length() > 0 || user.getPassword().length() > 0)
//                    Toast.makeText(getApplicationContext(), "email : " + user.getEmail() + " password " + user.getPassword(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    //See: http://developer.android.com/guide/topics/ui/menus.html
    //See: https://github.com/udacity/ud843-QuakeReport/commit/c24e4a9d3226d4aec8c847d454a7eab23872d721
    //Use: https://developer.android.com/guide/topics/ui/menus#options-menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Use: https://developer.android.com/guide/topics/ui/menus#RespondingOptionsMenu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
