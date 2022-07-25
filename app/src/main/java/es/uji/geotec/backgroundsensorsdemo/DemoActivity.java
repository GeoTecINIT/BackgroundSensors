package es.uji.geotec.backgroundsensorsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import es.uji.geotec.backgroundsensors.collection.BaseCollectorManager;
import es.uji.geotec.backgroundsensors.collection.CollectionConfiguration;
import es.uji.geotec.backgroundsensors.collection.CollectorManager;
import es.uji.geotec.backgroundsensors.sensor.Sensor;
import es.uji.geotec.backgroundsensors.service.BaseSensorRecordingService;
import es.uji.geotec.backgroundsensors.service.manager.ServiceManager;

public class DemoActivity extends AppCompatActivity {

    private static final String TAG = "Background Sensors Demo";

    private CollectorManager collectorManager;
    private ServiceManager serviceManager;
    private Spinner sensorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        collectorManager = new BaseCollectorManager(this);
        serviceManager = new ServiceManager(this, BaseSensorRecordingService.class);

        sensorSpinner = findViewById(R.id.sensors_spinner);
        List<Sensor> sensors = collectorManager.availableSensors();
        ArrayAdapter<Sensor> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sensors);
        sensorSpinner.setAdapter(adapter);

        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: " + adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onStartTap(View view) {
        Sensor sensor = (Sensor) sensorSpinner.getSelectedItem();
        CollectionConfiguration config = new CollectionConfiguration(sensor, SensorManager.SENSOR_DELAY_GAME, 50);
        serviceManager.startCollection(config, records -> {
            Log.d(TAG, "onRecordsCollected: " + records.size() + " records");
            Log.d(TAG, "a sample: " + records.get(0));
        });
    }

    public void onStopTap(View view) {
        Sensor sensor = (Sensor) sensorSpinner.getSelectedItem();
        this.serviceManager.stopCollection(sensor);
    }
}