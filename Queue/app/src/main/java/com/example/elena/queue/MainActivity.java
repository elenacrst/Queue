package com.example.elena.queue;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.elena.queue.data.ShoppingContract;
import com.example.elena.queue.data.ShoppingDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView mRecyclerView;
    private TextView mTextViewCurrentItem;
    private Queue mQueue;
    private Button mDoneButton;

    private static final int LOADER_ID = 1234;
    private SQLiteDatabase mDb;

    private static final String BUNDLE_LOADER_ACTION_KEY = "loader_action";
    private TextView mEmptyListTextView;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() !=null){
            Toolbar toolbar= (Toolbar)findViewById(R.id.action_bar);
            if (toolbar!= null){
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
            }
        }

        findViews();
        initializeRecyclerView();

        if (mQueue == null || mQueue.getCount() == 0){
            mDoneButton.setVisibility(View.INVISIBLE);
            mTextViewCurrentItem.setVisibility(View.INVISIBLE);
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }else{
            mDoneButton.setVisibility(View.VISIBLE);
            mTextViewCurrentItem.setVisibility(View.VISIBLE);
            mEmptyListTextView.setVisibility(View.INVISIBLE);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_LOADER_ACTION_KEY,"query");
        Loader<Cursor> loader = loaderManager.getLoader(LOADER_ID);
        if (loader == null){
            loaderManager.initLoader(LOADER_ID, bundle, this);
        }else{
            loaderManager.restartLoader(LOADER_ID,bundle, this);
        }

    }

    public void initializeRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    public void findViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTextViewCurrentItem = (TextView) findViewById(R.id.text_current_item);
        mDoneButton = (Button) findViewById(R.id.button_done);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading);
        mEmptyListTextView = (TextView)findViewById(R.id.text_empty_list);
    }

    public void setupRecyclerView(){

        ShoppingListAdapter mAdapter = new ShoppingListAdapter();
        mAdapter.setData(mQueue);
        if(mQueue.getCount()>0){
            mTextViewCurrentItem.setText("Buy next: "+mQueue.peek());
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showDialog(){
        AlertDialog.Builder mAlert;
        final EditText mEditTextAdd;
        mAlert= new AlertDialog.Builder(this);
        mEditTextAdd = new EditText(this);
        mAlert.setMessage("Add item to buy");
        mAlert.setView(mEditTextAdd);
        mAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(mEditTextAdd.getText() != null && mEditTextAdd.getText().toString().length()>0){
                    String text = mEditTextAdd.getText().toString();
                    //mQueue.put(text);
                    addProduct(text);
                    setupRecyclerView();
                }
            }
        });
        mAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        mAlert.show();
    }


    public void onAddButtonClick(View view) {
        showDialog();
    }

    public void onDoneButtonClick(View view) {
        deleteProduct();
        setupRecyclerView();
        if (mQueue.getCount() == 0){
            mTextViewCurrentItem.setText("");
            mDoneButton.setVisibility(View.INVISIBLE);
            mTextViewCurrentItem.setVisibility(View.INVISIBLE);
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }

    }

    private void deleteProduct(){

        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_LOADER_ACTION_KEY,"delete");
        Loader<Cursor> loader = loaderManager.getLoader(LOADER_ID);
        if (loader == null){
            loaderManager.initLoader(LOADER_ID, bundle, this);
        }else{
           loaderManager.restartLoader(LOADER_ID,bundle, this);
        }



    }

    //todo 4
    private Cursor mData;
    @Override
    public Loader<Cursor> onCreateLoader(int id,final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            int action;//0 = query, 1=delete, 2=insert
            String nameToInsert;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mLoadingIndicator.setVisibility(View.VISIBLE);
                if ( args != null && args.containsKey(BUNDLE_LOADER_ACTION_KEY)){
                    String argAction = args.getString(BUNDLE_LOADER_ACTION_KEY);
                    if(argAction.equals("query")){
                        action = 0;
                    }else if(argAction.equals("delete")){
                        action = 1;
                    }else if (argAction.equals("insert") && args.containsKey("name")){
                        nameToInsert = args.getString("name");
                        action = 2;
                    }
                }else action = 0;
                if(action == 0 && mData !=null ){
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    deliverResult(mData);
                }else this.forceLoad();
            }
            @Override
            public void deliverResult(Cursor data) {
                mData = data;
                super.deliverResult(data);
            }
            @Override
            public Cursor loadInBackground() {
                if(action == 0){
                    mData = getAllProducts();
                }else if(action == 1){
                    ShoppingDbHelper dbHelper  = new ShoppingDbHelper(getContext());
                    mDb = dbHelper.getWritableDatabase();
                    if (mData!=null && mData.moveToFirst()){
                        int id;
                        id = mData.getInt(mData.getColumnIndex(ShoppingContract.ShoppingEntry._ID));
                        mDb.delete(
                                ShoppingContract.ShoppingEntry.TABLE_NAME,
                                ShoppingContract.ShoppingEntry._ID+"="+id,
                                null
                        );
                        mData = getAllProducts();
                    }
                }else if (action == 2){
                    ShoppingDbHelper dbHelper  = new ShoppingDbHelper(getContext());
                    mDb = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    String name = nameToInsert;
                    cv.put(ShoppingContract.ShoppingEntry.COLUMN_PRODUCT_NAME, name);
                    mDb.insert(ShoppingContract.ShoppingEntry.TABLE_NAME, null, cv);
                    mData = getAllProducts();
                }
                return mData;
            }
        };
    }

    private Cursor getAllProducts(){
        ShoppingDbHelper dbHelper  = new ShoppingDbHelper(this);
        mDb = dbHelper.getReadableDatabase();
        return mDb.query(ShoppingContract.ShoppingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ShoppingContract.ShoppingEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (mQueue == null){
            mQueue = new Queue();
        }else
            while (mQueue.getCount()>0)
                mQueue.get();
        if(data != null)
            for(int i=0;  i<data.getCount(); i++){
                data.moveToPosition(i);
                String name = data.getString(
                       data.getColumnIndex(ShoppingContract.ShoppingEntry.COLUMN_PRODUCT_NAME)
                );
                mQueue.put(name);
            }
        if(mQueue.getCount() == 0){
            mDoneButton.setVisibility(View.INVISIBLE);
            mTextViewCurrentItem.setVisibility(View.INVISIBLE);
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }else{
            mEmptyListTextView.setVisibility(View.INVISIBLE);
            mDoneButton.setVisibility(View.VISIBLE);
            mTextViewCurrentItem.setVisibility(View.VISIBLE);
        }
        setupRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addProduct(String name){

        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_LOADER_ACTION_KEY,"insert");
        bundle.putString("name",name);
        Loader<Cursor> loader = loaderManager.getLoader(LOADER_ID);
        if (loader == null){
            loaderManager.initLoader(LOADER_ID, bundle, this);
        }else{
           loaderManager.restartLoader(LOADER_ID,bundle, this);
        }
    }
}

