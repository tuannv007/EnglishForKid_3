package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Nhahv on 6/29/2016.
 * <></>
 */
public class NetworkReceiver extends BroadcastReceiver {
    public static NetworkReceiverListener sNetworkListener;

    public static boolean isConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void setNetworkReceiver(NetworkReceiverListener listener) {
        sNetworkListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnect = isConnect(context);
        if (sNetworkListener != null) sNetworkListener.onNetworkConnectChange(isConnect);
    }

    public interface NetworkReceiverListener {
        void onNetworkConnectChange(boolean isConnect);
    }
}
