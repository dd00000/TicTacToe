package dandobre.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button localPlayButton = findViewById(R.id.button_play_local);
        localPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", "local");
            startActivity(intent);
        });

        Button aiPlayButton = findViewById(R.id.button_play_ai);
        aiPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", "ai");
            startActivity(intent);
        });

        Button quitButton = findViewById(R.id.button_quit);
        quitButton.setOnClickListener(view -> finish());
    }
}
