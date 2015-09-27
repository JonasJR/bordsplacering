package se.helsingkrona.snorsjoa.bordsplacering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    Button btnSearch;
    TextView tvNameAndPlace;
    EditText edtName;
    ImageView ivPicture;
    Hashtable table = new Hashtable<String, String>();
    String inputFile;
    BufferedReader br;
    String line = "";
    String cvsSplitBy = ";";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateList();
        initiateSystem();
        setListeners();
    }

    private void initiateList() {
        try {
            inputFile = getAssets()+"file.csv";
            br = new BufferedReader(new InputStreamReader(getAssets().open("file.csv")));
            while ((line = br.readLine()) != null) {
                String[] result = line.split(cvsSplitBy);
                table.put(result[0].toUpperCase(),result[1]);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResources();
            }
        });
    }

    private void setResources(){
        String str = search(edtName.getText().toString());
        tvNameAndPlace.setText(str);
        if(str.contains("A")){
            ivPicture.setImageResource(R.drawable.borda);
        }else if(str.contains("B")){
            ivPicture.setImageResource(R.drawable.bordb);
        }else if(str.contains("C")){
            ivPicture.setImageResource(R.drawable.bordc);
        }else if(str.contains("D")){
            ivPicture.setImageResource(R.drawable.bordd);
        }else if(str.contains("E")){
            ivPicture.setImageResource(R.drawable.borde);
        }else if(str.contains("F")){
            ivPicture.setImageResource(R.drawable.bordf);
        }else if(str.contains("G")){
            ivPicture.setImageResource(R.drawable.bordg);
        }else if(str.contains("H")){
            ivPicture.setImageResource(R.drawable.bordh);
        }else if(str.contains("I")){
            ivPicture.setImageResource(R.drawable.bordi);
        }else if(str.contains("J")){
            ivPicture.setImageResource(R.drawable.bordj);
        }else if(str.contains("K")){
            ivPicture.setImageResource(R.drawable.bordk);
        }else if(str.contains("L")){
            ivPicture.setImageResource(R.drawable.bordl);
        }else{
            ivPicture.setImageResource(R.drawable.bord);
        }
    }

    private void initiateSystem() {
        btnSearch = (Button)findViewById(R.id.btnSearch);
        tvNameAndPlace = (TextView)findViewById(R.id.tvNameAndPlace);
        edtName = (EditText)findViewById(R.id.edtName);
        ivPicture = (ImageView)findViewById(R.id.ivPicture);

        btnSearch.setText(getString(R.string.button));
        edtName.setHint(getString(R.string.edittext));
        ivPicture.setImageResource(R.drawable.bal);
    }

    private String search(String name){
        return (String)table.get(name.toUpperCase());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
