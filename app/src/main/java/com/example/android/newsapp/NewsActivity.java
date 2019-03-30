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

//		//Use: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
//		binding.list.setHasFixedSize(true);
//		binding.list.setLayoutManager(new LinearLayoutManager(this));
//		newsAdapter = new NewsAdapter(getApplicationContext(), newsViewModel.getContainerList());
//		binding.list.setAdapter(newsAdapter);
//
//		//Populate list
//		//See: https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity/49407157#49407157
//		//See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
//		//Use: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#a62e
//		newsViewModel.getContainerList().observe(this, new Observer<List<NewsContainer>>() {
//			@Override
//			public void onChanged(List<NewsContainer> containerList) {
//				//See: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#ff4c
//				newsAdapter.notifyDataSetChanged();
//			}
//		});
//
//		//Reset: https://stackoverflow.com/a/22474821/7316734
//
//		//Display progress
//		newsViewModel.getProgress().observe(this, new Observer<String>() {
//			@Override
//			public void onChanged(@Nullable String progress) {
//				// update UI
//				if (progress == null) {
//					return;
//				}
//
//				binding.counter.setText(progress);
//			}
//		});
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

//	/**
//	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
//	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#481b
//	 */
//	public class NewsHolder extends RecyclerView.ViewHolder {
//		String LOG_TAG = NewsHolder.class.getSimpleName();
//		private final ItemNewsBinding binding;
//
//		NewsHolder(ItemNewsBinding binding) {
//			super(binding.getRoot());
//			this.binding = binding;
//		}
//
//		public void bind(NewsContainer container) {
//			binding.setContainer(container);
//			binding.executePendingBindings();
//		}
//	}
//
//	/**
//	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
//	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#03b5
//	 */
//	public class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
//		String LOG_TAG = NewsAdapter.class.getSimpleName();
//		private final LiveData<List<NewsContainer>> containerList;
//		private Context context;
//
//		NewsAdapter(Context context, LiveData<List<NewsContainer>> containerList) {
//			this.containerList = containerList;
//			this.context = context;
//		}
//
//		@NonNull
//		@Override
//		public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//			ItemNewsBinding itemBinding = ItemNewsBinding.inflate(layoutInflater, parent, false);
//			return new NewsHolder(itemBinding);
//		}
//
//		@Override
//		public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
//			List<NewsContainer> containerListContents = this.containerList.getValue();
//			if (containerListContents == null) {
//				return;
//			}
//			NewsContainer container = containerListContents.get(position);
//			holder.bind(container);
//		}
//
//		@Override
//		public int getItemCount() {
//			List<NewsContainer> containerListContents = this.containerList.getValue();
//			if (containerListContents == null) {
//				return 0;
//			}
//			return containerListContents.size();
//		}
//	}
}
