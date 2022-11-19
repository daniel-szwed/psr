package pl.kielce.tu.mergeservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;

@RestController
public class MergeController {
    @Autowired
    private RootHost rootHost;

    @RequestMapping("/")
    public String helloWorld(){
        return rootHost.getAddress();
    }

    @RequestMapping(value = "/merge", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Double[] sort(@RequestBody Tuple<Double[]> arraysToMerge) {
        return mergeTwoSortedArrays(arraysToMerge.getLeft(), arraysToMerge.getRight());
    }

    public static <T extends Comparable<T>> T[] mergeTwoSortedArrays(T[] one, T[] two) {
        if (one.length < 1) {
            return two;
        }

        T[] sorted = (T[]) Array.newInstance(one[0].getClass(), one.length + two.length);

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