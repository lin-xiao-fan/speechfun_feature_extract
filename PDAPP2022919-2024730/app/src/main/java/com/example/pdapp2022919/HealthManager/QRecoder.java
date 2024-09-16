package com.example.pdapp2022919.HealthManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pdapp2022919.Database.Questionnaire.Questionnaire;
import com.example.pdapp2022919.Database.Questionnaire.QuestionnaireDao;
import com.example.pdapp2022919.Questionnaire.QRecoderAdapter;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QRecoder extends ScreenSetting {
    private static final SimpleDateFormat format = new SimpleDateFormat("MM/dd");

    private final QRecoderAdapter adapter = new QRecoderAdapter();
    private RecyclerView QrecycleView;
    private LineChart lineChart2;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrecoder);
        Button back_main_page_button7 = findViewById(R.id.back_main_page_button7);
        QrecycleView = findViewById(R.id.QrecycleView);
        lineChart2 = findViewById(R.id.lineChart2);
        initChart();
        Button information_button2 = findViewById(R.id.information_button2);
        QrecycleView.setLayoutManager(new LinearLayoutManager(this));
        QrecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        QrecycleView.setAdapter(adapter);
        information_button2.setOnClickListener(view -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                PopupWindow popupWindow = new popupwindow(this);
                popupWindow.showAtLocation(
                        LayoutInflater.from(this).inflate(R.layout.questionnaire_popup, null),
                        Gravity.CENTER, 0, 0
                );
            });
        });

        back_main_page_button7.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            QuestionnaireDao dao = DatabaseManager.getInstance(QRecoder.this).questionnaireDao();
            List<Questionnaire> list = dao.getAllQuestionnaire(Client.getUuid().toString());
            runOnUiThread(() -> adapter.setList(list));
            setData(list);
        }).start();
    }

    public class popupwindow extends PopupWindow implements View.OnClickListener {
        View view;

        public popupwindow(Context mContext) {
            this.view = LayoutInflater.from(mContext).inflate(R.layout.questionnaire_popup, null);
            this.setOutsideTouchable(true);
            this.view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        dismiss();
                    }
                    return true;
                }
            });
            this.setContentView(this.view);
            this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        }

        @Override
        public void onClick(View view) {
        }
    }

    private void initChart() {
        lineChart2.getDescription().setEnabled(false);//設置不要圖表標籤
//        lineChart2.setTouchEnabled(false);//設置不可觸碰
//        lineChart2.setDragEnabled(false);//設置不可互動

        lineChart2.setTouchEnabled(false);
        lineChart2.setDoubleTapToZoomEnabled(false);
        lineChart2.setDragEnabled(false);
        lineChart2.setDragYEnabled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ILineDataSet VHI = createSet(NameManager.VHI, 0xFFFB5800);//取得曲線(因為只有一條，故為0，若有多條則需指定)
        VHI.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSets.add(VHI);
        ILineDataSet VOS = createSet(NameManager.VOS,0xFF05941F );//取得曲線(因為只有一條，故為0，若有多條則需指定)
        VOS.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSets.add(VOS);
        ILineDataSet PHQ = createSet(NameManager.PHQ, 0xFF0072E3);//取得曲線(因為只有一條，故為0，若有多條則需指定)
        PHQ.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSets.add(PHQ);
        lineChart2.setData(new LineData(dataSets));
        Legend legend = lineChart2.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        XAxis x = lineChart2.getXAxis();

        x.setTextColor(Color.BLACK);
        x.setDrawGridLines(true);//畫X軸線
        x.setPosition(XAxis.XAxisPosition.BOTTOM);//把標籤放底部
        x.setLabelCount(6, true);//設置顯示8個標籤
        x.setTextColor(Color.GRAY);
        x.setTypeface(Typeface.DEFAULT_BOLD);
        //設置X軸標籤內容物
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return format.format(new Date((long) value * 84600000));
            }
        });
        YAxis y = lineChart2.getAxisLeft();
        y.setTextColor(Color.BLACK);
        y.setDrawGridLines(true);
        y.setAxisMaximum(100);//最高100
        y.setAxisMinimum(0);//最低0
        YAxis rightY = lineChart2.getAxisRight();
        rightY.setAxisMaximum(40);//最高100
        rightY.setAxisMinimum(0);//最低0
        rightY.setDrawGridLines(false);
    }

    private LineDataSet createSet(String name, int color) {
        LineDataSet set = new LineDataSet(null, name);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%d", (int) value);
            }
        });
        set.setColor(color);
        set.setLineWidth(2);
        set.setCircleColor(color);
        set.setDrawCircleHole(false);
        set.setValueTextColor(color);
        set.setDrawValues(true);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setValueTextSize(15);
        set.setValueTypeface(Typeface.DEFAULT_BOLD);
        return set;
    }

    private void setData(List<Questionnaire> list) {
        LineData data = lineChart2.getData();
        float min = -1, max = -1;
        for (int i = list.size() - 1; i > -1; i--) {
            float time = (float) list.get(i).time / 84600000;
            if (min == -1 || time < min) min = time;
            if (max == -1 || time > max) max = time;
            switch (list.get(i).q_type) {
                case NameManager.VHI:
                    data.addEntry(new Entry(time, list.get(i).q_score), 0);
                    break;
                case NameManager.VOS:
                    data.addEntry(new Entry(time, list.get(i).q_score), 1);
                    break;
                case NameManager.PHQ:
                    data.addEntry(new Entry(time, list.get(i).q_score), 2);
                    break;
            }
        }
        data.notifyDataChanged();
        lineChart2.setData(data);
        lineChart2.notifyDataSetChanged();
        XAxis x = lineChart2.getXAxis();
        System.out.println("min: " + min + "," + "max: " + max);
        float range = (max - min) * 0.1f;
        x.setAxisMinimum(min - range);
        x.setAxisMaximum(max + range);
    }
}
