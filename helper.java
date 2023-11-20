import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class helper {
    public static boolean containStop(String eachWord) {
        ArrayList<String> stops = new ArrayList<>(Arrays.asList("about", "all", "along", "also", "although", "among", "and", "any", "anyone", "anything", "are", "around", "because", "been", "before", "being", "both", "but", "came", "come", "coming", "could", "did", "each", "else", 
        "every", "for", "from", "get", "getting", "going", "got", "gotten", "had", "has", "have", "having", "her", "here", "hers", "him", "his", "how", "however", "into", "its", "like", "may", "most", "next", "now", "only", "our", "out", "particular", "same", "she", "should", "some", "take", "taken", "taking", "than", "that", "the", 
        "then", "there", "these", "they", "this", "those", "throughout", "too", "took", "very", "was", "went", "what", "when", "which", "while", "who", "why", "will", "with", "without", "would", "yes", "yet", "you", "your", "com", "doc", "edu", "encyclopedia", "fact", "facts", "free", "home", "htm", "html", "http", "information", "internet", "net", "new", "news", "official", "page", "pages", "resource", "resources", "pdf", "site", "sites", 
        "usa", "web", "wikipedia", "www", "one", "ones", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "tens", "eleven", "twelve", "dozen", "dozens", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety", "hundred", "hundreds", "thousand", "thousands", "million", "millions"));

        if (stops.contains(eachWord) || eachWord.length() <= 2) return true;
        return false;
    }

    public static ArrayList<String> removeDuplicates(ArrayList<String> array) {
        ArrayList<String> newArray = new ArrayList<>();
        for (String s : array) {
            if (newArray.contains(s)) continue;
            else newArray.add(s);
        }

        return newArray;
    }

    public static HashMap<String, Double> filterMin(HashMap<String, Double> HM) {
        HashMap<String, Double> newHM = new HashMap<>();
        HashMap<String, Double> output = new HashMap<>();

        for (Map.Entry<String, Double> entryOuter : HM.entrySet()) {
            String outerName = entryOuter.getKey();
            outerName = outerName.substring(outerName.indexOf("_") + 1);
            for (Map.Entry<String, Double> entryInner : HM.entrySet()) {
                String innerName = entryInner.getKey();
                innerName = innerName.substring(innerName.indexOf("_") + 1);

                if (outerName.equals(innerName)) {
                    double existedValue = (newHM.get(outerName) == null) ? Double.POSITIVE_INFINITY : newHM.get(outerName);
                    double updateValue = (entryInner.getValue() < existedValue) ? entryInner.getValue() : existedValue;
                    newHM.put(outerName, updateValue);
                }
            }
        }

        for (Map.Entry<String, Double> entryOuter : HM.entrySet()) {
            String outerName = entryOuter.getKey();
            outerName = outerName.substring(outerName.indexOf("_") + 1);
            double outerValue = entryOuter.getValue();

            for (Map.Entry<String, Double> entryInner : newHM.entrySet()) {
                String innerName = entryInner.getKey();
                innerName = innerName.substring(innerName.indexOf("_") + 1);
                double innerValue = entryInner.getValue();
    
                if (outerName.equals(innerName) && outerValue == innerValue) {
                    output.put(entryOuter.getKey(), outerValue);
                }
            }
        }

        return output;
    }

    public static void recoverProb(HashMap<String, Double> L_CB, String name) {
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Double> x_values = new ArrayList<>();
        for (Map.Entry<String, Double> entry : L_CB.entrySet()) {
            String currentName = entry.getKey().substring(entry.getKey().indexOf("_") + 1);
            if (name.equals(currentName)) {
                categories.add(entry.getKey().substring(0, entry.getKey().indexOf("_")));
                values.add(entry.getValue());
            }
        }

        Double min = findMin(values);
        for (Double value : values) {
            Double x;
            if ((value - min) < 7) {
                x = Math.pow(2, min - value);
            }
            else {
                x = 0.0;
            }
            x_values.add(x);
        }

        Double s = sumUp(x_values);

        // begin printing out the probabilities
        DecimalFormat formatter = new DecimalFormat("#0.00");
        for (int i = 0; i < categories.size(); i++) {
            if (i == categories.size() - 1) {
                System.out.print(categories.get(i) + ": " + formatter.format(x_values.get(i) / s));
                System.out.println();
            }
            else System.out.print(categories.get(i) + ": " + formatter.format(x_values.get(i) / s) + "\t");
        }
    }

    public static Double findMin(ArrayList<Double> values) {
        Double temp = Double.POSITIVE_INFINITY;
        for (Double value : values) {
            if (value < temp) temp = value;
        }
        return temp;
    }

    public static Double sumUp(ArrayList<Double> values) {
        Double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        return sum;
    }
}

