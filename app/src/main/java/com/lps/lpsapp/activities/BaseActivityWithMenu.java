package com.lps.lpsapp.activities;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by user on 16.08.2015.
 */
public abstract class BaseActivityWithMenu extends BaseActivity {
    protected ActionMode mActionMode;
    protected View selectedView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //this.mActionMode = this.startActionMode(mActionModeCallback);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }
    protected void onDestroy() {
        mActionMode = null;
        selectedView = null;
        mActionModeCallback = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if(this.mActionMode != null)
        //{
        //    this.mActionMode.finish();
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getOptionsMenuId() != null) {
            getMenuInflater().inflate(getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(!super.onOptionsItemSelected(item)) {
            return optionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(this.getContextMenuId() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(getContextMenuId(), menu);
            this.selectedView = v;
        }
    }

    public abstract Integer getOptionsMenuId();

    public abstract Integer getContextMenuId();

    public boolean contextMenuItemClicked(ActionMode mode, MenuItem item)
    {
        return false;
    }

    public boolean optionsItemSelected(MenuItem item)
    {
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            default:
                return super.onContextItemSelected(item);
        }
    }


    protected ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(getContextMenuId(), menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
           return contextMenuItemClicked(mode, item);
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
