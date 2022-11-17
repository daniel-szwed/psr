package pl.kielce.tu.mergesort;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class SortController {

    @RequestMapping("/")
    public String helloWorld(){
        return "Hello World from Spring Boot";
    }

    @RequestMapping(value = "/sort", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Double[] sort(@RequestBody Double[] array) {
        return mergeSort(array);
    }

    public static Double[] mergeSort(Double[] array) {
        List<Double[]> subarrays = new ArrayList<>();
        int i;
        for (i = 0; i < array.length; i++) {
            subarrays.add(new Double[] { array[i] });
        }

        Double[] result = null;
        while (result == null) {
            List<Tuple<Double[]>> requests = new ArrayList<>();
            for (i = 0; i < subarrays.size(); i += 2) {
                Tuple<Double[]> request;
                if (i + 1 < subarrays.size()) {
                    request = new Tuple<>(subarrays.get(i), subarrays.get(i + 1));
                } else {
                    request = new Tuple<>(subarrays.get(i), new Double[0]);
                }
                requests.add(request);
            }
            List<Double[]> responses = Collections.synchronizedList(new ArrayList<>());
            requests.parallelStream().forEach(request -> {
                Double[] response = mergeTwoSortedArrays(request.getLeft(), request.getRight(), Double[].class);
                responses.add(response);
            });
            subarrays = responses;
            if (subarrays.size() == 1) {
                result = subarrays.get(0);
            }
        }

        return result;
    }

    public static <T extends Comparable<T>> T[] mergeTwoSortedArrays(T[] one, T[] two, Class<T[]> tClass) {
        T[] result = (T[]) Array.newInstance(one[0].getClass(), one.length + two.length);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost("http://localhost:8090/merge");
        Tuple body = new Tuple(one, two);

        Gson gson = new Gson();
        String json = gson.toJson(body);

        StringEntity stringEntity = new StringEntity(json);
        request.addHeader("content-type", "application/json");
        request.setEntity(stringEntity);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseStringContent = EntityUtils.toString(entity);
                System.out.println(responseStringContent);
                result = (T[]) gson.fromJson(responseStringContent, result.getClass());
            }

        } catch (IOException | ParseException exception) {
            System.out.println("ERROR" + exception.getMessage());
            exception.printStackTrace();
        }
        return result;
    }
}