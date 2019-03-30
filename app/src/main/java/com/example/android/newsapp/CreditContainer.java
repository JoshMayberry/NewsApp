package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class CreditContainer extends BaseObservable {
	private String LOG_TAG = CreditContainer.class.getSimpleName();
	private Context context;

	private String name = null;
	private String description = null;
	private String link = null;

	@Override
	public String toString() {
		return "CreditContainer{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", link='" + link + '\'' +
				'}';
	}

	CreditContainer(Context context) {
		this.context = context;
	}

	//Getters
	//See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#6
	//See: Android Data Binding Library - Update UI using Observable objects: https://www.youtube.com/watch?v=gP_zj-CIBvM
	@Bindable
	public String getName() {
		return name;
	}

	@Bindable
	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	//Setters
	public CreditContainer setName(String name) {
		this.name = name;
		notifyPropertyChanged(BR.name);
		return this;
	}

	public CreditContainer setDescription(String description) {
		this.description = description;
		notifyPropertyChanged(BR.description);
		return this;
	}

	public CreditContainer setLink(String link) {
		this.link = link;
		return this;
	}

	//Methods
	public void onOpenPage() {
		Log.e(LOG_TAG, "@OpenPage");
		if (link == null) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(intent);
		}
	}
}