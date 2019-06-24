package com.example.webserver;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
    Context ctx;
    private static final String TAG= "webserver";


    public interface  WebserverListener{
        Boolean getLedStatus();
        void switchLEDon();
        void switchLEDoff();
    }

    private WebserverListener listener;

    public WebServer(int port, Context ctx, WebserverListener listener){
        super(port);
        this.ctx=ctx;
        this.listener=listener;
        try{
            start();
            Log.i(TAG, "webserver iniciado");
        }catch(IOException ioe){
            Log.e(TAG, "No ha sido posible iniciar el webserver", ioe);
        }
    }

    private StringBuffer readFile(){
        BufferedReader reader= null;
        StringBuffer buffer = new StringBuffer();
        try{
            reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open("home.html"), "UTF-8"));
            String mLine;
            while((mLine=reader.readLine())!=null){
                buffer.append(mLine);
                buffer.append("\n");
            }
        }catch(IOException ioe){
            Log.e(TAG, "Error leyendo la pagina home",ioe);
        }finally {
            if(reader !=null){
                try{
                    reader.close();
                }catch(IOException e){
                    Log.e(TAG,"Error cerrando el reader", e);
                }finally {
                    reader=null;
                }
            }
        }
        return buffer;
    }

    @Override
    public Response serve(IHTTPSession session){
        Map<String, List<String>> params = session.getParameters();
        if(params.get("on") !=null){
            listener.switchLEDon();
        }else if(params.get("off")!=null){
            listener.switchLEDoff();
        }
        String preweb = readFile().toString();
        String postweb;
        if(listener.getLedStatus()){
            postweb = preweb.replaceAll("#keytext", "ENCENDIDO");
            postweb = postweb.replaceAll("#keycolor", "MediumSeaGreen");
            postweb = postweb.replaceAll("#colorA", "#F2994A");
            postweb = postweb.replaceAll("#colorB", "#F2994C");
        }else{
            postweb = preweb.replaceAll("#keytext", "APAGADO");
            postweb = postweb.replaceAll("#keycolor", "Tomato");
            postweb = postweb.replaceAll("#colorA", "#3e5151");
            postweb = postweb.replaceAll("#colorB", "#decba4");
        }
        return newFixedLengthResponse(postweb);
    }

}
