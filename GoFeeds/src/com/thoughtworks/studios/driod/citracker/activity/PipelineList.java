package com.thoughtworks.studios.driod.citracker.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.thoughtworks.studios.driod.citracker.FeedParser;
import com.thoughtworks.studios.driod.citracker.FeedParserFactory;
import com.thoughtworks.studios.driod.citracker.ParserType;
import com.thoughtworks.studios.driod.citracker.model.Message;
import com.thoughtworks.studios.driod.citracker.model.Pipeline;
import com.thoughtworks.studios.driod.citracker.view.PipelineListAdapter;
import org.thoughtworks.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Understands named pipeline and show them with the information from the lastest run. Pass/fail
 */
public class PipelineList extends ListActivity {
    private List<Pipeline> pipelines;

    static String feedUrl = "http://go02.thoughtworks.com:8153/go/api/pipelines/%s/stages.xml";
    private static final String PIPELINE_1 = "cruise";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(org.thoughtworks.android.R.layout.main);
        pipelines = new ArrayList<Pipeline>();
        loadPipelines();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(),
                ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
        menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
                R.string.sax);
        menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(),
                R.string.dom);
        menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(),
                ParserType.XML_PULL.ordinal(), R.string.pull);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        ParserType type = ParserType.values()[item.getItemId()];
        ArrayAdapter<String> adapter =
                (ArrayAdapter<String>) this.getListAdapter();
        if (adapter.getCount() > 0) {
            adapter.clear();
        }
        this.loadPipelines();
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent myIntent = new Intent();
        myIntent.setClass(getApplicationContext(),MessageList.class);
//        myIntent.setClassName("com.thoughtworks.studios.driod.citracker.activity","com.thoughtworks.studios.driod.citracker.activity.MessageList");
        myIntent.putExtra("com.thoughtworks.studios.driod.citracker.activity.PipelineName", "Hello, Joe!"); // key/value pair, where key needs current package prefix.
        startActivity(myIntent);    
    }

    private void loadPipelines() {
        try {
            pipelines.add(loadSinglePipeline(PIPELINE_1));
            ArrayAdapter<Pipeline> adapter =
                    new PipelineListAdapter(this, R.layout.row, pipelines);
            this.setListAdapter(adapter);
        } catch (Throwable t) {
            Log.e("Go Feeds", t.getMessage(), t);
        }
    }

    private Pipeline loadSinglePipeline(String pipelineName) {
        String feedUrl1 = String.format(feedUrl, pipelineName);
        Log.i("Pipeline List", "loading feeds" + feedUrl1);
        FeedParser parser = FeedParserFactory.getParser(ParserType.SAX, feedUrl1);
        long start = System.currentTimeMillis();
        List<Message> messages = parser.parse();
        long duration = System.currentTimeMillis() - start;
        Log.i("Go Feeds", "Parser duration=" + duration);
        return new Pipeline(messages, pipelineName);
    }

}
