package br.com.bsilva.barcodereader;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.Date;
import java.util.List;

import br.com.bsilva.barcodereader.entity.Codes;
import br.com.bsilva.barcodereader.entity.DaoMaster;
import br.com.bsilva.barcodereader.entity.DaoSession;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "codes";

    private DaoMaster.DevOpenHelper helper;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;

    private List<Codes> codesList;
    private ArrayAdapter<Codes> adapter;

    Button scanbtn;
    ListView results;

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanbtn = (Button) findViewById(R.id.scanbtn);
        results = (ListView) findViewById(R.id.results);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST);
        }

        //Setando listeners
        setListener();

        initConexaoDB();

        loadListView();
    }


    //metodo responsável por setar os listeners
    private void setListener(){

        //Listener do button SCAN
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cria uma new intent para a activity de Scan
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        //Criando a Listener da ListView que controla o ContextMenu
        results.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE,1,Menu.NONE,"Copiar");
                menu.add(Menu.NONE,2,Menu.NONE,"Deletar");
            }
        });

    }

    private void initConexaoDB(){

        // do this once, for example in your Application class
        helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
    }

    public DaoSession getDaoSession() {
        return daoMaster.newSession();
    }

    private void loadListView(){

        daoSession = getDaoSession();

        codesList = daoSession.getCodesDao().loadAll();

        adapter = new ArrayAdapter<Codes>(this, android.R.layout.simple_list_item_1, codesList);

        results.setAdapter(adapter);

    }

    private void saveCode(Barcode barcode){

        daoSession = getDaoSession();

        Codes code = new Codes(barcode.displayValue,new Date());

        daoSession.getCodesDao().insert(code);

    }

    private void copyCode(int position){
        Adapter adapter = results.getAdapter();

        Codes code = (Codes) adapter.getItem(position);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto Copiado", code.getCode());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(MainActivity.this,"Texto Copiado",Toast.LENGTH_LONG).show();

    }

    private void deletar(int position) {

        daoSession = getDaoSession();

        daoSession.getCodesDao().delete(codesList.get(position));

        codesList.remove(position);
        adapter.notifyDataSetChanged();

        Toast.makeText(MainActivity.this,"Code Deletado!",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == resultCode){

            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");

                saveCode(barcode);

                loadListView();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (item.getItemId()) {
            case 1:
                copyCode(position);
                break;
            case 2:
                deletar(position);
                break;
        }
        return super.onContextItemSelected(item);
    }

}
