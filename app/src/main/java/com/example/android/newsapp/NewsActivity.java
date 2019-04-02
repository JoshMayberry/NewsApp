package com.example.android.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 * This {@link AppCompatActivity} displays a list of news articles.
 * Each news article is a {@link NewsContainer}. The list of articles is displayed by a {@link NewsManager}.
 * A {@link NewsViewModel} is used to load the articles from the internet.
 */
public class NewsActivity extends AppCompatActivity {
	String LOG_TAG = NewsActivity.class.getSimpleName();
	ActivityNewsBinding binding;

	NewsViewModel newsViewModel = null;

	/**
	 * This inflates the main layout using data binding
	 * See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#4
	 * Bug: https://stackoverflow.com/questions/34368329/data-binding-android-type-parameter-t-has-incompatible-upper-bounds-viewdata/37364609#37364609
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_news);

		setupLiveData();
		setupRecyclerView();
		setupSearchBar();
	}

	/**
	 * The {@link NewsViewModel} is responsible for updating the UI when data from the internet is available.
	 * See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#viewmodel
	 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#a2ea
	 */
	private void setupLiveData() {
		newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
		binding.setNewsViewModel(newsViewModel);
		binding.setLifecycleOwner(this);
		newsViewModel.loadData();

		newsViewModel.getProgress().observe(this, new Observer<String>() {
			@Override
			public void onChanged(@Nullable String progress) {
				if (progress != null) {
					binding.counter.setText(progress);
				}
			}
		});
	}

	/**
	 * The {@link NewsManager} is responsible for displaying the list of {@link NewsContainer} objects on the UI.
	 * See: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#ff4c
	 * See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
	 * See: https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity/49407157#49407157
	 * Use: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#a62e
	 */
	private void setupRecyclerView() {
		final NewsManager manager = new NewsManager(this);
		manager.formatAsListView(binding.list, newsViewModel);

		newsViewModel.getContainerList().observe(this, new Observer<List<NewsContainer>>() {
			@Override
			public void onChanged(List<NewsContainer> containerList) {
				manager.getAdapter().notifyDataSetChanged();
			}
		});
	}

	/**
	 * The search bar has a two-way data binding link with the {@link NewsViewModel}.
	 * Through this link, it is able to re-query the server for data.
	 * Data will be re-queried any time the search bar text is modified.
	 */
	private void setupSearchBar() {
		newsViewModel.getSearchValue().observe(this, new Observer<String>() {
			@Override
			public void onChanged(@Nullable String newValue) {
				newsViewModel.restartData();
			}
		});
	}

	/**
	 * This inflates the menu.
	 * See: http://developer.android.com/guide/topics/ui/menus.html
	 * See: https://github.com/udacity/ud843-QuakeReport/commit/c24e4a9d3226d4aec8c847d454a7eab23872d721
	 * Use: https://developer.android.com/guide/topics/ui/menus#options-menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * The menu allows the user to modify settings and see asset credits.
	 * Use: https://developer.android.com/guide/topics/ui/menus#RespondingOptionsMenu
	 */
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
