package picapau.contacts.onehandlist.res;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class GestureListener extends SimpleOnGestureListener {

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
        float velocityX, float velocityY) {
        // 横方向のflingのみ対応。
        int dx = (int) (e2.getX() - e1.getX()); // 横方向の移動距離。

        // flingを行うのは、以下の全てが満たされるとき。
        // ・横方向に一定値以上移動した
        // ・縦方向の移動成分より横方向の移動成分の方が強い(速度で比較)
        if ( Math.abs(dx) < 200
                || Math.abs(velocityX) < Math.abs(velocityY) ) {
            super.onFling(e1, e2, velocityX, velocityY);
            return false;//★興味ないフリックイベントはスルー。
        }

        // スクロール処理。
        return true;
    }
}