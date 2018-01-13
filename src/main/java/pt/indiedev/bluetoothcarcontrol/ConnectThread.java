package pt.indiedev.bluetoothcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by pjcfe on 13/01/2018.
 * This class will manage the connection with the remote device
 * It will start and stop the connection and will send data to the remote device
 */

public class ConnectThread extends Thread {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    //the remote device as a serial bluetooth board so the UUID is this
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int state;
    private OutputStream mOutStream=null;

    //constructor
    public ConnectThread(BluetoothDevice device){
        BluetoothSocket tmp=null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state=-1;   //not connected
        try{
            tmp=createBluetoothSocket(device);
            mSocket=tmp;
            state=0;    //trying to connect
            mSocket.connect();
            state=1; //connected
        }catch(IOException e){
            state=-1;   //not connected
            //we may create a fallback function
            Log.e("BT connect thread","Connection failed");
            e.printStackTrace();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    //function to return the state
    public int getTheState(){
        return state;
    }
    //function to close the connection
    public void close(){
        try{
            mSocket.close();
            state=-1;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //function to send data
    public void sendData(byte x){
        if (mOutStream == null) {
            try{
                mOutStream=mSocket.getOutputStream();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try{
            mOutStream.write(x);
        }catch (Exception err){
            err.printStackTrace();
        }
    }
}
