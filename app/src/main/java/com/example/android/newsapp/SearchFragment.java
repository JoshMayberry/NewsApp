package com.example.android.newsapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsapp.databinding.FragmentSearchBinding;
import com.example.android.newsapp.databinding.ItemNewsBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFragment extends Fragment {
	String LOG_TAG = SearchFragment.class.getSimpleName();
	FragmentSearchBinding binding;

	NewsAdapter newsAdapter = null;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentActivity activity = getActivity();
		if (activity == null) {
			Log.e(LOG_TAG, "Unknown Error: No Activity in onCreateView");
			return null;
		}

		//See: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment/40527833#40527833
		binding = FragmentSearchBinding.inflate(inflater, container, false);

		//See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#viewmodel
		NewsViewModel newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
		binding.setNewsViewModel(newsViewModel);
		newsViewModel.loadData();

		//LiveData needs the binding to know who the lifecycle owner is
		//See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#a2ea
		binding.setLifecycleOwner(this);

		//Use: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
		binding.list.setHasFixedSize(true);
		binding.list.setLayoutManager(new LinearLayoutManager(activity));
		newsAdapter = new NewsAdapter(activity.getApplicationContext(), newsViewModel.getContainerList());
		binding.list.setAdapter(newsAdapter);

		//Populate list
		//See: https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity/49407157#49407157
		//See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
		//Use: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#a62e
		newsViewModel.getContainerList().observe(this, new Observer<List<NewsContainer>>() {
			@Override
			public void onChanged(List<NewsContainer> containerList) {
				Log.e(LOG_TAG, "containerList: " + containerList.size());

				FragmentActivity activity = getActivity();
				if (activity == null) {
					Log.e(LOG_TAG, "Unknown Error: No Activity in onChanged");
					return;
				}

//				if (containerList.size() > 0) {
//					//See: https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe#ff4c
//					newsAdapter.notifyDataSetChanged();
//				} else {
				FragmentUtilities.showNoContent(activity.getSupportFragmentManager(), R.id.fragment_container);
//				}
			}
		});

		//Reset: https://stackoverflow.com/a/22474821/7316734

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


		return binding.getRoot();
	}

	/**
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#481b
	 */
	public class NewsHolder extends RecyclerView.ViewHolder {
		String LOG_TAG = NewsHolder.class.getSimpleName();
		private final ItemNewsBinding binding;

		NewsHolder(ItemNewsBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(NewsContainer container) {
			binding.setContainer(container);
			binding.executePendingBindings();
		}
	}

	/**
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#03b5
	 */
	public class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
		String LOG_TAG = NewsAdapter.class.getSimpleName();
		private final LiveData<List<NewsContainer>> containerList;
		private Context context;

		NewsAdapter(Context context, LiveData<List<NewsContainer>> containerList) {
			this.containerList = containerList;
			this.context = context;
		}

		@NonNull
		@Override
		public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			ItemNewsBinding itemBinding = ItemNewsBinding.inflate(layoutInflater, parent, false);
			return new NewsHolder(itemBinding);
		}

		@Override
		public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
			List<NewsContainer> containerListContents = this.containerList.getValue();
			if (containerListContents == null) {
				return;
			}
			NewsContainer container = containerListContents.get(position);
			holder.bind(container);
		}

		@Override
		public int getItemCount() {
			List<NewsContainer> containerListContents = this.containerList.getValue();
			if (containerListContents == null) {
				return 0;
			}
			return containerListContents.size();
		}
	}
}
