package ar.com.sebas.androidgooglemaps;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sebas on 17/10/2017.
 */

public class MapInteractor  implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener{

    private final MapActivity context;
    private GoogleMap googleMap;
    private Location currentLocation;
    private Polyline routeLines;

    private GoogleMapFragment mFirstMapFragment;

    public MapInteractor(MapActivity context, int map) {
        this.context = context;
        SupportMapFragment mapFragment = (SupportMapFragment) context.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //TODO: Implementar FusedLocationProviderApi en reemplazo de setOnMyLocationChangeListener
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(currentLocation == null) {
            final LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
        }
        this.currentLocation = location;
    }

    public void GetRecorrido(String addressText) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(addressText, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addresses.get(0);
        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
        GetRecorrido(destination);
    }

    public void GetRecorrido(final LatLng destination) {
        String serverKey = context.getResources().getString(R.string.google_maps_key);
        final LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transitMode(TransportMode.DRIVING)
                .unit(Unit.METRIC)
                .language(Language.SPANISH)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        Log.d("AndroidGoogleMaps", rawBody);
                        if(routeLines != null)
                        {
                            routeLines.remove();
                        }
                        if(!direction.getRouteList().isEmpty()) {
                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, context.getResources().getColor(R.color.colorRoute));
                            routeLines = googleMap.addPolyline(polylineOptions);

                            LatLng bound1 = new LatLng(origin.latitude <= destination.latitude
                                    ? origin.latitude
                                    : destination.latitude,
                                    origin.longitude <= destination.longitude
                                            ? origin.longitude
                                            : destination.longitude);
                            LatLng bound2 = new LatLng(origin.latitude > destination.latitude
                                    ? origin.latitude
                                    : destination.latitude,
                                    origin.longitude > destination.longitude
                                            ? origin.longitude
                                            : destination.longitude);
                            LatLngBounds bounds = new LatLngBounds(bound1, bound2);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d("AndroidGoogleMaps", t.getMessage());
                    }
                });
    }
}
