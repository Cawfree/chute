package io.github.cawfree.chute;

import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /* Static TAG Declaration. (Debugging) */
    public static final String TAG = MainActivity.class.getSimpleName();

    /* Static Declarations. */
    private static final int ID_REQUEST_DECODE  = 0;
    private static final int ID_RESPONSE_DECODE = 1;
    private static final int DELAY_SLEEP_MS     = 10;

    /* Member Variables. */
    private Chute mChute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the ContentView.
        this.setContentView(R.layout.activity_main);
        // Allocate a Chute.
        this.mChute = new Chute();
    }

    @Override
    protected final void onResume() {
        // Handle as usual.
        super.onResume();
        // Start the Chute.
        this.getChute().start(
        /** Allocate the RequestMap. */
        new HashMap<Integer, Handler.Callback>() { {
            /** Computationally intensive processing. */
            this.put(MainActivity.ID_REQUEST_DECODE, new Handler.Callback() { @Override public final boolean handleMessage(final Message pMessage) {
                // Sleep a little.
                try { Thread.sleep(MainActivity.DELAY_SLEEP_MS); } catch (InterruptedException pInterruptedException) { pInterruptedException.printStackTrace(); }
                // Fetch the Integer.
                final Integer lInteger = (Integer)pMessage.obj;
                // Inform the user that we've launched a background task.
                Log.d(MainActivity.TAG, "Background task! [" + lInteger + "] (Looper: " + Looper.myLooper() + ")");
                // Launch a new foreground task.
                MainActivity.this.getChute().foreground(ID_RESPONSE_DECODE, lInteger);
                // Assert that the message was handled.
                return true;
            } });
        } },
        /** Allocate the ResponseMap. */
        new HashMap<Integer, Handler.Callback>() { {
            // Register responses.
            this.put(MainActivity.ID_RESPONSE_DECODE, new Handler.Callback() { @Override public final boolean handleMessage(final Message pMessage) {
                // Fetch the Integer.
                final Integer lInteger = (Integer)pMessage.obj;
                // Inform the user that we've launched a foreground task.
                Log.d(MainActivity.TAG, "Foreground task! [" + lInteger + "] (Looper: " + Looper.myLooper() + ")");
                // Let's request another execution. Increment the Integer so we can see distinct processes in the output.
                MainActivity.this.getChute().background(MainActivity.ID_REQUEST_DECODE, Integer.valueOf(lInteger + 1));
                // Assert that the message was handled.
                return true;
            } });
        } });
        // Start the Background Task.
        this.getChute().background(MainActivity.ID_REQUEST_DECODE, Integer.valueOf(0));
    }

    @Override
    protected final void onPause() {
        // Stop the chute.
        this.getChute().stop();
        // Handle as usual.
        super.onPause();
    }

    private final Chute getChute() {
        return this.mChute;
    }

}
