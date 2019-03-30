package com.example.android.newsapp;

import android.content.Context;

class CreditManager extends GenericManager<CreditContainer> {
	CreditManager(Context context) {
		super(context);
		setSpacingId(R.dimen.credit_item_spacing);
		setItemLayoutId(R.layout.item_credit);
	}
}
