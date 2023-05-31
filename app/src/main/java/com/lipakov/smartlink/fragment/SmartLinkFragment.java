package com.lipakov.smartlink.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lipakov.smartlink.R;
import com.lipakov.smartlink.adapter.SmartLinkAdapter;
import com.lipakov.smartlink.databinding.SmartLinkFragmentBinding;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.viewmodel.SmartLinkViewModel;

import java.util.List;


public class SmartLinkFragment extends Fragment implements LifecycleOwner {
    private static final String TAG = SmartLinkFragment.class.getSimpleName();
    private LinearProgressIndicator linearProgressIndicator;
    private SmartLinkAdapter smartLinkAdapter;

    private RecyclerView recyclerView;
    private TextView warningText;

    private FloatingActionButton addUrlFab;

    private HandlerUrl handlerUrl;
    private SwipeRefreshLayout swipeContainer;

    @SuppressLint({"NewApi", "FragmentLiveDataObserve", "NotifyDataSetChanged", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.smart_link_fragment, container, false);
        SmartLinkFragmentBinding smartLinkFragmentBinding = SmartLinkFragmentBinding.bind(view);
        linearProgressIndicator = smartLinkFragmentBinding.linearProgressBar;
        warningText = smartLinkFragmentBinding.warningText;
        recyclerView = smartLinkFragmentBinding.recyclerViewOfSmartLinkList;
        addUrlFab = smartLinkFragmentBinding.addUrlFab;
        swipeContainer = smartLinkFragmentBinding.swipeContainer;
        swipeContainer.setColorSchemeResources(R.color.colorElementOfNavigationView);
        setupAdapter();
        linearProgressIndicator.setIndeterminate(true);
        smartLinkViewModelObserve();
        listenAddingOfUrl();
        listenSwipeContainer();
        return view;
    }

    private void setupAdapter() {
        smartLinkAdapter = new SmartLinkAdapter(getContext());
        recyclerView.setAdapter(smartLinkAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    @SuppressLint("FragmentLiveDataObserve")
    public void smartLinkViewModelObserve() {
        SmartLinkViewModel smartLinkViewModel = new ViewModelProvider(this).get(SmartLinkViewModel.class);
        smartLinkViewModel.getSmartLinkMutableLiveData(requireContext()).observe(this, smartLinkListUpdateObserver);
    }

    private final Observer<List<SmartLink>> smartLinkListUpdateObserver = smartLinkList -> {
        if (smartLinkList == null || smartLinkList.isEmpty()) {
            warningText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            warningText.setVisibility(View.GONE);
            smartLinkAdapter.updateSmartLinkList(smartLinkList);
        }
        linearProgressIndicator.setIndeterminate(false);
        swipeContainer.setRefreshing(false);
    };

    private void listenAddingOfUrl() {
        addUrlFab.setOnClickListener(v -> {
            handlerUrl.addUrl();
        });
    }

    private void listenSwipeContainer() {
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            smartLinkViewModelObserve();
        });
    }

    public void setHandlerUrl(HandlerUrl handlerUrl) {
        this.handlerUrl = handlerUrl;
    }

    public interface HandlerUrl {
        void addUrl();
    }
}
