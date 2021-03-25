package com.example.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ygj on 2021/3/22.
 */
public class PlusService extends Service {
    private String TAG = "PlusService";

    public static final int PLUS = 1;
    public static final String DESCRIPTION = "com.example.server.IPlus";

    //步骤一、创建Binder对象-->分析1
    Binder binder = new Stub();

    //步骤二、创建IInterface的匿名类对象，
    // 需要预先定义IPlus接口继承IInterface接口-->分析3
    IPlus plus = new IPlus() {
        //Client进程需要调用方法
        @Override
        public int add(int a, int b) {
            return a + b;
        }

        //实现IInterface中的唯一的方法
        @Override
        public IBinder asBinder() {
            return null;
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //步骤三
        //1、将("com.example.server.IPlus"，plus)作为(key,value)的形式存入到Binder对象中的Map<String,IInterface>中
        //2、之后Binder对象可以调用queryLocalInterface(String descriptor)去查找对应的IInterface的引用，然后调用目标方法
        binder.attachInterface(plus, DESCRIPTION);
        return binder;
    }


    //<--分析1：Stub类-->
    public static class Stub extends Binder {
        // 继承自Binder类 ->>分析2

        //重写onTransact
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            Log.e("Server", "code:" + code);
            switch (code) {
                case PLUS:
                    data.enforceInterface(DESCRIPTION);
                    int a = data.readInt();
                    int b = data.readInt();
                    int result = ((IPlus) queryLocalInterface(DESCRIPTION)).add(a, b);
                    if (reply != null) {
                        reply.writeInt(result);
                        Log.e("Server", "result:" + result);
                    }
                    return true;

            }

            return super.onTransact(code, data, reply, flags);
        }


    }


}
