package pt.indiedev.bluetoothcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter=null;
    private static final int REMOTE_DEVICE_SELECTED=1;
    private static final int REQUEST_ENABLE_BT=3;   //this value will allow the app to acknowledge that the bt was enabled
    private BluetoothDevice remoteBluetoothDevice=null;
    private ConnectThread mConnectThread=null;

    private SeekBar skVelocity, skTurn;
    private int direction=0;
    private int velocity=0;
    byte toSend=0x0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check this device for bluetooth support
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            Utils.showMessage(getApplicationContext(),"Bluetooth not available");
        }

        //seekbars controls
        skVelocity=findViewById(R.id.seekBarVelocity);
        skTurn=findViewById(R.id.seekBarTurn);
        //set the bars to stopped and foward
        skVelocity.setProgress(50);
        skTurn.setProgress(50);
        //callbacks
        skVelocity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                velocity=i-50;
                sendData();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        skTurn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                direction=i-50;
                sendData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void sendData() {
        //start by setting the data to send to be stopped facing forward
        toSend=1;
        if(velocity>0){
            if(direction>10)
                toSend=6;
            if(direction<-10)
                toSend=5;
        }else{
            toSend=2;   //going back
            if(direction>10)
                toSend=8;
            if(direction<-10)
                toSend=7;
        }
        if(Math.abs(velocity)<10){
            if(direction>10)
                toSend=4;
            if(direction<-10)
                toSend=3;
        }
        toSend=(byte)(toSend<<4);
        int speed=(int)Utils.scale(Math.abs(velocity),0,50,0,15);
        toSend += speed;
        if(mConnectThread!=null)
            mConnectThread.sendData(toSend);
    }

    //callback for activities that return a result
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode==RESULT_OK){
                Utils.showMessage(getApplicationContext(),"Bluetooth enabled");
            }
        }
        //REMOTE_DEVICE_SELECTED
        if(requestCode==REMOTE_DEVICE_SELECTED){
            if(resultCode==RESULT_OK){
                String selectedDevice=data.getStringExtra("RESULT_STRING");
                Utils.showMessage(getApplicationContext(),"Selected device: "+selectedDevice);
                String[] deviceAddress=selectedDevice.split("\n");
                remoteBluetoothDevice=mBluetoothAdapter.getRemoteDevice(deviceAddress[1]);
            }
        }
    }
    @Override
    protected void onDestroy(){
        byte x=0x00;    //send to the remote device a stop command
        if(mConnectThread!=null){
            mConnectThread.sendData(x);
            mConnectThread.close();
        }
        super.onDestroy();
    }
    //on click events for the buttons
    //on button
    public void onLigarClick(View v){
        //check if bluetooth is not enabled
        if(!mBluetoothAdapter.isEnabled()){
            //request bluetooth to be enabled
            Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
    }
    //search paired devices
    public void onProcurarClick(View v){
        //we need a new activity that return with the selected remote bluetooth device
        //this activity will need to have access to the mBluetoothAdapter
        Intent i=new Intent(this,SelectDevice.class);
        IntentHelper.addObjectForKey(mBluetoothAdapter,"btAdapter");
        startActivityForResult(i,REMOTE_DEVICE_SELECTED);
    }
    //connected to the selected device
    public void onAbrirClick(View v){
        //use the class ConnectThread to start a connection with the selected device
        if(remoteBluetoothDevice==null){
            Utils.showMessage(getApplicationContext(),"Please select a device first");
            return;
        }
        mConnectThread=new ConnectThread(remoteBluetoothDevice);
    }
    //click on exit button
    public void onSairClick(View v){
        byte x=0x00;    //send to the remote device a stop command
        if(mConnectThread!=null){
            mConnectThread.sendData(x);
            mConnectThread.close();
        }
        finish();
    }
    //click on stop button
    public void onPararClick(View v){
        byte x=0x00;    //send to the remote device a stop command
        if(mConnectThread!=null){
            mConnectThread.sendData(x);
        }
    }
}
