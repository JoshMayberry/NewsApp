package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//Must be public to use data binding

/**
 * Gathers data from the internet for {@link NewsAdapter} to populate {@link MainActivity} with.
 * See: https://medium.com/@taman.neupane/basic-example-of-livedata-and-viewmodel-14d5af922d0#7e70
 * Use: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4#e65b
 * <p>
 * The data binding connects with {@link MutableLiveData} the same way as {@link BaseObservable} methods, except no notification needs to happen.
 * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
 * See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
 */
public class New_NewsViewModel extends AndroidViewModel {
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
     */
    private MutableLiveData<String> error = new MutableLiveData<>();

    /**
     * What the user wants to search for
     * See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#7381
     */
    private MutableLiveData<String> searchValue = new MutableLiveData<>();

    New_NewsViewModel(@NonNull Application application) {
        super(application);
        context = application;

        //See: https://developer.android.com/guide/topics/ui/settings/use-saved-values#reading_preference_values
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application);

    }

    LiveData<List<NewsContainer>> getContainerList() {
        return containerList;
    }

    //Public getter methods of MutableLiveData are visible to databinding
    //See: https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f#49f8
    //See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
    public MutableLiveData<Integer> getBusy() {
        return busy;
    }

    public MutableLiveData<String> getProgress() {
        return progress;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<String> getSearchValue() {
        return searchValue;
    }

    New_NewsViewModel setBusy(Integer value) {
        this.busy.setValue(value);
        return this;
    }

    New_NewsViewModel setProgress(int number) {
        this.progress.setValue(String.valueOf(number));
        return this;
    }

    New_NewsViewModel setProgress(String text) {
        this.progress.setValue(text);
        return this;
    }

    New_NewsViewModel setError(int resourceId) {
        this.error.setValue(context.getString(resourceId));
        return this;
    }

    New_NewsViewModel setError(String text) {
        this.error.setValue(text);
        return this;
    }

    New_NewsViewModel setSearchValue(int resourceId) {
        this.searchValue.setValue(context.getString(resourceId));
        return this;
    }

    New_NewsViewModel setSearchValue(String text) {
        this.searchValue.setValue(text);
        return this;
    }

    boolean loadContainerList() {
        if ((myAsyncTask != null) && (myAsyncTask.getStatus() != AsyncTask.Status.FINISHED)) {
            return false;
        }
        myAsyncTask = new MyAsyncClass();
        myAsyncTask.execute();
        return true;
    }


    //See: https://dzone.com/articles/a-guide-to-formatting-code-snippets-in-javadoc

    /**
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
     *     Alternatively, a {@link Handler} can be used instead like so:
     *     <pre>
     *         //See: https://www.journaldev.com/22561/android-mvvm-livedata-data-binding#refactoring-observablefield-to-livedata
     *         new Handler().post(new Runnable() {
     *             @Override
     *             public void run() {
     *                 Toast.makeText(context, "Hello World", Toast.LENGTH_SHORT).show();
     *             }
     *         });
     *     </pre>
     * </p><p>
     * An AsyncTask fits better with what the background thread is meant to do, so that is what will be used.
     * See: https://tutorial.eyehunts.com/android/deference-between-handler-asynctask-thread-android/
     * </p>
     */
    @SuppressLint("StaticFieldLeak")
    private class MyAsyncClass extends AsyncTask<Void, Integer, List<NewsContainer>> {

        /**
         * Gets a list of {@link NewsContainer} objects from the internet
         */
        @Override
        protected List<NewsContainer> doInBackground(Void... voids) {
            Log.e(LOG_TAG, "@doInBackground");
            List<NewsContainer> data = new ArrayList<>();

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
         * Applies the downloaded list of {@link NewsContainer} objects to the UI
         */
        @Override
        protected void onPostExecute(List<NewsContainer> data) {
            containerList.setValue(data);
        }
    }
}
