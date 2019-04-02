package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

//Must be public to use data binding

/**
 * Gathers data from the internet for an {@link android.widget.AdapterView} to populate {@link NewsActivity} with.
 * See: https://medium.com/@taman.neupane/basic-example-of-livedata-and-viewmodel-14d5af922d0#7e70
 * Use: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
 * <p>
 * The data binding connects with {@link MutableLiveData} the same way as {@link BaseObservable} methods, except no notification needs to happen.
 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
 * See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
 * <p>
 * All Public getter methods for {@link MutableLiveData} variables are visible to data binding; no need for {@link @Binding}.
 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
 * See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
 */
public class NewsViewModel extends AndroidViewModel {
	private static String LOG_TAG = NewsViewModel.class.getSimpleName();
	private MyAsyncClass myAsyncTask = null;
	private SharedPreferences sharedPreferences;
	private Context context;

	// Mutable Objects
	/**
	 * Contains all found items.
	 */
	private MutableLiveData<List<NewsContainer>> containerList = new MutableLiveData<>();

	/**
	 * Displays how many items have been found.
	 */
	private MutableLiveData<String> progress = new MutableLiveData<>();

	/**
	 * Toggles the visibility progress bar.
	 * Can be set to {@link View#GONE} or {@link View#VISIBLE}.
	 */
	private MutableLiveData<Integer> busy = new MutableLiveData<>();

	private MutableLiveData<Integer> retry = new MutableLiveData<>();

	/**
	 * Displays any errors to the user.
	 * Will be invisible if set to {@link null}.
	 */
	private MutableLiveData<Drawable> errorImage = new MutableLiveData<>();

	private MutableLiveData<String> errorTitle = new MutableLiveData<>();

	private MutableLiveData<String> errorSubText = new MutableLiveData<>();

	/**
	 * What the user wants to search for.
	 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#7381
	 */
	private MutableLiveData<String> searchValue = new MutableLiveData<>();

	//Constructors
	public NewsViewModel(@NonNull Application application) {
		super(application);
		context = application;

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
	}

	//News Methods
	public LiveData<List<NewsContainer>> getContainerList() {
		return containerList;
	}

	public MutableLiveData<Integer> getBusy() {
		return busy;
	}

	public MutableLiveData<Integer> getRetry() {
		return retry;
	}

	public MutableLiveData<String> getProgress() {
		return progress;
	}

	public NewsViewModel setBusy(Integer value) {
		this.busy.setValue(value);
		return this;
	}

	public NewsViewModel setRety(Integer value) {
		this.retry.setValue(value);
		return this;
	}

	public NewsViewModel setProgress(int number) {
		this.progress.setValue(String.valueOf(number));
		return this;
	}

	public NewsViewModel setProgress(String text) {
		this.progress.setValue(text);
		return this;
	}

	//Error Methods
	public MutableLiveData<String> getErrorTitle() {
		return errorTitle;
	}

	public MutableLiveData<Drawable> getErrorImage() {
		return errorImage;
	}

	public MutableLiveData<String> getErrorSubText() {
		return errorSubText;
	}

	public NewsViewModel setErrorTitle(int resourceId) {
		this.errorTitle.setValue(context.getString(resourceId));
		return this;
	}

	public NewsViewModel setErrorTitle(String text) {
		this.errorTitle.setValue(text);
		return this;
	}

	public NewsViewModel setErrorImage(Integer resourceId) {
		if (resourceId == null) {
			this.errorImage.setValue(null);
			return this;
		}
		this.errorImage.setValue(context.getDrawable(resourceId));
		return this;
	}

	public NewsViewModel setErrorSubText(int resourceId) {
		this.errorSubText.setValue(context.getString(resourceId));
		return this;
	}

	public NewsViewModel setErrorSubText(String text) {
		this.errorSubText.setValue(text);
		return this;
	}

