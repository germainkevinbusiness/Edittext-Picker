package com.edittextpicker.aliazaz;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

public class EditTextPicker extends AppCompatEditText implements TextWatcher {

    private float minValue, maxValue;
    private Object defaultValue;
    private String mask;
    private Integer type;
    private Boolean reqFlag;
    static String TAG = EditTextPicker.class.getName();
    private boolean maskCheckFlag = true;

    public EditTextPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        ImplementListeners();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.EditTextPicker,
                    0, 0
            );

            try {
                //required flag
                reqFlag = a.getBoolean(R.styleable.EditTextPicker_required, true);

                //For type -> range and equal
                type = a.getInteger(R.styleable.EditTextPicker_type, 0);
                if (type == 1) {

                    minValue = a.getFloat(R.styleable.EditTextPicker_minValue, -1);
                    maxValue = a.getFloat(R.styleable.EditTextPicker_maxValue, -1);
                    defaultValue = a.getFloat(R.styleable.EditTextPicker_defaultValue, -1);

                    if (minValue == -1)
                        throw new RuntimeException("Min value not provided");
                    if (maxValue == -1)
                        throw new RuntimeException("Max value not provided");

                } else if (type == 2) {

                    defaultValue = a.getString(R.styleable.EditTextPicker_defaultValue);

                    if (defaultValue == null)
                        throw new RuntimeException("Default value not provided");
                }

                // For mask
                mask = a.getString(R.styleable.EditTextPicker_mask);
                if (mask != null) {
                    if (!mask.trim().isEmpty()) {
                        maskingEditText(mask);
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, "TextPicker: ", e);
            } finally {
                a.recycle();
            }
        }
    }

    private void ImplementListeners() {
        super.addTextChangedListener(this);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public Boolean getReqFlag() {
        return reqFlag;
    }

    public void setReqFlag(Boolean reqFlag) {
        this.reqFlag = reqFlag;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mask == null) return;
        maskCheckFlag = i2 != 0;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (mask == null) return;
        if (!maskCheckFlag) return;
        String txt = TextUtils.editTextLoopToNextChar(mask, editable.length() - 1);
        EditTextPicker.super.getText().insert(editable.length() - 1, txt);
    }

    // call for maskingEditText
    private void maskingEditText(String mask) {
        super.setFilters(TextUtils.setLengthEditText(mask)); //Setting length
    }

    // call for checking empty textbox
    public boolean isEmptyTextBox() {

        if (!reqFlag)
            return true;

        if (super.getText().toString().isEmpty()) {
            Log.i(this.getContext().getClass().getName(), this.getContext().getResources().getResourceEntryName(super.getId()) + ": Empty!!");
            super.setError("Required! ");
            super.setFocusableInTouchMode(true);
            super.requestFocus();

            invalidate();
            requestLayout();

            return false;
        }

        return true;
    }

    // call for checking range textbox
    public boolean isRangeTextValidate() {

        if (!reqFlag)
            return true;

        if (!isEmptyTextBox())
            return false;

        if (Float.valueOf(super.getText().toString()) < minValue || Float.valueOf(super.getText().toString()) > maxValue) {

            if ((Float) defaultValue != -1) {

                String dValue = String.valueOf(defaultValue);
                if ((Float) defaultValue == Math.round((Float) defaultValue))
                    dValue = (dValue.split("\\.")[0]);

                boolean flag = (super.getText().toString().equals(String.valueOf(dValue)));

                invalidate();
                requestLayout();

                if (flag)
                    return true;
            }

            String min = String.valueOf(minValue);
            String max = String.valueOf(maxValue);

            if (minValue == Math.round(minValue))
                min = (min.split("\\.")[0]);

            if (maxValue == Math.round(maxValue))
                max = (max.split("\\.")[0]);

            super.setError("Range is " + min + " to " + max + " !!");
            super.setFocusableInTouchMode(true);
            super.requestFocus();
            Log.i(this.getContext().getClass().getName(), this.getContext().getResources().getResourceEntryName(super.getId()) + ": Range is " + min + " to " + max + "!!");

            invalidate();
            requestLayout();

            return false;
        }

        return true;
    }

    // call for checking default value in textbox
    public boolean isTextEqual() {

        if (!reqFlag)
            return true;

        if (!isEmptyTextBox())
            return false;

        if (!super.getText().toString().equals(String.valueOf(defaultValue))) {

            super.setError("Not equal to default value: " + defaultValue + " !!");
            super.setFocusableInTouchMode(true);
            super.requestFocus();
            Log.i(this.getContext().getClass().getName(), this.getContext().getResources().getResourceEntryName(super.getId()) + ": Not Equal to default value: " + defaultValue + "!!");

            invalidate();
            requestLayout();

            return false;
        }

        return true;
    }

}
