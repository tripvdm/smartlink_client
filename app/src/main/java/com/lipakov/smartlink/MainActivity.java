package com.lipakov.smartlink;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

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
import com.lipakov.smartlink.utils.UtilsUI;
import com.lipakov.smartlink.view.AddingOfSmartLinkListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FIRST_SELECTED_ELEMENT_OF_NAVIGATION_VIEW = 0;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.navigationView)
    NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInActivityResultLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                String displayName = "";
                if (account == null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        account = task.getResult(ApiException.class);
                        displayName = account.getDisplayName();
                    } catch (ApiException e) {
                        Log.e("TAG", "Google sign in failed: " + e.getMessage());
                    }
                } else {
                    displayName = account.getDisplayName();
                }
                if (Objects.requireNonNull(displayName).isBlank()) {
                    displayMainActivity(getString(R.string.app_name));
                } else {
                    displayMainActivity(displayName);
                }
            });

    private void displayMainActivity(String displayLogin) {
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
            EditText urlInput = UtilsUI.createEditText(MainActivity.this);
            ProgressDialog progressDialog = UtilsUI.createProgressDialog(this);
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Add utl to field")
                    .setView(urlInput)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            materialAlertDialogBuilder
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        progressDialog.show();
                        AlertDialog alertDialog = materialAlertDialogBuilder.create();
                        AddingOfSmartLinkListener dialogInterface = new AddingOfSmartLinkListener(getApplicationContext(),
                                        urlInput, progressDialog, alertDialog);
                        dialogInterface.onClick(dialog, which);
                    }).show();
        } else if (itemId == R.id.settings) {
            /*TODO Добавить Настройки*/
        } else if (itemId == R.id.exit) {
            MainActivity.this.finish();
            System.exit(0);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}