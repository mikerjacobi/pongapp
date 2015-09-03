package com.jacobra.pongapp.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScoreScreen extends AppCompatActivity {
    private String id;
    private String p1;
    private String p2;
    private String startTime;
    private boolean isPregame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen);

        String create_game_response = (String)getIntent().getSerializableExtra("createData");
        JSONObject game;
        try {
            game = new JSONObject(create_game_response);
            TextView title = (TextView) findViewById(R.id.game_title);
            title.setText("Choose Starter");

            this.startTime = (String)game.get("time");
            this.id = (String)game.get("id");
            this.p1 = (String)game.get("p1");
            this.p2 = (String)game.get("p2");
        } catch (JSONException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        LongClickButton player1 = (LongClickButton) findViewById(R.id.player1);
        player1.setText(this.p1);
        LongClickButton player2 = (LongClickButton) findViewById(R.id.player2);
        player2.setText(this.p2);

        TextView p1ScoreTV = (TextView) findViewById(R.id.p1Score);
        TextView p2ScoreTV = (TextView) findViewById(R.id.p2Score);
        p1ScoreTV.setText(Integer.toString(0));
        p2ScoreTV.setText(Integer.toString(0));

        TextView stdout = (TextView) findViewById(R.id.stdout);
        Button rematch = (Button) findViewById(R.id.rematch);
        stdout.setVisibility(View.INVISIBLE);
        rematch.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_screen, menu);
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

    public void set_starter(View view){
        Map<Object,Object> data = new HashMap<Object,Object>();
        data.put("method", "POST");
        data.put("callback", new StarterCallBack());
        String player;
        switch (view.getId()) {
            case (R.id.player1):
                player = this.p1;
                break;
            case (R.id.player2):
                player = this.p2;
                break;
            default:
                player = "error";
                break;
        }
        String url = String.format("%s/starter/%s/%s",getString(R.string.server_url), this.id, player);
        data.put("url", url);
        new RestCaller().execute(data);
    }

    public void increment_score(View view) {
        if (isPregame){
            this.set_starter(view);
            this.isPregame = false;
            return;
        }

        LongClickButton scorer_button = (LongClickButton) findViewById(view.getId());
        scorer_button.setEnabled(false);
        Map<Object,Object> data = new HashMap<Object,Object>();
        data.put("method", "POST");
        data.put("callback", new ScoreCallBack());
        String player;
        switch (view.getId()) {
            case (R.id.player1):
                player = this.p1;
                break;
            case (R.id.player2):
                player = this.p2;
                break;
            default:
                player = "error";
                break;
        }
        String url = String.format("%s/score/%s/%s",getString(R.string.server_url), this.id, player);
        data.put("url", url);
        data.put("button", scorer_button);
        new RestCaller().execute(data);
    }

    public void decrement_score(View view) {
        LongClickButton scorer_button = (LongClickButton) findViewById(view.getId());
        scorer_button.setEnabled(false);
        Map<Object,Object> data = new HashMap<Object,Object>();
        data.put("method", "POST");
        data.put("callback", new ScoreCallBack());
        String player;
        switch (view.getId()) {
            case (R.id.player1):
                player = this.p1;
                break;
            case (R.id.player2):
                player = this.p2;
                break;
            default:
                player = "error";
                break;
        }
        String url = String.format("%s/unscore/%s/%s",getString(R.string.server_url), this.id, player);
        data.put("url", url);
        data.put("button", scorer_button);
        new RestCaller().execute(data);
    }

    public void rematch(View view) {
        Button but = (Button) findViewById(R.id.rematch);
        but.setEnabled(false);

        Map<Object, Object> data = new HashMap<Object, Object>();
        String url = String.format("%s/create/%s/%s", getString(R.string.server_url), this.p1, this.p2);
        data.put("method", "POST");
        data.put("url", url);
        data.put("callback", new CreateGameCallBack());

        new RestCaller().execute(data);


    }

    class CreateGameCallBack implements Callback{
        public void invoke(Map<Object, Object> data) {
            Button but = (Button) findViewById(R.id.rematch);
            but.setEnabled(true);
            if ((Integer) data.get("statusCode") == 200) {
                Intent intent = new Intent(getApplicationContext(), ScoreScreen.class);
                intent.putExtra("createData", (String) data.get("response"));
                startActivity(intent);
                finish();
            }
        }
    }


    class ScoreCallBack implements Callback{
        @Override
        public void invoke(Map<Object, Object> data) {
            JSONObject game;
            LongClickButton but = (LongClickButton) data.get("button");

            TextView stdout = (TextView) findViewById(R.id.stdout);
            if ((Integer) data.get("statusCode") == 200) {
                try {
                    game = new JSONObject((String) data.get("response"));
                    int p1Score = (Integer) game.get(p1);
                    int p2Score = (Integer) game.get(p2);
                    TextView p1ScoreTV = (TextView) findViewById(R.id.p1Score);
                    TextView p2ScoreTV = (TextView) findViewById(R.id.p2Score);
                    p1ScoreTV.setText(Integer.toString(p1Score));
                    p2ScoreTV.setText(Integer.toString(p2Score));
                    //String[] history = (String[]) data.get("history");
                    //stdout.setText(Arrays.toString(history));
                    if ((Boolean) game.get("game_over")){
                        stdout.setText(game.get("winner") + " wins.");
                        Button rematch = (Button) findViewById(R.id.rematch);
                        stdout.setVisibility(View.VISIBLE);
                        rematch.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                stdout.setVisibility(View.VISIBLE);
                stdout.setText("ERROR");
            }
            but.setEnabled(true);
        }
    }

    class StarterCallBack implements Callback{
        @Override
        public void invoke(Map<Object, Object> data) {
            JSONObject resp;
            TextView stdout = (TextView) findViewById(R.id.stdout);
            TextView title = (TextView) findViewById(R.id.game_title);

            if ((Integer) data.get("statusCode") == 200) {
                try {
                    resp = new JSONObject((String) data.get("response"));

                    if ((Boolean) resp.get("success")){
                        title.setText(startTime);
                    }
                    else{
                        title.setText("ERROR");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                stdout.setVisibility(View.VISIBLE);
                stdout.setText("ERROR");
            }
        }
    }

}
