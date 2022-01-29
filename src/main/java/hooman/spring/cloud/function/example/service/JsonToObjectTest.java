package hooman.spring.cloud.function.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import hooman.spring.cloud.function.example.model.RequestObject;

public class JsonToObjectTest {

    public static void main(String[] args) throws JsonProcessingException {
        Gson gson = new Gson();
        String personStr = "{\"id\":240,\"name\": \"Hooman\",\"nachname\": \"Paknahad\"}";
        RequestObject gsonObj = gson.fromJson(personStr, RequestObject.class);
        System.out.println(gsonObj);

        ObjectMapper objectMapper = new ObjectMapper();
        RequestObject jacksonObj =  objectMapper.readValue(personStr, RequestObject.class);
        System.out.println(jacksonObj);
    }
}
