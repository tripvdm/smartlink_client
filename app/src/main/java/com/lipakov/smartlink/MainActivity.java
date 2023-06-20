package com.lipakov.smartlink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.lipakov.smartlink.databinding.ActivityMainBinding;
import com.lipakov.smartlink.fragment.SmartLinkFragment;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.presenter.UserPresenter;

import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserPresenter.DisplayView {
    private static final String TAG = MainActivity.class.getSimpleName();
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

    public void startObserverOfSmartLink() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.smartLinkListFrame, new SmartLinkFragment())
                .commit();
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

}