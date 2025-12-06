package com.example.sudoku;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
{
    CustomButton[][] buttons;
    FrameLayout numpad, gameClear;
    int row = 0, column = 0;
    CustomButton clickedCustomButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnReset), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        numpad = (FrameLayout) findViewById(R.id.numpad);
        gameClear = (FrameLayout) findViewById(R.id.clearLayout);
        TableLayout table;
        table = (TableLayout) findViewById(R.id.tableLayout);
        buttons = new CustomButton[9][9];
        BoardGenerator board = new BoardGenerator();

        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                0, 1.0f);

        for (int i = 0; i < 9; i++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(tableRowParams);
            for (int j = 0; j < 9; j++)
            {
                buttons[i][j] = new CustomButton(this, i, j);

                buttons[i][j].setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        clickedCustomButton = (CustomButton) v;
                        numpad.setVisibility(View.VISIBLE);
                    }
                });

                buttons[i][j].setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        memoDialog(view);
                        return true;
                    }
                });

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        0, TableRow.LayoutParams.MATCH_PARENT,
                        1.0f);

                int leftMargin = (j % 3 == 0) ? 16 : 8;
                int topMargin = (i % 3 == 0) ? 16 : 8;
                int rightMargin = 8;
                int bottomMargin = 8;

                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                buttons[i][j].setLayoutParams(layoutParams);
                buttons[i][j].set(board.get(i, j));
                int num = (int) (Math.random() * 10) + 1;
                if (num > 7)
                {
                    buttons[i][j].set(0);
                }
                else
                {
                    buttons[i][j].setClickable(false);
                    buttons[i][j].setLongClickable(false);
                }

                tableRow.addView(buttons[i][j]);
            }

            table.addView(tableRow);
        }
    }

    public void memoDialog(View view)
    {
        CustomButton parentBtn = (CustomButton) view;
        View dialogView = (View) View.inflate(this, R.layout.dialog_memo, null);
        TableLayout memo = (TableLayout) dialogView.findViewById(R.id.dialogLayout);
        ToggleButton[] toggleButtons = new ToggleButton[9];

        for (int i = 0; i < 3; i++)
        {
            TableRow tableRow = (TableRow) memo.getChildAt(i);
            for (int j = 0; j < 3; j++)
            {
                toggleButtons[(i * 3) + j] = (ToggleButton) tableRow.getChildAt(j);
                toggleButtons[(i * 3) + j].setClickable(true);
                toggleButtons[(i * 3) + j].setFocusable(true);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Memo")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        boolean flag = true;
                        for (int k = 0; k < 9; k++)
                        {
                            if (toggleButtons[k].isChecked())
                            {
                                parentBtn.setMemo(k);
                                flag = false;
                            }
                        }
                        if (flag)
                        {
                            parentBtn.setMemo(-1);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("DELETE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        for (int k = 0; k < 9; k++)
                        {
                            toggleButtons[k].setChecked(false);
                        }
                        parentBtn.setMemo(-1);
                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setConflict(int row, int col)
    {
        int value = buttons[row][col].value;
        if (value == 0)
            return;

        for (int i = 0; i < 9; i++)
        {
            if (buttons[row][i].value == value && i != col)
            {
                buttons[row][i].setBackgroundResource(R.color.red);
                buttons[row][i].isConflict = true;
            }

            if (buttons[i][col].value == value && i != row)
            {
                buttons[i][col].setBackgroundResource(R.color.red);
                buttons[i][col].isConflict = true;
            }
        }

        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = boxRowStart; i < boxRowStart + 3; i++)
        {
            for (int j = boxColStart; j < boxColStart + 3; j++)
            {
                if (buttons[i][j].value == value && (i != row || j != col))
                {
                    buttons[i][j].setBackgroundResource(R.color.red);
                    buttons[i][j].isConflict = true;
                }
            }
        }
    }

    public void onclickNumpadBtn(View v)
    {
        if (((Button) v).getText().toString().equals("CANCEL"))
        {
            numpad.setVisibility(View.INVISIBLE);
            return;
        }
        else if (((Button) v).getText().toString().equals("DEL"))
            clickedCustomButton.set(0);
        else
            clickedCustomButton.set(Integer.parseInt(((Button) v).getText().toString()));

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                buttons[i][j].isConflict = false;
                buttons[i][j].setBackgroundResource(R.drawable.button_selector);
            }
        }

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                setConflict(i, j);
            }
        }

        boolean gameOver = true;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (buttons[i][j].value == 0 || buttons[i][j].isConflict)
                {
                    gameOver = false;
                    break;
                }
            }
        }

        if (gameOver)
        {
            gameClear.setVisibility(View.VISIBLE);
        }

        numpad.setVisibility(View.INVISIBLE);
    }
}