	/**
	 * Opens the network settings on the phone.
	 * Use: https://stackoverflow.com/questions/6000452/how-can-i-launch-mobile-network-settings-screen-from-my-code/6789616#6789616
	 */
	public void openNetworkSettings() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
		context.startActivity(intent);
	}

	/**
	 * Opens the data settings on the phone.
	 * Use: https://stackoverflow.com/questions/12147182/mobile-network-settings-in-android-4-1
	 */
	public void openDataSettings() {
		Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
		intent.setClassName("com.android.phone", "com.android.phone.MobileNetworkSettings");
		context.startActivity(intent);
	}

	/**
	 * Checks to see if the phone is connected to the internet or not.
	 * See: https://developer.android.com/training/basics/network-ops/connecting.html
	 * Use: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#DetermineConnection
	 */
	private boolean checkOnline() {
		if (QueryUtilities.checkOnline(context)) {
			setRety(View.GONE);
			return true;
		}

		Toast.makeText(context, "No Network", Toast.LENGTH_SHORT).show();
		containerList.setValue(null);
		setBusy(View.GONE);
		setRety(View.VISIBLE);
		setErrorImage(R.drawable.error_no_internet);
		setErrorTitle(R.string.error_internet_title);
		setErrorSubText(R.string.error_internet_subText);
		return false;
	}

	//Search Methods
	public MutableLiveData<String> getSearchValue() {
		return searchValue;
	}

	public NewsViewModel setSearchValue(int resourceId) {
		this.searchValue.setValue(context.getString(resourceId));
		return this;
	}

	public NewsViewModel setSearchValue(String text) {
		this.searchValue.setValue(text);
		return this;
	}

	/**
	 * The setter method for the user's query text.
	 * Example Use:
	 * <pre>
	 *     <SearchView
	 *          ...
	 * 			app:query="@{newsViewModel.searchValue}" />
	 * </pre>
	 * Use: https://stackoverflow.com/questions/49769861/android-databinding-query-property-of-searchview-inside-toolbar
	 */
	@BindingAdapter("query")
	public static void setQuery(SearchView view, String queryText) {
		//Prevent observer from firing upon code startup
		CharSequence viewText = view.getQuery();
		if ((queryText == null) && (viewText.length() == 0)) {
			return;
		}

		//Prevent infinite loops
		//See: https://medium.com/androiddevelopers/android-data-binding-2-way-your-way-ccac20f6313#97c2
		if (queryText != viewText) {
			view.setQuery(queryText, false);
		}
	}

	/**
	 * The getter method for the user's query text.
	 * This method allows two way binding; which means the value will automatically change here if the user changes it on the UI.
	 * Example Use:
	 * <pre>
	 *     <SearchView
	 *          ...
	 * 			app:query="@={newsViewModel.searchValue}" />
	 * </pre>
	 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#7381
	 */
	@InverseBindingAdapter(attribute = "query")
	public static String getQuery(SearchView view) {
		return String.valueOf(view.getQuery());
	}

	/**
	 * In order for two way binding to work, this extra method is needed.
	 * See: https://medium.com/androiddevelopers/android-data-binding-2-way-your-way-ccac20f6313#381d
	 * See: https://medium.com/@henglim/diving-into-android-data-binding-inverse-data-binding-783e5e5a83d3#2d93
	 * Use: https://medium.com/@henglim/diving-into-android-data-binding-inverse-data-binding-783e5e5a83d3#4e6d
	 */
	@BindingAdapter({"queryAttrChanged"})
	public static void setQueryChanged(SearchView view, final InverseBindingListener listener) {
		//See: https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener#public-methods_1
		//See: https://stackoverflow.com/questions/26306717/how-to-listen-to-keyboard-search-button-in-searchview/26306959#26306959
		view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return true; //false if the SearchView should perform the default action of showing any suggestions if available, true if the action was handled by the listener.
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				listener.onChange();
				return true; //true if the query has been handled by the listener, false to let the SearchView perform the default action.
			}
		});
	}

	//Data Methods
	public boolean loadData() {
		if (!checkOnline()) {
			return false;
		}
		if ((myAsyncTask != null) && (myAsyncTask.getStatus() != AsyncTask.Status.FINISHED)) {
			return false;
		}
		myAsyncTask = new MyAsyncClass();
		myAsyncTask.execute();
		return true;
	}

	public boolean resetData() {
		if (!checkOnline()) {
			return false;
		}
		if ((myAsyncTask == null) || (myAsyncTask.getStatus() == AsyncTask.Status.FINISHED)) {
			setSearchValue(null);
			return false;
		}

		myAsyncTask.cancel(true);
		return true;
	}

	public boolean restartData() {
		if (!checkOnline()) {
			return false;
		}
		if ((myAsyncTask != null) && (myAsyncTask.getStatus() != AsyncTask.Status.FINISHED)) {
			myAsyncTask.cancel(true);
		}
		myAsyncTask = new MyAsyncClass();
		myAsyncTask.execute();
		return true;
	}

	/**
	 * Generates a query for the guardian api.
	 * Here are some example queries that can be returned:
	 * - http://content.guardianapis.com/search?api-key=test&order-by=relevance&show-tags=contributor
	 * - http://content.guardianapis.com/search?api-key=test&order-by=relevance&show-tags=contributor&show-fields=thumbnail&q=debates
	 * See: https://open-platform.theguardian.com/explore/
	 * See: https://open-platform.theguardian.com/documentation/search
	 * Use: https://developer.android.com/guide/topics/ui/settings/use-saved-values#reading_preference_values
	 */
	private String getQuery() {
		//Start url
		StringBuilder query = new StringBuilder();
		query.append("https://content.guardianapis.com/search");

		//API Key
		query.append("?api-key=test");

		//Order By
		String orderBy = sharedPreferences.getString(context.getString(R.string.settings_order_by_key), null);
		if ((orderBy != null) && (!orderBy.isEmpty())) {
			query.append("&order-by=");
			query.append(orderBy);
		}

		//Show Author
		//See: https://stackoverflow.com/questions/46670935/get-the-author-name-from-the-guardian-open-platform/46676842#46676842
		query.append("&show-tags=contributor");

		//Show Sub Text
		query.append("&show-fields=trailText");

		//Show Images
		//See: https://stackoverflow.com/questions/40720921/rest-api-the-gurdian-get-image-url/40721009#40721009
		if (sharedPreferences.getBoolean(context.getString(R.string.settings_show_images_key), true)) {
			query.append(",thumbnail");
		}

		//Search Query
		String messageValue = searchValue.getValue();
		if ((messageValue != null) && (!messageValue.isEmpty())) {
			query.append("&q=");
			query.append(messageValue);
		}

		Log.e(LOG_TAG, "query: " + query.toString());
		return query.toString();
	}

	/**
	 * <p> See: https://dzone.com/articles/a-guide-to-formatting-code-snippets-in-javadoc </p>
	 * <h1>Why an AsyncTask is Used instead of a Loader</h1>
	 * <p>
	 * The {@link android.content.Loader} is deprecated;
	 * it is suggested that a combination {@link AndroidViewModel} and {@link LiveData} be used instead.
	 * See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#788a
	 * </p><p>
	 * An {@link AsyncTask} is used to run a background thread.
	 * When used with an {@link AndroidViewModel} and {@link LiveData}, there is no memory leak problem.
	 * See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#fb19
	 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#2dfd
	 * </p><p>
	 * Alternatively, a {@link Handler} can be used instead like so:
	 * <pre>
	 *     //See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
	 *     new Handler().post(new Runnable() {
	 *         @Override
	 *         public void run() {
	 *             Toast.makeText(context, "Hello World", Toast.LENGTH_SHORT).show();
	 *         }
	 *     });
	 * </pre>
	 * </p><p>
	 * An AsyncTask fits better with what the background thread is meant to do, so that is what will be used.
	 * See: https://tutorial.eyehunts.com/android/deference-between-handler-asynctask-thread-android/
	 * </p>
	 */
	@SuppressLint("StaticFieldLeak")
	private class MyAsyncClass extends AsyncTask<Void, Integer, List<NewsContainer>> {

		private String errorMessage = null;

		/**
		 * Gets a list of {@link NewsContainer} objects from the internet
		 */
		@Override
		protected List<NewsContainer> doInBackground(Void... voids) {
			List<NewsContainer> data = new ArrayList<>();

			try {
				//Get the initial JSON list of containers
				String jsonResponseList = QueryUtilities.makeHttpRequest(getQuery());
				if (jsonResponseList.isEmpty()) {
					Log.e(LOG_TAG, "Get JSON error");
					return new ArrayList<>();
				}

				//Generate a list of {@link NewsContainer} objects
				JSONObject root = new JSONObject(jsonResponseList);
				JSONObject response = root.optJSONObject("response");
				if (hasError(root, response)) {
					return new ArrayList<>();
				}

				JSONArray results = response.optJSONArray("results");
				if (results == null) {
					Log.e(LOG_TAG, "Unknown Error: No results");
					return new ArrayList<>();
				}
				for (int i = 0; i < results.length(); i++) {
					//FOR DEBUGGING: Show loading wheel
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException error) {
//						Log.e(LOG_TAG, "Unknown Thread Error", error);
//						return new ArrayList<>();
//					}

					NewsContainer container = new NewsContainer(context);
					JSONObject resultItem = results.optJSONObject(i);
					if (resultItem == null) {
						Log.e(LOG_TAG, "Unknown Error: No result item");
						return new ArrayList<>();
					}

					//Extract basic information
					container.setSection(resultItem.optString("sectionName"));
					container.setDate(resultItem.optString("webPublicationDate"));
					container.setTitle(resultItem.optString("webTitle"));
					container.setUrlPage(resultItem.optString("webUrl"));

					//Extract image
					JSONObject fields = resultItem.optJSONObject("fields");
					if (fields != null) {
						container.setSubText(fields.optString("trailText"));
						container.setUrlImage(fields.optString("thumbnail"));
					}

					//Extract author
					container.setAuthor(null);
					JSONArray tags = resultItem.optJSONArray("tags");
					if ((tags != null) && (tags.length() > 0)) {
						for (int j = 0; j < tags.length(); j++) {
							JSONObject tagItem = tags.optJSONObject(j);
							if (tagItem == null) {
								Log.e(LOG_TAG, "Unknown Error: No tag item");
								return new ArrayList<>();
							}
							String author = tagItem.getString("webTitle");
							if (author != null) {
								container.setAuthor(author);
								break;
							}
						}
					}
					data.add(container);
					publishProgress(data.size());
				}
			} catch (IOException | JSONException error) {
				Log.e(LOG_TAG, "Unknown Error", error);
				return new ArrayList<>();
			}
			return data;
		}

		/**
		 * Shows how many {@link NewsContainer} objects have been found on the UI
		 */
		@Override
		protected void onProgressUpdate(Integer... valueList) {
			if (valueList.length < 1) {
				return;
			}
			setProgress(valueList[0]);
		}

		/**
		 * Starts the {@link #busy} signal.
		 */
		@Override
		protected void onPreExecute() {
			setProgress(0);
			setBusy(View.VISIBLE);
			setErrorImage(null);
			setErrorTitle(null);
			setErrorSubText(null);
		}

		/**
		 * Applies the downloaded list of {@link NewsContainer} objects to the UI and removes the {@link #busy} signal.
		 * If there is no data, then the empty state is displayed.
		 */
		@Override
		protected void onPostExecute(List<NewsContainer> data) {
			if (errorMessage != null) {
				Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			}
			containerList.setValue(data);
			setBusy(View.GONE);

			//Account for empty state
			//See: https://blog.iamsuleiman.com/empty-states-advantage-user-retention/
			//See: https://alexzh.com/2017/02/05/how-to-setemptyview-to-recyclerview/
			if (data.size() < 1) {
				setErrorImage(R.drawable.error_no_content);
				setErrorTitle(R.string.error_article_title);
				setErrorSubText(R.string.error_article_subText);
			}
		}

		/**
		 * Resets the list contents and removes the {@link #busy} signal.
		 */
		@Override
		protected void onCancelled(List<NewsContainer> newsContainers) {
			super.onCancelled(newsContainers);
			setBusy(View.GONE);
//            setErrorTitle(R.string.error_cancel);
		}

		private boolean hasError(JSONObject root, JSONObject response) throws JSONException {
			if (response != null) {
				String status = response.optString("status");
				if (status == null) {
					Log.e(LOG_TAG, "Unknown Error: No Status");
					return true;
				}
				if (!status.equals("ok")) {
					Log.e(LOG_TAG, "Status Error: " + status);
					return true;
				}
			} else if (root.has("errorTitle")) {
				String message = root.getString("errorTitle");
				Log.e(LOG_TAG, "API Error: " + message);
				return true;
			}
			return false;
		}
	}
}
