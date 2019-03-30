package com.example.android.newsapp;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.android.newsapp.databinding.ActivityCreditsBinding;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class CreditActivity extends AppCompatActivity {
	String LOG_TAG = NewsActivity.class.getSimpleName();

	ActivityCreditsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_credits);

		CreditManager manager = new CreditManager(this);
		manager.formatAsListView(binding.list, generateList());
	}

	/**
	 * Uses three string arrays to generate a list of {@link CreditContainer}s.
	 * Use: https://developer.android.com/guide/topics/resources/string-resource#StringArray
	 */
	private ArrayList<CreditContainer> generateList() {
		Resources resources = getResources();
		String[] nameList = resources.getStringArray(R.array.credits_names);
		String[] descriptionList = resources.getStringArray(R.array.credits_descriptions);
		String[] linkList = resources.getStringArray(R.array.credits_links);

		ArrayList<CreditContainer> containerList = new ArrayList<>();
		for (int i = 0; i < nameList.length; i++) {
			containerList.add(new CreditContainer(this)
					.setName(nameList[i])
					.setDescription(descriptionList[i])
					.setLink(linkList[i]));
		}
		return containerList;
	}
}