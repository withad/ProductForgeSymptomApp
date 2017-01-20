package uk.co.withad.symptomapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener {

    private boolean submitEnabled = false;
    private int currentImage = R.drawable.front;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView body_view = (ImageView)findViewById(R.id.body_view);
        body_view.setOnTouchListener(this);
    }

    public void flipBody(View v) {
        if (currentImage == R.drawable.front) {
            currentImage = R.drawable.back;
        } else {
            currentImage = R.drawable.front;
        }

        ImageView body_view = (ImageView)findViewById(R.id.body_view);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentImage, options);

        body_view.setImageBitmap(bitmap);
    }

    public void setPain(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.pain_slider);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SeekBar pain_slider = (SeekBar)((Dialog)dialog).findViewById(R.id.pain_seekbar);
                int result = pain_slider.getProgress() + 1;
                Log.d("SymptomApp", Integer.toString(result));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void drawTouchPoint(float x, float y) {
        ImageView body_view = (ImageView)findViewById(R.id.body_view);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentImage, options);

        float scale_x = bitmap.getWidth() / (float)body_view.getWidth();
        float scale_y = bitmap.getHeight() / (float)body_view.getHeight();

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAlpha(50);
        paint.setColor(Color.RED);
        canvas.drawCircle(x * scale_x, y * scale_y, 40, paint);

        body_view.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int view_width = view.getWidth();
        int view_height = view.getHeight();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            //Log.d("SymptomApp", "X: " + event.getX() + ", Y: " + event.getY());
            //Log.d("SymptomApp", "Location X: " + x/view_width + ", Location Y: " + y/view_height);
            drawTouchPoint(x, y);

            if (!submitEnabled) {
                ((Button)findViewById(R.id.set_pain_button)).setEnabled(true);
                submitEnabled = true;
            }
        }

        return true;
    }
}
