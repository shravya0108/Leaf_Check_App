package com.example.bananaleafdisease;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bananaleafdisease.auth.LoginActivity;
import com.example.bananaleafdisease.info_page.Info;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        mDatabase = FirebaseDatabase.getInstance("https://banana-leaf-disease-default-rtdb.firebaseio.com/").getReference();
        String userId = currentUser.getUid();

        // Retrieve the first name of the user from the Firebase database
        mDatabase.child("users").child(userId).child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.getValue(String.class);
                if (firstName != null) {
                    TextView welcomeText = findViewById(R.id.welcomeText);
                    welcomeText.setText("Welcome " + firstName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView);
        ImageButton optionsButton = findViewById(R.id.menu_button);

        optionsButton.setOnClickListener(v -> showOptionsPopup());

        Menu bottomMenu = bottomNavigation.getMenu();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_identify) {
                    item.setIcon(R.drawable.ic_add_a_photo);
                    bottomMenu.findItem(R.id.nav_info).setIcon(R.drawable.outline_info_24);
                    bottomMenu.findItem(R.id.nav_about).setIcon(R.drawable.baseline_add_circle_outline_24);

                    load_fragments(new Identify(), false);
                } else if (id == R.id.nav_info) {
                    item.setIcon(R.drawable.ic_baseline_info_24);
                    bottomMenu.findItem(R.id.nav_identify).setIcon(R.drawable.ic_outline_add_a_photo_24);
                    bottomMenu.findItem(R.id.nav_about).setIcon(R.drawable.baseline_add_circle_outline_24);

                    load_fragments(new Info(), true);
                } else if (id == R.id.nav_about) {
                    item.setIcon(R.drawable.baseline_add_circle_24);
                    bottomMenu.findItem(R.id.nav_identify).setIcon(R.drawable.ic_outline_add_a_photo_24);
                    bottomMenu.findItem(R.id.nav_info).setIcon(R.drawable.outline_info_24);

                    load_fragments(new About(), false);
                } else {
                    load_fragments(new Identify(), true);
                }

                return true;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.nav_identify);
    }

    public void load_fragments(Fragment fragment, boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag) {
            ft.add(R.id.container, fragment);
        } else {
            ft.replace(R.id.container, fragment);
        }
        ft.commit();
    }

    private void showOptionsPopup() {


        View popupView = getLayoutInflater().inflate(R.layout.popup_options, null);
        popupView.setBackgroundColor(Color.WHITE);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(findViewById(R.id.menu_button));

        Button logoutButton = popupView.findViewById(R.id.logout_button);
        Button horticultureButton = popupView.findViewById(R.id.horticulture_button);


        horticultureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double currentLatitude = location.getLatitude();
                                    double currentLongitude = location.getLongitude();
                                    String uri = "geo:" + currentLatitude + "," + currentLongitude + "?q=horticulture+office";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Could not get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double currentLatitude = location.getLatitude();
                                    double currentLongitude = location.getLongitude();
                                    String uri = "geo:" + currentLatitude + "," + currentLongitude + "?q=horticulture+office";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Could not get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }


}