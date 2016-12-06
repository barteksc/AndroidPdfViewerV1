package com.github.barteksc.pdfviewer;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import static com.github.barteksc.pdfviewer.util.Constants.Pinch.MAXIMUM_ZOOM;
import static com.github.barteksc.pdfviewer.util.Constants.Pinch.MINIMUM_ZOOM;

/**
 * This Manager takes care of moving the PDFView,
 * set its zoom track user actions.
 */
class DragPinchManager implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private PDFView pdfView;
    private AnimationManager animationManager;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private float startDragX;
    private float startDragY;

    private boolean isSwipeEnabled;

    private boolean swipeVertical;

    private boolean scrolling = false;
    private boolean scaling = false;
    private boolean flingPageChange = false;

    private float maxFlingVelocity = 0;

    public DragPinchManager(PDFView pdfView, AnimationManager animationManager) {
        this.pdfView = pdfView;
        this.animationManager = animationManager;
        this.isSwipeEnabled = false;
        this.swipeVertical = pdfView.isSwipeVertical();
        gestureDetector = new GestureDetector(pdfView.getContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(pdfView.getContext(), this);
        maxFlingVelocity = ViewConfiguration.get(pdfView.getContext()).getScaledMaximumFlingVelocity();
        pdfView.setOnTouchListener(this);
    }

    public void enableDoubletap(boolean enableDoubletap) {
        if (enableDoubletap) {
            gestureDetector.setOnDoubleTapListener(this);
        } else {
            gestureDetector.setOnDoubleTapListener(null);
        }
    }

    public boolean isZooming() {
        return pdfView.isZooming();
    }

    private boolean isPageChange(float distance) {
        return Math.abs(distance) > Math.abs(pdfView.toCurrentScale(swipeVertical ? pdfView.getOptimalPageHeight() : pdfView.getOptimalPageWidth()) / 2);
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    public void setSwipeVertical(boolean swipeVertical) {
        this.swipeVertical = swipeVertical;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        pdfView.performClick();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (pdfView.getZoom() < pdfView.getMidZoom()) {
            pdfView.zoomWithAnimation(e.getX(), e.getY(), pdfView.getMidZoom());
        } else if (pdfView.getZoom() < pdfView.getMaxZoom()) {
            pdfView.zoomWithAnimation(e.getX(), e.getY(), pdfView.getMaxZoom());
        } else {
            pdfView.resetZoomWithAnimation();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        startDragX = e.getX();
        startDragY = e.getY();
        animationManager.stopFling();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrolling = true;
        if (isZooming() || isSwipeEnabled) {
            pdfView.moveRelativeTo(-distanceX, -distanceY);
        }

        return true;
    }

    public void onScrollEnd(MotionEvent event) {
        if (!isZooming() && !flingPageChange) {
            if (scaling) { //fix page position on screen after pinch zoom
                pdfView.showPage(pdfView.getCurrentPage());
                return;
            }
            if (isSwipeEnabled) {
                float distance;
                if (swipeVertical)
                    distance = event.getY() - startDragY;
                else
                    distance = event.getX() - startDragX;

                int diff = distance > 0 ? -1 : 1;

                if (isPageChange(distance)) {
                    pdfView.showPage(pdfView.getCurrentPage() + diff);
                } else {
                    pdfView.showPage(pdfView.getCurrentPage());
                }
            }
        } else {
            pdfView.loadPages();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (scaling) { //prevent fling after fast pinch
            return false;
        }

        if (!isZooming()) {
            int diff;
            if (swipeVertical) {
                float factor = velocityY / maxFlingVelocity;
                if (Math.abs(factor) < 0.1) { //cancel page change if too small velocity
                    return false;
                }
                diff = velocityY > 0 ? -1 : 1;
            } else {
                float factor = velocityX / maxFlingVelocity;
                if (Math.abs(factor) < 0.1) {
                    return false;
                }
                diff = velocityX > 0 ? -1 : 1;
            }

            flingPageChange = true;
            pdfView.showPage(pdfView.getCurrentPage() + diff);
        } else {
            int xOffset = (int) pdfView.getCurrentXOffset();
            int yOffset = (int) pdfView.getCurrentYOffset();
            animationManager.startFlingAnimation(xOffset,
                    yOffset, (int) (velocityX / 2),
                    (int) (velocityY / 2), xOffset * 2,
                    (int) (pdfView.toCurrentScale(pdfView.getOptimalPageWidth())),
                    yOffset * 2, (int) (pdfView.toCurrentScale(pdfView.getOptimalPageHeight())));
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scaling = true;
        float dr = detector.getScaleFactor();
        float wantedZoom = pdfView.getZoom() * dr;
        if (wantedZoom < MINIMUM_ZOOM) {
            dr = MINIMUM_ZOOM / pdfView.getZoom();
        } else if (wantedZoom > MAXIMUM_ZOOM) {
            dr = MAXIMUM_ZOOM / pdfView.getZoom();
        }
        pdfView.zoomCenteredRelativeTo(dr, new PointF(detector.getFocusX(), detector.getFocusY()));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        pdfView.loadPages();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (scrolling) {
                onScrollEnd(event);
            }
            scrolling = false;
            scaling = false;
            flingPageChange = false;
        }
        return retVal;
    }
}
