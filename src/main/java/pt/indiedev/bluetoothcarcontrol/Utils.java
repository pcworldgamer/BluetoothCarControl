package pt.indiedev.bluetoothcarcontrol;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by pjcfe on 13/01/2018.
 * This Class contains some static helper functions
 */

public class Utils {
    //This function show a message
    public static void showMessage(Context context,CharSequence text){

        int duration= Toast.LENGTH_SHORT;
        Toast toast=Toast.makeText(context, text, duration);
        toast.show();
    }
    //this function helps scale a value
    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
}
