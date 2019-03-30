package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//Image: https://pngtree.com/asmaa_14420

//Must be public to use data binding

/**
 * Gathers data from the internet for an {@link android.widget.AdapterView} to populate {@link NewsActivity} with.
 * See: https://medium.com/@taman.neupane/basic-example-of-livedata-and-viewmodel-14d5af922d0#7e70
 * Use: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
 * <p>
 * The data binding connects with {@link MutableLiveData} the same way as {@link BaseObservable} methods, except no notification needs to happen.
 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
 * See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
 */
public class NewsViewModel extends AndroidViewModel {
    private String LOG_TAG = NewsViewModel.class.getSimpleName();
    private SharedPreferences sharedPrefs;
    private MyAsyncClass myAsyncTask = null;
    private Context context;

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

    NewsViewModel(@NonNull Application application) {
        super(application);
        context = application;

        //See: https://developer.android.com/guide/topics/ui/settings/use-saved-values#reading_preference_values
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application);

    }

    LiveData<List<NewsContainer>> getContainerList() {
        return containerList;
    }

    //Public getter methods of MutableLiveData are visible to data binding
    //See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
    //See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
    public MutableLiveData<Integer> getBusy() {
        return busy;
    }

    public MutableLiveData<String> getProgress() {
        return progress;
    }

	public MutableLiveData<String> getSearchValue() {
		return searchValue;
	}

	public MutableLiveData<String> getErrorTitle() {
        return errorTitle;
    }

	public MutableLiveData<Drawable> getErrorImage() {
		return errorImage;
	}

	public MutableLiveData<String> getErrorSubText() {
		return errorSubText;
	}

	NewsViewModel setBusy(Integer value) {
        this.busy.setValue(value);
        return this;
    }

    NewsViewModel setProgress(int number) {
        this.progress.setValue(String.valueOf(number));
        return this;
    }

    NewsViewModel setProgress(String text) {
        this.progress.setValue(text);
        return this;
    }

    NewsViewModel setSearchValue(int resourceId) {
        this.searchValue.setValue(context.getString(resourceId));
        return this;
    }

    NewsViewModel setSearchValue(String text) {
        this.searchValue.setValue(text);
        return this;
    }

	NewsViewModel setErrorTitle(int resourceId) {
		this.errorTitle.setValue(context.getString(resourceId));
		return this;
	}

	NewsViewModel setErrorTitle(String text) {
		this.errorTitle.setValue(text);
		return this;
	}

	NewsViewModel setErrorImage(Integer resourceId) {
    	if (resourceId == null) {
    		return this;
		}
		this.errorImage.setValue(context.getDrawable(resourceId));
		return this;
	}

	NewsViewModel setErrorSubText(int resourceId) {
		this.errorSubText.setValue(context.getString(resourceId));
		return this;
	}

	NewsViewModel setErrorSubText(String text) {
		this.errorSubText.setValue(text);
		return this;
	}

	boolean loadData() {
        if ((myAsyncTask != null) && (myAsyncTask.getStatus() != AsyncTask.Status.FINISHED)) {
            return false;
        }
        myAsyncTask = new MyAsyncClass();
        myAsyncTask.execute();
        return true;
    }

    boolean resetData() {
        if ((myAsyncTask == null) || (myAsyncTask.getStatus() == AsyncTask.Status.FINISHED)) {
            return false;
        }

        myAsyncTask.cancel(true);
        return true;
    }

    boolean restartData() {
        if (myAsyncTask == null) {
            myAsyncTask = new MyAsyncClass();
        } else if (myAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            myAsyncTask.cancel(true);
        }
        myAsyncTask.execute();
        return true;
    }

    /**
     * Generates a query for the guardian api.
     * Here are some example queries that can be returned:
     * - http://content.guardianapis.com/search?api-key=test&order-by=relevance&show-tags=contributor
     * - http://content.guardianapis.com/search?api-key=test&order-by=relevance&show-tags=contributor&show-fields=thumbnail&q=debates
	 * See: https://open-platform.theguardian.com/explore/
     */
    private String getQuery() {
        //Start url
        StringBuilder query = new StringBuilder();
        query.append("https://content.guardianapis.com/search");

        //API Key
        query.append("?api-key=test");

        //Order By
//        query.append("&order-by=relevance");
//        query.append(sharedPrefs.getString(context.getString(R.string.settings_order_by_key), context.getString(R.string.settings_order_by_default)));

        //Show Author
        //See: https://stackoverflow.com/questions/46670935/get-the-author-name-from-the-guardian-open-platform/46676842#46676842
        query.append("&show-tags=contributor");

        //Show Images
        //See: https://stackoverflow.com/questions/40720921/rest-api-the-gurdian-get-image-url/40721009#40721009
//        if (sharedPrefs.getBoolean(context.getString(R.string.settings_show_images_key), true)) {
            query.append("&show-fields=thumbnail,trailText");
//        }

        //Search Query
        String messageValue = errorTitle.getValue();
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
                if (jsonResponseList == null) {
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
					try {
						Thread.sleep(100);
					} catch (InterruptedException error) {
						Log.e(LOG_TAG, "Unknown Thread Error", error);
						return new ArrayList<>();
					}

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
                    	Log.e(LOG_TAG, "fields: " + fields);
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
//                    Log.e(LOG_TAG, "container: " + container);
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
            super.onPreExecute();
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
