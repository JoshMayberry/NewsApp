package com.example.android.newsapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

/**
 * Allows the user to modify settings for this app.
 * Use: https://github.com/udacity/ud843-QuakeReport/blob/05190298d7e6a7de21e3beb8f940e133fea67b4c/app/src/main/java/com/example/android/quakereport/SettingsActivity.java
 */
public class SettingsActivity extends AppCompatActivity {
	/**
	 * Inflates the settings layout.
	 * Use: https://developer.android.com/guide/topics/ui/settings.html#inflate_the_hierarchy
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_container, new NewsPreferenceFragment())
				.commit();
	}


	public static class NewsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			//Use: https://developer.android.com/guide/topics/ui/settings.html#inflate_the_hierarchy
			setPreferencesFromResource(R.xml.settings_main, rootKey);

			SwitchPreferenceCompat showImagesPreference = findPreference(getString(R.string.settings_show_images_key));
			if (showImagesPreference != null) {
				//Use: https://developer.android.com/guide/topics/ui/settings/customize-your-settings#use_a_custom_summaryprovider
				showImagesPreference.setSummaryProvider(new Preference.SummaryProvider<SwitchPreferenceCompat>() {
					@Override
					public CharSequence provideSummary(SwitchPreferenceCompat preference) {
						boolean checked = preference.isChecked();
						if (checked) {
							return "Yes Please";
						}
						return "No Way!";
					}
				});

				//Use: https://developer.android.com/guide/topics/ui/settings/use-saved-values#onpreferencechangelistener
				showImagesPreference.setOnPreferenceChangeListener(this);
			}

			ListPreference orderByPreference = findPreference(getString(R.string.settings_order_by_key));
			if (orderByPreference != null) {
				//Use: https://developer.android.com/guide/topics/ui/settings/customize-your-settings#use_a_simplesummaryprovider
				orderByPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

				//Use: https://developer.android.com/guide/topics/ui/settings/use-saved-values#onpreferencechangelistener
				orderByPreference.setOnPreferenceChangeListener(this);
			}
		}

		//See: https://developer.android.com/reference/android/preference/Preference.OnPreferenceChangeListener.html
		//Use: https://developer.android.com/guide/topics/ui/settings/use-saved-values#onpreferencechangelistener
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			return true; //True to update the state of the Preference with the new value.
		}
	}
}