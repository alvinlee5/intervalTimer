package com.example.myfirstapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    MoreAccurateTimer timer;

    long secondsLeft = 0;
    long hours, hoursUp;
    long mins, minsUp;
    long secs, secsUp;
    long msUntilFinished;

    int workSet;
    int roundsCount;
    int totalRounds;
    int prepTime;

    int permOnMins, permOnSecs;
    int permOffMins, permOffSecs;
    int setOnMins, setOnSecs;
    int setOffMins, setOffSecs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
            {
                Intent intent = new Intent(this, MyPreferenceActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout_MainMenu = (FrameLayout) findViewById(R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        final ImageView splash = (ImageView) findViewById(R.id.splash);
        final Button start_button = (Button) findViewById(R.id.start_button);
        final LinearLayout pauseStop = (LinearLayout) findViewById(R.id.pauseStop);
        final Button seconds_off = (Button) findViewById(R.id.seconds_off);
        final Button seconds_on = (Button) findViewById(R.id.seconds_on);
        final Button rounds = (Button) findViewById(R.id.rounds);
        final TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
        final TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
        final Button pause = (Button) findViewById(R.id.pause);
        final Button stop = (Button) findViewById(R.id.stop);
        final Button resume = (Button) findViewById(R.id.resume);
        final TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
        final TextView timePassed = (TextView) findViewById(R.id.timePassed);
        final TextView setMessage = (TextView) findViewById(R.id.setMessage);

        final RelativeLayout countDown = (RelativeLayout) findViewById(R.id.countDown);
        final RelativeLayout roundCount = (RelativeLayout) findViewById(R.id.roundsCount);
        final GradientDrawable topBackground = (GradientDrawable) countDown.getBackground().mutate();
        final GradientDrawable botBackground = (GradientDrawable) roundCount.getBackground().mutate();


        final String[] offTimeArray;
        final String offTimeArrayName = "offTime";
        final int offTimeSelect = 0;
        final String[] onTimeArray;
        final String onTimeArrayName = "onTime";
        final int onTimeSelect = 1;
        final String[] roundsArray;
        final String roundsArrayName =  "rounds";
        final int roundsSelect = 2;

        offTimeArray = loadArray(offTimeArrayName);
        onTimeArray = loadArray(onTimeArrayName);
        roundsArray = loadArray(roundsArrayName);

        roundsPassed.setText("0/" + roundsArray[0]);
        seconds_off.setText("Seconds Off" + "\n" + "\n" + offTimeArray[0] + offTimeArray[1] + ":" + offTimeArray[2] + offTimeArray[3]);
        seconds_on.setText("Seconds On" + "\n" + "\n" + onTimeArray[0] + onTimeArray[1] + ":" + onTimeArray[2] + onTimeArray[3]);
        rounds.setText("Rounds" + "\n" + "\n" + roundsArray[0]);
        setMessage.setText("READY");

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_button.setVisibility(View.GONE);
                pauseStop.setVisibility(View.VISIBLE);
                topBackground.setColor(0xffFFD700);
                botBackground.setColor(0xffFFD700);
                timer = startCountDown();
                seconds_on.setEnabled(false);
                seconds_off.setEnabled(false);
                rounds.setEnabled(false);
                MediaPlayer.create(getApplicationContext(), R.raw.beep).start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                msUntilFinished = msUntilFinished - (msUntilFinished % 1000) + timer.cancel();
                pause.setVisibility(View.GONE);
                resume.setVisibility(View.VISIBLE);

            }
        });

        resume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resume.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                timer = preResumeCountDown();
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.cancel();
                timePassed.setText("00:00");
                timeRemaining.setText("00:00");
                setTimeLeft.setText("00:00");
                msUntilFinished = 0;
                start_button.setVisibility(View.VISIBLE);
                roundsPassed.setText("0/" + totalRounds);
                if (resume.getVisibility() == View.VISIBLE){
                    resume.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
                pauseStop.setVisibility(View.GONE);
                setMessage.setText("READY");
                topBackground.setColor(0xff00BFFF);
                botBackground.setColor(0xff00BFFF);
                seconds_on.setEnabled(true);
                seconds_off.setEnabled(true);
                rounds.setEnabled(true);
            }
        });

        seconds_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectionMenu(view, offTimeArray, offTimeArrayName, offTimeSelect);
            }
        });
        seconds_on.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openSelectionMenu(view, onTimeArray, onTimeArrayName, onTimeSelect);
            }
        });
        rounds.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openSelectionMenu(view, roundsArray, roundsArrayName, roundsSelect);
            }
        });
        new CountDownTimer(0, 0) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                /* Basically the section below fades out the splash screen and makes the button and
                edittext part visible. splash.animate() returns a ViewPropertyAnimator object, as
                well as .alpha and .setListener (which is why we can chain method calls). The
                setListener() method takes in an Animator.AnimatorListener object, which is basically
                the thing that "listens" to the animation (i.e. when it ends, stops, etc.).

                Note: Animator.AnimatorListener object that setListener() is supposed to take in
                can be replaced by AnimatorListenerAdapter() object.

                The AnimatorListenerAdapter() object basically has a bunch of methods that indicate
                when the animation begins, ends, etc. We override the desired method in that class
                (in this case onAnimationEnd(.)) to do what we want when the animation ends/starts/
                etc.
                 */
                splash.animate()
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            /* This is an anonymous class, which is why we declare (i.e. override
                            a superclass method) and instantiate in the single expression.

                            Or not...it is a well defined class which interfaces
                            Animator.AnimatorListener...(need to read up on interface and anonymous
                            classes which implements interfaces).
                             */
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                splash.setVisibility(View.GONE);
                                start_button.setVisibility(View.VISIBLE);
                                seconds_off.setVisibility(View.VISIBLE);
                                seconds_on.setVisibility(View.VISIBLE);
                                rounds.setVisibility(View.VISIBLE);
                                countDown.setVisibility(View.VISIBLE);
                                roundCount.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }.start();
    }

    public void openSelectionMenu(View view, final String[] timeArray, final String arrayName, final int selection){
        // Consider removing the try block...
        final FrameLayout layout_MainMenu = (FrameLayout) findViewById(R.id.mainmenu);        // framelayout object
        final PopupWindow numpad;
        final TextView offtime;
        TextView title;
        try{
            //Create instance of layout inflater, MainActivity.this returns object of type context,
            //getSystemService(Context.LAYOUT_INFLATER_SERVICE) returns a layout inflater for the given context
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //inflate a new view hierarchy from the specified XML resrouce (this throws an exception if error occurs)

            // Inflate the gridlayout (i.e. the numpad)
            View layout = inflater.inflate(R.layout.numpad, (ViewGroup) findViewById(R.id.numpad));

            // Get textview for the title of the selection popup
            title = (TextView) layout.findViewById(R.id.title);
            if (selection == 0){
                title.setText("Seconds Off");
            }else if (selection == 1){
                title.setText("Seconds On");
            }else if (selection == 2){
                title.setText("Rounds");
            }


            // Get the textview that holds the time and set to saved (or default) value
            offtime = (TextView) layout.findViewById(R.id.setOffTime);
            if (selection == 2){
                offtime.setText(timeArray[0]);
            }else{
                offtime.setText(timeArray[0]+timeArray[1]+ ":"+timeArray[2]+timeArray[3]);
            }

            // Set size of box that holds time / rounds (Revisit this ***)
            layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            int width = layout.getMeasuredWidth();
            int height = layout.getMeasuredHeight();
            height = height / 4;

            GradientDrawable background = (GradientDrawable) offtime.getBackground().mutate();
            background.setSize(width, height);

            // Measure size of layout again to set size of numpad background
            layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            width = layout.getMeasuredWidth();
            height = layout.getMeasuredHeight();

            LinearLayout gridBackground = (LinearLayout) layout.findViewById(R.id.linGrid);    // get linearlayout
            background = (GradientDrawable) gridBackground.getBackground().mutate();      // reusing background variable

            background.setSize((int) (1.2*width), (int) (1.1*height));

            //Create popupwindow object
            numpad = new PopupWindow(layout, -2, -2, true);     // -2 width and height wraps the window (subject to change if causes problems down the road)

            // Show the popup window
            numpad.showAtLocation(layout, Gravity.CENTER, 0, 0);

            //returns an ObjectAnimator object that animates the specified property between the given (integer) values
            ObjectAnimator animator_dim = ObjectAnimator.ofInt(layout_MainMenu.getForeground(), "alpha", 0, 180);
            animator_dim.setTarget(layout_MainMenu.getForeground());
            animator_dim.setDuration(700).start();

            // Set listener for when the popupwindow is dismissed
            numpad.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ObjectAnimator animator_undim = ObjectAnimator.ofInt(layout_MainMenu.getForeground(), "alpha", 180, 0);
                    animator_undim.setTarget(layout_MainMenu.getForeground());
                    animator_undim.setDuration(700).start();
                }
            });

            // Code for button presses
            String[] time = new String[4];
            Button btns[] = new Button[12];

            btns[0] = (Button) layout.findViewById(R.id.zero);
            btns[1] = (Button) layout.findViewById(R.id.one);
            btns[2] = (Button) layout.findViewById(R.id.two);
            btns[3] = (Button) layout.findViewById(R.id.three);
            btns[4] = (Button) layout.findViewById(R.id.four);
            btns[5] = (Button) layout.findViewById(R.id.five);
            btns[6] = (Button) layout.findViewById(R.id.six);
            btns[7] = (Button) layout.findViewById(R.id.seven);
            btns[8] = (Button) layout.findViewById(R.id.eight);
            btns[9] = (Button) layout.findViewById(R.id.nine);
            btns[10] = (Button) layout.findViewById(R.id.clear);
            btns[11] = (Button) layout.findViewById(R.id.ok);

            for (int i = 0; i <= 9; i++){
                final int finalI = i;
                btns[finalI].setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        if (selection == 2){
                            roundsSelect(timeArray, Integer.toString(finalI));
                            offtime.setText(timeArray[0]);
                        }else{
                            mutateTimeArray(timeArray, Integer.toString(finalI));
                            offtime.setText(timeArray[0]+timeArray[1]+ ":" +timeArray[2]+timeArray[3]);
                        }
                    }
                });
            }
            btns[10].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    if (selection == 2){
                        timeArray[0] = "0";
                        offtime.setText(timeArray[0]);
                    }else{
                        timeArray[0] = "0";
                        timeArray[1] = "0";
                        timeArray[2] = "0";
                        timeArray[3] = "0";
                        offtime.setText(timeArray[0]+timeArray[1]+ ":" +timeArray[2]+timeArray[3]);
                    }

                }
            });
            btns[11].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selection == 0 || selection == 1){
                        if (selection == 1 && Integer.parseInt(timeArray[0] + timeArray[1] + timeArray[2] + timeArray[3]) == 0){
                            timeArray[0] = "0";
                            timeArray[1] = "0";
                            timeArray[2] = "0";
                            timeArray[3] = "1";
                        }
                        if (!timeArray[0].equals("9") || !timeArray[1].equals("9")){
                            int tens = Integer.parseInt(timeArray[2]);
                            if (tens >= 6){
                                tens = tens - 6;
                                timeArray[2] = Integer.toString(tens);

                                int oneMins = Integer.parseInt(timeArray[1]);
                                if (oneMins == 9){
                                    timeArray[0] = Integer.toString(Integer.parseInt(timeArray[0])+1);
                                    timeArray[1] = "0";
                                }else{
                                    timeArray[1] = Integer.toString(oneMins+1);
                                }
                            }
                        }
                    }else{
                        if (timeArray[0].equals("0")){
                            timeArray[0] = "1";
                        }
                    }
                    saveArray(timeArray, arrayName);
                    numpad.dismiss();
                    if (selection == 0){
                        Button selector = (Button) findViewById(R.id.seconds_off);
                        selector.setText("Seconds Off" + "\n" + "\n" + timeArray[0] + timeArray[1] + ":" + timeArray[2] + timeArray[3]);
                    }else if (selection == 1){
                        Button selector = (Button) findViewById(R.id.seconds_on);
                        selector.setText("Seconds On" + "\n" + "\n" + timeArray[0] + timeArray[1] + ":" + timeArray[2] + timeArray[3]);
                    }else if (selection == 2){
                        TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
                        roundsPassed.setText("0/"+timeArray[0]);
                        Button selector = (Button) findViewById(R.id.rounds);
                        selector.setText("Rounds" + "\n" + "\n" + timeArray[0]);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void mutateTimeArray(String[] time, String buttonPress){
        int sizeOfArray = time.length;
        if (time[0].equals("0")){
            for (int i = 0; i<=sizeOfArray-2; i++){
                time[i] = time[i+1];
            }
            time[sizeOfArray-1] = buttonPress;
        }
    }
    public boolean saveArray(String[] array, String arrayName){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);    //MainActivity.this. at beginning
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 0; i < array.length; i++){
            editor.putString(arrayName + "_" + i, array[i]);
        }
        return editor.commit();
    }

    public String[] loadArray(String arrayName){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String array[] = new String[4];     //it's always 4
        for (int i = 0; i < array.length; i++){
            array[i] = sharedPref.getString(arrayName+"_"+ i, "0");
        }
        return array;
    }

    public void roundsSelect(String[] rounds, String buttonPress){
        if (rounds[0].length() != 3){
            if (!(rounds[0].equals("0") && buttonPress.equals("0"))){
                if (rounds[0].equals("0") && !buttonPress.equals("0")){
                    rounds[0] = buttonPress;
                }else{
                    rounds[0] = rounds[0] + buttonPress;
                }

            }
        }
    }
    public void setTimes(long hours, long mins, long secs, TextView time){
        if (hours > 0){
            if (String.valueOf(mins).length() == 1){
                if (String.valueOf(secs).length() == 1){
                    time.setText(hours + ":0" + mins + ":0" + secs);
                }else{
                    time.setText(hours + ":0" + mins + ":" + secs);
                }
            }else{
                if (String.valueOf(secs).length() == 1){
                    time.setText(hours + ":" + mins + ":0" + secs);
                }else{
                    time.setText(hours + ":" + mins + ":" + secs);
                }
            }
        } else{
            if (String.valueOf(mins).length() == 1){
                if (String.valueOf(secs).length() == 1){
                    time.setText("0" + mins + ":0" + secs);
                }else{
                    time.setText("0" + mins + ":" + secs);
                }
            }else{
                if (String.valueOf(secs).length() == 1){
                    time.setText(mins + ":0" + secs);
                }else{
                    time.setText(mins + ":" + secs);
                }
            }
        }
    }

    public void timeIncrement(){
        if (mins != 0 && secs == 0){
            mins = mins - 1;
        }else if (mins == 0 && hours != 0 && secs == 0){
            hours = hours - 1;
            mins = 59;
        }
        if (secs != 0){
            secs = secs - 1;
        }else{
            secs = 59;
        }

        if (minsUp != 59 && secsUp == 59){
            minsUp = minsUp + 1;
        }else if (minsUp == 59 && secsUp == 59){
            hoursUp = hoursUp + 1;
            minsUp = 0;
        }
        if (secsUp != 59){
            secsUp = secsUp + 1;
        }else{
            secsUp = 0;
        }
    }

    public void updateSetTime(){
        TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
        TextView setMessage = (TextView) findViewById(R.id.setMessage);
        RelativeLayout countDown = (RelativeLayout) findViewById(R.id.countDown);
        RelativeLayout roundCount = (RelativeLayout) findViewById(R.id.roundsCount);
        GradientDrawable topBackground = (GradientDrawable) countDown.getBackground().mutate();
        GradientDrawable botBackground = (GradientDrawable) roundCount.getBackground().mutate();
        TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);

        if (permOffMins == 0 && permOffSecs == 0){
            if (setOnMins !=0 && setOnSecs == 0){
                setOnMins = setOnMins - 1;
                setOnSecs = 59;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);
            }else if (setOnMins == 0 && setOnSecs == 1){
                setOnMins = permOnMins;
                setOnSecs = permOnSecs;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);
                roundsCount = roundsCount + 1;
                roundsPassed.setText(roundsCount + "/" + totalRounds);
                MediaPlayer.create(getApplicationContext(), R.raw.gunshot).start();
            }else{
                setOnSecs = setOnSecs - 1;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);
            }
        }else if (workSet == 1){
            if (setOnMins == 0 && setOnSecs == 1){
                MediaPlayer.create(getApplicationContext(), R.raw.boxingbell).start();
                workSet = 0;
                topBackground.setColor(0xffFF3333);
                botBackground.setColor(0xffFF3333);

                setMessage.setText("REST");
                setOnMins = permOnMins;
                setOnSecs = permOnSecs;
                setTimes(0, (long)setOffMins, (long)setOffSecs, setTimeLeft);
            }else if (setOnMins != 0 && setOnSecs == 0){
                setOnMins = setOnMins - 1;
                setOnSecs = 59;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);
            }else{
                setOnSecs = setOnSecs - 1;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);
            }

        }else{
            if (setOffMins == 0 && setOffSecs == 1){
                MediaPlayer.create(getApplicationContext(), R.raw.gunshot).start();
                workSet = 1;
                topBackground.setColor(0xff00FF00);
                botBackground.setColor(0xff00FF00);
                setMessage.setText("GO!");
                setOffMins = permOffMins;
                setOffSecs = permOffSecs;
                setTimes(0, (long)setOnMins, (long)setOnSecs, setTimeLeft);

                roundsCount = roundsCount + 1;
                roundsPassed.setText(roundsCount + "/" + totalRounds);
            }else if (setOffMins != 0 && setOffSecs == 0){
                setOffMins = setOffMins - 1;
                setOffSecs = 59;
                setTimes(0, (long)setOffMins, (long)setOffSecs, setTimeLeft);
            }else{
                setOffSecs = setOffSecs - 1;
                setTimes(0, (long)setOffMins, (long)setOffSecs, setTimeLeft);
            }
        }
    }

    public MoreAccurateTimer startCountDown(){
        String[] onTimeArray = loadArray("onTime");
        String[] offTimeArray = loadArray("offTime");
        String[] rounds = loadArray("rounds");

        secondsLeft = ((Long.parseLong(onTimeArray[0] + onTimeArray[1]) + Long.parseLong(offTimeArray[0]+offTimeArray[1]))*60 + Long.parseLong(onTimeArray[2] + onTimeArray[3]) + Long.parseLong(offTimeArray[2] + offTimeArray[3])) * Long.parseLong(rounds[0]);
        hours = secondsLeft / 3600;
        mins = (secondsLeft % 3600) / 60;
        secs = (secondsLeft % 3600) % 60;

        hoursUp = 0;
        minsUp = 0;
        secsUp = 0;

        permOnMins = (int) Long.parseLong(onTimeArray[0] + onTimeArray[1]);
        permOnSecs = (int) Long.parseLong(onTimeArray[2] + onTimeArray[3]);
        setOnMins = permOnMins;
        setOnSecs = permOnSecs;

        permOffMins = (int) Long.parseLong(offTimeArray[0] + offTimeArray[1]);
        permOffSecs = (int) Long.parseLong(offTimeArray[2] + offTimeArray[3]);
        setOffMins = permOffMins;
        setOffSecs = permOffSecs;

        totalRounds = Integer.parseInt(rounds[0]);
        roundsCount = 0;

        TextView timePassed = (TextView) findViewById(R.id.timePassed);
        TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
        TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
        TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
        TextView setMessage = (TextView) findViewById(R.id.setMessage);

        setMessage.setText("GET SET");
        roundsPassed.setText(roundsCount + "/" + totalRounds);
        setTimes(0, setOnMins, setOnSecs, setTimeLeft);
        timePassed.setText("00:00");
        setTimes(hours, mins, secs, timeRemaining);

        secondsLeft = secondsLeft + 3;
        workSet = -1;
        prepTime = 3;
        setTimeLeft.setText("00:0" + prepTime);

        return new MoreAccurateTimer(secondsLeft*1000, 1000){
            public void onTick(long ms){
                timerTick();
            }

            public void onFinish(){
                TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
                TextView timePassed = (TextView) findViewById(R.id.timePassed);
                TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
                TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
                TextView setMessage = (TextView) findViewById(R.id.setMessage);
                RelativeLayout countDown = (RelativeLayout) findViewById(R.id.countDown);
                RelativeLayout roundCount = (RelativeLayout) findViewById(R.id.roundsCount);
                GradientDrawable topBackground = (GradientDrawable) countDown.getBackground().mutate();
                GradientDrawable botBackground = (GradientDrawable) roundCount.getBackground().mutate();

                topBackground.setColor(0xff00BFFF);
                botBackground.setColor(0xff00BFFF);

                setMessage.setText("READY");

                roundsPassed.setText("0/" + totalRounds);
                timeRemaining.setText("00:00");
                setTimeLeft.setText("00:00");

                hours = secondsLeft / 3600;
                mins = (secondsLeft % 3600) / 60;
                secs = (secondsLeft % 3600) % 60;

                setTimes(hours, mins, secs, timePassed);
                timePassed.setText("00:00");
                Button start_button = (Button) findViewById(R.id.start_button);
                start_button.setVisibility(View.VISIBLE);

                LinearLayout pauseStop = (LinearLayout) findViewById(R.id.pauseStop);
                pauseStop.setVisibility(View.GONE);
                (findViewById(R.id.seconds_on)).setEnabled(true);
                (findViewById(R.id.seconds_off)).setEnabled(true);
                (findViewById(R.id.rounds)).setEnabled(true);
            }
        }.start();
    }

    public MoreAccurateTimer preResumeCountDown(){
        long msLeftInSec = msUntilFinished % 1000;
        return new MoreAccurateTimer(msLeftInSec, msLeftInSec){
            public void onTick(long ms){
            }
            public void onFinish(){
                timerTick();
                timer = resumeCountDown();
            }
        }.start();
    }

    public MoreAccurateTimer resumeCountDown(){
        long totalMsLeft = msUntilFinished / 1000 * 1000;
        msUntilFinished = 0;
        return new MoreAccurateTimer(totalMsLeft, 1000){
            public void onTick(long ms){
                timerTick();

            }
            public void onFinish(){
                TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
                TextView timePassed = (TextView) findViewById(R.id.timePassed);
                TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
                TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
                TextView setMessage = (TextView) findViewById(R.id.setMessage);
                RelativeLayout countDown = (RelativeLayout) findViewById(R.id.countDown);
                RelativeLayout roundCount = (RelativeLayout) findViewById(R.id.roundsCount);
                GradientDrawable topBackground = (GradientDrawable) countDown.getBackground().mutate();
                GradientDrawable botBackground = (GradientDrawable) roundCount.getBackground().mutate();

                topBackground.setColor(0xff00BFFF);
                botBackground.setColor(0xff00BFFF);

                setMessage.setText("READY");

                roundsPassed.setText("0/" + totalRounds);
                timeRemaining.setText("00:00");
                setTimeLeft.setText("00:00");

                hours = secondsLeft / 3600;
                mins = (secondsLeft % 3600) / 60;
                secs = (secondsLeft % 3600) % 60;

                setTimes(hours, mins, secs, timePassed);
                timePassed.setText("00:00");
                Button start_button = (Button) findViewById(R.id.start_button);
                start_button.setVisibility(View.VISIBLE);

                LinearLayout pauseStop = (LinearLayout) findViewById(R.id.pauseStop);
                pauseStop.setVisibility(View.GONE);
                (findViewById(R.id.seconds_on)).setEnabled(true);
                (findViewById(R.id.seconds_off)).setEnabled(true);
                (findViewById(R.id.rounds)).setEnabled(true);
            }
        }.start();

    }
    public void timerTick(){
        if (workSet == -1){
            if (prepTime == 2){
                workSet = -2;
            }
            prepTime = prepTime - 1;
            TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
            setTimeLeft.setText("00:0" + prepTime);
            MediaPlayer.create(getApplicationContext(), R.raw.beep).start();

        }else if (workSet == -2){
            TextView setTimeLeft = (TextView) findViewById(R.id.setTimeLeft);
            TextView roundsPassed = (TextView) findViewById(R.id.roundsPassed);
            TextView setMessage = (TextView) findViewById(R.id.setMessage);
            RelativeLayout countDown = (RelativeLayout) findViewById(R.id.countDown);
            RelativeLayout roundCount = (RelativeLayout) findViewById(R.id.roundsCount);
            GradientDrawable topBackground = (GradientDrawable) countDown.getBackground().mutate();
            GradientDrawable botBackground = (GradientDrawable) roundCount.getBackground().mutate();

            topBackground.setColor(0xff00FF00);
            botBackground.setColor(0xff00FF00);

            MediaPlayer.create(getApplicationContext(), R.raw.gunshot).start();
            setMessage.setText("GO!");
            roundsCount++;
            roundsPassed.setText(roundsCount + "/" + totalRounds);
            setTimes(0, setOnMins, setOnSecs, setTimeLeft);
            workSet = 1;

        } else{
            TextView timePassed = (TextView) findViewById(R.id.timePassed);
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);

            updateSetTime();
            timeIncrement();
            setTimes(hoursUp, minsUp, secsUp, timePassed);
            setTimes(hours, mins, secs, timeRemaining);
        }
    }

}


//http://www.androidhub4you.com/2012/07/how-to-create-popup-window-in-android.html
//http://stackoverflow.com/questions/3221488/blur-or-dim-background-when-android-popupwindow-active
//http://stackoverflow.com/questions/28806879/animating-drawable-alpha-property     12/23/16

//http://rushabh138.blogspot.ca/2013/09/onscreen-number-pad-in-android.html
// http://stackoverflow.com/questions/5821051/how-to-display-the-value-of-a-variable-on-the-screen
// http://stackoverflow.com/questions/3496269/how-to-put-a-border-around-an-android-textview
// http://stackoverflow.com/questions/3404582/adding-text-to-imageview-in-android 12/24/16

//http://stackoverflow.com/questions/28578701/create-android-shape-background-programmatically          12/25/16


//http://stackoverflow.com/questions/3876680/is-it-possible-to-add-an-array-or-object-to-sharedpreferences-on-android   12/28/16

