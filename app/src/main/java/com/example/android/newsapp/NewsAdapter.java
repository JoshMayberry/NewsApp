package com.example.android.newsapp;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.newsapp.databinding.ListItemBinding;

import java.util.List;

//See: https://www.javacodegeeks.com/2013/07/java-generics-tutorial-example-class-interface-methods-wildcards-and-much-more.html#generics-naming-convention

/**
 * Handles recycling views on a {@link MainActivity}
 */
class NewsAdapter extends ArrayAdapter<NewsContainer> {
    String LOG_TAG = NewsAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    NewsAdapter(Context context, List<NewsContainer> containerList) {
        super(context, 0, containerList);
    }

    //See: https://github.com/nomanr/android-databinding-example/blob/master/app/src/main/java/com/databinding/example/databindingexample/adapters/SimpleAdapter.java
    //Use: https://stackoverflow.com/questions/40557087/how-can-i-use-android-databinding-in-a-listview-and-still-use-a-viewholder-patte/40557175#40557175
    @Override
    public View getView(int position, View scrapView, ViewGroup parent) {
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }

        ListItemBinding binding = DataBindingUtil.getBinding(scrapView);
        if (binding == null) {
            binding = ListItemBinding.inflate(inflater, parent, false);
        }

        //The data binding does not seem to work with automatically changing the items... So this will be used for now...
        //Ideally, the code from here to the end will be gone
        //I think it does not work because the containers are populated before the views are created. This means that the setter methods are called before they can notify the binding that a change has taken place?
        NewsContainer container = getItem(position);
        if (container != null) {
            binding.time.setText(container.getTime());
            binding.date.setText(container.getDate());
            binding.title.setText(container.getTitle());
            binding.author.setText(container.getAuthor());
            binding.subText.setText(container.getSubText());
            binding.sectionName.setText(container.getSection());
        }
        return binding.getRoot();
    }
}
