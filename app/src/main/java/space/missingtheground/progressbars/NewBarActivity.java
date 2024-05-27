package space.missingtheground.progressbars;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NewBarActivity extends AppCompatActivity {
    public static final String ExtraReply = "space.missingtheground.progressbars.REPLY";
    
    private EditText editTitleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbar);
        editTitleView = findViewById(R.id.edit_title);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editTitleView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String word = editTitleView.getText().toString();
                    replyIntent.putExtra(ExtraReply, word);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}

