package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gathers data from the internet for {@link NewsAdapter} to populate {@link MainActivity} with.
 * See: https://medium.com/@taman.neupane/basic-example-of-livedata-and-viewmodel-14d5af922d0#7e70
 * Use: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
 */
class NewsViewModel extends AndroidViewModel {
    private String LOG_TAG = NewsViewModel.class.getSimpleName();

    private final MutableLiveData<List<NewsContainer>> containerList = new MutableLiveData<>();
    private final MutableLiveData<Integer> progress = new MutableLiveData<>();
    SharedPreferences sharedPrefs;
    Context context;

    NewsViewModel(@NonNull Application application) {
        super(application);
        context = application;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application);
    }

    LiveData<List<NewsContainer>> getContainerList() {
        return containerList;
    }

    //See: https://stackoverflow.com/questions/48239657/how-to-handle-android-livedataviewmodel-with-progressbar-on-screen-rotate/53238717#53238717
    LiveData<Integer> getProgress() {
        return progress;
    }

    private String queryGuardian() {
        StringBuilder query = new StringBuilder();
        query.append("https://content.guardianapis.com/search?order-by=");
        query.append(sharedPrefs.getString(context.getString(R.string.settings_order_by_key), context.getString(R.string.settings_order_by_default)));

        return "https://content.guardianapis.com/search?order-by=newest&show-elements=image&show-tags=contributor&q=football&api-key=test";
    }

    /**
     * The {@link android.content.Loader} is deprecated;
     * it is suggested that a combination {@link AndroidViewModel} and {@link LiveData} be used instead.
     * See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#788a
     *
     * An {@link AsyncTask} is used to run a background thread.
     * Apparently, when used with an {@link AndroidViewModel} and {@link LiveData}, there is no memory leak problem.
     * See: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#fb19
     */
    @SuppressLint("StaticFieldLeak")
    void loadContainerList() {
        new AsyncTask<Void, Integer, List<NewsContainer>>() {
            /**
             * Gets a list of {@link NewsContainer} objects from the internet
             */
            @Override
            protected List<NewsContainer> doInBackground(Void... voids) {
                Log.e(LOG_TAG, "@doInBackground");
                List<NewsContainer> data = new ArrayList<>();

//                try {
//                    //Get the initial JSON list of containers
//                    String jsonResponseList = QueryUtilities.makeHttpRequest(orderGroup.urlJsonList);
//                    if (jsonResponseList == null) {
//                        Log.e(LOG_TAG, "Get JSON error");
//                        return new ArrayList<>();
//                    }
//
//                    //Generate a list of {@link BaseContainer} objects
//                    JSONObject root = new JSONObject(jsonResponseList);
//                    JSONArray items = root.getJSONArray("items");
//                    for (int i = 0; i < items.length(); i++) {
//                        T container = (T) orderGroup.newInstance();
//
//                        //Account for sub-containers
//                        List<T> containerList = container.getContainerList(root, items.getJSONObject(i));
//                        if (containerList == null) {
//                            continue;
//                        }
//
//                        //Do not add duplicates
//                        for (T item : containerList) {
//                            if (!dataMap.containsKey(item.title.trim())) {
//                                dataMap.put(item.title.trim(), item);
//                            }
//                        }
//
//                        //Update Progress
//                        publishProgress(dataMap.size());
//                    }
//
//                } catch (IOException | JSONException error) {
//                    Log.e(LOG_TAG, "Unknown Error", error);
//                    return null;
//                }
//
//                //Sort Results
//                //See: https://stackoverflow.com/questions/1026723/how-to-convert-a-map-to-list-in-java/1026736#1026736
//                //Use: https://stackoverflow.com/questions/6826112/sorting-names-in-a-list-alphabetically/6826149#6826149
//                List<T> data = new ArrayList<>(dataMap.values());
//                Collections.sort(data, new Comparator<T>() {
//                    @Override
//                    public int compare(T item1, T item2) {
//                        return item1.title.compareToIgnoreCase(item2.title);
//                    }
//                });
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
                progress.setValue(valueList[0]);
            }

            /**
             * Applies the downloaded list of {@link NewsContainer} objects to the UI
             */
            @Override
            protected void onPostExecute(List<NewsContainer> data) {
                containerList.setValue(data);
            }
        }.execute();
    }
}

