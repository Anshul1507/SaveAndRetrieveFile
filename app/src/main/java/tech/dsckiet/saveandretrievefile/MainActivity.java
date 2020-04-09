package tech.dsckiet.saveandretrievefile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog;
    private String fileName;
    private Button mButton;
    private TextView mTextView;
    private String[] files = new String[4];
    ArrayDeque<String> deque, names = new ArrayDeque<String>(4);
    private MediaPlayer mp;
    private Object[] sizeUrlDeque;
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.next_btn);
        mTextView = findViewById(R.id.textView);

        deque = new ArrayDeque<String>();
        names = new ArrayDeque<String>();
        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.show();
        String baseURL = "https://d2to6du2km6iv2.cloudfront.net/flexible/";

        String[] urls = {baseURL + "clip.mp3", baseURL + "chunk.mp3", baseURL + "characteristic.mp3"};
        initialTask(urls);

        final String[] url2 = {baseURL + "car.mp3", baseURL + "balloon.mp3", baseURL + "casual.mp3"};

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitButton(getApplicationContext(), url2);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mp.stop();
        mp.release();
    }

    private void submitButton(Context context, String url2[]) {

        if (idx < 3) {
            new DownloadFileAsync().execute(url2[idx]);
            idx++;
        }
        sizeUrlDeque = deque.toArray();
        if (deque.size() > 0) {
            playMedia(deque.peek());
            context.deleteFile(deque.getFirst());
            files = context.fileList();
//            for (int i = 0; i < files.length; i++) {
//                Log.i("files in cache ->", files[i]);
//            }
            deque.removeFirst();
            names.removeFirst();

//            for (Iterator itr = deque.iterator(); itr.hasNext(); ) {
//                String url = itr.next().toString();
//                Log.i("Deque Left >> ", url);
//            }
//
//            for (Iterator itr = deque.iterator(); itr.hasNext(); ) {
//                String url = itr.next().toString();
//                Log.i("Deque New >> ", url);
//            }
        } else {
            Toast.makeText(context, "End", Toast.LENGTH_SHORT).show();
        }

    }

    private void playMedia(String file) {
        mp = new MediaPlayer();
        try {
            mp.setDataSource(getFilesDir() + "/" + file);//Write your location here
            mp.prepare();
            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialTask(String[] urls) {
        for (int i = 0; i < 3; i++) {
            new DownloadFileAsync().execute(urls[i]);
        }
    }


    private class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {
            String urlPath = aurl[0];

            fileName = urlPath.substring(47);

            InputStream stream = null;
            FileOutputStream fos = null;
            try {

                URL url = new URL(urlPath);
                URLConnection urlcon = url.openConnection();
                stream = urlcon.getInputStream();

                BufferedInputStream reader = new BufferedInputStream(stream);
                fos = openFileOutput(fileName, MODE_PRIVATE);
//                Log.i("TAG", "doInBackground: " + fileName.substring(0, fileName.length() - 4));
                deque.add(fileName);
                names.add(fileName.substring(0, fileName.length() - 4));
                int times = -1;
                while ((times = reader.read()) != -1) {
                    fos.write(times);
                }
//                Log.i("file saved to", getFilesDir() + "/" + fileName);
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
//            Log.d("Downloading..", progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
            mProgressDialog.dismiss();
        }
    }


}
