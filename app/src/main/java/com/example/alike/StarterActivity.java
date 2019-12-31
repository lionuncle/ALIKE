package com.example.alike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class StarterActivity extends AppCompatActivity {
    DrawerLayout drawer;
    Toolbar toolbar;
    SliderView sliderView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        drawer = findViewById(R.id.drawer_layout);
        //toolbar.setNavigationIcon(R.drawable.ic_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);


        sliderView = findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(new SliderAdapter(getApplicationContext()));
        sliderView.startAutoCycle();
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_about:
                        Toast.makeText(StarterActivity.this, "WE ARE TEAM ALIKE", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_log:
                        finish();
                        System.exit(0);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;


            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StarterActivity.this,MainActivity.class));
            }
        });

    }
}
