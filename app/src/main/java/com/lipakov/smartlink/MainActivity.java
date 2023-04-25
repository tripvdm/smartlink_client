package com.lipakov.smartlink;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.buylink.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.lipakov.smartlink.presenter.UserPresenter;
import com.lipakov.smartlink.utils.UtilsUI;
import com.lipakov.smartlink.view.AddingOfSmartLinkListener;

import org.apache.commons.validator.routines.UrlValidator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserPresenter.DisplayView {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FIRST_SELECTED_ELEMENT_OF_NAVIGATION_VIEW = 0;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.navigationView)
    NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;
    /*TODO need for onActivityResult*/
    private Intent signInIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        displayMainActivity(getString(R.string.app_name));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        /**TODO after release app**/
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInIntent = mGoogleSignInClient.getSignInIntent();
        signInActivityResultLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);

    private void onActivityResult(ActivityResult result) {
        Intent data = result.getData();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = task.getResult(ApiException.class);
                UserPresenter userPresenter = new UserPresenter(getApplicationContext(), MainActivity.this);
                userPresenter.addUser(account);
            } catch (ApiException e) {
                Log.e("TAG", "Google sign in failed: " + e.getMessage());
                /*TODO  signInActivityResultLauncher.launch(signInIntent) */
            }
        } else {
            displayMainActivity(account.getDisplayName());
        }
    }

    @Override
    public void displayMainActivity(String displayLogin) {
        setActionBar(displayLogin);
        navigationView.setNavigationItemSelectedListener(this);
        initSelectedElementOfNavigationView();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                null, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setActionBar(String displayName) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(displayName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setBackgroundDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), R.color.colorPrimary));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Start Create Options Menu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchOfLink);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
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

    private void initSelectedElementOfNavigationView() {
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(FIRST_SELECTED_ELEMENT_OF_NAVIGATION_VIEW);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.addURL) {
            EditText urlInput = getUrlInput();
            AlertDialog alertDialog = getAlertDialog(urlInput);
            ProgressDialog progressDialog = UtilsUI.createProgressDialog(this);
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
            positiveButton.setOnClickListener(view -> handlerOfPositiveButton(urlInput, alertDialog, progressDialog));
        } else if (itemId == R.id.settings) {
            /* TODO Добавить Настройки */
        } else if (itemId == R.id.exit) {
            MainActivity.this.finish();
            System.exit(0);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                .setTitle("Add URL")
                .setCancelable(false)
                .setView(urlInput)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                .setPositiveButton("Confirm", (dialogInterface, i) -> {})
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

    private void handlerOfPositiveButton(EditText urlInput, AlertDialog alertDialog, ProgressDialog progressDialog) {
        alertDialog.dismiss();
        progressDialog.show();
        AddingOfSmartLinkListener addingOfSmartLinkListener = new AddingOfSmartLinkListener(getApplicationContext(), urlInput, progressDialog, alertDialog);
        addingOfSmartLinkListener.onClick();
    }

}