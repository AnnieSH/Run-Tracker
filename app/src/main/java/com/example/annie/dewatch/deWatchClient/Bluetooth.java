package com.example.annie.dewatch.deWatchClient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Annie on 2018-03-20.
 */

public class Bluetooth {

    // Bluetooth variables
    public BluetoothAdapter btAdapter;
    public BluetoothSocket btSocket = null;
    public InputStream btInStream = null;
    public OutputStream btOutStream = null;
    public boolean btConnected = false;
    public static final String btAddress = "00:06:66:7D:83:B4";

    public Bluetooth() {

    }

    public void closeConnection() {
        try {
            btInStream.close();
            btInStream = null;
        } catch(IOException e) {}

        try {
            btOutStream.close();
            btOutStream = null;
        } catch(IOException e) {}

        try {
            btSocket.close();
            btSocket = null;
        } catch(IOException e) {}

        btConnected = false;
    }



    public void getBtDevice() {
        BluetoothDevice btDevice = btAdapter.getRemoteDevice(btAddress);

        if(btConnected)
            closeConnection();

        CreateSerialBluetoothDeviceSocket(btDevice);
        if(btSocket == null) {
            return;
        }
        ConnectToSerialBluetoothDevice();
    }

    private void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        btSocket = null;

        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString ("00001101-0000-1000-8000-00805F9B34FB");

        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            btSocket = device.createRfcommSocketToServiceRecord (MY_UUID);
        }
        catch (IOException e) {
            btSocket = null;
        }
    }

    private void ConnectToSerialBluetoothDevice() {
        try {
            btSocket.connect();
        } catch (IOException e) {
            Log.e("Connect to bt device", e.getMessage());
            return;
        }

        btConnected = GetInputOutputsStreamsForSocket();
    }

    private boolean GetInputOutputsStreamsForSocket() {
        try {
            btInStream = btSocket.getInputStream();
            btOutStream = btSocket.getOutputStream();
        } catch(IOException e) {
            Log.e("Get IO for Socket", e.getMessage());
            return false;
        }

        return true;
    }
}
