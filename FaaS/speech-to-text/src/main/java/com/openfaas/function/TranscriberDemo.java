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
	String firstHypothesis = "";
	boolean first = true;
        while ((result = recognizer.getResult()) != null) {
	    System.out.format("(TranscriberDemo) Hypothesis: %s\n", result.getHypothesis());
	    if(first) {
		first = false;
		firstHypothesis = result.getHypothesis();
	    }
	}
	recognizer.stopRecognition();
	
	return firstHypothesis;
    }
}

