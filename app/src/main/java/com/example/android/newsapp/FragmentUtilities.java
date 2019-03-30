package com.example.android.newsapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtilities {
	static private SearchFragment searchFragment = new SearchFragment();
	static private NoContentFragment noContentFragment = new NoContentFragment();
	static private NoInternetFragment noInternetFragment = new NoInternetFragment();

	//See: https://material.io/design/communication/empty-states.html
	//Use: https://developer.android.com/guide/components/fragments.html?utm_source=udacity&utm_medium=course&utm_campaign=android_basics#Transactions
	static private boolean switchFragment(FragmentManager manager, int replaceId, Fragment fragment, boolean hasBack) {
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(replaceId, fragment);

		if (hasBack) {
			transaction.addToBackStack(null);
		}

		transaction.commit();
		return true;
	}

	static public boolean showSearch(FragmentManager manager, int replaceId) {
		return switchFragment(manager, replaceId, searchFragment, false);
	}

	static public boolean showNoContent(FragmentManager manager, int replaceId) {
		return switchFragment(manager, replaceId, noContentFragment, true);
	}

	static public boolean showNoInternet(FragmentManager manager, int replaceId) {
		return switchFragment(manager, replaceId, noInternetFragment, false);

	}
}
