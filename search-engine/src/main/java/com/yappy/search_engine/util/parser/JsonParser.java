package com.yappy.search_engine.util.parser;

import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonParser;
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

            jsonParser.close(); // close the parser
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transcriptionAudios;
    }

   /* public List<TranscriptionAudio> parseJson(InputStream inputStream) {
        List<TranscriptionAudio> transcriptionAudios = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Iterate over top-level keys (video URLs)
            Iterator<String> keys = rootNode.fieldNames();
            while (keys.hasNext()) {
                String videoUrl = keys.next();
                JsonNode videoDataArray = rootNode.get(videoUrl);

                // Extract texts for the current video
                JsonNode textsArray = videoDataArray.get(0);
                StringBuilder texts = new StringBuilder();
                for (int i = 0; i < textsArray.size(); i++) {
                    JsonNode segment = textsArray.get(i);
                    String text = segment.get(4).asText();
                    texts.append(text.trim());
                    if (i < textsArray.size() - 1) {
                        texts.append(" ");
                    }
                }

                // Extract language for the current video
                JsonNode languageData = videoDataArray.get(1);
                String language = languageData.get(0).asText();

                TranscriptionAudio transcriptionAudio = new TranscriptionAudio(videoUrl, texts.toString(), language);
                transcriptionAudios.add(transcriptionAudio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transcriptionAudios;
    }*/

    /*public List<TranscriptionAudio> parseJson(InputStream inputStream) {
        List<TranscriptionAudio> transcriptionAudios = new ArrayList<>();
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject jsonObject = new JSONObject(tokener);

        // Итерация по ключам верхнего уровня (ссылкам на видео)
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String videoUrl = keys.next();
            JSONArray videoDataArray = jsonObject.getJSONArray(videoUrl);

            // Извлечение текстов для текущего видео
            JSONArray textsArray = videoDataArray.getJSONArray(0);
            StringBuilder texts = new StringBuilder();
            for (int i = 0; i < textsArray.length(); i++) {
                JSONArray segment = textsArray.getJSONArray(i);
                String text = segment.getString(4);
                texts.append(text.trim());
                if (i < textsArray.length() - 1) {
                    texts.append(" ");
                }
            }

            // Извлечение языка для текущего видео
            JSONArray languageData = videoDataArray.getJSONArray(1);
            String language = languageData.getString(0);

            TranscriptionAudio transcriptionAudio = new TranscriptionAudio(videoUrl, texts.toString(), language);
            transcriptionAudios.add(transcriptionAudio);
        }
        return transcriptionAudios;
    }*/
}
