package com.example.webserver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity implements WebServer.WebserverListener {

    private WebServer server;
    private final String PIN_LED = "BCM18";
    public Gpio mLedGpio;
    public static final String TAG = "WEBSERVER";

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        server = new WebServer(8181, this, this);
        PeripheralManager service = PeripheralManager.getInstance();
        try{
            mLedGpio = service.openGpio(PIN_LED);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        }catch(IOException e){
            Log.e(TAG, "Error en el API PeripheralIO",e);
        }
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        server.stop();
        if(mLedGpio !=null){
            try{
                mLedGpio.close();
            }catch(IOException e){
                Log.e(TAG, "Error en el API Peripheral",e);
            }finally {
                mLedGpio =null;
            }
        }
    }


    @Override
    public Boolean getLedStatus() {
        try{
            return mLedGpio.getValue();
        }catch(IOException e){
            Log.e(TAG,"Error on PeripheralIO API", e);
            return false;
        }
    }

    @Override
    public void switchLEDon() {
        try{
            mLedGpio.setValue(true);
            Log.i(TAG,"LED switched ON");
        }catch(IOException e){
            Log.e(TAG,"Error on Peripheral API",e);
        }
    }

    @Override
    public void switchLEDoff() {
        try{
            mLedGpio.setValue(false);
            Log.i(TAG,"LED switched OFF");
        }catch(IOException e){
            Log.e(TAG,"Error on PeripheralIO API",e);
        }
    }
}
