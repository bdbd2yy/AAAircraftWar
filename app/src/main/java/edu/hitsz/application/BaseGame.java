package edu.hitsz.application;

import android.content.Context;
import androidx.annotation.WorkerThread;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.hitsz.basic.AbstractFlyingObject;

/** Surface ownership and drawing live here; gameplay remains in GameEngine. */
public final class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "AircraftGame";
    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final RectF destination = new RectF();
    private final Object inputLock = new Object();
    private GameEngine game;
    private Thread gameThread;
    private volatile boolean running;
    private boolean resumed;
    private boolean surfaceReady;
    private boolean movePending;
    private boolean restartPending;
    private float touchX;
    private float touchY;
    private int pointerId = -1;
    private volatile Viewport viewport;

    public BaseGame(Context context) {
        super(context);
        ImageManager.initialize(context.getApplicationContext());
        game = new EasyGame();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        setContentDescription("飞机大战，拖动控制飞机，游戏结束后轻触重新开始");
    }

    // These lifecycle methods run on the main thread. Never join while holding a game-state lock.
    public void resumeGame() {
        resumed = true;
        startIfReady();
    }

    public void pauseGame() {
        resumed = false;
        stopThread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
        startIfReady();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        viewport = new Viewport(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        stopThread();
    }

    private void startIfReady() {
        if (resumed && surfaceReady && gameThread == null) {
            running = true;
            gameThread = new Thread(this, "aircraft-game-loop");
            gameThread.start();
        }
    }

    private void stopThread() {
        running = false;
        Thread thread = gameThread;
        if (thread == null) {
            return;
        }
        thread.interrupt();
        boolean interrupted = false;
        while (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
                interrupted = true;
            }
        }
        gameThread = null;
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        synchronized (inputLock) {
            movePending = false;
            restartPending = false;
        }
        pointerId = -1;
    }

    @Override
    @WorkerThread
    public void run() {
        Log.d(TAG, "Game thread started");
        try {
            while (running) {
                long start = SystemClock.uptimeMillis();
                consumeInput();
                game.update();
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        drawGame(canvas);
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                long remaining = GameConfig.STEP_MILLIS - (SystemClock.uptimeMillis() - start);
                if (remaining > 0) {
                    try {
                        Thread.sleep(remaining);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } finally {
            Log.d(TAG, "Game thread stopped");
        }
    }

    private void consumeInput() {
        synchronized (inputLock) {
            if (restartPending && game.isGameOver()) {
                game = new EasyGame();
            }
            restartPending = false;
            if (movePending && !game.isGameOver()) {
                int halfWidth = game.getHero().getWidth() / 2;
                int halfHeight = game.getHero().getHeight() / 2;
                game.getHero().setLocation(
                        Math.max(halfWidth, Math.min(GameConfig.WINDOW_WIDTH - halfWidth, touchX)),
                        Math.max(halfHeight, Math.min(GameConfig.WINDOW_HEIGHT - halfHeight, touchY)));
            }
            movePending = false;
        }
    }

    private void drawGame(Canvas canvas) {
        Viewport frame = new Viewport(canvas.getWidth(), canvas.getHeight());
        viewport = frame;
        canvas.drawColor(Color.BLACK);
        int saved = canvas.save();
        canvas.translate(frame.left, frame.top);
        canvas.scale(frame.scale, frame.scale);
        canvas.clipRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        int top = game.getBackgroundTop();
        destination.set(0, top - GameConfig.WINDOW_HEIGHT, GameConfig.WINDOW_WIDTH, top);
        canvas.drawBitmap(ImageManager.EASY_BACKGROUND_IMAGE, null, destination, paint);
        destination.offset(0, GameConfig.WINDOW_HEIGHT);
        canvas.drawBitmap(ImageManager.EASY_BACKGROUND_IMAGE, null, destination, paint);
        drawObjects(canvas, game.getEnemyBullets());
        drawObjects(canvas, game.getHeroBullets());
        drawObjects(canvas, game.getEnemies());
        drawObjects(canvas, game.getSupplies());
        if (!game.getHero().notValid()) {
            drawObject(canvas, game.getHero());
        }
        paint.setColor(Color.RED);
        paint.setTextSize(22);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("SCORE: " + game.getScore(), 12, 28, paint);
        canvas.drawText("LIFE: " + game.getHero().getHp(), 12, 55, paint);
        if (game.isGameOver()) {
            paint.setColor(0xBB000000);
            canvas.drawRect(0, 280, GameConfig.WINDOW_WIDTH, 440, paint);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(32);
            canvas.drawText("游戏结束", 256, 330, paint);
            paint.setTextSize(22);
            canvas.drawText("得分 " + game.getScore() + " · 轻触重新开始", 256, 390, paint);
        }
        canvas.restoreToCount(saved);
    }

    private void drawObjects(Canvas canvas, Iterable<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject object : objects) {
            if (!object.notValid()) {
                drawObject(canvas, object);
            }
        }
    }

    private void drawObject(Canvas canvas, AbstractFlyingObject object) {
        canvas.drawBitmap(ImageManager.get(object),
                object.getLocationX() - object.getWidth() / 2f,
                object.getLocationY() - object.getHeight() / 2f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Viewport frame = viewport;
        if (frame == null) {
            return true;
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            pointerId = event.getPointerId(0);
        }
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            int index = event.findPointerIndex(pointerId);
            if (index >= 0) {
                float x = (event.getX(index) - frame.left) / frame.scale;
                float y = (event.getY(index) - frame.top) / frame.scale;
                synchronized (inputLock) {
                    if (action != MotionEvent.ACTION_DOWN || (x >= 0 && x <= 512 && y >= 0 && y <= 768)) {
                        touchX = x;
                        touchY = y;
                        movePending = true;
                    }
                }
            }
        }
        if (action == MotionEvent.ACTION_UP) {
            performClick();
            pointerId = -1;
        } else if (action == MotionEvent.ACTION_CANCEL
                || (action == MotionEvent.ACTION_POINTER_UP
                && event.getPointerId(event.getActionIndex()) == pointerId)) {
            pointerId = -1;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        synchronized (inputLock) {
            restartPending = true;
        }
        return true;
    }

    private static final class Viewport {
        final float scale;
        final float left;
        final float top;

        Viewport(int width, int height) {
            scale = Math.max(0.001f, Math.min(width / 512f, height / 768f));
            left = (width - 512 * scale) / 2;
            top = (height - 768 * scale) / 2;
        }
    }
}
