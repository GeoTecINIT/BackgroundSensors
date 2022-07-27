# BackgroundSensors
[![](https://jitpack.io/v/GeotecINIT/BackgroundSensors.svg)](https://jitpack.io/#GeotecINIT/BackgroundSensors)

The _backgroundsensors_ library is an Android library that allows to collect data from the IMU
sensors (i.e., accelerometer and gyroscope) and the magnetometer (if they are present in the device).

The aim of this library is to ensure the data collection from the requested sensors even when the
application is in background and the screen's device is off (i.e., idle device). To do so, the data
collection is carried on a [foreground service](https://developer.android.com/guide/components/foreground-services)
and using a [wake lock](https://developer.android.com/training/scheduling/wakelock), 
as proposed by [González-Pérez et al.](https://doi.org/10.1016/j.pmcj.2022.101550) to ensure a systematic data collection.


## Installation
To install the library you have to add the [Jitpack](https://jitpack.io) repository to file where your project
describes the repositories:

<details>
  <summary>build.gradle (project)</summary>

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```
</details>

<details>
  <summary>settings.gradle</summary>

```groovy
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```
</details>

Then, just add the dependency:

```groovy
dependencies {
    implementation 'com.github.GeotecINIT:BackgroundSensors:1.0.0-alpha'
}
```

> **Note**: if you are considering to create a new library extending the features of 
> BackgroundSensors, use `api` instead of `implementation`. If you are extending the library
> directly in an application, `implementation` should work.

## Requirements
The library has the following requirements:

- A device running Android 7.0 (API level 24) or superior.
- _(Optional)_ For apps targeting an API level 31 or higher and willing to collect data from the sensors
at a sampling rate higher than 200Hz, the following permission must be added:
  
```xml
<uses-permission android:name="android.permission.HIGH_SAMPLING_RATE_SENSORS" />
```

## Usage

The usage of the library is pretty straightforward. The sensors are defined in the enum [`BaseSensor`](#basesensor), and
the [`SensorManager`](#sensormanager) can be used to know which one of them are available in the device. 

To manage the data collection, an instance of the [`ServiceManager`](#servicemanager) must be created injecting 
the `BaseSensorRecordingService` (i.e., the service for collecting data from the `BaseSensor`). Then, 
the instance can be used to start and stop the data collection.

To start the data collection, a [`CollectionConfiguration`](#collectionconfiguration) must be provided, indicating the type of sensor
to collect data from, the sensor delay (i.e., time between samples) and the batch size 
(i.e., how many samples to report at the same time). Also, a [`RecordCallback`](#recordcallback) must be provided to receive the collected data.

The next example illustrates the steps.

```java
public class Demo extends Activity {
    
    private SensorManager sensorManager;
    private ServiceManager serviceManager;
    
    protected void onCreate() {
        // ...
        
        sensorManager = new SensorManager(context);
        serviceManager = new ServiceManager(this, BaseSensorRecordingService.class);
        
        // ...
    }
    
    public void setupUI() {
        List<Sensor> availableSensors = sensorManager.availableSensors(BaseSensor.values());
        // ...
    }
    
    public void startCollection(Sensor sensor) {
        CollectionConfiguration config = new CollectionConfiguration(
                sensor,
                android.hardware.SensorManager.SENSOR_DELAY_GAME,   // Sensor delay
                50                                                  // Batch size
        );
        
        serviceManager.startCollection(config, records -> {
            // ...
        });
    }
}
```

> **Note**: a full example can be found in [DemoActivity](app/src/main/java/es/uji/geotec/backgroundsensorsdemo/DemoActivity.java)

As the library uses a foreground service for the data collection, a notification is shown while the service
is working. The notification has some default texts and icons, but you can override these settings.

To change the notification's texts add these strings to your `strings.xml` with the desired values:

```xml
<resources>
    <!-- ... -->
    <string name="sensor_recording_channel_description">Sensorization</string>             <!-- Channel name -->
    <string name="sensorization_notification_title">Sensorization</string>                 <!-- Notification's title -->
    <string name="sensorization_notification_text">Collecting data from sensors</string>   <!-- Notification's body text -->
    <!-- ... -->
</resources>
```

To change the notification's icon add a drawable named `ic_sensor_service.xml`

## API
### [`BaseSensor`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/sensor/BaseSensor.java)

| **Value**       | **Description**                      |
|-----------------|--------------------------------------|
| `ACCELEROMETER` | Represents the accelerometer sensor. |
| `GYROSCOPE`     | Represents the gyroscope sensor.     |
| `MAGNETOMETER`  | Represents the magnetometer sensor.  |

The `BaseSensor` enum implements the interface [`Sensor`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/sensor/Sensor.java),
so some methods use `Sensor` as parameters and return types.


### [`SensorManager`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/sensor/SensorManager.java)

| **Method**                           |  **Return type**  | **Description**                                                      |
|--------------------------------------|-------------------|----------------------------------------------------------------------|
| `availableSensors(Sensor[] sensors)` | `List<Sensor>`    | From the provided sensors, returns the ones available in the device. |
| `isSensorAvailable(Sensor sensor)`   | `boolean`         | Returns `true` if the specified sensor is available in the device .  |


### [`CollectionConfiguration`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/collection/CollectionConfiguration.java)

| **Field**     |  **Type** | **Description**                                                                                                                           |
|---------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `sensor`      | `Sensor`  | Sensor of the collection configuration.                                                                                                   |
| `sensorDelay` | `int`     | Sampling rate (i.e., time between samples). Use constants defined in the native android.hardware.SensorManager or a value in nanoseconds. |
| `batchSize`   | `int`     | Quantity of sensor samples to be reported each time.                                                                                      |

### [`Record`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/record/Record.java)
Base record of the library. Its purpose is to be extended by other classes to create specific type of records.

| **Field**     |  **Type** | **Description**                              |
|---------------|-----------|----------------------------------------------|
| `sensor`      | `Sensor`  | Sensor of the collection configuration.      |
| `timestamp`   | `long`    | Timestamp at which the record was collected. |

#### [`TriAxialRecord`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/record/TriAxialRecord.java)
Specific record for the samples obtained from triaxial sensors (i.e., accelerometer, gyroscope and magnetometer).

| **Field** | **Type** | **Description**                           |
|-----------|----------|-------------------------------------------|
| `x`       | `float`  | Value of the component _x_ of the sensor. |
| `y`       | `float`  | Value of the component _y_ of the sensor. |
| `z`       | `float`  | Value of the component _z_ of the sensor. |

### [`RecordCallback`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/record/callback/RecordCallback.java)

| **Method**                            | **Return type** | **Description**                                               |
|---------------------------------------|-----------------|---------------------------------------------------------------|
| `onRecordsCollected(List<T> sensors)` | `void`          | Receives a list of collected records (i.e., sensor samples) . |

`T` is a generic type, in this case, any type extending the class [`Record`](#record).


### [`ServiceManager`](backgroundsensors/src/main/java/es/uji/geotec/backgroundsensors/service/manager/ServiceManager.java)

| **Method**                                                                 | **Return type** | **Description**                                                       |
|----------------------------------------------------------------------------|-----------------|-----------------------------------------------------------------------|
| `startCollection(CollectionConfiguration config, RecordCallback callback)` | `void`          | Starts data collection for the sensor specified in the configuration. |
| `stopCollection(Sensor sensor)`                                            | `void`          | Stops data collection for the specified sensor.                       |

> **Note**: the collection on a specific sensor can only be started **once**. This means that
> if you want to change the `sensorDelay` or the `batchSize` for a sensor that is already being 
> collected, you must stop the collection first and then start it again with the new configuration. 

## Extending the functionality
The library can be extended to support other sensors, for example, the heart rate monitor of a 
WearOS smartwatch.

To extend the functionality, the developer must follow the next steps:
1. Create its own enum of sensors implementing the `Sensor` interface.
2. Create its own records extending the `Record` class.
3. Create a new collector manager extending the `CollectorManager` class.
4. Create a new service extending the `SensorRecordingService` service, and implement the method
to return the collector manager created at 3. Declare the new service into the `AndroidManifest.xml`.
5. Ready to go! Just inject the class reference of the new service to the `ServiceManager`. 

> **Note**: the developer is in charge of requesting the required permissions for the new sources,
> in case they are needed.


## License
See [LICENSE](LICENSE).


