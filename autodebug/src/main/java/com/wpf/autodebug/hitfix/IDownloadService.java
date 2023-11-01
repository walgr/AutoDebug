package com.wpf.autodebug.hitfix;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
public interface IDownloadService extends IInterface {
    void addPatch(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException;

    void queryPatchByQR(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException;

    /* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
    public static class Default implements IDownloadService {
        @Override // com.taobao.sophix.aidl.IDownloadService
        public void queryPatchByQR(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException {
        }

        @Override // com.taobao.sophix.aidl.IDownloadService
        public void addPatch(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: C:\Users\王朋飞\Downloads\classes.dex */
    public static abstract class Stub extends Binder implements IDownloadService {
        private static final String DESCRIPTOR = "com.taobao.sophix.aidl.IDownloadService";
        static final int TRANSACTION_addPatch = 2;
        static final int TRANSACTION_queryPatchByQR = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDownloadService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDownloadService)) {
                return (IDownloadService) queryLocalInterface;
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
                    queryPatchByQR(parcel.readString(), IPatchStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    addPatch(parcel.readString(), IPatchStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
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
        public static class Proxy implements IDownloadService {
            public static IDownloadService sDefaultImpl;
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

            @Override // com.taobao.sophix.aidl.IDownloadService
            public void queryPatchByQR(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iPatchStatusCallback != null ? iPatchStatusCallback.asBinder() : null);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().queryPatchByQR(str, iPatchStatusCallback);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.taobao.sophix.aidl.IDownloadService
            public void addPatch(String str, IPatchStatusCallback iPatchStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iPatchStatusCallback != null ? iPatchStatusCallback.asBinder() : null);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().addPatch(str, iPatchStatusCallback);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IDownloadService iDownloadService) {
            if (Proxy.sDefaultImpl != null || iDownloadService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iDownloadService;
            return true;
        }

        public static IDownloadService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}