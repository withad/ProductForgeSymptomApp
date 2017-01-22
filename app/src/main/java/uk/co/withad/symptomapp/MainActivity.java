package uk.co.withad.symptomapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener {

    private int currentImage = R.drawable.front;

    private Point currentPoint;
    private int currentPain;

    Button setPainButton;

    ImageView bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPainButton = (Button)findViewById(R.id.set_pain_button);

        bodyView = (ImageView)findViewById(R.id.body_view);
        bodyView.setOnTouchListener(this);
    }

    public void flipBody(View v) {
        if (currentImage == R.drawable.front) {
            currentImage = R.drawable.back;
        } else {
            currentImage = R.drawable.front;
        }

        updateImage();
    }

    public void setPain(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.pain_slider);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SeekBar pain_slider = (SeekBar)((Dialog)dialog).findViewById(R.id.pain_seekbar);
                currentPain = pain_slider.getProgress() + 1;
                storeCurrentData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void testButton(View v) {
        currentPain = 1;
        if (currentPoint != null) {
            storeCurrentData();
        }
    }

    private void storeCurrentData() {
        // Do the actual data storage here...
        float viewHeight = bodyView.getHeight();
        float viewWidth = bodyView.getWidth();

        float scaledX = currentPoint.x/viewWidth;
        float scaledY = currentPoint.y/viewHeight;

        ArrayList<Integer> hits = new ArrayList<>();
        boolean viewingFront = currentImage == R.drawable.front;
        String[] maps = new String[2];

        String mask_human_figure = "mask_human_figure_";
        String front = "front_";
        String back = "back_";

        String csv;

        if (viewingFront) {
            hits.addAll(checkMapForHits(mask_human_figure + front, 60, scaledX, scaledY));
            hits.addAll(checkMapForHits(mask_human_figure + front + "cortical_parc_", 45, scaledX, scaledY));
            csv = generateCSVLine(hits) + padCommas(58 + 34);
        } else {
            hits.addAll(checkMapForHits(mask_human_figure+back, 58, scaledX, scaledY));
            hits.addAll(checkMapForHits(mask_human_figure+back+"cortical_", 34, scaledX, scaledY));
            csv = padCommas(60 + 45) + generateCSVLine(hits);
        }

        //Log.d("SymptomApp", "Pain " + currentPain + " at " + currentPoint.toString() + ", scaled (" + scaledX + "), (" + scaledY + ")");
        Log.d("SymptomApp", "Hits: " + hits.toString());
        Log.d("SymptomApp", "Hits as CSV " + csv);

        File file = new File(getFilesDir(), "pain_data.csv");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(csv.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Log.d("SystemApp", "Failed to write file.");
        }

        // TODO: Erase current point after setting?
        currentPoint = null;
        updateImage();

        setPainButton.setEnabled(false);
    }

    private String padCommas(int n) {
        String commas = new String();
        for (int i = 0; i < n; i++) {
            commas += ",";
        }
        return commas;
    }

    private ArrayList<Integer> checkMapForHits(String maskPrefix, int noOfMasks, float xScale, float yScale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap mask;
        Point maskPoint;
        ArrayList<Integer> pixelValues = new ArrayList<>();
        ArrayList<Integer> hits = new ArrayList<Integer>();

        boolean hadAHit = false;

        for (int i = 1; i <= noOfMasks; i++) {
            int image_resource = getResources().getIdentifier(maskPrefix + i, "drawable", getApplicationContext().getPackageName());
            mask = BitmapFactory.decodeResource(getResources(), image_resource, options);
            maskPoint = new Point((int)(mask.getWidth() * xScale), (int)(mask.getHeight() * yScale));

            pixelValues.clear();
            for (Point p : surroundingPoints(maskPoint)) {
                pixelValues.add(mask.getPixel(p.x, p.y));
            }

            if (pixelValues.contains(-1)) {
                hits.add(currentPain);
                hadAHit = true;
            } else {
                hits.add(0);
            }
        }

        if (!hadAHit) {
            Log.d("SymptomApp", "No hits in " + maskPrefix);
        }

        return hits;
    }

    private ArrayList<Point> surroundingPoints(Point centre) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(centre.x - 1,  centre.y - 1));
        points.add(new Point(centre.x,      centre.y - 1));
        points.add(new Point(centre.x + 1,  centre.y - 1));
        points.add(new Point(centre.x - 1,  centre.y));
        points.add(centre);
        points.add(new Point(centre.x + 1,  centre.y));
        points.add(new Point(centre.x - 1,  centre.y + 1));
        points.add(new Point(centre.x,      centre.y + 1));
        points.add(new Point(centre.x + 1,  centre.y + 1));

        return points;
    }

    private String generateCSVLine(ArrayList<Integer> hitsList) {
        String csv = new String();

        for (Integer i : hitsList) {
            if (i != 0) {
                csv += i;
            }

            csv += ","; //TODO: Might be adding an extra comma at the end
        }

        return csv;
    }

    private void updateImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentImage, options);

        if (currentPoint != null) {
            float scale_x = bitmap.getWidth() / (float) bodyView.getWidth();
            float scale_y = bitmap.getHeight() / (float) bodyView.getHeight();

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAlpha(50);
            paint.setColor(Color.RED);
            canvas.drawCircle(currentPoint.x * scale_x, currentPoint.y * scale_y, 40, paint);
        }

        bodyView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            currentPoint = new Point((int)event.getX(), (int)event.getY());
            updateImage();

            if (currentPoint != null) {
                setPainButton.setEnabled(true);
            }
        }

        return true;
    }
}
