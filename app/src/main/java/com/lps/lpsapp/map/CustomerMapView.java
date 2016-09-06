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
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;

import com.lps.core.gui.ScalableView;
import com.lps.core.gui.ScaleGestureDetectorCompat;
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.activities.ActorsActivity;
import com.lps.lpsapp.activities.BookingActivity;
import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.positions.RangedBeacon;
import com.lps.lpsapp.viewModel.booking.TableState;
import com.lps.lpsapp.viewModel.booking.TableStateEnum;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.lpsapp.viewModel.rooms.RoomModel;
import com.lps.lpsapp.viewModel.rooms.Table;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dle on 31.07.2015.
 */
public class CustomerMapView extends ScalableView {
    private static String TAG = "CustomerMapView";
    // Attributes
    private int mWandColor = Color.BLACK;
    private float mExampleDimension = 0;
    private Drawable mBackground;
    private TextPaint mTextPaint;
    private float mPaintStrokeWidth;
    private Paint mPaint;
    private Paint mCPaint;
    private HashMap<String, Actor> actors;
    private RoomModel mRoomModel;

    private List<RangedBeacon> beaconDatas;
    private Rect calculationResult;

    public CustomerMapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
        init(attrs, 0);
        ScaleGestureDetectorCompat.synchronizeScale = true;
        actors = new HashMap<>();


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setmRoomModel(RoomModel roomModel) {
        this.mRoomModel = roomModel;
        String imageDataBytes = roomModel.backgroungImage.substring(roomModel.backgroungImage.indexOf(",") + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        mBackground = new BitmapDrawable(getResources(), bitmap);
        this.CreateMapObjects();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public boolean hasRoomModel() {
        if (this.mRoomModel == null) {
            return false;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minChartSize = getResources().getDimensionPixelSize(R.dimen.min_chart_size);
        this.setMinChartSize(minChartSize, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void DrawOnUnscaledCanvas(Canvas canvas) {
        super.DrawOnUnscaledCanvas(canvas);
        if (mRoomModel == null) {
            return;
        }


        if (mBackground != null) {
            mBackground.setBounds((int) this.getDrawX(0), (int) this.getDrawY(0), (int) this.getDrawX(mRoomModel.wight), (int) this.getDrawY(mRoomModel.height));
            mBackground.draw(canvas);
        }

        if (SettingsActivity.ShowCircles && this.beaconDatas != null && this.calculationResult != null) {
            canvas.save();
            canvas.clipPath(path, Region.Op.INTERSECT);
//            for (int i = 0; i < 3; i++) {
//                RangedBeacon beaconData = this.beaconDatas.get(i);
//                Path path = new Path();
//                path.addCircle(this.getDrawX(beaconData.x), this.getDrawY(beaconData.y), (float) this.getDrawX((float) beaconData.getFactoredDistance()) - this.getDrawX(0), Path.Direction.CW);
//                path.close();
//
//                canvas.clipPath(path, Region.Op.INTERSECT);
//
//
//            }
            canvas.drawColor(Color.GREEN);
            canvas.restore();
        }



    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.setLayoutForMapObjects();
        super.onDraw(canvas);
    }

    @Override
    protected float getDrawX(float x) {
        float newWight = this.mRoomModel.wight;
        float xOffset = 0;
        if ((float) this.mContentRect.height() / (float) this.mContentRect.width() < this.mRoomModel.height / this.mRoomModel.wight) {
            newWight = this.mRoomModel.height * (float)this.mContentRect.width() / (float)this.mContentRect.height();
            xOffset = (newWight - this.mRoomModel.wight) / 2f;
        }
        float x1 = ((x + xOffset) / newWight) * 2f - 1f;
        return super.getDrawX(x1);
    }


    @Override
    protected float getDrawY(float y) {
        float newHight = this.mRoomModel.height;
        float yOffset = 0;
        if ((float) this.mContentRect.height() / (float) this.mContentRect.width() > this.mRoomModel.height / this.mRoomModel.wight) {
            newHight = this.mRoomModel.wight * (float)this.mContentRect.height() / (float)this.mContentRect.width();
            yOffset = (newHight - this.mRoomModel.height) / 2f;
        }
        float y1 = (newHight - y - yOffset) * 2f / newHight - 1f;
        return super.getDrawY(y1);
    }

    public void setBooking(List<TableState> model) {
        final BookingActivity activity = (BookingActivity) ((ContextThemeWrapper) this.getContext()).getBaseContext();
        for (TableState state : model) {
            for (final Table table : this.mRoomModel.tables) {
                if (table.id.equals(state.tableId)) {
                    table.guiElement.setState(state);
                    if (state.getTableState() == TableStateEnum.Free || state.getTableState() == TableStateEnum.BookedForMe || state.getTableState() == TableStateEnum.Waiting) {
                        table.guiElement.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                table.guiElement.setSelected(!table.guiElement.isSelected());
                                activity.validateBooking();
                            }
                        });

                    }
                    break;
                }
            }
        }

    }

    public List<Table> getSelectedTables() {
        List<Table> selected = new ArrayList<>();
        for (Table table : this.mRoomModel.tables) {
            if (table.guiElement.isSelected()) {
                selected.add(table);
            }
        }
        return selected;
    }

    public void clearSelectedTables() {
        for (Table table : this.mRoomModel.tables) {
            table.guiElement.setSelected(false);
        }
    }


    private void CreateMapObjects() {
        for (Table table : mRoomModel.tables) {
            GuiTable view = null;
            if (table.type.equals("Table1")) {
                view = new GuiTable(getContext(), 1);
            } else if (table.type.equals("Table2")) {
                view = new GuiTable(getContext(), 2);
            } else if (table.type.equals("Table3")) {
                view = new GuiTable(getContext(), 3);
            } else if (table.type.equals("Table4")) {
                view = new GuiTable(getContext(), 4);
            } else {
                view = new GuiTable(getContext(), 1);
            }

            table.guiElement = view;
            view.setText(table.description);
            this.addView(view);

            if (table.angle != 0.0) {
                view.setPivotY(0);
                view.setPivotX(0);
                view.setRotation(Math.round(table.angle));

            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) table.wight, (int) table.height);
            view.setLayoutParams(lp);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void clearActors() {
        for (Actor actor : this.actors.values()) {
            this.removeView(actor.guiElement);
        }
        this.actors.clear();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void removeActor(String deviceId) {
        if (this.actors.containsKey(deviceId)) {
            Actor actor = this.actors.get(deviceId);
            this.removeView(actor.guiElement);
            this.actors.remove(deviceId);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void addActor(Actor actor) {
        if(this.actors.containsKey(actor.position.deviceId)) {
            return;
        }
        ActorsActivity host = (ActorsActivity) ((ContextThemeWrapper) this.getContext()).getBaseContext();
        this.actors.put(actor.position.deviceId, actor);
        GuiDevice myButton;
        if (actor.position.deviceId.equals(((LpsApplication) this.getContext().getApplicationContext()).getAndroidId())) {
            myButton = new GuiDevice(getContext(), actor.position.deviceId, true);
        } else {
            myButton = new GuiDevice(getContext(), actor.position.deviceId, false);
        }
        myButton.setText(actor.userName);
        actor.guiElement = myButton;

        int wight = Math.round(this.getDrawX(Math.round(actor.position.x + actor.wight)) - this.getDrawX(Math.round(actor.position.x)));
        int height = Math.round(this.getDrawY(Math.round(actor.position.y + actor.height)) - this.getDrawY(Math.round(actor.position.y)));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(wight, height);
        actor.guiElement.setLayoutParams(lp);
        actor.guiElement.setX(Math.round(this.getDrawX(Math.round(actor.position.x))));
        actor.guiElement.setY(Math.round(this.getDrawY(Math.round(actor.position.y))));
        myButton.setOnClickListener(host);

        this.addView(myButton);
        ViewCompat.postInvalidateOnAnimation(this);
    }


    private void setLayoutForMapObjects() {
        if(!this.hasRoomModel()) {
            return;
        }

        for (Table table : this.mRoomModel.tables) {
            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)table.guiElement.getLayoutParams();
            param.width = Math.round(this.getDrawX(Math.round(table.x + table.wight)) - this.getDrawX(Math.round(table.x)));
            param.height = Math.round(this.getDrawY(Math.round(table.y + table.height)) - this.getDrawY(Math.round(table.y)));
            table.guiElement.setLayoutParams(param);
            table.guiElement.setX(Math.round(this.getDrawX(Math.round(table.x))));
            table.guiElement.setY(Math.round(this.getDrawY(Math.round(table.y))));
        }

        for (Actor actor : this.actors.values()) {
            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)actor.guiElement.getLayoutParams();
            param.width = Math.round(this.getDrawX(Math.round(actor.position.x + actor.wight)) - this.getDrawX(actor.position.x));
            param.height = Math.round(this.getDrawY(Math.round(actor.position.y + actor.height)) - this.getDrawY(actor.position.y));
            actor.guiElement.setLayoutParams(param);
            actor.guiElement.setX(this.getDrawX(Math.round(actor.position.x)));
            actor.guiElement.setY(this.getDrawY(Math.round(actor.position.y)));


        }
    }

    private Path path;
    public void setCalculationResult(List<RangedBeacon> beaconDatas, Rect bounds, Path path) {
        this.beaconDatas = beaconDatas;
        this.calculationResult = bounds;
        this.path = path;
        ViewCompat.postInvalidateOnAnimation(this);
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

//        if (a.hasValue(R.styleable.CustomerMapView_mapViewDrawable)) {
//            mBackground = a.getDrawable(
//                    R.styleable.CustomerMapView_mapViewDrawable);
//            mBackground.setCallback(this);
//        }
//        mBackground = this.getBackground();

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

    }

    public Actor findActorByDeviceId(String deviceId) {
        for (Actor actor : this.actors.values()) {
            if (actor.position.deviceId.equals(deviceId)) {
                return actor;
            }
        }

        return null;
    }

    public void positionChanged(final DevicePosition position,final long interval) {
        if(!this.hasRoomModel()) {
            return;
        }

        if (this.actors.containsKey(position.deviceId)) {
            final Actor actor = this.actors.get(position.deviceId);
            actor.setPosition(position.x,position.y,interval);
        } else if (position.deviceId.equals("xxx")){
            DevicePosition pos = new DevicePosition();
            pos.deviceId = position.deviceId;
            pos.x = position.x;
            pos.y = position.y;

            Actor actor = new Actor();
            actor.position = position;

            this.addActor(actor);

        }

    }
}
