package es.kya.test;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by kingcreek on 26/8/17.
 */

import android.annotation.SuppressLint;
import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;


public class ServicioTimer extends Service {

    private final IBinder servicioBinder = new RunServiceBinder();
    public class RunServiceBinder extends Binder {
        ServicioTimer getService() {
            return ServicioTimer.this;
        }
    }

    boolean estaEjecutandose = false;

    String FORMAT = "%02d:%02d:%02d";
    private static String TAG = "Servicio";
    public static final String PAQETE = "com.example.ash.carritosbeta1"; //ejemplo com.proyecto.MainActivity
    Intent bi = new Intent(PAQETE);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Comienza el timer...");
        estaEjecutandose = true;
        cdt = new CountDownTimer(9000, 1000) {
            public void onTick(long millisUntilFinished) {
                @SuppressLint("DefaultLocale") String tiempo = ""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                //con esto se envia el tiempo
                bi.putExtra("Tiempo", tiempo);
                sendBroadcast(bi);
            }
            public void onFinish() {
                //se envia el tiempo finalizado
                estaEjecutandose = false;
                bi.putExtra("Fin", "Tiempo terminado!");
                sendBroadcast(bi);
                stopSelf();
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return servicioBinder;
    }

    public boolean estaEjecutandose() {
        return estaEjecutandose;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Timer cancelado");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    public void background() {
        stopForeground(true);
    }
    public void foreground() {
        startForeground(1, createNotification());
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Cuenta atras")
                .setContentText("Pulsa para regresar a la cuenta")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }

}
