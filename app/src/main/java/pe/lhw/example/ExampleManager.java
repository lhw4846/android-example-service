package pe.lhw.example;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pe.lhw.example.service.ExampleData;
import pe.lhw.example.service.ExampleService;
import pe.lhw.example.service.StartRMData;

/**
 * Created by lhw48 on 2016-09-09.
 */
public class ExampleManager {
    private static final String TAG = "ExampleManager";

    private static Context context;
    private static ExampleManager client = null;    // singleton
    private Map<ExampleConsumer, Boolean> consumers = new HashMap<ExampleConsumer, Boolean>();
    private Messenger serviceMessenger = null;
    protected ExampleNotifier exampleNotifier = null;

    public static ExampleManager getInstanceForApplication(Context context) {
        if (!isInstantiated()) {
            Log.d(TAG, "ExampleManager instance creation");
            client = new ExampleManager(context);
        }
        return client;
    }

    private ExampleManager(Context context) {
        this.context = context;
    }

    /**
     * Binds an Android <code>Activity</code> or <code>Service</code> to the <code>ExampleService</code>.
     *
     * @param consumer
     *            the <code>Activity</code> or <code>Service</code> that needs to use the service.
     */
    public void bind(ExampleConsumer consumer) {
        if (consumers.keySet().contains(consumer)) {
            Log.i(TAG, "This consumer is already bound");
        } else {
            Log.i(TAG, "This consumer is not bound.  binding: " + consumer);
            consumers.put(consumer, false);
            Intent intent = new Intent(consumer.getApplicationContext(), ExampleService.class);
            consumer.bindService(intent, exampleServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "consumer count is now:" + consumers.size());
        }
    }

    /**
     * Unbinds an Android <code>Activity</code> or <code>Service</code> to the <code>ExampleService</code>. This should
     * typically be called in the onDestroy() method.
     *
     * @param consumer
     *            the <code>Activity</code> or <code>Service</code> that no longer needs to use the service.
     */
    public void unBind(ExampleConsumer consumer) {
        if (consumers.keySet().contains(consumer)) {
            Log.i(TAG, "Unbinding");
            consumer.unbindService(exampleServiceConnection);
            consumers.remove(consumer);
        } else {
            Log.i(TAG, "This consumer is not bound to: " + consumer);
            Log.i(TAG, "Bound consumers: ");
            for (int i = 0; i < consumers.size(); i++) {
                Log.i(TAG, " " + consumers.get(i));
            }
        }
    }

    public void startExampleUpdates(int uniqueId) throws RemoteException {
        Message msg = Message.obtain(null, ExampleService.MSG_START_EXAMPLE, 0, 0);
        StartRMData obj = new StartRMData(uniqueId);
        msg.obj = obj;
        msg.replyTo = exampleCallback;
        serviceMessenger.send(msg);
    }

    public void stopExampleUpdates(int uniqueId) throws RemoteException {
        Message msg = Message.obtain(null, ExampleService.MSG_STOP_EXAMPLE, 0, 0);
        StartRMData obj = new StartRMData(uniqueId);
        msg.obj = obj;
        serviceMessenger.send(msg);
    }

    private ServiceConnection exampleServiceConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "we have a connection to the service now");
            serviceMessenger = new Messenger(service);
            Iterator<ExampleConsumer> consumerIterator = consumers.keySet().iterator();
            while (consumerIterator.hasNext()) {
                ExampleConsumer consumer = consumerIterator.next();
                Boolean alreadyConnected = consumers.get(consumer);
                if (!alreadyConnected) {
                    consumer.onExampleServiceConnect();  // notify connection to consumer
                    consumers.put(consumer, true);
                }
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    public static final int MSG_CHANGED_EXAMPLE = 0;

    // example callback
    static class ExampleHandler extends Handler {
        private final WeakReference<ExampleManager> exampleManager;

        ExampleHandler(ExampleManager manager) {
            exampleManager = new WeakReference<ExampleManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGED_EXAMPLE:
                    ExampleData data = (ExampleData) msg.obj;
                    if (data == null) {
                        Log.d(TAG, "null example received");
                    } else {
                        ExampleManager manager = exampleManager.get();
                        if(manager.exampleNotifier != null) {
                            Log.d(TAG, "Calling bcg notifier on :" + manager.exampleNotifier);
                            manager.exampleNotifier.onExampleChanged(data);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger exampleCallback = new Messenger(new ExampleHandler(this));

    public ExampleNotifier getExampleNotifier() {
        return exampleNotifier;
    }

    public void setExampleNotifier(ExampleNotifier notifier) {
        exampleNotifier = notifier;
    }

    /**
     * Determines if the singleton has been constructed already. Useful for not overriding settings set declaratively in
     * XML
     *
     * @return true, if the class has been constructed
     */
    public static boolean isInstantiated() {
        return (client != null);
    }
}
