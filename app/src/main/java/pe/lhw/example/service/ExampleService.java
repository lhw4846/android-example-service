package pe.lhw.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pe.lhw.example.Example;
import pe.lhw.example.ExampleManager;

/**
 * Created by lhw on 2016-09-09.
 */
public class ExampleService extends Service {
    private static final String TAG  = "ExampleService";
    private Map<Integer, Callback> exampleCallback = new HashMap<Integer, Callback>();
    private int bindCount = 0;

    /** Command to the service to display a message */
    public static final int MSG_START_EXAMPLE = 0;
    public static final int MSG_STOP_EXAMPLE = 1;

    private Timer timer;  // example method
    private int icount = 0;
    private float fcount = 0.f;
    private double dcount = 0.d;

    class IncomingHandler extends Handler {
        private final WeakReference<ExampleService> mService;

        IncomingHandler(ExampleService service) {
            mService = new WeakReference<ExampleService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            ExampleService service = mService.get();
            StartRMData startRMData = (StartRMData) msg.obj;

            if(service != null) {
                switch (msg.what) {
                    case MSG_START_EXAMPLE:
                        Log.d(TAG, "start bcg received");
                        service.startExampleUpdates(startRMData.getUniqueId(), new pe.lhw.example.service.Callback(msg.replyTo));
                        break;
                    case MSG_STOP_EXAMPLE:
                        Log.d(TAG, "stop bcg received");
                        service.stopExampleUpdates(startRMData.getUniqueId());
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "binding");
        bindCount++;
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind (Intent intent) {
        Log.i(TAG, "unbind called");
        bindCount--;
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate of ExampleService called");
        timer = new Timer();  // initialization
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy of ExampleService called");
        super.onDestroy();
    }

    /** methods for clients */
    public void startExampleUpdates(Integer uniqueId, Callback callback) {
        Log.d(TAG, "startBcg called");
        if(exampleCallback.containsKey(uniqueId)) {
            Log.d(TAG, "Already logging -- will replace existing callback");
            exampleCallback.remove(uniqueId);
        }
        exampleCallback.put(uniqueId, callback);
        // start updates
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // callback
                Iterator<Integer> callbackIterator = exampleCallback.keySet().iterator();
                Example example = Example.fromReceivedData(icount++, fcount++, dcount++);
                while(callbackIterator.hasNext()) {
                    exampleCallback.get(callbackIterator.next()).call(ExampleManager.MSG_CHANGED_EXAMPLE, new ExampleData(example));
                }
            }
        }, 5000, 5000);  // interval 5 seconds
    }

    public void stopExampleUpdates(Integer uniqueId) {
        Log.d(TAG, "stopBcg called");
        exampleCallback.remove(uniqueId);
        Log.d(TAG, "Currently logging " + exampleCallback.size());
        if (exampleCallback.size() == 0) {
            // stop updates\
            timer.cancel();
        }
    }
}
