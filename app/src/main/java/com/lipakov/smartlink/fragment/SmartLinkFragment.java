package com.lipakov.smartlink.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.lipakov.smartlink.presenter.SmartLinkPresenter;
import com.lipakov.smartlink.viewmodel.SmartLinkViewModel;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;


public class SmartLinkFragment extends Fragment implements SmartLinkPresenter.SmartLinkView, SmartLinkAdapter.RefreshingSmartLinkList {
    private static final String TAG = SmartLinkFragment.class.getSimpleName();
    private LinearProgressIndicator linearProgressIndicator;
    private SmartLinkAdapter smartLinkAdapter;
    private SmartLinkViewModel smartLinkViewModel;
    private RecyclerView recyclerView;
    private TextView warningText;
    /*TODO*/
    private FloatingActionButton addUrlFab;
    private SwipeRefreshLayout swipeContainer;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    @SuppressLint("FragmentLiveDataObserve")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setRetainInstance(true);
        smartLinkViewModel = new ViewModelProvider(this).get(SmartLinkViewModel.class);
        smartLinkViewModel.getSmartLinkMutableLiveData(requireContext()).observe(this, smartLinkListUpdateObserver);
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
        addUrlFab.setOnClickListener(v -> addUrl(getLayoutInflater()));
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
        smartLinkAdapter = new SmartLinkAdapter(requireContext(), this::refreshSmartLinkFragment);
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
        swipeContainer.setOnRefreshListener(() -> smartLinkViewModel.getRefreshingSmartLinkMutableLiveData(requireContext()).observe(this, smartLinkListUpdateObserver));
    }

    public void addUrl(LayoutInflater layoutInflater) {
        final View view = layoutInflater.inflate(R.layout.adding_of_link, null);
        alertDialog = getAlertDialog(view);
        Button positiveButton = activatePositiveButton(alertDialog);
        EditText inputUrl = view.findViewById(R.id.urlInput);
        addTextChangedListener(positiveButton, inputUrl);
        positiveButton.setOnClickListener(v -> {
            SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(requireContext(), this);
            Editable url = inputUrl.getText();
            alertDialog.dismiss();
            progressDialog = getProgressDialog();
            progressDialog.show();
            smartLinkPresenter.addSmartLink(url.toString());
        });
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Ожидайте...");
        return progressDialog;
    }

    private AlertDialog getAlertDialog(View view) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.please_input_url))
                .setCancelable(false)
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
                .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {})
                .show();
    }

    private Button activatePositiveButton(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        return positiveButton;
    }

    private void addTextChangedListener(Button positiveButton, EditText inputUrl) {
        inputUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SmartLinkFragment.this.onTextChanged(charSequence, positiveButton, inputUrl);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void onTextChanged(CharSequence charSequence, Button positiveButton, EditText urlInput) {
        ColorStateList colorStateList;
        UrlValidator validator = new UrlValidator();
        if (validator.isValid(String.valueOf(charSequence))) {
            colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green));
            positiveButton.setEnabled(true);
        } else {
            colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red));
            positiveButton.setEnabled(false);
        }
        urlInput.setBackgroundTintList(colorStateList);
    }

    public void showNotify(String notify) {
        Toast toast = Toast.makeText(requireContext(), notify, Toast.LENGTH_SHORT);
        TextView toastMessage = toast.getView().findViewById(android.R.id.message);
        setTextMessage(notify, toastMessage);
        toast.show();
        alertDialog.show();
        progressDialog.dismiss();
        refreshSmartLinkFragment();
    }

    private void setTextMessage(String notify, TextView toastMessage) {
        if (!notify.isBlank()) {
            toastMessage.setText(notify);
        } else {
            toastMessage.setText(R.string.success_url_added);
        }
    }

    @Override
    public void refreshSmartLinkFragment() {
        smartLinkViewModel.getRefreshingSmartLinkMutableLiveData(requireContext()).observe(this, smartLinkListUpdateObserver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (smartLinkViewModel != null) {
            smartLinkViewModel.saveState();
        }
    }
}
