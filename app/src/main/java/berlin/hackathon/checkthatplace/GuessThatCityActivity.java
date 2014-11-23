package berlin.hackathon.checkthatplace;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.sensors.MagnetSensor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

public class GuessThatCityActivity extends CardboardActivity implements CardboardView.StereoRenderer, MagnetSensor.OnCardboardTriggerListener {

    private MapFragment map1; // Might be null if Google Play services APK is not available.
    private MapFragment map2; // Might be null if Google Play services APK is not available.
    private CardboardView view;
    private MagnetSensor sensor;

    private View menuLeft;
    private View menuRight;

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn1l;
    private Button btn2l;
    private Button btn3l;
    private Button btn4l;
    private LocationInfo currentTarget;

    private View wrong1;
    private View wrong2;
    private View correct1;
    private View correct2;

    private int currentButton = 1;


    private class LocationInfo {
        public LatLng coord;
        public String name;

        public LocationInfo(String name, double lat, double lon) {
            this.name = name;
            this.coord = new LatLng(lat, lon);
        }
    }

    private List<LocationInfo> infos = new LinkedList<LocationInfo>();


    public void setupLocations() {

        //      infos.add(new LocationInfo("MNTN VIEW",37.422036, -122.084068));

        infos.add(new LocationInfo("BERLIN", 52.520819, 13.409406));

        infos.add(new LocationInfo("ROME", 41.890210, 12.492231));

        infos.add(new LocationInfo("NEW YORK", 40.689249, -74.0445));

        infos.add(new LocationInfo("LONDON", 51.500729, -0.124625));

        infos.add(new LocationInfo("NIGERIA", 16.864930, 11.95376));

        infos.add(new LocationInfo("ORIGON", 45.123645, -123.113888));

        infos.add(new LocationInfo("Guitar shaped field", -33.867898, -63.987062));


        infos.add(new LocationInfo("Jesus loves you", 43.645074, -115.993081));


        infos.add(new LocationInfo("ARIZONA", 32.663367, -111.487618));

        infos.add(new LocationInfo("WYOMING", 44.525049, -110.83819));

        infos.add(new LocationInfo("Batman Symbol", 26.357896, 127.783809));

        infos.add(new LocationInfo("The Balm", 25.006615, 54.988055));

    }

    private void setButton1(String name) {
        btn1.setText(name);
        btn1l.setText(name);
    }


    private void setButton2(String name) {
        btn2.setText(name);
        btn2l.setText(name);
    }


    private void setButton4(String name) {
        btn4.setText(name);
        btn4l.setText(name);
    }

    private void setButton3(String name) {
        btn3.setText(name);
        btn3l.setText(name);
    }

