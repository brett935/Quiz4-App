//
//
//Brett Napier
//CSC309 EKU
//Assignment 7
//4-19-2016
//
//
package com.brettnapier.quiz4;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView questionView;
    TextView scoreView;
    TextView questionNumView;
    TextView currentQuizView;
    Button buttonTrue;
    Button buttonFalse;
    private String title;
    private String question1;
    private String question2;
    private String question3;
    private Boolean answer1;
    private Boolean answer2;
    private Boolean answer3;
    private int currentQuiz;
    private int currentQuestion;
    private int correctAnswers;
    List<String> quizList = new ArrayList<>();
    List<String> questionList = new ArrayList<>();
    List<Boolean> answerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView_quiznum);
        questionView = (TextView) findViewById( R.id.textView_question);
        scoreView = (TextView) findViewById( R.id.textView_score );
        questionNumView = (TextView) findViewById( R.id.textView_questionnum );
        currentQuizView = (TextView) findViewById( R.id.textView_quiznum );

        new dataRequest().execute(); //create object that downloads JSON and parses and stores it
        currentQuestion=0; //start on the first quiz

        //true button
        buttonTrue = (Button) findViewById( R.id.button_true );
        buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button function after no quizzes left
                if(currentQuestion<questionList.size()) {
                    //get the answer for the current question and compare it to button
                    if (answerList.get(currentQuestion) == Boolean.TRUE) {
                        correctAnswers++;
                    }
                    currentQuestion++; //move to next quiz
                    update(); //call function to update the textview to current quiz
                }
                else {
                    //hide buttons and questions
                    questionView.setVisibility(View.INVISIBLE);
                    buttonTrue.setVisibility(View.INVISIBLE);
                    buttonFalse.setVisibility(View.INVISIBLE);
                }

            }
        });
        //false button
        buttonFalse = (Button) findViewById( R.id.button_false );
        buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button function after no quizzes left
                    if(currentQuestion<questionList.size()) {
                        //get the answer for the current question and compare it to button
                        if (answerList.get(currentQuestion) == Boolean.FALSE) {
                            correctAnswers++;
                        }
                        currentQuestion++; //move to next quiz                                  /////////////////////////problem here, check to make sure it is less than num quizzes
                        update(); //call function to update the textview to current quiz
                    }
                    else {
                        //hide buttons and questions
                        questionView.setVisibility(View.INVISIBLE);
                        buttonTrue.setVisibility(View.INVISIBLE);
                        buttonFalse.setVisibility(View.INVISIBLE);
                    }
            }
        });

    }

    //class to request JSON data from online server and parse it to list
    class dataRequest extends AsyncTask<String,Void,String>{

        private int numQuizes; //stores how many quizes were retrieved

        //retrieves the amount of quizes available to download
        private int getNumQuizzes(){
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
                    String m = mResponseObject.getString("count");
                    numQuizes = Integer.valueOf( m );
                }
            }catch (Exception e){
                textView.setText("Error");
            }

            return (numQuizes);
        }

        //downloads, parses JSON and stores it in list
        private void getQuiz(int numQuizes){
            //
            //change back to (int i=0; i<array.length();i++)
            //
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
                        title = mResponseObject.getString("title");
                        quizList.add(title); //store in list

                        //get the results as an array
                        //JSONArray array = responseData.getJSONArray( "question" );
                        JSONArray array = mResponseObject.getJSONArray("qlist");


                        //question 1
                        JSONObject arrayObject1 = array.getJSONObject(0);
                        question1 = arrayObject1.getString("question");
                        answer1 = Boolean.parseBoolean(arrayObject1.getString("answer"));
                        questionList.add( question1 ); //store in list
                        answerList.add( answer1 ); //store in list

                        //question 2
                        JSONObject arrayObject2 = array.getJSONObject(1);
                        question2 = arrayObject2.getString("question");
                        answer2 = Boolean.parseBoolean(arrayObject2.getString("answer"));
                        questionList.add( question2 ); //store in list
                        answerList.add( answer2 ); //store in list

                        //question 3
                        JSONObject arrayObject3 = array.getJSONObject(2);
                        question3 = arrayObject3.getString("question");
                        answer3 = Boolean.parseBoolean(arrayObject3.getString("answer"));
                        questionList.add( question3 );
                        answerList.add( answer3 );
                    }

                }catch (Exception e){
                    Log.w("",e.getMessage()); //write error to log if any
                }
            }
        }

        //String...params is an array of paramaters
        protected String doInBackground(String...params){

            int num = getNumQuizzes();
            getQuiz(num);

            return"";
        }

        //process the results
        protected void onPostExecute( String resp ){
            update(); //update views after JSON has been stored
        }
    }

    public void update(){
        //textView.setText();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update views
                questionView.setText( questionList.get(currentQuestion) ); //load current question in the view
                scoreView.setText( "Score: " + correctAnswers );
                questionNumView.setText( "Question: " + (currentQuestion+1) );
                //determine what quiz you are on, each quiz has 3 questions
                if (currentQuestion%3==0){
                    currentQuiz++;
                }
                currentQuizView.setText( "Quiz: " + currentQuiz );

            }
        });
    }

    //called when app is exited
    protected void onPause(){
        super.onPause();

        //change the saved value
        final SharedPreferences preferences = getSharedPreferences( "appdata", MODE_PRIVATE );
        SharedPreferences.Editor prefEdit = preferences.edit();
        prefEdit.putInt( "questionNum" , currentQuestion );
        prefEdit.commit();

    }
    protected void onResume(){
        super.onResume();

        //get the current value from shared preferences
        final SharedPreferences preferences = getSharedPreferences( "appdata", MODE_PRIVATE );
        int currentQuestion = preferences.getInt("questionNum", 0);

    }

}


