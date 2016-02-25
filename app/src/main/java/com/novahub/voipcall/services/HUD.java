package com.novahub.voipcall.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.novahub.voipcall.R;
import com.novahub.voipcall.activity.AcceptActivity;
import com.novahub.voipcall.activity.MakingCallConferenceActivity;

import java.lang.reflect.Method;

/**
 * Created by samnguyen on 25/02/2016.
 */
public class HUD extends Service {
    HUDView mView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();
        mView = new HUDView(this);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                0,
////              WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
////                      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                PixelFormat.TRANSLUCENT);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.RIGHT | Gravity.TOP;
//        params.setTitle("Load Average");
//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//        wm.addView(mView, params);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.activity_incoming_call, null);
        Button buttonAccept = (Button) myView.findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptCall(getApplicationContext());
            }
        });

        Button buttonReject = (Button) myView.findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectCall();
                wm.removeViewImmediate(myView);
                onDestroy();
            }
        });
        // Add layout to window manager
        wm.addView(myView, params);
    }

    private void acceptCall(Context context){
        Intent intent = new Intent(context, AcceptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    private void disconnectCall(){
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getBaseContext(),"onDestroy", Toast.LENGTH_LONG).show();
        if(mView != null)
        {
//            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
    }
}

class HUDView extends ViewGroup {
    private Paint mLoadPaint;

    public HUDView(Context context) {
        super(context);
        Toast.makeText(getContext(),"HUDView", Toast.LENGTH_LONG).show();

        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setTextSize(10);
        mLoadPaint.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("Hello World", 5, 15, mLoadPaint);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
        return true;
    }

}
