package pl.kielce.tu.mergeservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;

@RestController
public class MergeController {
    @Autowired
    private RootHost rootHost;

    @RequestMapping(value = "/merge", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Double[] sort(@RequestBody Tuple<Double[]> arraysToMerge) {
        Double[] arrayA = arraysToMerge.getLeft();
        Double[] arrayB = arraysToMerge.getRight();
        String logA = "Empty array";
        String logB = "Empty array";
        String logAB = "Empty array";
        if (arrayA != null && arrayA.length > 0) {
            logA = arrayToString(arrayA, "A");
        }
        if (arrayB != null && arrayB.length > 0) {
            logB = arrayToString(arrayB, "B");
        }

        Double[] merged = mergeTwoSortedArrays(arraysToMerge.getLeft(), arraysToMerge.getRight());
        logAB = arrayToString(merged, "A + B") + "\n";
        Printer.getInstance()
                .enqueueLogMessage(logA, logB, logAB)
                .printMessages();

        return merged;
    }

    private String arrayToString(Double[] arrayA, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Tablica %s: [", name));
        Arrays.stream(arrayA)
                .forEach(item -> {
                    sb.append(item.toString() + ", ");
                });
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
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