/*
Author: Sandeep Singh Sandha
Email: sandha.iitr@gmail.com
*/


/*
 * Modified by: Miguel Matey Sanz
 * Email: matey@uji.es
 */


package es.uji.geotec.backgroundsensors.time.ntp;

import android.os.SystemClock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GoodClock {

    public static int DEFAULT_REQUESTS_PER_NTP_UPDATE = 10;
    public static long DEFAULT_UPDATE_INTERVAL = 15*60*1000; //Default update every 15  minutes


    SntpDsense client = null;
    String ntpHost = "time.google.com";
    int timeout = 3000;


    public boolean SntpSuceeded;
    Thread NTP_update;
    boolean NTP_thread_running=false;

    boolean use_drift_correction =false;

    //stores the drift in the ntp_clockoffset
    double drift =0.0;

    //these are affected when system time jumps
    double first_ntp_offset = 0;
    long first_ntp_monotonic_time=0;
    long first_ntp_sys_time=0;

    long curr_ntp_offset = 0;
    long curr_ntp_monotonic_time=0;
    long curr_ntp_sys_time=0;

    //below numbers are used in the drift computation
    double total_ntp_offset_run =0.0;
    double total_ntp_monotonic_time_run =0.0;
    double total_ntp_offset_change =0.0;
    double total_ntp_monotonic_time_passed=0.0;

    boolean is_first =true; //stores whether it is a first NTP update

    private long updateInterval;

    /*
    1) Initializing SntpDsense client
    2) Initialize SNTP (NTP) has not been done
     */
    public GoodClock() {
        this(DEFAULT_REQUESTS_PER_NTP_UPDATE, DEFAULT_UPDATE_INTERVAL);
    }//end GoodClock()

    public GoodClock(int requestsPerNTPUpdate, long updateInterval) {
        this.updateInterval = updateInterval;

        try{
            client = new SntpDsense(requestsPerNTPUpdate);
            SntpSuceeded=false;
            NTP_update=null;

            // period = 10000;//how often to do the NTP update
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //start the dsense library
    /*
    Create a thread to periodically update the SntpDsense client ntp update function
     */
    public void startSync()
    {
        try{
            //start thread only if not running
            if(NTP_update==null) {
                NTP_thread_running=true;
                NTP_update = new Thread(thread_periodic_update_NTP);
                NTP_update.start();

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }//end start

    public boolean singleSync() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureTask = executorService.submit(new SingleSyncFuture(this));

        try {
            return futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        return false;
    }

    private boolean futureSync() {
        SntpSuceeded = this.doSync();
        return SntpSuceeded;
    }

    public void stop()
    {
        NTP_thread_running=false;
        Thread moribund = NTP_update;
        NTP_update = null;
        moribund.interrupt();
    }//end stop

    //return currentTimeMillis based on the below logic:
    /*
    1) Stores the NTP offset of the system time and updates the offset periodically
     */
    public long currentTimeMillis()
    {

    /*
    We have to make sure client is not null
     */
        if(client==null)
            return -1;//client not initialized

        long now = -1;
        try{

            //client.get_ntp_update_sys_time(): last sys time NTP was updated.
            //SystemClock.elapsedRealtime(): monotonic system elaspsed time since boot.
            //client.get_ntp_update_monotonic_time(): monotonic system elasped time at instant of NTP offset calcuation.
            //client.getNtp_clockoffset(): offset of system time with NTP server at time of NTP update
            long elapsed_time_since_last_ntp = SystemClock.elapsedRealtime() - curr_ntp_monotonic_time;
            long drift_correction = (long)((drift)*(double)(elapsed_time_since_last_ntp));
            now = drift_correction+curr_ntp_offset+curr_ntp_sys_time+elapsed_time_since_last_ntp;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return now;
    }//end long currentTimeMillis


    /*
   Gives the time now in milliseconds based on corrections
     */
    public long Now()
    {

    /*
    We have to make sure client is not null
     */
        if(client==null)
            return -1;//client not initialized

        long now = -1;
        try{

            long elapsed_time_since_last_ntp = SystemClock.elapsedRealtime() - curr_ntp_monotonic_time;
            long drift_correction = (long)((drift)*(double)(elapsed_time_since_last_ntp));
            now = drift_correction+curr_ntp_offset+curr_ntp_sys_time+elapsed_time_since_last_ntp;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return now;
    }//end long Now

    public long getNtp_clockoffset()
    {
        try{
            return client.getNtp_clockoffset();
        }
        catch (Exception e)
        {
            return Integer.MAX_VALUE;
        }

    }

    public double getDrift()
    {
        return drift;

    }//end getDrift()

    Runnable thread_periodic_update_NTP = new Runnable() {
        @Override
        public void run() {
            Thread thisThread = Thread.currentThread();
            while(NTP_update == thisThread) {
                try {

                    System.out.println("GoodClock Thread is running");
                    SntpSuceeded = doSync();

                    Thread.sleep(updateInterval);

                } catch (InterruptedException interruptedException) {
                    System.out.println("GoodClock Thread interrupted");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!NTP_thread_running)
                {
                    break;
                }

            }//end  while (true)
        }//end run
    };

    private boolean doSync() {
        boolean succeeded = client.requestTime(ntpHost, timeout);

        if (succeeded) {
            //is this the first update
            if (is_first) {
                is_first = false;


                curr_ntp_offset = client.getNtp_clockoffset();
                curr_ntp_monotonic_time = client.get_ntp_update_monotonic_time();
                curr_ntp_sys_time = client.get_ntp_update_sys_time();

                //these are set during the first time
                first_ntp_monotonic_time = curr_ntp_monotonic_time;
                first_ntp_offset = curr_ntp_offset;
                first_ntp_sys_time = curr_ntp_sys_time;

            } else {


                curr_ntp_offset = client.getNtp_clockoffset();
                curr_ntp_monotonic_time = client.get_ntp_update_monotonic_time();
                curr_ntp_sys_time = client.get_ntp_update_sys_time();

                //if there was jump in the system time, then the previous
                //difference in monotonic time will not match with the difference in the system time

                long diff_monotonic = curr_ntp_monotonic_time - first_ntp_monotonic_time;
                long diff_system = curr_ntp_sys_time - first_ntp_sys_time;

                long jump_system_time = diff_monotonic - diff_system;

                //note this clock difference will be due to the jump in system time due to correction (NTP or Nitz at the system level)
                //we need to check there was no jump in the system time

                if (Math.abs(jump_system_time) < 10)//there is an insignificant jump of 10 ms or less.

                {

                    total_ntp_offset_run = (curr_ntp_offset - first_ntp_offset);
                    total_ntp_monotonic_time_run = (curr_ntp_monotonic_time - first_ntp_monotonic_time);

                    double curr_drift = 0.0;

                    if (total_ntp_monotonic_time_run > (10.0 * 60.0 * 1000.0))//if current run > 10 minutes
                    {

                        curr_drift = (total_ntp_offset_change + total_ntp_offset_run) / (total_ntp_monotonic_time_passed + total_ntp_monotonic_time_run);

                        //System.out.println("Sandeep: curr run"+total_ntp_offset_run+":"+total_ntp_monotonic_time_run);
                    } else//we only use the value from the previous runs, in case system time is changed
                    {
                        curr_drift = (total_ntp_offset_change) / (total_ntp_monotonic_time_passed);
                    }


                    if (curr_drift * (1000.0 * 60.0 * 60.0 * 24.0) > 40) {
                        drift = curr_drift;
                    } else
                        drift = 0.0;

                }

                //we are starting a new offset calcuation after the jump
                else {

                    total_ntp_offset_change = total_ntp_offset_change + total_ntp_offset_run;
                    total_ntp_monotonic_time_passed = total_ntp_monotonic_time_passed + total_ntp_monotonic_time_run;


                    is_first = true;

                    curr_ntp_offset = client.getNtp_clockoffset();
                    curr_ntp_monotonic_time = client.get_ntp_update_monotonic_time();
                    curr_ntp_sys_time = client.get_ntp_update_sys_time();

                    //these are set during the first time
                    first_ntp_monotonic_time = curr_ntp_monotonic_time;
                    first_ntp_offset = curr_ntp_offset;
                    first_ntp_sys_time = curr_ntp_sys_time;

                }


            }//end else

        }

        return succeeded;
    }

    private class SingleSyncFuture implements Callable<Boolean> {

        private GoodClock goodClock;

        public SingleSyncFuture(GoodClock goodClock) {
            this.goodClock = goodClock;
        }

        @Override
        public Boolean call() throws Exception {
            return this.goodClock.futureSync();
        }
    }
}//end GoodClock