package es.uji.geotec.backgroundsensors.record.accumulator;

import java.util.LinkedList;
import java.util.List;

import es.uji.geotec.backgroundsensors.record.Record;
import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;

public class RecordAccumulator<T extends Record> {

    private RecordCallback<T> callback;
    private int limit;
    private List<T> records;

    public RecordAccumulator(RecordCallback<T> callback, int limit) {
        this.callback = callback;
        this.limit = limit;
        this.records = new LinkedList<>();
    }

    public void accumulateRecord(T record) {
        records.add(record);

        if (records.size() == limit) {
            callback.onRecordsCollected(new LinkedList<T>(records));
            records.clear();
        }
    }
}
