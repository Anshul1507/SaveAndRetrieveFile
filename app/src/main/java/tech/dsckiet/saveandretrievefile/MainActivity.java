package tech.dsckiet.saveandretrievefile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String baseURL = "https://d2to6du2km6iv2.cloudfront.net/flexible/";

        String[] urls = {baseURL + "clip.mp3",baseURL+"chunk.mp3",baseURL+"characteristic.mp3"};
        mProgressDialog = new ProgressDialog(this);
        for(int i=0;i<3;i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }else{
                    new DownloadFileAsync().execute(urls[i]);
                }
            }
        }
    }


    private class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... aurl) {
            String urlPath = aurl[0];
            String fileName = "audio";
            File output = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            Log.i("check", "output: " + output);
            File dir = new File(output.getAbsolutePath());
            Log.i("check", "dir: " + dir);
            dir.mkdirs();
            String path = dir + "/" + urlPath.substring(47);
            Log.i("check", "path: " + path);
            File f = new File(path);

            if (f.exists()) {
                f.delete();
            }

            InputStream stream = null;
            FileOutputStream fos = null;
            try {

                URL url = new URL(urlPath);
                URLConnection urlcon = url.openConnection();
                stream = urlcon.getInputStream();

                InputStreamReader reader = new InputStreamReader(stream);
                fos = new FileOutputStream(f.getPath());

                int times = -1;
                while ((times = reader.read()) != -1) {
                    fos.write(times);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("Downloading..",progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
            mProgressDialog.dismiss();
        }
    }
}
