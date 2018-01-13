package pt.indiedev.bluetoothcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

public class SelectDevice extends AppCompatActivity {
    ListView listView;
    private ArrayAdapter<String> mArrayAdapter=null;
    private BluetoothAdapter mBluetoothAdapter=null;
    private Set<BluetoothDevice> pairedDevice=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        //get the bluetooth adapter from the intenthelper class
        mBluetoothAdapter=(BluetoothAdapter)IntentHelper.getObjectForKey("btAdapter");

        mArrayAdapter=new ArrayAdapter<String>(this,R.layout.simplerow,0);
        listView=findViewById(R.id.lvDevices);

        searchDevicesPaired();
    }

    private void searchDevicesPaired() {
        pairedDevice=mBluetoothAdapter.getBondedDevices();
        if(pairedDevice.size()>0){
            for(BluetoothDevice device : pairedDevice){
                mArrayAdapter.add(device.getName() + "\n"+ device.getAddress());
            }
        }
        listView.setAdapter(mArrayAdapter);
        //makes the listview responde to the click/tap
        listView.setClickable(true);

        //callback
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj=listView.getItemAtPosition(i);
                String address=obj.toString();
                terminateActivity(address);
            }
        });
    }

    private void terminateActivity(String address) {
        Intent i=new Intent();
        i.putExtra("RESULT_STRING",address);
        setResult(RESULT_OK,i);
        finish();
    }

}
