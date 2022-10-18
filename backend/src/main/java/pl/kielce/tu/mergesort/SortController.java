package pl.kielce.tu.mergesort;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

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
                // TODO: calculate result in separate service
                Double[] response = mergeTwoSortedArrays(request.getLeft(), request.getRight());
                responses.add(response);
            });
            subarrays = responses;
            if (subarrays.size() == 1) {
                result = subarrays.get(0);
            }
        }

        return result;
    }

    public static <T extends Comparable<T>> T[] mergeTwoSortedArrays(T[] one, T[] two) {
        if (one.length < 1) {
            return two;
        }

        T[] sorted = (T[])Array.newInstance(one[0].getClass(), one.length + two.length);

        int i = 0, j = 0, k = 0;

        while (i < one.length && j < two.length) {
            if (one[i].compareTo(two[j]) <= 0) {
                sorted[k++] = one[i++];
            } else {
                sorted[k++] = two[j++];
            }
        }
        while (i < one.length) {
            sorted[k++] = one[i++];
        }
        while (j < two.length) {
            sorted[k++] = two[j++];
        }

        return sorted;
    }
}