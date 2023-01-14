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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class SortController {
    @Autowired
    private MergeServiceProvider mergeServiceProvider;

    @RequestMapping("/register")
    public String register(HttpServletRequest request, @RequestBody String port) {
        String remoteAddress = request.getRemoteHost() + ":" + port;
        mergeServiceProvider.addNode(remoteAddress);
        return remoteAddress;
    }

    @RequestMapping("/unregister")
    public String unregister(HttpServletRequest request) {
        String remoteAddress = request.getRemoteHost();
        mergeServiceProvider.removeNode(remoteAddress);
        return remoteAddress;
    }

    @RequestMapping(value = "/sort", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Double[]> sort(@RequestBody Double[] array) {
        try {
            return new ResponseEntity<>(mergeSort(array), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double[] mergeSort(Double[] array) throws Exception {
        List<Double[]> subarrays = new ArrayList<>();
        int i;
        for (i = 0; i < array.length; i++) {
            subarrays.add(new Double[] { array[i] });
        }
        AtomicBoolean failed = new AtomicBoolean(false);
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
                Double[] response = new Double[0];
                try {
                    response = mergeTwoSortedArrays(request.getLeft(), request.getRight());
                } catch (Exception e) {
                    failed.set(true);
                }
                responses.add(response);
            });
            if(failed.get())
                throw new Exception("Service unavailable");
            subarrays = responses;
            if (subarrays.size() == 1) {
                result = subarrays.get(0);
            }
        }

        return result;
    }

    public <T extends Comparable<T>> T[] mergeTwoSortedArrays(T[] one, T[] two) throws Exception {
        if (one.length == 0)
            return two;

        T[] result = (T[]) Array.newInstance(one[0].getClass(), one.length + two.length);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String address = mergeServiceProvider.getNextNodeAddress();
        int counter = 0;
        while(counter < 10) {
            HttpPost request = new HttpPost(String.format("http://%s/merge", address));
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
                    System.out.println("Odpowiedz serwisu scalajacego:");
                    System.out.println(responseStringContent);
                    return (T[]) gson.fromJson(responseStringContent, result.getClass());
                }

            } catch (IOException | ParseException exception) {
                System.out.println("ERROR" + exception.getMessage());
                exception.printStackTrace();
                address = mergeServiceProvider.failureOccurs(address);
            } finally {
                counter += 1;
            }
        }
        return result;
    }
}