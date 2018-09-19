package kr.co.sology.fw.ctrl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;

import kr.co.sology.todayismyturn.R;

/**
 * 간단메모 관리자<br>
 * Simple Drawing Note.
 *
 */
@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
public class MemoCtrl extends View {


	private Paint      _paint;
	private int        intColor; // COLOR
	private int        intStrokeWidth     = 8;        // THICKNESS
	private boolean    isEraser           = false;    // ERASER MODE
	ArrayList<MemoViewPointEntity> _point = new ArrayList<>();
	
	/**
	 * Class to store pointer information when dragging
	 * 
	 */
	class MemoViewPointEntity {
		float x;
		float y;
		int color;
		int stroke;
		int alpha;
		boolean isErase;
		boolean isDraw;
		MemoViewPointEntity(float x, float y, boolean isEraseMode, boolean isDraw) {
			this.x       = x;
			this.y       = y;
			this.color   = intColor;
			this.stroke  = intStrokeWidth;
			this.alpha   = 255;
			this.isErase = isEraseMode;
			this.isDraw  = isDraw;
		}
	}
	
	
	/**
	 * 생성자
	 * 
	 * @param context context
	 */
	public MemoCtrl(Context context) {
		super(context);

		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		_paint = new Paint();
		_paint.setAntiAlias(true);
		_paint.setDither(true);
		_paint.setStyle(Paint.Style.FILL);
		_paint.setStrokeCap(Paint.Cap.ROUND);

		intColor = 0x000000;
		intColor = context.getResources().getColor(R.color.memo_tool_background); // default pen color
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		/* Touch Event Listener */
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE :
					_point.add(new MemoViewPointEntity(event.getX(), event.getY(), getEraser(), true));
					invalidate();
					break;
				case MotionEvent.ACTION_UP :
				case MotionEvent.ACTION_DOWN :
					_point.add(new MemoViewPointEntity(event.getX(), event.getY(), getEraser(), false));
					break;
				}
				return true;
			}
		});
	}

	/**
	 * 그리기 이벤트를 수신한다<br>
	 * Drawing Event.
	 *
	 * @param canvas canvas to draw
     */
	@Override
	protected void onDraw(Canvas canvas) {
		for (int i=1; i<_point.size(); i++) {
			if (!_point.get(i).isDraw) continue;
			if (_point.get(i).isErase) {
				_paint.setStrokeWidth(60);
				_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			}
			else {
				_paint.setColor(_point.get(i).color);
				_paint.setStrokeWidth(_point.get(i).stroke);
				_paint.setAlpha(_point.get(i).alpha);
				_paint.setXfermode(null);
			}
			canvas.drawLine(_point.get(i-1).x, _point.get(i-1).y, _point.get(i).x, _point.get(i).y, _paint);
		}
		super.onDraw(canvas);
	}
	
	
	//////////////////////////////////////////////////////////////
	//
	// GETTER & SETTER
	//
	//////////////////////////////////////////////////////////////

	/**
	 * 펜 색상 설정<br>
	 * set pen color.
	 *
	 * @param color color
     */
	public void setPenColor(int color) {
		intColor = color;
	}

	/**
	 * 펜 굵기를 설정<br>
	 * set stroke
	 *
	 * @param size stroke thickness
     */
	public void setPenStrokeThick(int size) {
		intStrokeWidth = size;
	}

	/**
	 * 현재의 펜 굵기<br>
	 * get stroke
	 *
	 * @return stroke thickness
     */
	public int getPenStrokeThick() {
		return intStrokeWidth;
	}

	/**
	 * 지우개 모드 활성화/비활성화<br>
	 * set eraser mode or not
	 *
	 * @param isErase eraser mode or not
     */
	public void setEraser(boolean isErase) {
		isEraser = isErase;
	}

	/**
	 * 현재 지우개 모드인지를 알려준다<br>
	 * get eraser mode or not
	 *
	 * @return eraser mode or not
     */
	public boolean getEraser() {
		return isEraser;
	}
	
}
