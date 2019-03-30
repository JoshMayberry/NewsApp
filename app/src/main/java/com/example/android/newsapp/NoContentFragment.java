package com.example.android.newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsapp.databinding.FragmentNoContentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NoContentFragment extends Fragment {
	String LOG_TAG = NoContentFragment.class.getSimpleName();
	FragmentNoContentBinding binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//See: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment/40527833#40527833
		binding = FragmentNoContentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}
}
