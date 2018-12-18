package com.cpsc411.campususer.finalmet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ItineraryActivity extends Activity implements OnItemClickListener {

    //listview obj
    private ListView itinerary_listview;
    //defaults
    private String goal_default = "0";
    private String start_default = "0";
    private String metTime_default = "0";
    private String description_default = "Enter description";
    //arraylist of Pair objects
    private ArrayList<ArrayList<String>> met_description_List;
    //Pair data structure containing two string values, hence a pair of string values.
    private ArrayList<String> tuples;

    long startTime = 0;
    long elapsedTime = 0;

    //save position of item being edited.
    int edit_position = 0;

    //create cancelled resultcode
    int RESULT_DELETED = 3;
    int RESULT_ALARM = 4;
    int ALARM_CANCEL = 5;

    //iterator for my list to use for alarms
    int iterator = 1;

    int size = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        //arraylist of Pair objects
        met_description_List = new ArrayList<ArrayList<String>>();

        //initialize placeholder item so its not empty
        itinerary_listview = findViewById(R.id.itinerary_view);
        //Sets TextView that says List is empty.
        itinerary_listview.setEmptyView(findViewById(R.id.empty_list_item));
        //Get intents if any
        Intent intent = getIntent();
        //For when a listview item is clicked.
        itinerary_listview.setOnItemClickListener(this);

        startTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itinerary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_event:
                Toast.makeText(this, "A New Event!", Toast.LENGTH_SHORT).show();
                //make intents and start EventActivity
                Intent intent = new Intent(this, EventActivity.class);
                intent.putExtra("met", metTime_default);
                intent.putExtra("description", description_default);
                startActivityForResult(intent, 1);
                return true;
            case R.id.set_alarms:
                Toast.makeText(this, "Setting the alarms!", Toast.LENGTH_SHORT).show();

                int hour = Integer.parseInt(met_description_List.get(0).get(4));
                int mins = Integer.parseInt(met_description_List.get(0).get(5));

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, hour);
                calendar.add(Calendar.MINUTE, mins);

                Intent settingAlarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                //settingAlarmIntent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
               // settingAlarmIntent.putExtra(AlarmClock.EXTRA_IS_PM, PM);
                settingAlarmIntent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                settingAlarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
                settingAlarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

                startActivityForResult(settingAlarmIntent, RESULT_ALARM);

                Toast.makeText(this, "FirstAlarmSet!", Toast.LENGTH_SHORT).show();

               // onBackPressed(); "This will cause the app to close
                return super.onOptionsItemSelected(item);
                //return true;

            case  R.id.cancel_alarms:
                //to canccel alarm
                Intent cancelAlarmIntent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                cancelAlarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

                startActivityForResult(cancelAlarmIntent, ALARM_CANCEL);

                Toast.makeText(this, "Cancelling the alarms", Toast.LENGTH_SHORT).show();

                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public long toHours(long ms){
        long hrs;
        hrs = ms;
        hrs = hrs / 1000;
        hrs = hrs / 60;

        return hrs;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //for receiving the activity
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                int h = data.getIntExtra("hour", 0);
                int m = data.getIntExtra("minute", 0);
                String g = Long.toString(h) + ":" + Long.toString(m);
                startTime = data.getLongExtra("start",0);
                elapsedTime = System.currentTimeMillis() - startTime;

                String metTime = Long.toString(elapsedTime);
                String description = data.getStringExtra("description");

                tuples = new ArrayList<String>();
                tuples.add(g);
                tuples.add(Long.toString(startTime));
                tuples.add(metTime);
                tuples.add(description);
                tuples.add(Integer.toString(h));
                tuples.add(Integer.toString(m));

                met_description_List.add(tuples);

                size += 1;

                //create a List that will take map objects
                ArrayList<HashMap<String, String>> newdata = new ArrayList<HashMap<String, String>>();

                for (ArrayList array : met_description_List) {
                    //create the map obj, that way we can map the met time and description
                    //mapping makes it easier later to distinguish met time and description
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("goal", g);
                    map.put("start", array.get(1).toString());
                    map.put("met", array.get(2).toString());
                    map.put("description", array.get(3).toString());
                    newdata.add(map);
                }

                //create resource, from, and to variables
                int resource = R.layout.item_layout;
                String[] from = {"goal", "description"};
                int[] to = {R.id.metTView, R.id.descriptionTextView};

                //create and set adapter
                SimpleAdapter adapter = new SimpleAdapter(this, newdata, resource, from, to);
                itinerary_listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "CANCELLED", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_DELETED){
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            }


        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Saved Changes!", Toast.LENGTH_SHORT).show();

                int h = data.getIntExtra("hour", 0);
                int m = data.getIntExtra("minute", 0);
                String g = Long.toString(h) + ":" + Long.toString(m);
                startTime = data.getLongExtra("start",0);
                elapsedTime = System.currentTimeMillis() - startTime;

                String metTime = Long.toString(elapsedTime);
                String description = data.getStringExtra("description");

                //get item that was selected
                ArrayList<String> item = met_description_List.get(edit_position);
                item.set(0, g);
                item.set(1,Long.toString(startTime));
                item.set(2,metTime);
                item.set(3,description);

                //set back into the list for the listview
                met_description_List.set(edit_position, item);

                //create a List that will take map objects
                ArrayList<HashMap<String, String>> newdata = new ArrayList<HashMap<String, String>>();

                for (ArrayList array : met_description_List) {
                    //create the map obj, that way we can map the met time and description
                    //mapping makes it easier later to distinguish met time and description
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("goal", array.get(0).toString());
                    map.put("start", array.get(1).toString());
                    map.put("met", array.get(2).toString());
                    map.put("description", array.get(3).toString());
                    newdata.add(map);
                }

                //create resource, from, and to variables
                int resource = R.layout.item_layout;
                String[] from = {"met", "description"};
                int[] to = {R.id.metTView, R.id.descriptionTextView};

                //create and set adapter
                SimpleAdapter adapter = new SimpleAdapter(this, newdata, resource, from, to);
                itinerary_listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "CANCELLED", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_DELETED){
                met_description_List.remove(edit_position);
                size -= 1;
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == RESULT_ALARM){

            if (met_description_List.size() > 1 ){
                if (iterator < size){
                    int hour = Integer.parseInt(met_description_List.get(iterator).get(4));
                    int mins = Integer.parseInt(met_description_List.get(iterator).get(5));

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, hour);
                    calendar.add(Calendar.MINUTE, mins);

                    Intent settingAlarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    settingAlarmIntent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                    settingAlarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
                    settingAlarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

                    startActivityForResult(settingAlarmIntent, RESULT_ALARM);
                    iterator++;
                }
            }
        }
        else if (requestCode == ALARM_CANCEL){

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //TO-DO:
        //save position globally
        edit_position = position;
        ArrayList<String> item = met_description_List.get(position);

        Intent edit_intent = new Intent(this, EventActivity.class);
        edit_intent.putExtra("met", item.get(2));
        edit_intent.putExtra("description", item.get(3));
        startActivityForResult(edit_intent, 2);
    }

    //TimersTask
    public void startTime(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (met_description_List == null){
                    //do nothing
                    int size = met_description_List.size();

                }else {
                    updateList(met_description_List);
                }
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, 0, 2000);
    }

    //update the listview
    public void updateList(final ArrayList<ArrayList<String>> met_description_List)
    {
        itinerary_listview.post(new Runnable() {
            @Override
            public void run() {
                //create a List that will take map objects
                ArrayList<HashMap<String, String>> newdata = new ArrayList<HashMap<String, String>>();

                for (ArrayList array : met_description_List) {
                    long new_elapsetime =
                            System.currentTimeMillis() - Long.parseLong(array.get(1).toString());
                    //Long g_ms = Long.parseLong(array.get(0).toString());

                    //create the map obj, that way we can map the met time and description
                    //mapping makes it easier later to distinguish met time and description
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("goal", array.get(0).toString());
                    map.put("start", array.get(1).toString());
                    map.put("met", Long.toString(new_elapsetime));
                    map.put("description", array.get(3).toString());
                    newdata.add(map);
                }

                //create resource, from, and to \
                int resource = R.layout.item_layout;
                String[] from = {"goal", "description"};
                int[] to = {R.id.metTView, R.id.descriptionTextView};

                //create and set adapter
                SimpleAdapter adapter =
                        new SimpleAdapter(ItineraryActivity.this, newdata, resource, from, to);
                itinerary_listview.setAdapter(null);
                itinerary_listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
