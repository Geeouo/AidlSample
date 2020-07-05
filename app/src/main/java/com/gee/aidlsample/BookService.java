package com.gee.aidlsample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Create by Gee on 2020/7/5.
 */
public class BookService extends Service {

    private final String TAG = "BookService";
    ArrayList bookList = new ArrayList();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate -->");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand -->");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind -->");
        return new IBookManager.Stub() {
            @Override
            public List<Book> getBookList() throws RemoteException {
                //todo
                for (int i = 0; i < 2; i++) {
                    Book abook = new Book(i, "name-->" + i);
                    bookList.add(abook);
                }
                return bookList;
            }

            @Override
            public void addBook(Book book) throws RemoteException {
                if (bookList != null) {
                    bookList.add(book);
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy -->");
    }
}
