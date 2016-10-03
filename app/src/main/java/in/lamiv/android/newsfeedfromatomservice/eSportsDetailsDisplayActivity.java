package in.lamiv.android.newsfeedfromatomservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class eSportsDetailsDisplayActivity extends AppCompatActivity {

    public static String ARG_ITEM_ID = "item_id";
    public static String ARG_TITLE = "item_title";
    public static String ARG_SUMMARY = "item_summary";
    public static String ARG_RIGHTS = "item_rights";
    public static String ARG_ICON_URL = "item_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sports_details_display);

        ((TextView)findViewById(R.id.details_text)).setText(getIntent().getStringExtra(ARG_TITLE));
        ((TextView)findViewById(R.id.details_summary)).setText(getIntent().getStringExtra(ARG_SUMMARY));
        ((TextView)findViewById(R.id.details_rights)).setText(getIntent().getStringExtra(ARG_RIGHTS));

        if(getIntent().getStringExtra(ARG_ICON_URL) != null) {
            new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                    .execute(getIntent().getStringExtra(ARG_ICON_URL));
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
