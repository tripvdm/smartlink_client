package com.lipakov.smartlink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.lipakov.smartlink.databinding.ActivityMainBinding;
import com.lipakov.smartlink.fragment.SmartLinkFragment;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.presenter.UserPresenter;
import com.lipakov.smartlink.utils.UtilsUI;
import com.lipakov.smartlink.view.AddingOfSmartLinkListener;

import org.apache.commons.validator.routines.UrlValidator;

import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserPresenter.DisplayView, AddingOfSmartLinkListener.AddingOfSmartLink, SmartLinkFragment.HandlerUrl {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FIRST_SELECTED_ELEMENT_OF_NAVIGATION_VIEW = 0;
    private ActivityMainBinding activityMainBinding;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearProgressIndicator linearProgressIndicator;
    private Intent signInIntent;
    private GoogleSignInClient googleSignInClient;
    private UserPresenter userPresenter;
    private Disposable userDisposable;
    private EditText urlInput;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private final ActivityResultLauncher<Intent> signInActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        initViews();
        initSignInIntent();
        String jsonUserSl = getJsonUserSlFromUserPresenter();
        signInApp(jsonUserSl);
    }

    private void signInApp(String jsonUserSl) {
        if (jsonUserSl.isBlank()) {
            signInActivityResultLauncher.launch(signInIntent);
        } else {
            Gson gson = new Gson();
            UserSl userSl = gson.fromJson(jsonUserSl, UserSl.class);
            displayMainActivity(userSl.getLogin());
            startObserverOfSmartLink();
        }
    }

    private void initViews() {
        drawerLayout = activityMainBinding.drawerLayout;
        navigationView = activityMainBinding.navigationView;
        linearProgressIndicator = activityMainBinding.linearProgressBar;
    }

    private void initSignInIntent() {
        FirebaseApp.initializeApp(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInIntent = googleSignInClient.getSignInIntent();
    }

    private String getJsonUserSlFromUserPresenter() {
        userPresenter = new UserPresenter(getApplicationContext(), this);
        return userPresenter.getSharedPreferences().getString("usersl", "");
    }

    private void onActivityResult(ActivityResult result) {
        Intent data = result.getData();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            linearProgressIndicator.setIndeterminate(true);
            userDisposable = addUser(task);
            startObserverOfSmartLink();
        } catch (ApiException e) {
            Log.e(TAG, e.getLocalizedMessage());
            signInActivityResultLauncher.launch(signInIntent);
        }
    }

    @Override
    public void startObserverOfSmartLink() {
        SmartLinkFragment smartLinkFragment = (SmartLinkFragment) getSupportFragmentManager().findFragmentById(R.id.smartLinkListFrame);
        if (smartLinkFragment == null) {
            smartLinkFragment = new SmartLinkFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.smartLinkListFrame, smartLinkFragment)
                    .commit();
        } else {
            smartLinkFragment.smartLinkViewModelObserve();
        }
        smartLinkFragment.setHandlerUrl(this);
    }

    private Disposable addUser(Task<GoogleSignInAccount> task) throws ApiException {
        GoogleSignInAccount account = task.getResult(ApiException.class);
        return userPresenter.addUser(account);
    }

    @Override
    public void displayMainActivity(String displayLogin) {
        linearProgressIndicator.setIndeterminate(false);
        setActionBar(displayLogin);
        activityMainBinding.navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = activityMainBinding.drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setActionBar(String displayName) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(displayName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.colorPrimary));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (userDisposable != null) {
            userDisposable.dispose();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (itemId == R.id.signOut) {
            googleSignInClient.signOut();
            signInActivityResultLauncher.launch(signInIntent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void addUrl() {
        urlInput = getUrlInput();
        alertDialog = getAlertDialog(urlInput);
        progressDialog = UtilsUI.createProgressDialog(this);
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        urlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.this.onTextChanged(charSequence, positiveButton, urlInput);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        positiveButton.setOnClickListener(view -> handlerOfPositiveButton());
    }

    @Override
    public EditText getEditText() {
        return urlInput;
    }

    @Override
    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    @NonNull
    private EditText getUrlInput() {
        EditText urlInput = UtilsUI.createEditText(MainActivity.this);
        urlInput.setHint("http://url...");
        ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black));
        urlInput.setBackgroundTintList(colorStateList);
        return urlInput;
    }

    private AlertDialog getAlertDialog(EditText urlInput) {
        return new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.please_input_url))
                .setCancelable(false)
                .setView(urlInput)
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
                .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {})
                .show();
    }

    private void onTextChanged(CharSequence charSequence, Button positiveButton, EditText urlInput) {
        ColorStateList colorStateList;
        UrlValidator validator = new UrlValidator();
        if (validator.isValid(String.valueOf(charSequence))) {
            colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.green));
            positiveButton.setEnabled(true);
        } else {
            colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.red));
            positiveButton.setEnabled(false);
        }
        urlInput.setBackgroundTintList(colorStateList);
    }

    private void handlerOfPositiveButton() {
        AddingOfSmartLinkListener addingOfSmartLinkListener = new AddingOfSmartLinkListener(getApplicationContext(), this);
        addingOfSmartLinkListener.onClick();
    }

}