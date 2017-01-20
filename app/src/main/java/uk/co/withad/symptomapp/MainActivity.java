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

    private void storeCurrentData() {
        // Do the actual data storage here...
        float viewHeight = bodyView.getHeight();
        float viewWidth = bodyView.getWidth();

        float scaledX = currentPoint.x/viewWidth;
        float scaledY = currentPoint.y/viewHeight;

        //Log.d("SymptomApp", "X: " + event.getX() + ", Y: " + event.getY());
        //Log.d("SymptomApp", "Location X: " + x/view_width + ", Location Y: " + y/view_height);

        Log.d("SymptomApp", "Pain " + currentPain + " at " + currentPoint.toString() + ", scaled (" + scaledX + "), (" + scaledY + ")");

        currentPoint = null;
        updateImage();

        setPainButton.setEnabled(false);
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
