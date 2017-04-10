package com.tunahansalih.drawingexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mukesh.DrawingView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Button mSaveButton, mPenButton, mEraserButton, mClearButton;
    private Spinner mSpinner;
    private DrawingView mDrawingView;
    private String selectedDataType = "0";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setListeners();

    }

    private void setListeners() {
        mSaveButton.setOnClickListener(this);
        mPenButton.setOnClickListener(this);
        mEraserButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mSpinner.setOnItemSelectedListener(this);

    }

    private void initializeUI() {
        mDrawingView = (DrawingView) findViewById(R.id.scratch_pad);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mPenButton = (Button) findViewById(R.id.pen_button);
        mEraserButton = (Button) findViewById(R.id.eraser_button);
        mClearButton = (Button) findViewById(R.id.clear_button);

        //Spinner
        mSpinner = (Spinner) findViewById(R.id.data_spinner);
        List<String> dataTypes = new ArrayList<>();
        dataTypes.add("0");
        dataTypes.add("1");
        dataTypes.add("2");
        dataTypes.add("3");
        dataTypes.add("4");
        dataTypes.add("5");
        dataTypes.add("6");
        dataTypes.add("7");
        dataTypes.add("8");
        dataTypes.add("9");
        dataTypes.add("+");
        dataTypes.add("-");
        dataTypes.add("*");
        dataTypes.add("/");
        dataTypes.add("=");
        dataTypes.add(")");
        dataTypes.add("(");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);






    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                File dir = new File(Environment.getExternalStorageDirectory().toString() + "/dataset/" + selectedDataType);
                try{
                    if(dir.mkdir()) {
                        System.out.println("Folder created");
                    } else {
                        System.out.println("Folder is not created");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                mDrawingView.saveImage(Environment.getExternalStorageDirectory().toString() + "/dataset/" + selectedDataType , "" + ts,
                        Bitmap.CompressFormat.PNG, 100);
                break;
            case R.id.pen_button:

                mDrawingView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this,R.color.colorPen));
                mDrawingView.setPenSize(50);
                break;
            case R.id.eraser_button:
                mDrawingView.initializeEraser();
                mDrawingView.setEraserSize(100);
                break;
            case R.id.clear_button:
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPen));
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this,R.color.colorPen));
                mDrawingView.setPenSize(50);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        switch (item){
            case("+"):
                selectedDataType = "plus";
                break;
            case("-"):
                selectedDataType = "minus";
                break;
            case("/"):
                selectedDataType = "slash";
                break;
            case("*"):
                selectedDataType = "times";
                break;
            case("("):
                selectedDataType = "openingParanthesis";
                break;
            case(")"):
                selectedDataType = "closingParanthesis";
                break;
            case("="):
                selectedDataType = "equals";
            default:
                selectedDataType = item;

        }
        selectedDataType = item;
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
