package com.jacobra.pongapp.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class CreateGame extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void create_game(View view) {
        Button but = (Button) findViewById(R.id.create_game);
        but.setEnabled(false);

        Map<Object, Object> data = new HashMap<Object, Object>();
        TextView p1tv = (TextView) findViewById(R.id.player1);
        String p1 = p1tv.getText().toString();
        TextView p2tv = (TextView) findViewById(R.id.player2);
        String p2 = p2tv.getText().toString();
        String url = String.format("%s/create/%s/%s", getString(R.string.server_url), p1, p2);
        data.put("method", "POST");
        data.put("url", url);
        data.put("callback", new CreateGameCallBack());
        new RestCaller().execute(data);


    }

    class CreateGameCallBack implements Callback{
        public void invoke(Map<Object, Object> data) {
            Button but = (Button) findViewById(R.id.create_game);
            but.setEnabled(true);
            if ((Integer) data.get("statusCode") == 200) {
                Intent intent = new Intent(getApplicationContext(), ScoreScreen.class);
                intent.putExtra("createData", (String) data.get("response"));
                startActivity(intent);
            }
        }
    }
}