    private ArrayList<LocationInfo> currentLocations = new ArrayList<LocationInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_that_city);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupLocations();
        final Handler handler = new Handler();

        Runnable increaseMenuSelection = new Runnable() {
            @Override
            public void run() {
                currentButton++;
                if (currentButton == 5)
                    currentButton = 1;

                setButtonSelected(currentButton);

                handler.postDelayed(this, 1000);


            }
        };

        handler.postDelayed(increaseMenuSelection, 1000);

        btn1 = (Button) this.findViewById(R.id.btn_1_right);
        btn1.setFocusableInTouchMode(true);
        btn2 = (Button) this.findViewById(R.id.btn_2_right);
        btn2.setFocusableInTouchMode(true);
        btn3 = (Button) this.findViewById(R.id.btn_3_right);
        btn3.setFocusableInTouchMode(true);
        btn4 = (Button) this.findViewById(R.id.btn_4_right);
        btn4.setFocusableInTouchMode(true);

        wrong1 = this.findViewById(R.id.wrong_1);
        wrong2 = this.findViewById(R.id.wrong_2);
        correct1 = this.findViewById(R.id.correct_1);
        correct2 = this.findViewById(R.id.correct_2);

        wrong1.setVisibility(View.GONE);
        wrong2.setVisibility(View.GONE);
        correct1.setVisibility(View.GONE);
        correct2.setVisibility(View.GONE);


        btn1l = (Button) this.findViewById(R.id.btn_1_left);
        btn1l.setFocusableInTouchMode(true);
        btn2l = (Button) this.findViewById(R.id.btn_2_left);

        btn2l.setFocusableInTouchMode(true);
        btn3l = (Button) this.findViewById(R.id.btn_3_left);
        btn3l.setFocusableInTouchMode(true);
        btn4l = (Button) this.findViewById(R.id.btn_4_left);
        btn4l.setFocusableInTouchMode(true);

        this.menuLeft = this.findViewById(R.id.menu_left);
        this.menuLeft.setAlpha(0.0f);

        this.menuRight = this.findViewById(R.id.menu_right);
        this.menuRight.setAlpha(0.0f);

        this.sensor = new MagnetSensor(this);

        this.sensor.setOnCardboardTriggerListener(this);

        view = (CardboardView) this.findViewById(R.id.cardboard);
        view.setRenderer(this);

        this.setCardboardView(view);

        setUpMapIfNeeded();
    }

    @Override
    public void onCardboardTrigger() {

        LocationInfo info = this.currentTarget;
        LocationInfo selected = this.currentLocations.get (this.currentButton);

        if (info.name.equals(selected.name))
        {
            // WON
            correct1.setVisibility(View.VISIBLE);
            correct2.setVisibility(View.VISIBLE);
        }
        else
        {
            //WRONG

            // WON
            wrong1.setVisibility(View.VISIBLE);
            wrong2.setVisibility(View.VISIBLE);
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setNewRandomPosition();
            }
        }, 3000);

    }


    private void setNewRandomPosition() {


        wrong1.setVisibility(View.GONE);
        wrong2.setVisibility(View.GONE);
        correct1.setVisibility(View.GONE);
        correct2.setVisibility(View.GONE);


        this.menuLeft.setAlpha(0.0f);
        this.menuRight.setAlpha(0.0f);

        // choose 4 random positions

        currentLocations = new ArrayList<LocationInfo>();

        Random rand = new Random();


        for (int i = 0; i < 4; ++i) {

            boolean inserted = false;
            LocationInfo info = null;
            do {
                // select random city
                info = infos.get(rand.nextInt(infos.size()));

                if (!currentLocations.contains(info)) {
                    inserted = true;
                    currentLocations.add(info);
                }
            } while (!inserted);


            switch (i) {
                case 0:
                    setButton1(info.name);
                    break;
                case 1:
                    setButton2(info.name);
                    break;
                case 2:
                    setButton3(info.name);
                    break;
                case 3:
                    setButton4(info.name);
                    break;
            }
        }


        this.currentTarget = currentLocations.get(rand.nextInt(currentLocations.size()));

        this.infos.remove(this.currentTarget);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentTarget.coord)      // Sets the center of the map to Mountain View
                .zoom(18)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder


        map1.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(currentTarget.coord)      // Sets the center of the map to Mountain View
                        .zoom(19)                   // Sets the zoom
                        .bearing(300)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder


                map1.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2), 20000, null);

            }
        }, 500);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition_ = new CameraPosition.Builder()
                .target(currentTarget.coord)      // Sets the center of the map to Mountain View
                .zoom(18)                   // Sets the zoom
                .bearing(10)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        map2.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_));


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(currentTarget.coord)      // Sets the center of the map to Mountain View
                        .zoom(19)                   // Sets the zoom
                        .bearing(310)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder


                map2.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2), 20000, null);

            }
        }, 500);


        this.menuLeft.animate().alpha(1.0f).setDuration(3000).setStartDelay(8000);
        this.menuRight.animate().alpha(1.0f).setDuration(3000).setStartDelay(8000);

        btn1.requestFocus();
        btn1l.requestFocus();


    }

    protected void setButtonSelected(int num) {
        switch (num) {
            case 1:
                btn1l.requestFocus();
                break;
            case 2:
                btn2l.requestFocus();

                break;
            case 3:
                btn3l.requestFocus();
                break;
            case 4:
                btn4l.requestFocus();

                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        this.sensor.stop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        this.sensor.start();

        View contentView = this.findViewById(R.id.main_content);
        contentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void setUpMapIfNeeded() {


        if (map1 == null) {
            map1 = ((MapFragment) getFragmentManager().findFragmentById(R.id.map1));
            map1.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map1.getMap().setBuildingsEnabled(false);
            map1.getMap().getUiSettings().setZoomControlsEnabled(false);
            map1.getMap().getUiSettings().setAllGesturesEnabled(false);
        }

        if (map2 == null) {
            map2 = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2));
            map2.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map2.getMap().setBuildingsEnabled(false);
            map2.getMap().getUiSettings().setAllGesturesEnabled(false);
            map2.getMap().getUiSettings().setZoomControlsEnabled(false);
        }

        setNewRandomPosition();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(EyeTransform eyeTransform) {

    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i2) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onRendererShutdown() {

    }
}
