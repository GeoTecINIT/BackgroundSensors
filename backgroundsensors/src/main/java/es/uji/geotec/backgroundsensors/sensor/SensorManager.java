package es.uji.geotec.backgroundsensors.sensor;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SensorManager {

    private Context context;

    public SensorManager(Context context) {
        this.context = context;
    }

    public List<Sensor> availableSensors(Sensor[] sensors) {
        List<Sensor> availableSensors = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (isSensorAvailable(sensor))
                availableSensors.add(sensor);
        }

        return availableSensors;
    }

    public boolean isSensorAvailable(Sensor sensor) {
        return context.getPackageManager().hasSystemFeature(sensor.getSystemFeature());
    }
}
