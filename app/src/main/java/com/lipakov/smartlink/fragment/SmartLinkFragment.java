package com.lipakov.smartlink.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private SmartLinkViewModel smartLinkViewModel;
    private RecyclerView recyclerView;
    private TextView warningText;
    /*TODO*/
    private FloatingActionButton addUrlFab;

    private SwipeRefreshLayout swipeContainer;

    @SuppressLint("FragmentLiveDataObserve")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        smartLinkViewModel = new ViewModelProvider(this).get(SmartLinkViewModel.class);
        smartLinkViewModel.getSmartLinkMutableLiveData(requireContext(), false).observe(this, smartLinkListUpdateObserver);
    }

    @SuppressLint({"NewApi", "NotifyDataSetChanged", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.smart_link_fragment, container, false);
        SmartLinkFragmentBinding smartLinkFragmentBinding = SmartLinkFragmentBinding.bind(view);
        setPullViews(smartLinkFragmentBinding);
        swipeContainer.setColorSchemeResources(R.color.colorElementOfNavigationView);
        linearProgressIndicator.setIndeterminate(true);
        setupAdapter();
        listenSwipeContainer();
        return view;
    }

    private void setPullViews(SmartLinkFragmentBinding smartLinkFragmentBinding) {
        linearProgressIndicator = smartLinkFragmentBinding.linearProgressBar;
        warningText = smartLinkFragmentBinding.warningText;
        recyclerView = smartLinkFragmentBinding.recyclerViewOfSmartLinkList;
        addUrlFab = smartLinkFragmentBinding.addUrlFab;
        swipeContainer = smartLinkFragmentBinding.swipeContainer;
    }

    private void setupAdapter() {
        smartLinkAdapter = new SmartLinkAdapter(getContext(), getLayoutInflater());
        recyclerView.setAdapter(smartLinkAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    /**
    * Если список null, тогда не соединения,
    * если список не null, но пустой, тогда выводим об прдепреждении пустого списка,
    * Иначе выводим список
    * */
    private final Observer<List<SmartLink>> smartLinkListUpdateObserver = smartLinkList -> {
        if (smartLinkList == null) {
            setStateOfFragment(true, R.string.not_connection_text);
        } else if (smartLinkList.isEmpty()) {
            setStateOfFragment(true, R.string.empty_list_text);
        } else {setStateOfFragment(false, -1);
            smartLinkAdapter.updateSmartLinkList(smartLinkList);
        }
        linearProgressIndicator.setIndeterminate(false);
        swipeContainer.setRefreshing(false);
    };

    private void setStateOfFragment(boolean warning, int resIdOfWarning) {
        if (warning) {
            warningText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            warningText.setText(resIdOfWarning);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            warningText.setVisibility(View.GONE);
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private void listenSwipeContainer() {
        swipeContainer.setOnRefreshListener(() -> smartLinkViewModel.getSmartLinkMutableLiveData(requireContext(), true).observe(this, smartLinkListUpdateObserver));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (smartLinkViewModel != null) {
            smartLinkViewModel.saveState(warningText.getVisibility());
        }
    }
}
