package com.tunahansalih.drawingexample;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    private Button mSaveButton, mPenButton, mClearButton, mEquals;
    private TextView mCalculator;
    private DrawingView mDrawingView;
    private String selectedDataType = "0";

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
                String addText = "5";
                if(isSet == true){
                    mCalculator.setText("0");
                    isSet = false;
                }
                String text = String.valueOf(mCalculator.getText());

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
                dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //messageText.setText("uploading started.....");
                                Log.i("server","uploading started.....");
                            }
                        });

                        uploadFile(uploadFilePath + "" + uploadFileName);

                    }
                }).start();

                // TODO set addText to serverResponseMessage
                if(text.compareTo("0")==0){
                    mCalculator.setText(addText);
                } else {
                    mCalculator.setText(text+addText);
                }
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
                String cText = String.valueOf(mCalculator.getText());
                // TODO change myResult to cText value

                String myResult = "35+(5*2+1)";
                double result = 0;
                Expression expression = new ExpressionBuilder(myResult).build();
                try {
                    // Calculate the result and display
                    result = expression.evaluate();
                } catch (ArithmeticException ex) {
                    // Display an error message
                    Log.i("message","Some error happened");
                }


                isSet = true;
                mCalculator.setText(String.valueOf((int)result));
                break;
        }
    }

    //TextView messageText;
    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    /**********  File Path *************/
    final String uploadFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/dataset/";
    String uploadFileName = "service_lifecycle.png";

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("server","Source File not exist :" +uploadFilePath + "" + uploadFileName);
                    //messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;
                            Log.i("server",msg);
                            //messageText.setText(msg);
                            Toast.makeText(MainActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("MalformedURLException Exception : check script url.");
                        Log.i("server","MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Log.i("server","Got Exception : see logcat ");
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block 
    }
}
