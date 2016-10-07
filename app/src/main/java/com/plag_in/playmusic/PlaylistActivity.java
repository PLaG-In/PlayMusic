package com.plag_in.playmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        int BOOKSHELF_ROWS = 5;
        int BOOKSHELF_COLUMNS = 5;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        for (int i = 0; i < BOOKSHELF_ROWS; i++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tableRow.setBackgroundResource(R.drawable.background);

            for (int j = 0; j < BOOKSHELF_COLUMNS; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.ic_song);

                tableRow.addView(imageView, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}
