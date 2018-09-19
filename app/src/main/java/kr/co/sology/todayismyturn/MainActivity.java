package kr.co.sology.todayismyturn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.sology.fw.core.GlobalEnv;
import kr.co.sology.fw.ctrl.DialogCtrl;
import kr.co.sology.fw.ctrl.ToastCtrl;
import kr.co.sology.fw.ctrl.WebViewCtrl;
import kr.co.sology.fw.util.RuntimePermissionUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String strLogId = "MainActivity";
    ConstraintLayout layoutForBody;
    public List<String> arrBallList;
    SoundPool soundPool;
    int intSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShowBallTask().execute();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layoutForBody = findViewById(R.id.body_layout);

        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        intSoundId = soundPool.load(this, R.raw.dice, 1);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ShowBallTask().execute();
            }
        }, 1000);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        // int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_codescan) {
            loadCodeScanView();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GlobalEnv.intRequestCodeScanKey :
                if (resultCode== Activity.RESULT_OK) {
                    String strResult = data.getStringExtra(GlobalEnv.strScanResultKey);
                    loadScanResultView(strResult);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    /////////////////////////////////////////////////////////////////////
    //
    // Load View
    //
    /////////////////////////////////////////////////////////////////////



    private void loadMakeNumberView() {
        if (layoutForBody!=null) {
            layoutForBody.removeAllViews();
            View.inflate(this, R.layout.layout_getnumber, layoutForBody);
        }
    }

    private void loadBrowserView(String strUrl) {
        layoutForBody.removeAllViews();
        WebViewCtrl wc = new WebViewCtrl(this, layoutForBody);
        wc.loadPage(strUrl);
    }

    private void loadCodeScanView() {
        try {
            if (RuntimePermissionUtil.getInstance(this).hasPermissionCamera()) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setClassName(this, "com.google.zxing.client.android.CaptureActivity");
                startActivityForResult(intent, GlobalEnv.intRequestCodeScanKey);
            }
        } catch (Exception e) {
            Log.e(strLogId, "loadCodeScanView() " + e.toString());
        }
    }

    public void loadScanResultView(String contents) {
        if (contents.contains("http://") || contents.contains("https://")) loadBrowserView(contents); // if 'http://' or 'https://' protocol?
        else { // general string
            AlertDialog.Builder ab = DialogCtrl.getAlertDialog(this, contents);
            ab.setMessage(contents);
            ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            });
            ab.create();
            ab.show();
        }
    }

    private List<String> getRandomString() {
        List<String> arr = new ArrayList<>();
        for (int t=0; t<6; t++) { // first row
            arr.add("0");
        }
        for (int t=0; t<5; t++) {
            int[] row = getRandomNumber();

            for (int aRow : row) {
                arr.add(Integer.toString(aRow));
            }
        }
        return arr;
    }

    private int[] getRandomNumber() {
        int[] row = new int[]{0,0,0,0,0,0};
        while (true) {
            int t = randomize();
            boolean is = false;
            for (int aRow : row) {
                if (aRow == t) {
                    is = true;
                    break;
                }
            }
            if (!is) {
                for (int i=0; i<row.length; i++) {
                    if (row[i] == 0) {
                        row[i] = t;
                        if (i == row.length - 1) {
                            Arrays.sort(row);
                            return row;
                        }
                        break;
                    }
                }
            }
        }
    }

    private int randomize() {
        return (int)(Math.random()*45) + 1;
    }


    /////////////////////////////////////////////////////////////////////
    //
    // Control Runtime Permission
    //
    // For Runtime Permission Over Build.VERSION_CODES.M (marshmallow)
    // phone-state, read-contacts, fine-location, use-storage.
    //
    /////////////////////////////////////////////////////////////////////

    /**
     * This override is for versions above marshmallow.
     * The version of marshmallow has been changed to ask the user permission at the point of launching.
     *
     *
     * @param requestCode request codes is defined GlobalEnv
     * @param permissions  permission
     * @param grantResults grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GlobalEnv.intPermissionRequestCamera : // camera permission is used code-scan
                if (RuntimePermissionUtil.getInstance(this).isGranted(grantResults)) loadCodeScanView();
                else ToastCtrl.showMessageLong(this, getString(R.string.permission_camera));
                break;
        }
    }


    /**
     * adapter for gridview
     *
     */
    private class BallAdapter extends ArrayAdapter<String> {

        BallAdapter(Context context, int resource) {
            super(context, resource, arrBallList);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            try {
                if (v==null) {
                    LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert li != null;
                    v = li.inflate(R.layout.layout_ball, null);
                }
                int id = getResources().getIdentifier("b" + arrBallList.get(position), "drawable", getPackageName());
                ImageView btn = v.findViewById(R.id.ball);
                btn.setImageDrawable(getResources().getDrawable(id));
            }catch (Exception e) {
                Log.e(strLogId, e.toString());
                e.printStackTrace();
            }
            return v;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ShowBallTask extends AsyncTask<Void, Void, Void> {

        FloatingActionButton fab = findViewById(R.id.fab);

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadMakeNumberView();

                    GridView gv = findViewById(R.id.grid_ball);
                    ArrayAdapter<String> arrayAdapter = new BallAdapter(MainActivity.this, R.layout.layout_ball);
                    gv.setAdapter(arrayAdapter);

                    gv.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ball), .1f, .1f));

                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            arrBallList = getRandomString();

            fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab));
            soundPool.play(intSoundId, 1.0F, 1.0F, 1, 0, 1.0F);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ball));
            //fab.setClickable(true);
            super.onPostExecute(aVoid);
        }
    }

}
