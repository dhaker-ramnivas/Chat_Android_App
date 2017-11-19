package com.example.ramnivas.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    EditText enterMsg;
    Button sendButton;

    ArrayList<String> list = new ArrayList<String>();

    ArrayAdapter<String> adapter;
    String  name;

    private Socket socket;

    {
        try {
            socket = IO.socket("http://192.168.43.24:1234");

        } catch (URISyntaxException e) {
            Log.v("TAG", "error connecting to socket");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Name For Chatting");
        alertDialog.setMessage("Enter Name");
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       name = input.getText().toString();

                        if(name.length()==0)
                        {
                            name="Anonymous";
                        }
                        socket.emit("name",name);
                    }
                });

        alertDialog.show();
        socket.connect();


        init();

        socket.on("chat message", data);
        listView.setAdapter(adapter);


    }

    private void init() {
        listView = (ListView) findViewById(R.id.listview);
        sendButton = (Button) findViewById(R.id.send);
        enterMsg = (EditText) findViewById(R.id.msg);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);


    }

    public void sendData(View V) {
        String message = enterMsg.getText().toString();
        socket.emit("chat message", message);
    }

    private Emitter.Listener data = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            String msg;
            try {

                msg = (String) args[0];
                list.add(msg);

                adapter.notifyDataSetChanged();
                // Toast.makeText(MainActivity.this,"hii",Toast.LENGTH_LONG).show();


            } catch (Exception e) {
                Log.d("EXP", e.toString());
                return;
            }
        }

    };
}
