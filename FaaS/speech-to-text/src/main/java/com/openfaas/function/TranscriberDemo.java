package com.openfaas.function;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

public class TranscriberDemo {       

    public static String speechToText(String wavInBase64) throws Exception {

        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

	StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
	
        byte[] decodedBytes = Base64.getDecoder().decode(wavInBase64);
        InputStream stream = new ByteArrayInputStream(decodedBytes);

        recognizer.startRecognition(stream);
	SpeechResult result;
        List<String> stringList = new ArrayList<>();
	boolean first = true;
        while ((result = recognizer.getResult()) != null) {
            String text = result.getHypothesis();
	    System.out.format("(TranscriberDemo) Hypothesis: %s\n", text);
            stringList.add(string);
	}
	recognizer.stopRecognition();
	
	// Build the JSON string manually
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < stringList.size(); i++) {
            String string = stringList.get(i);
            jsonBuilder.append("\"").append(string).append("\"");
            if (i < stringList.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        String jsonString = jsonBuilder.toString();
	
	return jsonString;
    }
}

