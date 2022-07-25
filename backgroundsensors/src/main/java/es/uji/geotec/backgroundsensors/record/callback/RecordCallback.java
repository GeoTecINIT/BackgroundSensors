package es.uji.geotec.backgroundsensors.record.callback;

import java.util.List;

import es.uji.geotec.backgroundsensors.record.Record;

public interface RecordCallback<T extends Record> {
    void onRecordsCollected(List<T> records);
}
