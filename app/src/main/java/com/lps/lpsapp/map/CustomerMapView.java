package com.lps.lpsapp.map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lps.core.gui.ScalableView;
import com.lps.core.gui.ScaleGestureDetectorCompat;
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.activities.ActorsActivity;
import com.lps.lpsapp.activities.BookingActivity;
import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.positions.BeaconData;
import com.lps.lpsapp.viewModel.booking.TableState;
import com.lps.lpsapp.viewModel.booking.TableStateEnum;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.lpsapp.viewModel.rooms.RoomModel;
import com.lps.lpsapp.viewModel.rooms.Table;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dle on 31.07.2015.
 */
public class CustomerMapView extends ScalableView  {
    private static String TAG = "CustomerMapView";
    // Attributes
    private int mWandColor = Color.BLACK;
    private float mExampleDimension = 0;
    private Drawable mBackground;
    private TextPaint mTextPaint;
    private float mPaintStrokeWidth;
    private Paint mPaint;
    private Paint mCPaint;
    private List<Actor> actors;
    private RoomModel mRoomModel;

    private List<BeaconData> beaconDatas;
    private Rect calculationResult;

    public CustomerMapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
        init(attrs, 0);
        ScaleGestureDetectorCompat.synchronizeScale = true;
        actors = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG,"onSizeChanged");
    }

    public void setmRoomModel(RoomModel roomModel)
    {
        this.mRoomModel = roomModel;
        String imageDataBytes = roomModel.backgroungImage.substring(roomModel.backgroungImage.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        mBackground = new BitmapDrawable(getResources(), bitmap);
        this.CreateMapObjects();
        this.invalidate();
    }

    public boolean hasRoomModel()
    {
        if(this.mRoomModel == null)
        {
            return false;
        }

        return true;
    }

    @Override
    public void DrawOnScaledCanvas(Canvas canvas)
    {
        super.DrawOnScaledCanvas(canvas);

        if (mBackground != null) {
            //mBackground.setBounds(this.mContentRect.left, this.mContentRect.top, this.mContentRect.right, this.mContentRect.bottom);
            //mBackground.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minChartSize = getResources().getDimensionPixelSize(R.dimen.min_chart_size);
        this.setMinChartSize(minChartSize, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void DrawOnUnscaledCanvas(Canvas canvas)
    {
        super.DrawOnUnscaledCanvas(canvas);
        if(mRoomModel == null)
        {
            return;
        }

        if(mBackground != null) {
            mBackground.setBounds((int) this.getDrawX(0) , (int) this.getDrawY(0), (int) this.getDrawX(mRoomModel.wight), (int) this.getDrawY(mRoomModel.height));
            mBackground.draw(canvas);
        }


//        float width = this.getDrawX(0.1f) - this.getDrawX(0f);
//        mPaint.setStrokeWidth(width);
//        Path path = new Path();
//        for(int i = 0; i < mRoomModel.border.size();i++)
//        {
//            Point point = this.mRoomModel.border.get(i);
//            if(i == 0)
//            {
//                path.moveTo(this.getDrawX( point.x),this.getDrawY(point.y));
//            }
//            else
//            {
//                path.lineTo(this.getDrawX(point.x), this.getDrawY(point.y));
//            }
//        }
//        path.close();
//        canvas.clipPath(path, Region.Op.DIFFERENCE);
//        canvas.drawColor(Color.BLACK);


        //mBackground.draw(canvas);

        if(SettingsActivity.ShowCircles && this.beaconDatas != null && this.calculationResult != null)
        {
            canvas.save();
            for(int i = 0; i < 3; i++) {
                BeaconData beaconData = this.beaconDatas.get(i);
                Path path = new Path();
                path.addCircle(this.getDrawX(beaconData.x), this.getDrawY(beaconData.y), (float) this.getDrawX((float) beaconData.getFactoredDistance()) - this.getDrawX(0), Path.Direction.CW);
                path.close();

                canvas.clipPath(path, Region.Op.INTERSECT);


            }
            canvas.drawColor(Color.GREEN);
            canvas.restore();
        }



        //canvas.clipPath(path);
        //canvas.drawColor(Color.MAGENTA);

        this.setLayoutForMapObjects();


        /*Point lastPoint = null;
        for(int i = 0; i < mRoomModel.border.size();i++)
        {
            Point point = this.mRoomModel.border.get(i);
            if(i == 0)
            {
                lastPoint = point;
                continue;
            }
            else
            {
                canvas.drawLine(this.getDrawX(point.x), this.getDrawY(point.y), this.getDrawX(lastPoint.x), this.getDrawY(lastPoint.y), mPaint);
                lastPoint = point;
            }

            if(i == mRoomModel.border.size() - 1)
            {
                canvas.drawLine(this.getDrawX(lastPoint.x), this.getDrawY(lastPoint.y), this.getDrawX(this.mRoomModel.border.get(0).x), this.getDrawY(this.mRoomModel.border.get(0).y), mPaint);
            }
        }

        for(int i = 1; i < mRoomModel.wight;i++) {
            canvas.drawLine(this.getDrawX(0), this.getDrawY(i), this.getDrawX(mRoomModel.height), this.getDrawY(i), mCPaint);
        }
        for(int j = 1; j < mRoomModel.height;j++) {
            canvas.drawLine(this.getDrawX(j), this.getDrawY(0), this.getDrawX(j), this.getDrawY(mRoomModel.wight), mCPaint);
        }*/

    }

    @Override
    protected float getDrawX(float x) {
        float newWight =  this.mRoomModel.wight;
        float xOffset = 0;
        if((float)this.mContentRect.height() / (float)this.mContentRect.width() < this.mRoomModel.height / this.mRoomModel.wight)
        {
            newWight =  this.mRoomModel.height * this.mContentRect.width() / this.mContentRect.height();
            xOffset = (newWight - this.mRoomModel.wight) / 2;
        }
        float x1 =  ((x + xOffset) / newWight) * 2f - 1;
        return super.getDrawX(x1);
    }

    @Override
    protected float getDrawY(float y) {

        float newHight =  this.mRoomModel.height;
        float yOffset = 0;
        if((float)this.mContentRect.height() / (float)this.mContentRect.width() > this.mRoomModel.height / this.mRoomModel.wight)
        {
            newHight =  this.mRoomModel.wight * this.mContentRect.height() / this.mContentRect.width();
            yOffset = (newHight - this.mRoomModel.height) / 2;
        }
        float y1 =  (newHight - y - yOffset) * 2f / newHight - 1;
        return super.getDrawY(y1);
    }

//    @Override
//    protected float getDrawWidth(float width) {
//        return super.getDrawWidth(width * mContentRect.width() / this.mRoomModel.wight);
//    }
//
//    @Override
//    protected float getDrawHeight(float height) {
//        return super.getDrawHeight(height * mContentRect.height() / this.mRoomModel.height);
//    }

    public void setBooking(List<TableState> model)
    {
        final BookingActivity activity = (BookingActivity)((ContextThemeWrapper)this.getContext()).getBaseContext();
        for (TableState state:model) {
            for (final Table table:this.mRoomModel.tables) {
                if(table.id.equals(state.tableId))
                {
                    table.setBookingState(state);
                    if(state.getTableState() == TableStateEnum.Free || state.getTableState() == TableStateEnum.BookedForMe || state.getTableState() == TableStateEnum.Waiting)
                    {
                        table.guiElement.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Boolean selected = table.getSelected();
                                table.setSelected(!selected);
                                activity.validateBooking();
                            }
                        });

                    }
                    break;
                }
            }
        }

    }

    public List<Table> getSelectedTables()
    {
        List<Table> selected = new ArrayList<>();
        for (Table table:this.mRoomModel.tables) {
            if(table.getSelected())
            {
                selected.add(table);
            }
        }
        return selected;
    }

    public void clearSelectedTables()
    {
        for (Table table:this.mRoomModel.tables) {
            table.setSelected(false);
        }
    }



    private void CreateMapObjects()
    {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(Table table: mRoomModel.tables) {
            View view = null;
            if(table.type.equals("Table1")) {
                view = inflater.inflate(R.layout.layout_table1, null);
            } else if(table.type.equals("Table2")) {
                view = inflater.inflate(R.layout.layout_table2, null);
            } else if(table.type.equals("Table3")) {
                view = inflater.inflate(R.layout.layout_table3, null);
            } else if(table.type.equals("Table4")) {
                view = inflater.inflate(R.layout.layout_table4, null);
            } else {
                view = inflater.inflate(R.layout.layout_table4, null);
            }

            table.guiElement = (ImageView)view;
            this.addView(view);

            if(table.angle != 0.0) {
                if(table.type.equals("Table1")) {
                    view.setPivotY(0);
                    view.setPivotX(0);
                } else if(table.type.equals("Table2")) {
                    view.setPivotY(0);
                    view.setPivotX(0);
                } else if(table.type.equals("Table3")) {
                    view.setPivotY(0);
                    view.setPivotX(0);
                } else if(table.type.equals("Table4")) {
                    view.setPivotY(0);
                    view.setPivotX(0);
                }

                view.setRotation(Math.round(table.angle));
            }

            view.setLayoutParams(this.applayTableLayoutParams((FrameLayout.LayoutParams)view.getLayoutParams(),table));

            //TextView text = new TextView(getContext());
            //text.setText(table.description);

        }
    }

    public void addActor(Actor actor)
    {
        ActorsActivity host = (ActorsActivity)((ContextThemeWrapper)this.getContext()).getBaseContext();
        this.actors.add(actor);
        GuiDevice myButton = new GuiDevice(getContext(),actor.position);

        if(actor.position.deviceId.equals(((LpsApplication)this.getContext().getApplicationContext()).getAndroidId())) {
            myButton.setBackground(getResources().getDrawable(R.drawable.round_button_yellow));
        }
        else
        {
            myButton.setBackground(getResources().getDrawable(R.drawable.round_button_blau));
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)actor.position.guiElement.wight,(int) actor.position.guiElement.height);
        lp.leftMargin = (int)this.getDrawX((float)actor.position.x);
        lp.topMargin = (int)(this.getDrawY((float)actor.position.y) - actor.position.guiElement.height);
        myButton.setOnLongClickListener(host);
        this.addView(myButton, lp);
    }


    private void setLayoutForMapObjects()
    {
        for (Table table:this.mRoomModel.tables) {
            table.guiElement.setLayoutParams(this.applayTableLayoutParams((FrameLayout.LayoutParams)table.guiElement.getLayoutParams(),table));
        }

        for (Actor actor:this.actors) {
            float width = this.getDrawX((float)(actor.position.x + actor.position.guiElement.wight)) - this.getDrawX((float)actor.position.x);
            float hight = this.getDrawY((float)(actor.position.y + actor.position.guiElement.height)) - this.getDrawY((float)actor.position.y);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)width,(int)hight);
            lp.leftMargin = (int)this.getDrawX((float) actor.position.x);
            lp.topMargin = Math.round(this.getDrawY((float)actor.position.y));
            actor.position.guiElement.setLayoutParams(lp);
            //Log.d(TAG, "Actor Layout width:" + lp.width + "height:" + lp.height + "leftMargin:" + lp.leftMargin + "topMargin" + lp.topMargin);
        }
    }

    public void setCalculationResult(List<BeaconData> beaconDatas, Rect bounds)
    {
        this.beaconDatas = beaconDatas;
        this.calculationResult = bounds;
        this.invalidate();
    }

    private FrameLayout.LayoutParams applayTableLayoutParams(FrameLayout.LayoutParams param,Table table)
    {
        param.width = Math.round(this.getDrawX(Math.round(table.x + table.wight)) - this.getDrawX(Math.round(table.x)));
        param.height = Math.round(this.getDrawY(Math.round(table.y + table.height)) - this.getDrawY(Math.round(table.y)));
        param.leftMargin = Math.round(this.getDrawX(Math.round(table.x)));
        param.topMargin = Math.round(this.getDrawY(Math.round(table.y)));
        return param;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomerMapView, defStyle, 0);

        mWandColor = a.getColor(
                R.styleable.CustomerMapView_wandColor,
                mWandColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.CustomerMapView_mapViewDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.CustomerMapView_mapViewDrawable)) {
            mBackground = a.getDrawable(
                    R.styleable.CustomerMapView_mapViewDrawable);
            mBackground.setCallback(this);
        }
        mBackground = this.getBackground();

        mPaintStrokeWidth = a.getFloat(R.styleable.CustomerMapView_wandWidth, mPaintStrokeWidth);


        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        mPaint = new Paint();
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        mPaint.setColor(mWandColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        mCPaint = new Paint();
        mCPaint.setStrokeWidth(3);
        mCPaint.setColor(Color.BLACK);
        mCPaint.setStyle(Paint.Style.STROKE);
        mCPaint.setAntiAlias(true);

        a.recycle();

    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mWandColor);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //mTextHeight = fontMetrics.bottom;
    }

    public void positionChanged(DevicePosition position) {
        boolean exists = false;
        for (Actor actor:this.actors) {
            if(actor.position.deviceId.equals(position.deviceId))
            {
                actor.position.x = position.x;
                actor.position.y = position.y;
                exists = true;
                break;
            }
        }

        if(!exists)
        {
            DevicePosition pos = new DevicePosition();
            pos.deviceId = position.deviceId;
            pos.x = position.x;
            pos.y = position.y;

            Actor actor = new Actor();
            actor.position = position;

            this.addActor(actor);
        }
        this.invalidate();
    }
}
