package es.kya.test;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Servicio";
    Intent i;

    private ServicioTimer servicioTimer;
    TextView contador;
    Button iniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        contador = (TextView) findViewById(R.id.mostrar);
        iniciar = (Button) findViewById(R.id.iniciar);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar.setEnabled(false);
                //inicia el servicio
                startService(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        i = new Intent(this, ServicioTimer.class);
        bindService(i, mConnection, 0);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //aqui obtienes los datos enviados por el servicio
            //obtienes el tiempo que lleva
            if (intent.getExtras() != null) {
                if (intent.hasExtra("Tiempo")) {
                    String tiempo = intent.getStringExtra("Tiempo");
                    contador.setText(tiempo);
                } if (intent.hasExtra("Fin")) {
                    //se recibe que se ha finalizado el contador
                    String tiempo = intent.getStringExtra("Fin");
                    contador.setText(tiempo);
                    iniciar.setEnabled(true);
                    //cierras el servicio ya que no es necesario mantenerlo, sera creado al pulsar el boton nuevamente
                    stopService(new Intent(MainActivity.this, ServicioTimer.class));
                }
            }
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            ServicioTimer.RunServiceBinder binder = (ServicioTimer.RunServiceBinder) service;
            servicioTimer = binder.getService();
            servicioTimer.background();
            if (servicioTimer.estaEjecutandose()) {
                iniciar.setEnabled(false);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(ServicioTimer.PAQETE));
        Log.i(TAG, "Broadcast registrado");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Broadcast desligado");
    }

    @Override
    public void onStop() {
        try {
            if (servicioTimer.estaEjecutandose()) {
                servicioTimer.foreground();
            }
            unregisterReceiver(br);
        } catch (Exception e) {}
        super.onStop();
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "Termina el servicio");
        super.onDestroy();
    }
}