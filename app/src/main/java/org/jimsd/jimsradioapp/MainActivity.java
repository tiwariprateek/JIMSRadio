package org.jimsd.jimsradioapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import layout.AboutUs;
import layout.ContactUs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout nav_layout;
    //TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //txt_title=(TextView)findViewById(R.id.txt_main_welcome);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);
        nav_layout = (LinearLayout) findViewById(R.id.layout_frag_main);
        nav_layout.removeAllViews();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_frag_main, new ShowSelector()).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            //txt_title.setText("Welcome user");

        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert)
                    .setTitle("Exit..?")
                    .setMessage("Are you sure you want to exit..?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shows) {
            Toast.makeText(this, "Viewing shows..", Toast.LENGTH_SHORT).show();
            nav_layout.removeAllViews();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new ShowSelector()).addToBackStack(null).commit();
            //txt_title.setText("Welcome User");

        }else if (id == R.id.nav_fav) {
            Toast.makeText(this, "Viewing Favourites..", Toast.LENGTH_SHORT).show();
            nav_layout.removeAllViews();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new FavoritesFragment()).addToBackStack(null).commit();
            //txt_title.setText("Favourites");

        } else if (id == R.id.nav_schedule) {
            Toast.makeText(this, "Schedules", Toast.LENGTH_SHORT).show();
            nav_layout.removeAllViews();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new ScheduleFragment()).addToBackStack(null).commit();
            //txt_title.setText("Upcoming Schedules");
        }
        else if (id == R.id.nav_suggest) {
            Toast.makeText(this, "Submit suggestions to the admin..", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new SuggestionFragment()).addToBackStack(null).commit();
            //txt_title.setText("Suggestions");

        }
        else if (id == R.id.nav_rate) {
            Toast.makeText(this, "Rate the application..", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new RatingFragment()).addToBackStack(null).commit();
            //txt_title.setText("Rating");

        }
        else if (id == R.id.nav_about) {
            Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new AboutUs()).addToBackStack(null).commit();

        }
        else if (id == R.id.nav_contact) {
            Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_frag_main, new ContactUs()).addToBackStack(null).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}