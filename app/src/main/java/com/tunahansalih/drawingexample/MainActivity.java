package com.tunahansalih.drawingexample;

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

import java.io.File;

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
                mDrawingView.setPenSize(50);
                break;
            case R.id.set_number:
                String addText = "5";
                if(isSet == true){
                    mCalculator.setText("0");
                    isSet = false;
                }
                String text = String.valueOf(mCalculator.getText());
                if(text.compareTo("0")==0){
                    mCalculator.setText(addText);
                } else {
                    mCalculator.setText(text+addText);
                }
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setPenSize(50);
                break;
            case R.id.clear_button:
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDrawingCanvas));
                mDrawingView.initializePen();
                mDrawingView.setPenColor(ContextCompat.getColor(this, R.color.colorPen));
                mDrawingView.setPenSize(50);
                break;
            case R.id.equals:
                String myResult = "135";
                isSet = true;
                mCalculator.setText(myResult);
                break;
        }
    }
}
