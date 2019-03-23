package com.example.android.newsapp;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Must be public to use databinding
/**
 * Holds data pertaining to a list item for a {@link NewsAdapter}
 * This is a Generic class that is meant to be extended by a child class.
 *
 * Some methods (such as {@link #getContainerList}) have a default action.
 * They can be overridden by children to modify behavior.
 *
 * Use: https://stackoverflow.com/questions/18204190/java-abstract-classes-returning-this-pointer-for-derived-classes/39897781#39897781
 */
public class NewsContainer extends BaseObservable {
    String LOG_TAG = NewsContainer.class.getSimpleName();

    public String author = null;
    public String title = null;
    public String subText = null;
    public Date date = null;
    private long dateRaw = -1;
    private String urlPage = null;

    @Override
    public String toString() {
        return "NewsContainer{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", subText='" + subText + '\'' +
                ", date=" + date +
                ", dateRaw=" + dateRaw +
                ", urlPage='" + urlPage + '\'' +
                '}';
    }

    NewsContainer() {
    }

    //Getters
    //See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#6
    //See: Android Data Binding Library - Update UI using Observable objects: https://www.youtube.com/watch?v=gP_zj-CIBvM
    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public String getSubText() {
        return subText;
    }

    @Bindable
    public String getAuthor() {
        return author;
    }

    @Bindable
    public Date getDate() {
        return date;
    }

    Uri getPage() {
        if (urlPage.isEmpty()) {
            return null;
        }
        return Uri.parse(urlPage);
    }

    //Setters
    NewsContainer setAuthor(String author) {
        this.author = author;
        notifyPropertyChanged(BR.author);
        return this;
    }

    NewsContainer setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
        return this;
    }

    NewsContainer setSubText(String subText) {
        this.subText = subText;
        notifyPropertyChanged(BR.subText);
        return this;
    }

    NewsContainer setDate(Date date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
        return this;
    }

    NewsContainer setDateRaw(long dateRaw) {
        this.dateRaw = dateRaw;
        return this;
    }

    NewsContainer setUrlPage(String urlPage) {
        this.urlPage = urlPage;
        return this;
    }

//    /**
//     * Returns a list of sub-items for this container.
//     * If there are no sub items, then just return a list of length 1 that contains {@code this}.
//     * Can be overridden to parse the {@link JSONObject}s differently.
//     * @see #populateContainerInfo
//     */
//    List<NewsContainer> getContainerList(JSONObject root, JSONObject number) throws JSONException, IOException {
//        List<NewsContainer> data = new ArrayList<>();
//        if (!populateContainerInfo(root, number)) {
//            return null;
//        }
//        data.add(this);
//        return data;
//    }
//
//    /**
//     * Populates this container with data from the provided {@link JSONObject}s.
//     * Can be overridden to parse the {@link JSONObject}s differently.
//     * @see #parseListData
//     * @see #parseDetailData
//     */
//    boolean populateContainerInfo(JSONObject root, JSONObject number) throws JSONException, IOException {
//        if (!parseListData(root, number)) {
//            return false;
//        }
//
//        String jsonResponseDetails = getJsonUrlDetails();
//        if (jsonResponseDetails == null) {
//            return false;
//        }
//
//        return parseDetailData(QueryUtilities.makeHttpRequest(jsonResponseDetails));
//    }
//
//    /**
//     * What URL to get detailed information about this container from.
//     * Can be overridden to parse the {@link JSONObject}s differently.
//     * @see #parseDetailData
//     */
//    String getJsonUrlDetails() {
//        if (indexPage == -1) {
//            return null;
//        }
//        return "https://xenoblade.fandom.com/api/v1/Articles/Details?ids=" + indexPage;
//    }
//
//    /**
//     * Gets basic information from a list-style {@link JSONObject}.
//     * Can be overridden to parse the {@link JSONObject}s differently.
//     */
//    boolean parseListData(JSONObject root, JSONObject number) throws JSONException {
//        //Remove category pages
//        String url = number.getString("url");
//        if (url.contains("Category:")) {
//            return false;
//        }
//
//        //Remove image pages
//        String title = number.getString("title");
//        if (title.contains(".")) {
//            return false;
//        }
//
//        setIndexPage(number.getInt("id"));
//        setTitle(title);
//        setPage(root.getString("basepath") + url);
//        return (indexPage != -1) && (!urlPage.isEmpty());
//    }
//
//    /**
//     * Gets detailed information from a single-style {@link JSONObject}.
//     * Can be overridden to parse the {@link JSONObject}s differently.
//     * @see #getJsonUrlDetails
//     */
//    boolean parseDetailData(String jsonResponse) throws JSONException {
//        JSONObject items = new JSONObject(jsonResponse)
//                .getJSONObject("items");
//        JSONObject number = items.getJSONObject(items.keys().next());
//        setSubText(number.getString("abstract"));
//        setImage(number.getString("thumbnail"));
//        return true;
//    }
}
