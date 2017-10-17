package ar.com.sebas.androidgooglemaps;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String serverKey = "AIzaSyAQb2I3ERqD4ch75NHMOblaldwvDiYWFkU";
        LatLng origin = new LatLng(37.7849569, -122.4068855);
        LatLng destination = new LatLng(37.7814432, -122.4460177);
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
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d("AndroidGoogleMaps", t.getMessage());
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
