package com.brettnapier.quiz4;

import android.os.AsyncTask;
import android.os.Debug;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

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

        private int numQuizes;


        private int getNumQuizzes(){
            String m="";
            try{
                URL url = new URL("http://people.eku.edu/styere/quiz.php");
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
                    httpURLConnection.disconnect();
                    String resp = sb.toString();
                    //StringBuilder bs = new StringBuilder();
                    //get the query as a JSON object
                    JSONObject mResponseObject = new JSONObject( resp);
                    //JSONObject responseData = mResponseObject.getJSONObject( "count");
                    //get the results as an array
                    //JSONArray array = responseData.getJSONArray( "results" );
                     m = mResponseObject.getString("count");
                    numQuizes = Integer.valueOf( m );
                }

            }catch (Exception e){
                textView.setText("Error");
            }

            //clean up references and delete objects

            return (numQuizes);
        }

        private void getQuiz(int numQuizes){
            List<String> quizList;
            String m="";

            for(int i=0; i<numQuizes; i++){
                try{
                    String urlString = "http://people.eku.edu/styere/quiz.php?id=" +  i;
                    URL url = new URL(urlString);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
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
                        httpURLConnection.disconnect();

                        String resp = sb.toString();
                        //StringBuilder bs = new StringBuilder();
                        //get the query as a JSON object
                        JSONObject mResponseObject = new JSONObject( resp );
                        //JSONObject responseData = mResponseObject.getJSONObject("title");

                        //get the results as an array
                        //JSONArray array = responseData.getJSONArray( "question" );
                        JSONArray array = mResponseObject.getJSONArray("qlist");

                        //question 1
                        JSONObject arrayObject1 = array.getJSONObject(0);
                        String question1 = arrayObject1.getString("question");
                        Boolean answer1 = Boolean.parseBoolean(arrayObject1.getString("answer"));

                        //question 2
                        JSONObject arrayObject2 = array.getJSONObject(1);
                        String question2 = arrayObject2.getString("question");
                        Boolean answer2 = Boolean.parseBoolean(arrayObject2.getString("answer"));

                        //question 3
                        JSONObject arrayObject3 = array.getJSONObject(2);
                        String question3 = arrayObject3.getString("question");
                        Boolean answer3 = Boolean.parseBoolean( arrayObject3.getString("answer") );

                        //just for debugging
                        String s = "";
                        s = s+" ";
                    }



                }catch (Exception e){
                    Log.w("",e.getMessage());
                }
            }
        }

        //String...params is an array of paramaters
        protected String doInBackground(String...params){

            int num = getNumQuizzes();
            getQuiz(num);

            return"stub";
            //Debug.waitForDebugger();

        }

        //process the results
        protected void onPostExecute( String resp ){
            update(resp);
           /*
            try {
                StringBuilder sb = new StringBuilder();
                //get the query as a JSON object
                JSONObject mResponseObject = new JSONObject( resp);
                //JSONObject responseData = mResponseObject.getJSONObject( "count");
                //get the results as an array
                //JSONArray array = responseData.getJSONArray( "results" );
                String m = mResponseObject.getString("count");
                numQuizes = Integer.valueOf( m );
                textView.setText(m);

                */
                /*
                //process the first five results
                for(int i=0; i<array.length(); i++){
                    String title = array.getJSONObject(i).getString("title" );
                    String qlist = array.getJSONObject(i).getString("qlist");
                    String question = array.getJSONObject(i).getString("question");
                    String answer = array.getJSONObject(i).getString("answer");

                    textView.setText(title.toString() +" "+ qlist.toString() +" "+ question.toString() +" "+ answer.toString());
                } */

      /*      }catch (Exception e){
                textView.setText(e.getMessage());
            }*/

        }
    }

    public void update(String m){
        textView.setText(m);
    }
}


