package uk.co.withad.symptomapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    public void DrawCircle(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.body_view);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(100, 100, 10, paint);

        ImageView body_view = (ImageView)findViewById(R.id.body_view);
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
        }

        return true;
    }
}
