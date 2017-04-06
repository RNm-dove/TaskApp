package com.example.ryosuke.nakagawa.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "com.example.ryosuke.nakagawa.taskapp.TASK";
    public String searchText;


    private Realm mRealm;
    private RealmResults<Task> mTaskRealmResults;
    private RealmQuery<Task> query;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };
    private ListView mListView;
    private TaskAdapter mTaskAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InputActivity.class);

                startActivity(intent);
            }
        });



        //realmの設定
        mRealm = Realm.getDefaultInstance();
        query = mRealm.where(Task.class);
        mTaskRealmResults = mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);

        //sortボタンを押したときの設定
        //EditTextで検索指定

        Button sortButtton = (Button) findViewById(R.id.sortButton);
        sortButtton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.serchEditText);
                searchText = editText.getText().toString();
                if (searchText.matches("")) {
                    mTaskRealmResults =mRealm.where(Task.class).findAll();
                    mTaskRealmResults.sort("date", Sort.DESCENDING);
                    reloadListView();
                } else {
                    if(query.equalTo("category", searchText) != null ){
                        mTaskRealmResults = query.findAll();
                        mTaskRealmResults.sort("date", Sort.DESCENDING);
                        reloadListView();
                    }
                }
            }
        });


        //listview の設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewをタップした時の処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK,task.getId());
                startActivity(intent);
            }
        });

        //ListView を長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                //タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);

                //ダイアログを表示
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        ;
                        results.deleteFirstFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        alarmManager.cancel(resultPendingIntent);
                        reloadListView();

                    }

                });

                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return  true;
            }
        });

        reloadListView();

    }

    private void reloadListView(){

        ArrayList<Task> taskArrayList = new ArrayList<>();

        for (int i = 0; i< mTaskRealmResults.size() ; i++){
            if(!mTaskRealmResults.get(i).isValid()) continue;

            Task task = new Task();

            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setCategory(mTaskRealmResults.get(i).getCategory());
            task.setDate(mTaskRealmResults.get(i).getDate());

            taskArrayList.add(task);

        }

        mTaskAdapter.setTaskArrayList(taskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();

        mRealm.close();
    }

}
