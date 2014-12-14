package edu.cmsc434.paintdrip.paintdripprototype.Share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import edu.cmsc434.paintdrip.paintdripprototype.R;

public class ShareActivity extends Activity {

    private UiLifecycleHelper uiHelper;
    private Button fbShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        fbShareButton = (Button) findViewById(R.id.fbShareButton);
        fbShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateFbShare();
            }
        });

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
    }

    private void initiateFbShare() {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink("https://developers.facebook.com/android")
                .setPicture(
                        "http://m.c.lnkd.licdn.com/mpr/mpr/p/4/005/052/10a/0e7ce98.jpg")
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {
                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                                        Exception error, Bundle data) {
                        Log.e("Activity",
                                String.format("Error: %s", error.toString()));
                    }
                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Log.i("Activity", "Success!");
                    }
                });
    }



    @Override
    protected void onResume(){
        super.onResume();
        AppEventsLogger.activateApp(this);
        uiHelper.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        uiHelper.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
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
}
