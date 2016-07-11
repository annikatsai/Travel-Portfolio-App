package annikatsai.portfolioapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

//    @Override
//    public void onMapLongClick(final LatLng point) {
//        Toast.makeText(this, "Long Press", Toast.LENGTH_LONG).show();
//        // Custom code here...
//        showAlertDialogForPoint(point);
//    }
//
//    // Display the alert that adds the marker
//    private void showAlertDialogForPoint(final LatLng point) {
//        // inflate message_item.xml view
//        View messageView = LayoutInflater.from(MapsActivity.this).
//                inflate(R.layout.message_item, null);
//        // Create alert dialog builder
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        // set message_item.xml to AlertDialog builder
//        alertDialogBuilder.setView(messageView);
//
//        // Create alert dialog
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // Configure dialog button (OK)
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Define color of marker icon
//						BitmapDescriptor defaultMarker =
//								BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//                        // Extract content from alert dialog
//                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
//                                getText().toString();
//                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
//                                getText().toString();
//                        // Creates and adds marker to the map
//                        Marker marker = map.addMarker(new MarkerOptions()
//                                .position(point)
//                                .title(title)
//                                .snippet(snippet)
//                                .icon(customMarker));
//                        dropPinEffect(marker);
//                    }
//                });
//
//        // Configure dialog button (Cancel)
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
//                });
//
//        // Display the dialog
//        alertDialog.show();
//    }
//
//    private void dropPinEffect(final Marker marker) {
//        // Handler allows us to repeat a code block after a specified delay
//        final android.os.Handler handler = new android.os.Handler();
//        final long start = SystemClock.uptimeMillis();
//        final long duration = 1500;
//
//        // Use the bounce interpolator
//        final android.view.animation.Interpolator interpolator =
//                new BounceInterpolator();
//
//        // Animate marker with a bounce updating its position every 15ms
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                // Calculate t for bounce based on elapsed time
//                float t = Math.max(
//                        1 - interpolator.getInterpolation((float) elapsed
//                                / duration), 0);
//                // Set the anchor
//                marker.setAnchor(0.5f, 1.0f + 14 * t);
//
//                if (t > 0.0) {
//                    // Post this event again 15ms from now.
//                    handler.postDelayed(this, 15);
//                } else { // done elapsing, show window
//                    marker.showInfoWindow();
//                }
//            }
//        });
//    }
}
