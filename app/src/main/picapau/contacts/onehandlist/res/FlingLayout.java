package picapau.contacts.onehandlist.res;

import picapau.contacts.onehandlist.Main;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;


public class FlingLayout extends LinearLayout {
    private int PAGE_WIDTH;
    private int MIN_FLING_MOVE;
    private int PAGES_NUM = 3;

//    private boolean mIsFlingMode = true;
    private GestureDetector mDetector;
    private Scroller mScroller;
    
    public Main main;
    
    public int GetPageWidth()
    {
    	return PAGE_WIDTH;
    }

    public FlingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mDetector = new GestureDetector(getContext(), new GestureListener());
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
    }

    // 画面サイズの初期化
    public void resetPosition()
    {
        // 本体の画面サイズを取得する
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        
        // フリックに使用する画面の幅を定義する
        PAGE_WIDTH = disp.getWidth();
        
        // フリックとして認識される最小の長さを定義する
        MIN_FLING_MOVE = PAGE_WIDTH / 8;
        
        // フリックさせる全体のコントロールの幅をセット
        setLayoutParams(new FrameLayout.LayoutParams(PAGE_WIDTH * PAGES_NUM,getLayoutParams().height));
                
        // 真ん中のページにスクロール。
        scrollTo(PAGE_WIDTH, 0);
        
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // まずGestureDetectorに処理させる
        mDetector.onTouchEvent(event);

        // 画面からタッチを離した
        if ( event.getAction() == MotionEvent.ACTION_UP )
        {
        	// 必ず真ん中の画面に戻す
            int currentX = getScrollX();
            int targetX = PAGE_WIDTH;
        	//scrollTo(PAGE_WIDTH, 0);
/*        	
            // 近い方の境界へ自動スクロール。
            int currentX = getScrollX();
            int targetX = 0; // スクロール先。
            if ( currentX % PAGE_WIDTH < PAGE_WIDTH / 2 ) {
                targetX = currentX / PAGE_WIDTH * PAGE_WIDTH;
            } else {
                targetX = (currentX / PAGE_WIDTH + 1) * PAGE_WIDTH;
                targetX = Math.min(targetX, PAGE_WIDTH * (PAGES_NUM - 1));
            }
*/
            mScroller.startScroll(currentX, 0, targetX - currentX, 0);

            
            
            // 再描画を促す。
            // これによりView#computeScroll()がコールバックされ、
            // その中でView#scrollTo()することにより再び再描画がかかる。
            // この再描画連鎖が、Scroller#computeScrollOffset()がfalseを返すまで続く。
            invalidate();            
            return true;
        }
        // true固定だと何か意味ある？
        return true;
    }

    // invalidateの時にコールバックされる関数
    @Override
    public void computeScroll()
    {
        if ( mScroller.computeScrollOffset() ) { // まだ自動スクロール中?
            // スクロール位置を更新。
            // scrollTo()内でinvalidateされるので、
            // 明示的にinvalidate()を呼ぶ必要はない。
            scrollTo(mScroller.getCurrX(), 0);
        }
    }

    /*
     * GestureDetectorが検出したジェスチャを受け取るリスナ。
     */
    private class GestureListener extends SimpleOnGestureListener
    {

    	// タッチ検知
        @Override
        public boolean onDown(MotionEvent e)
        {
            if ( !mScroller.isFinished() ) { // 自動スクロール中?
                mScroller.forceFinished(true);
                return true;
            }

            return true;
        }

        
        // フリック検知
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            // 横方向のflingのみ対応。
            int dx = (int) (e2.getX() - e1.getX()); // 横方向の移動距離。

        	// 必ず真ん中の画面に戻す
            int currentX = getScrollX();
            int targetX = PAGE_WIDTH; // スクロール先。
            mScroller.startScroll(currentX, 0, targetX - currentX, 0);
            
            // 最小限の長さを満たしていない or 縦方向(Y)の方が横方向(X)よりもフリックが強い場合
            if ( Math.abs(dx) < MIN_FLING_MOVE || Math.abs(velocityX) < Math.abs(velocityY) )
            {
            	// 単なるフリックとして判定、先の処理には進まない
                super.onFling(e1, e2, velocityX, velocityY);
                return false;
            }

/*
            // スクロール先を決める。
            int currentX = getScrollX();
            int targetX = 0;
            if ( velocityX > 0 ) { // 右へのfling?
                if ( currentX <= 0 ) return false;
                targetX = currentX / PAGE_WIDTH * PAGE_WIDTH;
            } else { // 左へのfling
                if ( currentX >= PAGE_WIDTH * (PAGES_NUM - 1) ) return false;
                targetX = PAGE_WIDTH * (currentX / PAGE_WIDTH + 1);
            }

            // 自動スクロール開始。
            // fling()なら、初速度が遅いと途中で止まる。
            // startScroll()なら、ページ境界まで行く。
            if ( mIsFlingMode ) {
                mScroller.fling(currentX, 0, -(int) velocityX, 0, Math.min(
                        currentX, targetX), Math.max(currentX, targetX), 0, 0);
            } else {
                mScroller.startScroll(currentX, 0,
                        targetX - currentX, 0);
            }
*/
            // 再描画を促す。
            // これによりView#computeScroll()がコールバックされ、
            // その中でView#scrollTo()することにより再び再描画がかかる。
            // この再描画連鎖が、Scroller#computeScrollOffset()がfalseを返すまで続く。
            invalidate();
            
            return true;
        }

        // スクロール検知
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            // ドラッグによるスクロール。
            scrollBy((int) distanceX, 0);
            return true;
        }

    }

}