package pt.indiedev.bluetoothcarcontrol;

import java.util.Hashtable;

/**
 * Created by pjcfe on 13/01/2018.
 * This Class implements a mechanism that will allow an object to be accessible from another activity
 * This codes was found in this stackoverflow article
 * https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
 */

public class IntentHelper {
    private static IntentHelper _instance;  //singleton
    private Hashtable<String, Object> _hash;    //dictionary for the objects

    private IntentHelper() {
        _hash = new Hashtable<String, Object>();
    }

    private static IntentHelper getInstance() {
        if(_instance==null) {
            _instance = new IntentHelper();
        }
        return _instance;
    }

    public static void addObjectForKey(Object object, String key) {
        getInstance()._hash.put(key, object);
    }

    public static Object getObjectForKey(String key) {
        IntentHelper helper = getInstance();
        Object data = helper._hash.get(key);
        helper._hash.remove(key);
        helper = null;
        return data;
    }
}
