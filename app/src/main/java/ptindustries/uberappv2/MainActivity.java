package ptindustries.uberappv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity
{

    Switch switch1;
    Button startButton;


    public void checkState(View view)
    {
        String userType = "rider";

        if(switch1.isChecked()) //driver
        {
            userType = "driver";

            ParseUser.getCurrentUser().put("riderOrDriver", userType);

            startActivity(new Intent(getApplicationContext(), distanceActivity.class));
        }

        else //rider
        {
            ParseUser.getCurrentUser().put("riderOrDriver", userType);

            startActivity(new Intent(getApplicationContext(), riderActivity.class));

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = (Switch) findViewById(R.id.switch1);
        startButton = (Button) findViewById(R.id.getStartedButton);

        if(ParseUser.getCurrentUser() == null)
            ParseAnonymousUtils.logIn(new LogInCallback()
            {
                @Override
                public void done(ParseUser user, ParseException e)
                {
                    if(e == null)
                        Log.i("Login", "Sucessful");
                    else
                        Log.i("Login","Unsucessful");
                }
            });


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
