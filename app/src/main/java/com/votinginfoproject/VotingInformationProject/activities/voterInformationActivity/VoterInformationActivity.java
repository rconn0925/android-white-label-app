package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ContestListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment.ElectionDetailsListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesListFragment;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;
import com.votinginfoproject.VotingInformationProject.views.BottomNavigationBar;

public class VoterInformationActivity extends BaseActivity<VoterInformationPresenter> implements
        VoterInformationView,
        BottomNavigationBar.BottomNavigationBarCallback,
        PollingSitesListFragment.PollingSitesListener,
        ElectionDetailsListFragment.ElectionDetailsListFragmentCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ContestListFragment.ContestListListener {

    private final static String TAG = VoterInformationActivity.class.getSimpleName();
    private final static String TOP_LEVEL_TAG = "VIP_TOP_LEVEL_TAG";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voter_information);

        BottomNavigationBar mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        if (mBottomNavigationBar != null) {
            mBottomNavigationBar.setListener(this);
        }

        setPresenter(new VoterInformationPresenterImpl());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startPollingLocation();
        }

        if (savedInstanceState != null) {
            VoterInformation.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Do not call super here until the Support Toolbar Parcelable crash is fixed
        VoterInformation.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            VoterInformation.onRestoreInstanceState(savedInstanceState);
        }

        setPresenter(new VoterInformationPresenterImpl());
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();

        Log.v(TAG, "back count: " + manager.getBackStackEntryCount());

        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getPresenter().backNavigationBarButtonClicked();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void pollsButtonSelected() {
        getPresenter().pollingSitesButtonClicked();
    }

    @Override
    public void ballotButtonSelected() {
        getPresenter().ballotButtonClicked();
    }

    @Override
    public void detailsButtonSelected() {
        getPresenter().detailsButtonClicked();
    }

    @Override
    public void presentParentLevelFragment(Fragment parentLevelFragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        manager.popBackStack(TOP_LEVEL_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.replace(R.id.layout_content, parentLevelFragment, TOP_LEVEL_TAG);

        transaction.addToBackStack(TOP_LEVEL_TAG);

        transaction.commit();
    }

    @Override
    public void presentChildLevelFragment(Fragment childLevelFragment) {
        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        String fragmentTag = String.valueOf(childLevelFragment.hashCode());
        transaction.replace(R.id.layout_content, childLevelFragment, fragmentTag);

        transaction.addToBackStack(fragmentTag);

        transaction.commit();
    }

    @Override
    public void navigateBack() {
        onBackPressed();
    }

    @Override
    public void scrollCurrentFragmentToTop() {
        FragmentManager manager = getFragmentManager();
        int entryCount = manager.getBackStackEntryCount();

        if (entryCount > 0) {
            FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(entryCount - 1);
            Fragment lastFragment = manager.findFragmentByTag(entry.getName());

            if (lastFragment instanceof BottomNavigationFragment) {
                ((BottomNavigationFragment) lastFragment).resetView();
            }
        }
    }

    @Override
    public void mapButtonClicked(@LayoutRes int currentSort) {
        getPresenter().mapButtonClicked(currentSort);
    }

    @Override
    public void listButtonClicked(@LayoutRes int currentSort) {
        getPresenter().listButtonClicked(currentSort);
    }

    //Polling Site List Interface
    @Override
    public void pollingSiteClicked(PollingLocation location) {
        Log.v(TAG, "Polling location Clicked: ");
    }

    @Override
    public void contestClicked(Contest contest) {
        //TODO navigate to Contest Fragment
        Log.v(TAG, "Contest clicked: " + contest.toString());
    }

    @Override
    public void reportErrorClicked() {
        Log.v(TAG, "Report Error Clicked()");
    }

    //Election Details Interface
    @Override
    public void navigateToURL(String urlString) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.background_blue));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(urlString));
    }

    @Override
    public void navigateToErrorView() {
        //TODO add things here
        Log.v(TAG, "Report Error Clicked()");
    }

    @Override
    public void navigateToDirectionsView(String address) {
        //TODO add other things here
        Log.v(TAG, "Address selected: " + address);
    }

    @Override
    public void startPollingLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            VoterInformation.setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Not implemented
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Not implemented
    }
}
