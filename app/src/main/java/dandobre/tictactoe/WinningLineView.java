package dandobre.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class WinningLineView extends View {

    private final Paint paint;
    private float startX, startY, endX, endY;
    private boolean shouldDrawLine;

    public WinningLineView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        shouldDrawLine = false;
    }

    public void setLine(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        shouldDrawLine = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldDrawLine) {
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }
}
