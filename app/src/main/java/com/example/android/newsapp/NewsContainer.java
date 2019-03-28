package com.example.android.newsapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Holds data pertaining to a list item for a {@link NewsAdapter}
 * This is a Generic class that is meant to be extended by a child class.
 * <p>
 * Some methods (such as {@link #}) have a default action.
 * They can be overridden by children to modify behavior.
 * <p>
 * Use: https://stackoverflow.com/questions/18204190/java-abstract-classes-returning-this-pointer-for-derived-classes/39897781#39897781
 */
class NewsContainer {
    String LOG_TAG = NewsContainer.class.getSimpleName();
    Context context = null;

    //See: https://developer.android.com/reference/java/text/DateFormat.html
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    private String date = null;
    private String time = null;
    private String title = null;
    private String author = null;
    private String section = null;
    private String subText = null;
    private String dateRaw = null;
    private String urlPage = null;

    @Override
    public String toString() {
        return "NewsContainer{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", subText='" + subText + '\'' +
                ", dateRaw='" + dateRaw + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", urlPage='" + urlPage + '\'' +
                ", section='" + section + '\'' +
                '}';
    }

    NewsContainer(Context context) {
        this.context = context;
    }

    //Getters
    String getTitle() {
        return title;
    }

    String getSubText() {
        return subText;
    }

    String getAuthor() {
        if (author == null) {
            return context.getString(R.string.error_author);
        }
        return author;
    }

    String getSection() {
        return section;
    }

    String getDate() {
        return date;
    }

    String getTime() {
        return time;
    }

    Uri getPage() {
        return QueryUtilities.createUri(urlPage);
    }

    //Setters
    NewsContainer setAuthor(String text) {
        this.author = text;
        return this;
    }

    NewsContainer setSection(String text) {
        this.section = text;
        return this;
    }

    NewsContainer setTitle(String text) {
        this.title = text;
        return this;
    }

    NewsContainer setSubText(String text) {
        this.subText = text;
        return this;
    }

    NewsContainer setDate(String dateRaw) {
        try {
            this.date = dateFormat.parse(dateRaw).toString();
            this.time = timeFormat.parse(dateRaw).toString();
        } catch (ParseException error) {
            Log.e(LOG_TAG, "Parse Date Error", error);
            this.date = null;
            this.time = null;
        }
        return this;
    }

    NewsContainer setUrlPage(String text) {
        this.urlPage = text;
        return this;
    }
}
