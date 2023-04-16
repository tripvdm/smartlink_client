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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buylink.R;
import com.lipakov.smartlink.adapter.SmartLinkAdapter;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.viewmodel.SmartLinkViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartLinkFragment extends Fragment implements LifecycleOwner {
    private static final String TAG = SmartLinkFragment.class.getSimpleName();

    private SmartLinkViewModel smartLinkViewModel;
    private SmartLinkAdapter smartLinkAdapter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerViewOfSmartLinkList)
    RecyclerView recyclerView;

    @SuppressLint({"NewApi", "FragmentLiveDataObserve"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.smart_link_fragment, container, false);
        ButterKnife.bind(this, view);
        smartLinkAdapter = new SmartLinkAdapter();
        recyclerView.setAdapter(smartLinkAdapter);
        smartLinkViewModel = new ViewModelProvider(this).get(SmartLinkViewModel.class);
        smartLinkViewModel.getSmartLinkLiveData().observe(this, userListUpdateObserver);
        return view;
    }

    private final Observer<List<SmartLink>> userListUpdateObserver = new Observer<>() {
        @Override
        public void onChanged(List<SmartLink> smartLinkList) {
            smartLinkAdapter.updateSmartLinkList(smartLinkList);
        }
    };

}
