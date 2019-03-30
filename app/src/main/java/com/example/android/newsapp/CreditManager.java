package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsapp.databinding.ItemCreditBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This {@link LinearLayoutManager} handles how items are laid out on the screen.
 * It contains the classes needed to replace a {@link android.widget.ListView} with a {@link RecyclerView}.
 * See: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
 * Use: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#now-the-extra-recyclerview-bits
 */
class CreditManager extends LinearLayoutManager {
	private Context context;

	CreditManager(Context context) {
		super(context);
		this.context = context;
	}

	void formatAsListView(RecyclerView view, List<CreditContainer> containerList) {

		view.setHasFixedSize(true);
		view.setLayoutManager(this);
		view.addItemDecoration(new VerticalSpaceItemDecorator());
//		view.addItemDecoration(new ShadowVerticalSpaceItemDecorator(R.drawable.drop_shadow_secondary));
		view.setAdapter(new CreditAdapter(containerList));
	}

	/**
	 * This {@link RecyclerView.ViewHolder} handles loading item contents, user interactions, and item order.
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#481b
	 */
	class CreditHolder extends RecyclerView.ViewHolder {
		String LOG_TAG = CreditHolder.class.getSimpleName();
		private final ItemCreditBinding binding;

		CreditHolder(ItemCreditBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(CreditContainer container) {
			binding.setContainer(container);
			binding.executePendingBindings();
		}
	}

	/**
	 * This {@link RecyclerView.Adapter} makes use of data binding to link views with {@link CreditHolder}.
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#03b5
	 *
	 */
	class CreditAdapter extends RecyclerView.Adapter<CreditHolder> {
		String LOG_TAG = com.example.android.newsapp.CreditManager.class.getSimpleName();
		private final List<CreditContainer> containerList;

		CreditAdapter(List<CreditContainer> containerList) {
			this.containerList = containerList;
		}

		@NonNull
		@Override
		public CreditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			ItemCreditBinding itemBinding = ItemCreditBinding.inflate(layoutInflater, parent, false);
			return new CreditHolder(itemBinding);
		}

		@Override
		public void onBindViewHolder(@NonNull CreditHolder holder, int position) {
			CreditContainer container = this.containerList.get(position);
			holder.bind(container);
		}

		@Override
		public int getItemCount() {
			return this.containerList.size();
		}
	}

	/**
	 * This {@link RecyclerView.ItemDecoration} spaces out items in the {@link CreditAdapter}.
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
				outRect.bottom = (int) context.getResources().getDimension(R.dimen.credit_item_spacing);
			}
		}
	}

	/**
	 * This {@link RecyclerView.ItemDecoration} creates a drop shadow for items in the {@link CreditAdapter}.
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
