package com.tunahansalih.drawingexample;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mukesh.DrawingView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    private Button mSaveButton, mPenButton, mClearButton, mEquals;
    private TextView mCalculator;
    private DrawingView mDrawingView;
    private String selectedDataType = "0";
    String addText = "5";
    String cText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setListeners();

        upLoadServerUri = "http://www.androidexample.com/media/UploadToServer.php";
    }

    private void setListeners() {
        mSaveButton.setOnClickListener(this);
        mPenButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mEquals.setOnClickListener(this);
    }

    private void initializeUI() {
        mDrawingView = (DrawingView) findViewById(R.id.scratch_pad);
        mSaveButton = (Button) findViewById(R.id.set_number);
        mPenButton = (Button) findViewById(R.id.pen_button);
        mClearButton = (Button) findViewById(R.id.clear_button);
        mEquals = (Button) findViewById(R.id.equals);
        mCalculator = (TextView) findViewById(R.id.calculator);
    }
    boolean isSet = false;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pen_button:
                mCalculator.setText("0");
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setPenSize(20);
                break;
            case R.id.set_number:

                if(isSet == true){
                    mCalculator.setText("0");
                    isSet = false;
                }
                cText = String.valueOf(mCalculator.getText());

                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/dataset/");
                try {
                    if (dir.mkdirs()) {
                        System.out.println("Folder created");
                        Log.d("created", dir.getAbsolutePath());
                    } else {
                        System.out.println("Folder is not created");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                mDrawingView.saveImage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/dataset", "/" + ts,
                        Bitmap.CompressFormat.PNG, 100);
                uploadFileName = ts+".png";

                Bitmap bm = BitmapFactory.decodeFile(uploadFilePath + "" + uploadFileName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                byte[] byteArrayImage = baos.toByteArray();
                String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                Log.i("response",encodedImage);

                new postRequest().execute(encodedImage);

                // TODO set addText to serverResponseMessage

                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setPenSize(20);
                break;
            case R.id.clear_button:
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setPenSize(20);
                break;
            case R.id.equals:
                cText = String.valueOf(mCalculator.getText());
                // TODO change myResult to cText value

                String myResult = "35+(5*2+1)";
                double result = 0;
                Expression expression = new ExpressionBuilder(cText).build();
                try {
                    // Calculate the result and display
                    result = expression.evaluate();
                } catch (ArithmeticException ex) {
                    // Display an error message
                    Log.i("message","Some error happened");
                }


                isSet = true;
                Log.i("result",String.valueOf((int)result));
                mCalculator.setText(String.valueOf((int)result));
                break;
        }
    }

    String upLoadServerUri = null;

    /**********  File Path *************/
    final String uploadFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/dataset/";
    String uploadFileName = "service_lifecycle.png";


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public class postRequest extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            String myUrl = "http://178.62.243.149:8000/polls/postData";
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("image", params[0]);
            JSONObject json = new JSONObject(hashMap);
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(myUrl)
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String str = response.body().string();
                Log.i("response",str);
                return str;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String number) {
            if(cText.compareTo("0")==0){
                mCalculator.setText(number);
            } else {
                mCalculator.setText(cText+number);
            }
        }
    }
}
