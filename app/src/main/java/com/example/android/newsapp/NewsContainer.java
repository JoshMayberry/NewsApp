package com.example.android.newsapp;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Must be public to use databinding
/**
 * Holds data pertaining to a list item for a {@link NewsAdapter}
 * This is a Generic class that is meant to be extended by a child class.
 *
 * Some methods (such as {@link #}) have a default action.
 * They can be overridden by children to modify behavior.
 *
 * Use: https://stackoverflow.com/questions/18204190/java-abstract-classes-returning-this-pointer-for-derived-classes/39897781#39897781
 */
public class NewsContainer extends BaseObservable {
    String LOG_TAG = NewsContainer.class.getSimpleName();

    //See: https://developer.android.com/reference/java/text/DateFormat.html
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    public String title = null;
    public String author = null;
    public String section = null;
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
                ", section='" + section + '\'' +
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
    public String getSection() {
        return section;
    }

    @Bindable
    public String getDate() {
        return dateFormat.format(date);
    }

    @Bindable
    public String getTime() {
        return timeFormat.format(date);
    }

    Uri getPage() {
        return QueryUtilities.createUri(urlPage);
    }

    //Setters
    NewsContainer setAuthor(String author) {
        this.author = author;
        notifyPropertyChanged(BR.author);
        return this;
    }

    public void setSection(String section) {
        this.section = section;
        notifyPropertyChanged(BR.section);
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

    NewsContainer setDate(long dateRaw) {
        this.dateRaw = dateRaw;
        this.date = new Date(dateRaw);
        notifyPropertyChanged(BR.date);
        notifyPropertyChanged(BR.time);
        return this;
    }

    NewsContainer setUrlPage(String urlPage) {
        this.urlPage = urlPage;
        return this;
    }
}
