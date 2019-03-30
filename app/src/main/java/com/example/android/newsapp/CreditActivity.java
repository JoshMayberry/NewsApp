package com.example.android.newsapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.newsapp.databinding.ActivityCreditsBinding;
import com.example.android.newsapp.databinding.ItemCreditBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreditActivity extends AppCompatActivity {
	String LOG_TAG = NewsActivity.class.getSimpleName();

	ActivityCreditsBinding binding;
	ArrayList<CreditContainer> containerList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_credits);
		populateList();

		//Use: https://developer.android.com/guide/topics/ui/layout/recyclerview#workflow
		binding.list.setHasFixedSize(true);
		binding.list.setLayoutManager(new LinearLayoutManager(this));
		binding.list.setAdapter(new CreditAdapter(containerList));
	}

	//See: https://developer.android.com/guide/topics/resources/string-resource#StringArray
	private void populateList() {
		Resources resources = getResources();
		String[] nameList = resources.getStringArray(R.array.credits_names);
		String[] descriptionList = resources.getStringArray(R.array.credits_descriptions);
		String[] linkList = resources.getStringArray(R.array.credits_links);

		for (int i = 0; i < nameList.length; i++) {
			containerList.add(new CreditContainer(this)
					.setName(nameList[i])
					.setDescription(descriptionList[i])
					.setLink(linkList[i]));
		}
	}

	/**
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#481b
	 */
	public class CreditHolder extends RecyclerView.ViewHolder {
		String LOG_TAG = CreditHolder.class.getSimpleName();
		private final ItemCreditBinding binding;

		CreditHolder(ItemCreditBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(CreditContainer container) {
			binding.setContainer(container);
			binding.executePendingBindings();
		}
	}

	/**
	 * See: https://traversoft.com/2016/01/31/replace-listview-with-recyclerview/#lets-get-on-with-it
	 * Use: https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4#03b5
	 */
	public class CreditAdapter extends RecyclerView.Adapter<CreditHolder> {
		String LOG_TAG = CreditAdapter.class.getSimpleName();
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
}