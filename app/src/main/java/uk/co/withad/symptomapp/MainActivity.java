package uk.co.withad.symptomapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements  View.OnTouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView body_view = (ImageView)findViewById(R.id.body_view);
        body_view.setOnTouchListener(this);
    }

    public void Flip(View v) {
        ImageView front = (ImageView) findViewById(R.id.body_view);
        ImageView back = (ImageView) findViewById(R.id.back_view);

        if (front.getVisibility() == View.VISIBLE) {
            back.setVisibility(View.VISIBLE);
            front.setVisibility(View.INVISIBLE);
        } else {
            back.setVisibility(View.INVISIBLE);
            front.setVisibility(View.VISIBLE);
        }
    }

    public void SetPain(View v) {
        /*Dialog yourDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.pain_slider, (ViewGroup)findViewById(R.id.pain_slider_root));
        yourDialog.setContentView(layout);*/

        //yourDialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.pain_slider);
        // Add the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void drawTouchPoint(float x, float y) {
        ImageView body_view = (ImageView)findViewById(R.id.body_view);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.front, options);

        float scale_x = bitmap.getWidth() / body_view.getWidth();
        float scale_y = bitmap.getHeight() / body_view.getHeight();

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAlpha(50);
        paint.setColor(Color.RED);
        canvas.drawCircle(x * scale_x, y * scale_y, 40, paint);

        body_view.setOnTouchListener(this);
        body_view.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int view_width = view.getWidth();
        int view_height = view.getHeight();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            Log.d("SymptomApp", "X: " + event.getX() + ", Y: " + event.getY());
            Log.d("SymptomApp", "Location X: " + x/view_width + ", Location Y: " + y/view_height);
            drawTouchPoint(x, y);
        }

        return true;
    }
}
