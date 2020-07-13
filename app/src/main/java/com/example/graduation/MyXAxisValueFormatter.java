package com.example.graduation;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Collection;

public class MyXAxisValueFormatter  extends ValueFormatter {
    private String[] mValues = new String[] {};
    private int mValueCount = 0;



    //`setValues`를 사용하여 축 레이블을 설정할 경우

    public MyXAxisValueFormatter() {
    }

    // 특정 `setValues`를 사용하여 축 레이블을 설정할 경우
    public MyXAxisValueFormatter(String[] values) {
        if (values != null)
            setValues(values);
    }

    //x축의 라벨을 지정하는 생성자
    public MyXAxisValueFormatter(Collection<String> values) {
        if (values != null)
            setValues(values.toArray(new String[values.size()]));
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);

        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        return mValues[index];
    }

    public String[] getValues()
    {
        return mValues;
    }

    public void setValues(String[] values)
    {
        if (values == null)
            values = new String[] {};

        this.mValues = values;
        this.mValueCount = values.length;
    }
}
