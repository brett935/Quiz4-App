package com.brettnapier.quiz4;

import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view );

        button = (Button) findViewById( R.id.button );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String queryData = "?id=9";
                String queryData = "";

                new dataRequest().execute(queryData);
            }
        });

    }

    //class to request JSON data from online server
    class dataRequest extends AsyncTask<String,Void,String>{

        int numQuizes;


        //String...params is an array of paramaters
        protected String doInBackground(String...params){

            //Debug.waitForDebugger();

            try{
                String queryData = params[0];
                String baseUrl = "http://people.eku.edu/styere/quiz.php";

                //assemble query as URL
                String queryURL = baseUrl + URLEncoder.encode(queryData, "UTF-8");
                //convert string to URL class
                URL url = new URL( queryURL );
                //make the HTTP connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //continue if successful
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                    //query was accepted, assemble the response
                    StringBuilder sb = new StringBuilder();
                    BufferedReader input = new BufferedReader(new InputStreamReader( httpURLConnection.getInputStream() ));
                    while (true){
                        String line = input.readLine();
                        //end of data?
                        if (line==null) break;
                        //add to the collected input
                        sb.append(line);
                    }
                    input.close();
                    return sb.toString();
                }

            }catch (Exception e){
                return(e.getMessage());
            }

            return "";
        }

        //process the results
        protected void onPostExecute( String resp ){
            try {
                StringBuilder sb = new StringBuilder();
                //get the query as a JSON object
                JSONObject mResponseObject = new JSONObject( resp);
                //JSONObject responseData = mResponseObject.getJSONObject( "count");
                //get the results as an array
                //JSONArray array = responseData.getJSONArray( "results" );
                String m = mResponseObject.getString("count");
                textView.setText(m);

                /*
                //process the first five results
                for(int i=0; i<array.length(); i++){
                    String title = array.getJSONObject(i).getString("title" );
                    String qlist = array.getJSONObject(i).getString("qlist");
                    String question = array.getJSONObject(i).getString("question");
                    String answer = array.getJSONObject(i).getString("answer");

                    textView.setText(title.toString() +" "+ qlist.toString() +" "+ question.toString() +" "+ answer.toString());
                } */

            }catch (Exception e){
                textView.setText(e.getMessage());
            }
        }
    }
}
