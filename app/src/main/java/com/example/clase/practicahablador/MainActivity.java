package com.example.clase.practicahablador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clase.practicahablador.robot.ChatterBot;
import com.example.clase.practicahablador.robot.ChatterBotFactory;
import com.example.clase.practicahablador.robot.ChatterBotSession;
import com.example.clase.practicahablador.robot.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextToSpeech textToSpeech;//para el hablador de android
    EditText editText;//editext para recojer el texto
    private boolean señal;//señal
    private ScrollView scrollView;
    private TextView tv;//textview para imprimir los resultados
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        tv = (TextView)findViewById(R.id.textView);
        this.init();
    }
    public void init(){
        Intent intent= new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 0);
    }
    //------metodos de los botones------------------------------
    public void btEnciarTexto(View v){
        //obtenemos el texto escrito en el editext
        String texto = editText.getText().toString();
        //para el chatbot
       // this.leer(texto);
        Tarea t= new Tarea();
        t.execute(texto);
        //---no lo consigo----
       // TextView tv = new TextView(this);
       // tv.setText(texto);
      //  scrollView.addView(tv);//añadimos los texview
        tv.append("tu:" + texto + "\n");
    }
    public void btEnviarSonido(View v){
        escribir();
    }
 //---metodo implementado del TextToSpeech.OnInitListener---------------------------------
 //---- indica la terminación de la inicialización del motor de síntesis de voz.
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            //se puede reproducir
            señal = true;
        } else{
            //no se puede reproducir
            señal = false;
        }
    }
    //--------------- recibir el resultado de nuevo--------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 0) {
            //si ACTION_CHECK_TTS_DATA tiene exito
            //el estado tiene exito entonces procede
            if(resultCode== TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech= new TextToSpeech(this, this);
                textToSpeech.setLanguage(Locale.getDefault());
            } else{
                Intent intent= new Intent();
      //niciar la actividad que instala los archivos de recursos en el dispositivo que se requieren para TTS esté en marcha.
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if(requestCode== 1) {
            ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          //  TextView tv = new TextView(this);
           // scrollView.addView(tv);
            Tarea t= new Tarea();
            t.execute(textos.get(0));
            tv.append("tu:" + textos.get(0) + "\n");
            //leer(textos.get(0));
        }
    }
    //---------------------------------------------------------------------------------------
    public void leer(String a){
        if(señal) {
              textToSpeech.setLanguage(Locale.CHINA);//no entiendo es el qu lee mejor
              textToSpeech.setPitch((float) 1.0);
              textToSpeech.speak(a, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(this, "Fallo de lectura", Toast.LENGTH_SHORT).show();
        }


    }

    public void escribir(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, 1);
    }
    //---------------------------------------------------------------------------------------
    //----------------asytac hebra en segundo palno para el chartbot-----------------------
    public class Tarea extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot1 = null;
            try {
              //  bot1 = factory.create(ChatterBotType.PANDORABOTS,"b0dafd24ee35a477");
                //solo habla en ingles el de arriba
                bot1 = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession bot1session = bot1.createSession();
            String s = params[0];

            try {
                return bot1session.think(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String out) {
            System.out.println("biennn");
           // TextView tv = new TextView(MainActivity.this);
           String contar=tv.getText().toString();

            tv.setText(contar+"bot> " + out + "\n");
           // scrollView.addView(tv);
            leer(out);
            System.out.println("malll:" + out);

        }
    }
}
