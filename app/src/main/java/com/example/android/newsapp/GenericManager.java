package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This {@link LinearLayoutManager} handles how items are laid out on the screen.
 * It contains the classes needed to replace a {@link android.widget.ListView} with a {@link RecyclerView}.
 * See: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
 * Use: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#now-the-extra-recyclerview-bits
 */
abstract class GenericManager<T extends BaseObservable> extends LinearLayoutManager {
	private Context context;

	private Integer spacing = null;
	private int itemLayoutId;
	private GenericAdapter genericAdapter;

	GenericManager(Context context) {
		super(context);
		this.context = context;
	}

	GenericManager setSpacingId(Integer resourceId) {
		if (resourceId == null) {
			this.spacing = null;
		} else {
			this.spacing = (int) context.getResources().getDimension(resourceId);
		}
		return this;
	}

	GenericManager setItemLayoutId(int resourceId) {
		this.itemLayoutId = resourceId;
		return this;
	}

	//Use: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
	void formatAsListView(RecyclerView view, List<T> containerList) {

		view.setHasFixedSize(true);
		view.setLayoutManager(this);
		if (spacing != null) {
			view.addItemDecoration(new VerticalSpaceItemDecorator());
		}

//		view.addItemDecoration(new ShadowVerticalSpaceItemDecorator(R.drawable.drop_shadow_secondary));

		genericAdapter = new GenericAdapter(containerList);
		view.setAdapter(genericAdapter);
	}

	//Use: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
	void formatAsListView(RecyclerView view, NewsViewModel viewModel) {
		LiveData containerList = viewModel.getContainerList();

		view.setHasFixedSize(true);
		view.setLayoutManager(this);
		if (spacing != null) {
			view.addItemDecoration(new VerticalSpaceItemDecorator());
		}

		genericAdapter = new GenericAdapter(containerList);
		view.setAdapter(genericAdapter);
	}

	GenericAdapter getAdapter() {
		return genericAdapter;
	}

	/**
	 * This {@link RecyclerView.ViewHolder} handles loading item contents, user interactions, and item order.
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * See: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#481b
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#c856
	 */
	class GenericHolder extends RecyclerView.ViewHolder {
		String LOG_TAG = GenericHolder.class.getSimpleName();
		private final ViewDataBinding binding;

		GenericHolder(ViewDataBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(T container) {
			binding.setVariable(BR.container, container);
			binding.executePendingBindings();
		}
	}

	/**
	 * This {@link RecyclerView.Adapter} makes use of data binding to link views with {@link GenericHolder}.
	 * It can handle either {@link List} objects or {@link LiveData<List>} objects.
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * See: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#03b5
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#6620
	 */
	class GenericAdapter extends RecyclerView.Adapter<GenericHolder> {
		String LOG_TAG = GenericManager.class.getSimpleName();
		private List<T> containerList = null;
		private LiveData<List<T>> liveContainerList = null;

		GenericAdapter(List<T> containerList) {
			this.containerList = containerList;
		}

		GenericAdapter(LiveData<List<T>> containerList) {
			this.liveContainerList = containerList;
		}

		@Override
		public int getItemViewType(int position) {
			return itemLayoutId;
		}

		@NonNull
		@Override
		public GenericHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			ViewDataBinding itemBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
			return new GenericHolder(itemBinding);
		}

		@Override
		public void onBindViewHolder(@NonNull GenericHolder holder, int position) {
			if (containerList != null) {
				T container = containerList.get(position);
				holder.bind(container);

			} else if (liveContainerList != null) {
				List<T> containerListContents = liveContainerList.getValue();
				if (containerListContents != null) {
					T container = containerListContents.get(position);
					holder.bind(container);
				}
			}
		}

		@Override
		public int getItemCount() {
			if (containerList != null) {
				return this.containerList.size();

			} else if (liveContainerList != null) {
				List<T> containerListContents = liveContainerList.getValue();
				if (containerListContents != null) {
					return containerListContents.size();
				}
			}
			return 0;
		}
	}

	/**
	 * This {@link RecyclerView.ItemDecoration} spaces out items in the {@link GenericAdapter}.
	 * Use: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#now-the-extra-recyclerview-bits
	 */
	class VerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
		String LOG_TAG = VerticalSpaceItemDecorator.class.getSimpleName();

		@Override
		public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
			RecyclerView.Adapter adapter = parent.getAdapter();
			if (adapter == null) {
				Log.e(LOG_TAG, "Unknown Error: No adapter");
				return;
			}

			//Account for spacing
			if (parent.getChildAdapterPosition(view) != adapter.getItemCount() - 1) {
				outRect.bottom = spacing;
			}
		}
	}

	/**
	 * This {@link RecyclerView.ItemDecoration} creates a drop shadow for items in the {@link GenericAdapter}.
	 * Use: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#now-the-extra-recyclerview-bits
	 */
	class ShadowVerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
		private Drawable divider;

		ShadowVerticalSpaceItemDecorator(int resourceId) {
			this.divider = ContextCompat.getDrawable(context, resourceId);
		}

		@Override
		public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
			int paddingLeft = parent.getPaddingLeft();
			int paddingRight = parent.getWidth() - parent.getPaddingRight();

			for (int i = 0; i < parent.getChildCount(); i++) {
				View item = parent.getChildAt(i);
				RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
				int paddingTop = item.getBottom() + layoutParams.bottomMargin;
				int paddingBottom = paddingTop + divider.getIntrinsicHeight();

				divider.setBounds(paddingLeft, paddingTop, paddingRight, paddingBottom);
				divider.draw(canvas);
			}
		}
	}
}
