package com.example.android.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.newsapp.databinding.ActivityNewsBinding;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

//To Emulate on an AMD Processor
//Use: https://android-developers.googleblog.com/2018/07/android-emulator-amd-processor-hyper-v.html
//Use: https://forum.level1techs.com/t/solved-no-virtualization-support-with-gigabyte-ga-ab350-gaming-3/114171/8

public class NewsActivity extends AppCompatActivity {
	String LOG_TAG = NewsActivity.class.getSimpleName();
	ActivityNewsBinding binding;

	NewsViewModel newsViewModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#4
		//Bug: https://stackoverflow.com/questions/34368329/data-binding-android-type-parameter-t-has-incompatible-upper-bounds-viewdata/37364609#37364609
		binding = DataBindingUtil.setContentView(this, R.layout.activity_news);

		setupLiveData();
		setupRecyclerView();
		setupSearchBar();
	}

	private void setupLiveData() {
		//See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#viewmodel
		newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
		binding.setNewsViewModel(newsViewModel);
		newsViewModel.loadData();

		//LiveData needs the binding to know who the lifecycle owner is
		//See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#a2ea
		binding.setLifecycleOwner(this);
	}

	private void setupRecyclerView() {
		final NewsManager manager = new NewsManager(this);
		manager.formatAsListView(binding.list, newsViewModel);

		//Populate list
		//See: https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity/49407157#49407157
		//See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
		//Use: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#a62e
		newsViewModel.getContainerList().observe(this, new Observer<List<NewsContainer>>() {
			@Override
			public void onChanged(List<NewsContainer> containerList) {
				//See: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#ff4c
				manager.getAdapter().notifyDataSetChanged();
			}
		});

		//Display progress
		newsViewModel.getProgress().observe(this, new Observer<String>() {
			@Override
			public void onChanged(@Nullable String progress) {
				// update UI
				if (progress == null) {
					return;
				}

				binding.counter.setText(progress);
			}
		});

	}

	private void setupSearchBar() {
		newsViewModel.getSearchValue().observe(this, new Observer<String>() {
			@Override
			public void onChanged(@Nullable String newValue) {
				newsViewModel.restartData();
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
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_credits:
				startActivity(new Intent(this, CreditActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
