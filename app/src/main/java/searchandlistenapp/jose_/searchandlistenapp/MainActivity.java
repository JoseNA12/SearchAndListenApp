package searchandlistenapp.jose_.searchandlistenapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;
// josuarez2297@gmail.com   86471415JSC

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private Button bt_Ingresar;
    private Button bt_Buscar;
    private EditText et_NombreCancion;

    private Button bt_Play;
    private Button bt_Pause;
    private Button bt_Skip;


    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "12ea5b1a5bb14faa856dac7852dffe28";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "searchandlistenapp://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitComponentes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        // The next 19 lines of the code are what you need to copy & paste! :)
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
                {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this); //  Failure to run Spotify.destroyPlayer will result in your app leaking resources.
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

        bt_Buscar.setVisibility(View.VISIBLE);
        bt_Ingresar.setVisibility(View.INVISIBLE);
        et_NombreCancion.setVisibility(View.VISIBLE);

        bt_Play.setVisibility(View.VISIBLE);
        bt_Pause.setVisibility(View.VISIBLE);
        bt_Skip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoggedOut() {

        bt_Buscar.setVisibility(View.INVISIBLE);
        bt_Ingresar.setVisibility(View.VISIBLE);
        et_NombreCancion.setVisibility(View.INVISIBLE);

        bt_Play.setVisibility(View.INVISIBLE);
        bt_Pause.setVisibility(View.INVISIBLE);
        bt_Skip.setVisibility(View.INVISIBLE);

        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    private void InitComponentes()
    {
        et_NombreCancion = (EditText) findViewById(R.id.et_NombreCancion);

        bt_Ingresar = (Button) findViewById(R.id.button_IniciarSesion);
        bt_Ingresar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // The only thing that's different is we added the 5 lines below.
                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private", "streaming"}); // pueden haber mas
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
            }
        });

        bt_Buscar = (Button) findViewById(R.id.button_BuscarCancion);
        bt_Buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BuscarCancion(et_NombreCancion.getText().toString());
            }
        });

        bt_Play = (Button) findViewById(R.id.button_Play);
        bt_Play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ContinuarCancion();
            }
        });

        bt_Pause = (Button) findViewById(R.id.button_Pause);
        bt_Pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PausarCancion();
            }
        });

        bt_Skip = (Button) findViewById(R.id.button_Skip);
        bt_Skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SiguienteCancion();
            }
        });

    }

    private void BuscarCancion(String pNombreCancion)
    {
        // "https://open.spotify.com/search/playlists/" + pNombreCancion
        //https://open.spotify.com/search/results/Metaraus
        // This is the line that plays a song.

        //HttpGetRequest mirequest = new HttpGetRequest();
        //Log.d("MAEEEE", mirequest.doInBackground("https://open.spotify.com/search/results/Metaraus"));

        mPlayer.playUri(null, "spotify:playlist:1Zy3Fpd14zvi2Iax3NWqdL", 0, 0); // playlist
    }

    private void SiguienteCancion()
    {
        mPlayer.skipToNext(null);
    }

    private void PausarCancion()
    {
        mPlayer.pause(null);
    }

    private void ContinuarCancion()
    {
        mPlayer.resume(null);
    }



}
