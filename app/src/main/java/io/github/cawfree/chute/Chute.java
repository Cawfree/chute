package io.github.cawfree.chute;

/**
 * Created by Alexander Thomas (@Cawfree) on 28/03/17.
 */

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.Map;

/** A background class for handling generic asynchronous tasks. */
public final class Chute {

    /** Guarantees execution along the Main (UI) Thread. */
    public static void assertMainThread() {
        // Aren't we calling from the Main Thread?
        if(Looper.getMainLooper() != Looper.myLooper()) {
            // Throw an Exception; indicate misuse of the Chute.
            throw new IllegalStateException("Must be called from the main mHandlerThread.");
        }
    }

    /* Static TAG Declaration. (Debugging) */
    private static final String TAG = Chute.class.getSimpleName();

    /* Member Variables. */
    private final Object                         mLock;
    private       HandlerThread                  mHandlerThread;
    private       Handler                        mHandler;
    private       Handler                        mResultHandler;
    private       boolean                        mRunning;

    /** Constructor. */
    public Chute() {
        // Assure execution on the UI Thread.
        Chute.assertMainThread();
        // Initialize Member Variables.
        this.mLock          = new Object();
        this.mRunning       = false;
        this.mResultHandler = null;
    }

    /** Starts the BackgroundThread. Provide a Handler Callback used for computationally intensive processing operations. */
    public void start(final Map<Integer, Handler.Callback> pRequestMap, final Map<Integer, Handler.Callback> pResponseMap) {
        // Assure execution on the UI Thread.
        Chute.assertMainThread();
        // Allocate the HandlerThread.
        this.mHandlerThread = new HandlerThread(TAG);
        // Start the HandlerThread.
        this.getHandlerThread().start();
        // Register the Handler which carries the local Callback implementation.
        this.mHandler = new Handler(this.getHandlerThread().getLooper(), new Handler.Callback() { @Override public final boolean handleMessage(final Message pMessage) {
            // Fetch the corresonding Request callback.
            final Handler.Callback lCallback = pRequestMap.get(pMessage.what);
            // Execute the Callback.
            return lCallback.handleMessage(pMessage);
        } });
        // Assign the ResultHandler, back on the UIThread.
        this.mResultHandler = new Handler(new Handler.Callback() { @Override public final boolean handleMessage(final Message pMessage) {
            // Fetch the corresonding Response callback.
            final Handler.Callback lCallback = pResponseMap.get(pMessage.what);
            // Execute the Callback.
            return lCallback.handleMessage(pMessage);
        } });
        // Assert that the Thread is running.
        this.mRunning = true;
    }

    /** Posts a message to be processed on the foreground.  */
    public final void foreground(final int pMessage, final Object pData) {
        // Launch the ResultHandler.
        this.getResultHandler().obtainMessage(pMessage, pData).sendToTarget();
    }

    /** Posts a Message to be processed on the Background. */
    public final void background(final int pMessage, final Object pData) {
        // Synchronize along the Lock.
        synchronized(this.getLock()) {
            // Ensure we're still running.
            if(this.isRunning()) {
                // Post the Message.
                this.getHandler().obtainMessage(pMessage, pData).sendToTarget();
            }
        }
    }

    /** Stops the BackgroundThread. */
    public void stop() {
        // Assure execution on the UI Thread.
        Chute.assertMainThread();
        // Synchroninze along the Lock.
        synchronized(this.getLock()) {
            // Assert that the Thread is no longer running.
            this.setRunning(false);
            // Remove any pending Callbacks/Messages.
            this.getHandler().removeCallbacksAndMessages(null);
            // Quit the HandlerThread.
            this.getHandlerThread().quit();
        }
    }

    private final HandlerThread getHandlerThread() {
        return this.mHandlerThread;
    }

    private final Handler getHandler() {
        return this.mHandler;
    }

    private final void setResultHandler(final Handler pHandler) {
        this.mResultHandler = pHandler;
    }

    public final Handler getResultHandler() {
        return this.mResultHandler;
    }

    private final void setRunning(final boolean pIsRunning) {
        this.mRunning = pIsRunning;
    }

    public final boolean isRunning() {
        return this.mRunning;
    }

    private final Object getLock() {
        return this.mLock;
    }

}
