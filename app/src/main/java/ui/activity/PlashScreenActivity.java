package ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.framgia.englishforkid_3.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.local.SQLiteCommon;
import data.model.DataModel;
import util.JsoupParserHtml;

public class PlashScreenActivity extends AppCompatActivity {
    private final String FONT_BSC = "fonts/font_bsc.ttf";
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.text_plash_screen)
    TextView mTextPlashScreen;
    private SQLiteCommon mSqLiteCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequireScreen();
        setContentView(R.layout.activity_plash_screen);
        ButterKnife.bind(this);
        initViews();
    }

    private void setRequireScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void initViews() {
        mSqLiteCommon = new SQLiteCommon(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_BSC);
        mTextPlashScreen.setTypeface(typeface);
        new JsoupAsyncTask().execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<DataModel> listDataModel = new ArrayList<>();
                listDataModel.addAll(JsoupParserHtml.parseSongs());
                mSqLiteCommon.saveListDataModel(listDataModel, SQLiteCommon.TYPE_TABLE_SONGS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                List<DataModel> listDataModel = new ArrayList<>();
                listDataModel.addAll(JsoupParserHtml.parseShortStories());
                mSqLiteCommon
                    .saveListDataModel(listDataModel, SQLiteCommon.TYPE_TABLE_SHORT_STORIES);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(PlashScreenActivity.this, MainActivity.class));
            finish();
        }
    }
}
