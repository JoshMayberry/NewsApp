package com.example.android.newsapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.newsapp.databinding.ActivityCreditsBinding;
import com.example.android.newsapp.databinding.ItemCreditBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class CreditActivity extends AppCompatActivity {
	String LOG_TAG = MainActivity.class.getSimpleName();

	ActivityCreditsBinding binding;

	ArrayList<CreditContainer> containerList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_credits);
		populateList();
		binding.list.setAdapter(new CreditAdapter(this, containerList));
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
	 * Handles recycling views on a {@link MainActivity}.
	 */
	private class CreditAdapter extends ArrayAdapter<CreditContainer> {
		String LOG_TAG = CreditAdapter.class.getSimpleName();
		private LayoutInflater inflater;

		CreditAdapter(Context context, List<CreditContainer> containerList) {
			super(context, 0, containerList);
		}

		//See: https://github.com/nomanr/android-databinding-example/blob/master/app/src/main/java/com/databinding/example/databindingexample/adapters/SimpleAdapter.java
		//Use: https://stackoverflow.com/questions/40557087/how-can-i-use-android-databinding-in-a-listview-and-still-use-a-viewholder-patte/40557175#40557175
		@Override
		public View getView(int position, View scrapView, ViewGroup parent) {
			if (inflater == null) {
				inflater = LayoutInflater.from(getContext());
			}

			ItemCreditBinding binding = DataBindingUtil.getBinding(scrapView);
			if (binding == null) {
				binding = ItemCreditBinding.inflate(inflater, parent, false);
			}

			binding.setContainer(getItem(position));

			return binding.getRoot();
		}
	}
}