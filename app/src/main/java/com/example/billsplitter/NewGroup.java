package com.example.billsplitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class NewGroup extends AppCompatActivity {

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_appartment:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_house:
                if (checked)
                    // Ninjas rule
                    break;

            case R.id.radio_trip:
                if (checked)
                    // Ninjas rule
                    break;

            case R.id.radio_other:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
    }
}
