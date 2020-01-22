package com.xxmassdeveloper.mpchartexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.xxmassdeveloper.mpchartexample.Pro.AdapterSpecialities;
import com.xxmassdeveloper.mpchartexample.Pro.Item;
import com.xxmassdeveloper.mpchartexample.Pro.Model;
import com.xxmassdeveloper.mpchartexample.Pro.MyServicesInterface;
import com.xxmassdeveloper.mpchartexample.Pro.RetrofitInstance;
import com.xxmassdeveloper.mpchartexample.Pro.SpecialitiesResponse;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CombinedChartActivity extends DemoBase {

    private CombinedChart chart;
    private final int count = 12;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    XAxis xAxis;
    CombinedData data;
    ProgressBar progressBar;
    SearchView dateTimeSearch;
    AlertDialog alertDialog;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    ImageView flowImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);

        dateTimeSearch = findViewById(R.id.dateTimeSearch);
        flowImg = findViewById(R.id.flowImg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.rec);
        recyclerView.setLayoutManager(layoutManager);

        setTitle("Average of patients with time cost");

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        progressBar = findViewById(R.id.progress_bar);
        NoData();

        // draw bars behind lines
        chart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.LINE
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);


        data = new CombinedData();


        data.setValueTypeface(tfLight);
        listSpecialities(this,"","");


        dateTimeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**Display first dialog (from, to, search)*/
                LayoutInflater inflater = getLayoutInflater();
                View fromToSearch = inflater.inflate(R.layout.from_to_dialog, null);
                final Button fromBtn = fromToSearch.findViewById(R.id.fromBtn);
                final Button toBtn = fromToSearch.findViewById(R.id.toBtn);
                Button searchBtn = fromToSearch.findViewById(R.id.searchBtn);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CombinedChartActivity.this);
// ...Irrelevant code for customizing the buttons and title

                dialogBuilder.setView(fromToSearch);

                alertDialog = dialogBuilder.create();
                alertDialog.show();

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        alertDialog.dismiss();
                    }
                });
                /**Display second dialog Date Picker*/


                fromBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker(fromBtn);
                    }
                });

                toBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker(toBtn);
                    }
                });

                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //action
                        listSpecialities(CombinedChartActivity.this, fromBtn.getText().toString(), toBtn.getText().toString());
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void NoData(){
        progressBar.setVisibility(View.INVISIBLE);
        chart.setVisibility(View.INVISIBLE);
        flowImg.setVisibility(View.VISIBLE);
    }

    private void ShowData(){
        progressBar.setVisibility(View.VISIBLE);
        chart.setVisibility(View.VISIBLE);
        flowImg.setVisibility(View.INVISIBLE);
    }
    private void DatePicker(final Button button){
        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag("n");
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    getString(R.string.clean) // Optional
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2200, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
//                    Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                button.setText(myDateFormat.format(date));
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
//                        textView.setText("");
                button.setText("");
            }
        });

        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2020, Calendar.MARCH, 4, 15, 20).getTime());
        dateTimeFragment.show(getSupportFragmentManager(), "n");
    }

    private LineData generateLineData(ArrayList<Entry> entries, ArrayList<Entry> entries2) {

        LineData d = new LineData();

        LineDataSet set = new LineDataSet(entries, "TotalNewSlotsAverage");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);


        LineDataSet set2 = new LineDataSet(entries2, "TotalFollowupSlotsAverage");
        set2.setColor(Color.rgb(240, 238, 70));
        set2.setLineWidth(2.5f);
        set2.setCircleColor(Color.rgb(240, 238, 70));
        set2.setCircleRadius(5f);
        set2.setFillColor(Color.rgb(240, 238, 70));
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set2.setDrawValues(true);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(Color.rgb(240, 238, 70));

        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        d.addDataSet(set2);

        return d;
    }

    private BarData generateBarData(ArrayList<BarEntry> entries1, ArrayList<BarEntry> entries2) {

        BarDataSet set1 = new BarDataSet(entries1, "FollowupSlotsAverage");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "NewSlotsAverage");
