package com.wpf.autodebug.hitfix;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
public interface IPatchStatusCallback extends IInterface {
    void onLoad(int i, int i2, String str, int i3) throws RemoteException;

    /* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
    public static class Default implements IPatchStatusCallback {
        @Override // com.taobao.sophix.aidl.IPatchStatusCallback
        public void onLoad(int i, int i2, String str, int i3) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
    public static abstract class Stub extends Binder implements IPatchStatusCallback {
        private static final String DESCRIPTOR = "com.taobao.sophix.aidl.IPatchStatusCallback";
        static final int TRANSACTION_onLoad = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPatchStatusCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IPatchStatusCallback)) {
                return (IPatchStatusCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    onLoad(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
        public static class Proxy implements IPatchStatusCallback {
            public static IPatchStatusCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.taobao.sophix.aidl.IPatchStatusCallback
            public void onLoad(int i, int i2, String str, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onLoad(i, i2, str, i3);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IPatchStatusCallback iPatchStatusCallback) {
            if (Proxy.sDefaultImpl != null || iPatchStatusCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iPatchStatusCallback;
            return true;
        }

        public static IPatchStatusCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}