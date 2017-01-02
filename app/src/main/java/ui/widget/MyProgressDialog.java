package ui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.framgia.englishforkid_3.R;

import util.Constant;

/**
 * Created by tuanbg on 1/2/17.
 */
public class MyProgressDialog extends ProgressDialog {
    private Context mContext;

    public MyProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMessage(mContext.getResources().getString(R.string.msg_loading));
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();
        autoHideDialog();
    }

    public void autoHideDialog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dismiss();
            }
        }, Constant.TIME_DELAY_DIALOG);
    }
}