//        set2.setStackLabels(new String[]{"NewSlotsAverage"});
        set2.setColors(Color.rgb(23, 197, 255));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    @Override
    public void saveToGallery() { /* Intentionally left empty */ }

    public void listProviderTiming(String code, final String dateFrom, final String dateTo) {
        MyServicesInterface myServicesInterface = (MyServicesInterface) RetrofitInstance.getService();
        Call<Model> call = myServicesInterface.getProviderTiming(dateFrom, dateTo, code + "");
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.isSuccessful()) {
                    System.out.println("*********************** on response ********************");
                    Model appointmentItems = response.body();

                    if (appointmentItems != null && appointmentItems.getProviderStats() != null) {
                        progressBar.setVisibility(View.GONE);
                        ArrayList<BarEntry> followupSlotsAverage = new ArrayList<>();
                        ArrayList<BarEntry> newSlotsAverage = new ArrayList<>();
                        ArrayList<Entry> totalfollowupSlotsAverage = new ArrayList<>();
                        ArrayList<Entry> totalnewSlotsAverage = new ArrayList<>();
                        doctorNames = new ArrayList<>();
                        for (int index = 0; index < appointmentItems.getProviderStats().size(); index++) {
                            //FollowupSlotsAverage
                            followupSlotsAverage.add(new BarEntry(0, Integer.parseInt(appointmentItems.getProviderStats().get(index).getNewSlotsAverage())));

                            //doctorNames
                            doctorNames.add(appointmentItems.getProviderStats().get(index).getFirstNameEN() + " " + appointmentItems.getProviderStats().get(index).getFamilyNameEN());

                            //totalfollowupSlotsAverage
                            totalfollowupSlotsAverage.add(new Entry(index, Integer.parseInt(appointmentItems.getFollowupSlotsAverage())));
                            if (index == appointmentItems.getProviderStats().size() - 1) {
                                totalfollowupSlotsAverage.add(new Entry(index + 1, Integer.parseInt(appointmentItems.getFollowupSlotsAverage())));
                                totalnewSlotsAverage.add(new Entry(index + 1, Integer.parseInt(appointmentItems.getNewSlotsAverage())));
                            }

                            //totalnewSlotsAverage
                            totalnewSlotsAverage.add(new Entry(index, Integer.parseInt(appointmentItems.getNewSlotsAverage())));

                            // NewSlotsAverage
                            newSlotsAverage.add(new BarEntry(0, Integer.parseInt(appointmentItems.getProviderStats().get(index).getFollowupSlotsAverage())));
                        }


                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return doctorNames.get((int) value % doctorNames.size());
                            }
                        });
                        data.setData(generateBarData(followupSlotsAverage, newSlotsAverage));
                        data.setData(generateLineData(totalnewSlotsAverage, totalfollowupSlotsAverage));
                        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

                        chart.setData(data);
                        chart.invalidate();

                    }
                } else {
                    System.out.println("no access to resources");

                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                call.cancel();

            }
        });
    }

    public void listSpecialities(final Context context, @Nullable final String dateFrom,@Nullable final String dateTo) {
        MyServicesInterface myServicesInterface = (MyServicesInterface) RetrofitInstance.getService();
        Call<SpecialitiesResponse> call = myServicesInterface.getSpecialities();
        call.enqueue(new Callback<SpecialitiesResponse>() {
            @Override
            public void onResponse(Call<SpecialitiesResponse> call, Response<SpecialitiesResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("*********************** on response ********************");
                    SpecialitiesResponse appointmentItems = response.body();
                    if (appointmentItems != null) {
                        Gson gson = new Gson();
                        Log.e("YYYYYYYY", gson.toJson(appointmentItems));
                        if (!dateFrom.equals("")){
                            listProviderTiming(appointmentItems.getItems().get(0).getCode(), dateFrom, dateTo);
                        }

                        recyclerView.setAdapter(new AdapterSpecialities(context, appointmentItems.getItems(), new AdapterSpecialities.OnItemClickListener() {
                            @Override
                            public void onItemClick(Item item) {
                                progressBar.setVisibility(View.VISIBLE);

                                Toast.makeText(CombinedChartActivity.this, item.getCode(), Toast.LENGTH_SHORT).show();
                                listProviderTiming(item.getCode(), dateFrom, dateTo);
                                ShowData();
                            }
                        }));


                    }
                } else {
                    System.out.println("no access to resources");

                }
            }

            @Override
            public void onFailure(Call<SpecialitiesResponse> call, Throwable t) {
                call.cancel();

            }
        });
    }

}
