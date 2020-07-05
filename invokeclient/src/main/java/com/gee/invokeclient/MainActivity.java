package com.gee.invokeclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gee.aidlsample.Book;
import com.gee.aidlsample.IBookManager;

import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Button getBookListButton;
    private Button addBookButton;
    private IBookManager iBookManager;
    private boolean isBindSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        /*ComponentName componentName = new ComponentName("com.gee.aidlsample", "com.gee.aidlsample.BookService");
        intent.setComponent(componentName);*/
        intent.setAction("gee.aidlsample.bookmanage");
        intent.setPackage("com.gee.aidlsample");
        isBindSuccess = bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        listView = findViewById(R.id.listview);
        getBookListButton = findViewById(R.id.getbooklist_btn);
        addBookButton = findViewById(R.id.addbook_btn);

        getBookListButton.setOnClickListener(this);
        addBookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getbooklist_btn:
                if (isBindSuccess()) {
                    try {
                        List<Book> bookList = iBookManager.getBookList();
                        listView.setAdapter(new ArrayAdapter<Book>(MainActivity.this, android.R.layout.simple_list_item_1, bookList));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(MainActivity.this, "服务未连接...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addbook_btn:
                if (isBindSuccess) {
                    try {
                        int nextInt = new Random().nextInt(20);
                        Book book = new Book(nextInt, "name-->" + nextInt);
                        iBookManager.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(MainActivity.this, "服务未连接...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private boolean isBindSuccess() {
        return this.isBindSuccess;
    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);
            try {
                service.linkToDeath(deathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBookManager = null;
        }
    };

    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (iBookManager == null) {
                return;
            }
            iBookManager.asBinder().unlinkToDeath(deathRecipient, 0);
            iBookManager = null;
            bindService();
        }
    };
}
