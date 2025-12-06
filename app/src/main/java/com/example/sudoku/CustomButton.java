package com.example.sudoku;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomButton extends FrameLayout
{
    int row;
    int col;
    int value;
    boolean isConflict = false;
    TextView textView;
    TableLayout memo;
    TextView[] memos = new TextView[9];

    public CustomButton(Context context, int row, int col)
    {
        super(context);
        this.row = row;
        this.col = col;
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setClickable(false);
        textView.setFocusable(false);
        addView(textView);
        setClickable(true);
        setBackgroundResource(R.drawable.button_selector);

        memo = (TableLayout) findViewById(R.id.tableLayout);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout memo = (TableLayout) layoutInflater.inflate(R.layout.layout_memo, null);
        addView(memo);


        for (int i = 0; i < 3; i++)
        {
            TableRow tableRow = (TableRow) memo.getChildAt(i);
            for (int j = 0; j < 3; j++)
            {
                memos[(i * 3) + j] = (TextView) tableRow.getChildAt(j);
            }
        }
    }

    public void set(int a)
    {
        this.value = a;
        if (a == 0)
        {
            textView.setText("");
        }
        else
        {
            textView.setText(String.valueOf(a));
        }
        setMemo(-1);
    }

    public void setMemo(int a)
    {
        if (a == -1)
        {
            for(int i = 0; i<9; i++)
                memos[i].setVisibility(INVISIBLE);
        }
        else
        {
            memos[a].setVisibility(VISIBLE);
        }
    }
}
