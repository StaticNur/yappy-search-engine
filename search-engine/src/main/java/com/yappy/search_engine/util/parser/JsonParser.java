package com.yappy.search_engine.util.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yappy.search_engine.model.TranscriptionAudio;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonParser {

    public List<TranscriptionAudio> parseJson(InputStream inputStream) {
        List<TranscriptionAudio> transcriptionAudios = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonFactory jsonFactory = objectMapper.getFactory();
            com.fasterxml.jackson.core.JsonParser jsonParser = jsonFactory.createParser(inputStream);

            while (!jsonParser.isClosed()) {
                JsonToken jsonToken = jsonParser.nextToken();
                if (jsonToken == null) break;

                if (jsonToken == JsonToken.FIELD_NAME) {
                    String url = jsonParser.getCurrentName();
                    if (url.matches("^https://cdn-st\\.rutubelist\\.ru/media.*")) {
                        StringBuilder transcription = new StringBuilder();
                        String language = null;

                        jsonParser.nextToken(); // move to the value array

                        // Extract texts
                        jsonParser.nextToken(); // move to the first inner array
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                    if (jsonParser.currentToken() == JsonToken.VALUE_STRING) {
                                        transcription.append(jsonParser.getText()).append(" ");
                                    } else {
                                        jsonParser.skipChildren(); // skip non-string elements
                                    }
                                }
                            } else {
                                jsonParser.skipChildren(); // skip non-array elements
                            }
                        }

                        // Extract language
                        jsonParser.nextToken(); // move to the second inner array
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            if (jsonParser.currentToken() == JsonToken.VALUE_STRING) {
                                language = jsonParser.getText();
                            }
                        }

                        TranscriptionAudio transcriptionAudio = new TranscriptionAudio(url, transcription.toString().trim(), language);
                        transcriptionAudios.add(transcriptionAudio);
                    }
                }
            }
            jsonParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transcriptionAudios;
    }
}
