package com.camilomontoya.cortana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 *
 * MainActivity es el archivo java de la actividad principal donde se desarrolla la app,
 * en la cual se requiere la lista de dispositivos bluetooth para conectar y un
 * boton que refresque las conexiones
 *
 * @autor   Camilo Jose Montoya
 * @version 0.1
 */
public class MainActivity extends AppCompatActivity {

    private Button btnPaired;
    private ListView deviceList;

    private BluetoothAdapter myBlue = null;
    private Set pairedDevices;

    /**
     *
     * Conecta al boton y la lista de dispositivos Bluetooth emparejados
     * con el Java, para utilizar el click de el boton como busqueda de
     * dispositivos emparejados y al seleccionar un elemento, ligar este
     * como elemento para enviar informacion
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPaired = (Button) findViewById(R.id.button);
        deviceList = (ListView) findViewById(R.id.listView);

        myBlue = BluetoothAdapter.getDefaultAdapter();

        if(myBlue == null){
            aviso("Bluetooth no esta activado");
            finish();
        } else {
            if(myBlue.isEnabled()){
                Intent turnBtn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBtn,1);
            }
        }


        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }

    /**
     * Metodo para buscar los dispositivos emparejados y agregarlos a un ArrayList
     * que luego se adapta al ListView
     */
    private void pairedDevicesList(){
        pairedDevices = myBlue.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size()>0){
            for (Object o: pairedDevices) {
                BluetoothDevice bt = (BluetoothDevice) o;
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            aviso("No hay dispositivos disponibles");
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);
    }

    /**
     * Se crea un objeto AdapterView.OnItemClickListener para ser utilizado
     * en el setOnItemClickListener de cada elemento del ListView
     */
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);
            Intent i = new Intent(MainActivity.this,CortanaControl.class);
            i.putExtra("EXTRA_ADDRESS",address);
            startActivity(i);
        }
    };

    /**
     * Metodo para acortar la generacion de Toast
     *
     * @param text String enviado para crear el mensaje
     */
    private void aviso(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
