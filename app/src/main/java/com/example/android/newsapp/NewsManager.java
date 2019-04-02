package com.example.android.newsapp;

import android.content.Context;

public class NewsManager extends GenericManager<NewsContainer> {
	public NewsManager(Context context) {
		super(context);
		setSpacingId(R.dimen.news_item_spacing);
		setItemLayoutId(R.layout.item_news);
	}
}
