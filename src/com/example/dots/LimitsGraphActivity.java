package com.example.dots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The graph view for the always random coherence version of the
 * main testing environment. Based off the GraphActivity class
 */
public class LimitsGraphActivity extends Activity {
    LineChart mLineChart;
    int directionIndex = 13;
    int responseIndex = 14;
    int reactionTimeIndex = 15;
    int trialIndex = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.limits_graph_activity);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        GraphView latestGraph = (GraphView) findViewById(R.id.latestGraph);
        //TextView text = (TextView) findViewById(R.id.graph_text);


        ArrayList<DataPoint> dataPointList = new ArrayList<DataPoint>();
        //DataPoint[] dataPoints = new DataPoint[];
        //LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();



        // parse file
        String fileName = "limitsData.txt";
        //File file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
        File file = new File(getExternalFilesDir(null),/*LoginSignupActivity.usernameFileData.get(0) + */"limitsData.txt");
        Scanner input = new Scanner(System.in);
        try {
            input = new Scanner(file);
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("limitsData.txt was not found");
        }
        int trialNum = 1;
        while(input.hasNext())
        {
            String line = input.nextLine();
            String[] arr = line.split("\t");
            //System.out.println(Arrays.toString(arr));
            if(arr.length == 16) {
                dataPointList.add(new DataPoint(trialNum, Integer.parseInt(arr[reactionTimeIndex])));
                trialNum++;
            }
        }
        DataPoint[] dataPoints = dataPointList.toArray(new DataPoint[dataPointList.size()]);
        DataPoint[] latestPoints = new DataPoint[Math.min(dataPoints.length,20)];
        int loopIndex = 0;
        int average = 0;
        int bestTime = Integer.MAX_VALUE;
        while(loopIndex < dataPoints.length && loopIndex < 20){
            if(dataPoints.length > 20) {
                latestPoints[loopIndex] = new DataPoint(loopIndex, dataPoints[dataPoints.length - 20 + loopIndex].getY());
            }
            else {
                latestPoints[loopIndex] = new DataPoint(loopIndex, dataPoints[loopIndex].getY());
            }
            average+=latestPoints[loopIndex].getY();
            if(latestPoints[loopIndex].getY() < bestTime) {

                bestTime = (int)latestPoints[loopIndex].getY();
            }
            loopIndex++;
        }
        if(dataPoints.length < 20) {
            average = 0;
        }
        else {
            average = average / 20;
        }
        LineGraphSeries<DataPoint> latestSeries = new LineGraphSeries<DataPoint>(latestPoints);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);
        latestGraph.addSeries(latestSeries);
        GridLabelRenderer mLabelRenderer = graph.getGridLabelRenderer();
        GridLabelRenderer mLabelRenderer2 = latestGraph.getGridLabelRenderer();
        mLabelRenderer.setHorizontalAxisTitle("Trial Number");
        mLabelRenderer2.setHorizontalAxisTitle("Trial Number (Last 20 Trials)");
        //mLabelRenderer.setHorizontalAxisTitleColor(Color.BLACK);
        mLabelRenderer2.setVerticalAxisTitle("Reaction Time");
        mLabelRenderer.setVerticalAxisTitle("Reaction Time");
        TextView averageText = (TextView)findViewById(R.id.statistics);
        if(average == 0) {
            averageText.setText("Please play more trials to show more statistics");
        }
        else {
            averageText.setText("Average Reaction Time of Last 20 Trials: " + average + " ms" + "\n" + "Best Time: " + bestTime + "ms");
        }
        //mLabelRenderer.setHorizontalAxisTitleColor(Color.BLACK);



    }

    public void createMPGraph()
    {
        // parse file
        String fileName = "limitsData.txt";
        //File file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
        File file = new File(getExternalFilesDir(null), /*LoginSignupActivity.usernameFileData.get(0) + */"limitsData.txt");

        Scanner input = new Scanner(System.in);

        ArrayList<Float> reacTimeValues = new ArrayList<Float>();
        ArrayList<Entry> reacTimeVals = new ArrayList<Entry>();
        try {
            input = new Scanner(file);
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("limitsData.txt was not found");
        }
        int trialNum = 1;
        while(input.hasNext())
        {
            String line = input.nextLine();
            String[] arr = line.split("\t");
            //System.out.println(Arrays.toString(arr));
            if(arr.length == 16) {
                reacTimeVals.add(new Entry(Integer.parseInt(arr[reactionTimeIndex]), trialNum));
                System.out.println(Integer.parseInt(arr[reactionTimeIndex]));
                trialNum++;
            }
        }

        LineDataSet setReactionTime = new LineDataSet(reacTimeVals, "Reaction Time");
        //setReactionTime.setColor(Color.rbg(255, 255, 255));

        //Hung - commented out for redundancy/unused code
        /*mLineChart = (LineChart) findViewById(R.id.linechart);
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 1; i <= trialNum; i++ )
        {

            if(trialNum <= 20)
            {
                if(i%5 == 0)
                {
                    xVals.add(Integer.toString(i));
                }
            }
            else if (trialNum <= 50)
            {
                if(i%10 == 0)
                {
                    xVals.add(Integer.toString(i));
                }
            }
            xVals.add(Integer.toString(i));
        }

        LineData data = new LineData(xVals, setReactionTime);
        mLineChart.setDescription("Y-axis: Reaction Time\nX-axis: Trial #");
        mLineChart.setData(data);
        // disable the drawing of values into the chart
        mLineChart.setDrawYValues(false);
        // enable touch gestures
        mLineChart.setTouchEnabled(true);
        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);
        mLineChart.animateXY(3000, 3000);
        */
    }

    public void onBackClick(View v)
    {
        //onBackPressed();
        Intent intent = new Intent(this,GraphMenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        Intent intent = new Intent(LimitsGraphActivity.this,
                GraphMenuActivity.class);
        startActivity(intent);

        return;
    }

}