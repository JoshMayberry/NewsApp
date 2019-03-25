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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.newsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String LOG_TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;
    NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#4
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Network connection check
        //See: https://developer.android.com/training/basics/network-ops/connecting.html
        //Use: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#DetermineConnection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.e(LOG_TAG, "No Network Connection");
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.emptyView.setText(R.string.error_internet);
            return;
        }

        //Empty list state
        //See: https://material.io/design/communication/empty-states.html
        //See: https://developer.android.com/reference/android/widget/AdapterView.html#setEmptyView(android.view.View)
        binding.list.setEmptyView(binding.emptyView);

        //Create Adapter
        newsAdapter = new NewsAdapter(this, new ArrayList<NewsContainer>());
        binding.list.setAdapter(newsAdapter);

        //Open article when an item is clicked
        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NewsContainer container = newsAdapter.getItem(position);
                if (container == null) {
                    return;
                }
                Uri uri = container.getPage();
                if (uri == null) {
                    return;
                }
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(websiteIntent);
            }
        });

        //Populate list
        //See: https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity/49407157#49407157
        //Use: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
        NewsViewModel model = ViewModelProviders.of(this).get(NewsViewModel.class);
        model.loadContainerList();
        model.getContainerList().observe(this, new Observer<List<NewsContainer>>() {
            @Override
            public void onChanged(@Nullable List<NewsContainer> containerList) {
                if (containerList == null) {
                    return;
                }

                binding.counter.setVisibility(View.GONE);
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.emptyView.setText(R.string.error_article);

                newsAdapter.clear();
                if (!containerList.isEmpty()) {
                    newsAdapter.addAll(containerList);
                }
            }
        });

        //Display progress
        model.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                // update UI
                if (progress == null) {
                    return;
                }

                binding.counter.setText(String.valueOf(progress));
            }
        });
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
