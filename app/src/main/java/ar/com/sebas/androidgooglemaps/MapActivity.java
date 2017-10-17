package ar.com.sebas.androidgooglemaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MapActivity extends AppCompatActivity {

    private EditText destino;
    private MapInteractor mapInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState != null) {
            return;
        }
        destino = (EditText)findViewById(R.id.destino);

        mapInteractor = new MapInteractor(this, R.id.map);
    }

    public void btnRecorrido_click(View view) {
        mapInteractor.GetRecorrido(destino.getText().toString());
    }
}
