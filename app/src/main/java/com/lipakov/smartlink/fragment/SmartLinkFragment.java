package com.lipakov.smartlink.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lipakov.smartlink.R;
import com.lipakov.smartlink.adapter.SmartLinkAdapter;
import com.lipakov.smartlink.databinding.SmartLinkFragmentBinding;
import com.lipakov.smartlink.viewmodel.SmartLinkViewModel;


public class SmartLinkFragment extends Fragment implements LifecycleOwner {
    private static final String TAG = SmartLinkFragment.class.getSimpleName();
    private LinearProgressIndicator linearProgressIndicator;

    private SmartLinkFragmentBinding smartLinkFragmentBinding;
    @SuppressLint({"NewApi", "FragmentLiveDataObserve", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.smart_link_fragment, container, false);
        smartLinkFragmentBinding = SmartLinkFragmentBinding.bind(view);
        linearProgressIndicator = smartLinkFragmentBinding.linearProgressBar;
        linearProgressIndicator.setIndeterminate(true);
        SmartLinkAdapter smartLinkAdapter = createAdapter();
        SmartLinkViewModel smartLinkViewModel = new ViewModelProvider(this).get(SmartLinkViewModel.class);
        smartLinkViewModel.getSmartLinkMutableLiveData(requireContext()).observe(this, response -> {
            smartLinkAdapter.updateSmartLinkList(response);
            linearProgressIndicator.setIndeterminate(false);
        });
        return view;
    }

    private SmartLinkAdapter createAdapter() {
        RecyclerView recyclerView = smartLinkFragmentBinding.recyclerViewOfSmartLinkList;
        SmartLinkAdapter smartLinkAdapter = new SmartLinkAdapter(getContext());
        recyclerView.setAdapter(smartLinkAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return smartLinkAdapter;
    }
}
