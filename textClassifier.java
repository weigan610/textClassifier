import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class textClassifier {
    public static final double EPSILON = 0.1;
    public static ArrayList<people> peoples = new ArrayList<>();
    public static ArrayList<String> words_T = new ArrayList<>();
    public static HashMap<String, Integer> Occ_T = new HashMap<>();
    public static HashMap<String, Double> Freq_T = new HashMap<>();
    public static HashMap<String, Integer> Occ_T_WC = new HashMap<>();
    public static HashMap<String, Double> Freq_T_WC = new HashMap<>();
    public static HashMap<String, Double> P_C = new HashMap<>();
    public static HashMap<String, Double> P_WC = new HashMap<>();
    public static HashMap<String, Double> L_C = new HashMap<>();
    public static HashMap<String, Double> L_WC = new HashMap<>();
    public static HashMap<String, Double> L_CB = new HashMap<>();
    public static HashMap<String, Double> Prediction = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // The training set's cardinality 
        int T_size = Integer.parseInt(args[1]);

        // begin reading the corpus and populating the peoples array representing the whole corpus 
        String eachLine;
        try {
            FileReader fr = new FileReader(args[0]);
            BufferedReader br = new BufferedReader(fr);

            int index = 0;
            people eachPerson = new people();
            ArrayList<String> biographies = new ArrayList<>();
            while ((eachLine = br.readLine()) != null) {
                eachLine = eachLine.trim();
                if (eachLine.equals("")) {
                    // We as long as a word appear once, it should count, but more than once will not 
                    if (biographies.size() != 0) {
                        biographies = helper.removeDuplicates(biographies);
                        eachPerson.setBiographies(biographies);
                        peoples.add(eachPerson);
                    }

                    index = 0;
                    eachPerson = new people();
                    biographies = new ArrayList<>();
                    continue;
                }
                switch(index) {
                    case 0: 
                        eachPerson.setName(eachLine);
                        index++;
                        break;
                    case 1: 
                        eachPerson.setCategory(eachLine);
                        index++;
                        break;
                    case 2: 
                        String[] eachLineSplitted = eachLine.split(" ");
                        for (int j = 0; j < eachLineSplitted.length; j++) {
                            // trim the ending comma/terminator
                            if (eachLineSplitted[j].contains(".")) eachLineSplitted[j] = eachLineSplitted[j].replaceAll(".$", "");
                            else if (eachLineSplitted[j].contains(",")) eachLineSplitted[j] = eachLineSplitted[j].replaceAll(",$", "");

                            // turn each word into lowercase 
                            eachLineSplitted[j] = eachLineSplitted[j].toLowerCase();

                            // Check if it's a stopping word and add if otherwise 
                            if (! helper.containStop(eachLineSplitted[j])) biographies.add(eachLineSplitted[j]);
                        }
                        break;
                }
            }

            br.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        // populate the Occ_T and words_T 
        for (int i = 0; i < T_size; i++) {
            people currentPerson = peoples.get(i);
            String currentCategory = currentPerson.getCategory();
            ArrayList<String> biographies = currentPerson.getBiographies();

            int updateValue = (Occ_T.get(currentCategory) == null) ? 1 : Occ_T.get(currentCategory) + 1;
            Occ_T.put(currentCategory, updateValue);

            words_T.addAll(biographies);
        }
        words_T = helper.removeDuplicates(words_T);

        // populate the Freq_T 
        // because the Occ_T already represents the data for the training set, we can directly iterate through Occ_T to populate our Freq_T 
        for (Map.Entry<String, Integer> entry : Occ_T.entrySet()) {
            String currentCategory = entry.getKey();
            int currentValue = (int) entry.getValue();
            double calculatedValue = (double) currentValue / T_size;
            Double updateValue = Double.valueOf(calculatedValue);
            Freq_T.put(currentCategory, updateValue);
        }

        // populate the Occ_T_WC 
        for (int i = 0; i < T_size; i++) {
            people currentPerson = peoples.get(i);
            String currentCategory = currentPerson.getCategory();
            ArrayList<String> biographies = currentPerson.getBiographies();

            for (int j = 0; j < biographies.size(); j++) {
                String currentWord = biographies.get(j);
                // The title of each table entry is simply the concatenation of the following: 
                // current category, "_", and current word. 
                String tableEntry = currentCategory + "_" + currentWord;

                int updateValue = (Occ_T_WC.get(tableEntry) == null) ? 1 : Occ_T_WC.get(tableEntry) + 1;
                Occ_T_WC.put(tableEntry, updateValue);
            }
        }

        // populate the Freq_T_WC 
        for (Map.Entry<String, Integer> entry : Occ_T_WC.entrySet()) {
            String currentWC = entry.getKey();
            String currentCategory = currentWC.substring(0, currentWC.indexOf("_"));
            double calculatedValue = (double) entry.getValue() / Occ_T.get(currentCategory);
            Double updateValue = Double.valueOf(calculatedValue);
            Freq_T_WC.put(currentWC, updateValue);
        }

        // populate the P_C and L_C 
        for (Map.Entry<String, Double> entry : Freq_T.entrySet()) {
            String currentCategory = entry.getKey();
            double currentValue = (double) entry.getValue();
            double calculatedValue = (currentValue + EPSILON) / (1 + Occ_T.size() * EPSILON);
            Double updateValue = Double.valueOf(calculatedValue);
            P_C.put(currentCategory, updateValue);

            double L_C_Value = (-1) * Math.log(calculatedValue) / Math.log(2);
            L_C.put(currentCategory, L_C_Value);
        }

        // populate the P_WC and L_WC 
        for (Map.Entry<String, Double> entry : Freq_T_WC.entrySet()) {
            String currentWC = entry.getKey();
            double currentValue = (double) entry.getValue();
            double calculatedValue = (currentValue + EPSILON) / (1 + 2 * EPSILON);
            Double updateValue = Double.valueOf(calculatedValue);
            P_WC.put(currentWC, updateValue);

            double L_WC_Value = (-1) * Math.log(calculatedValue) / Math.log(2);
            L_WC.put(currentWC, L_WC_Value);
        }

        // Applying the classifier 
        double tackleNull = EPSILON / (1 + 2 * EPSILON);
        tackleNull = (-1) * Math.log(tackleNull) / Math.log(2);
        // foreach biography: 
        for (int i = T_size; i < peoples.size(); i++) {
            people currentPerson = peoples.get(i);
            ArrayList<String> biographies = currentPerson.getBiographies();
            // foreach category: 
            for (Map.Entry<String, Double> entry : L_C.entrySet()) {
                String currentCategory = entry.getKey();
                double currentValue = (double) entry.getValue();
                double updateValue = currentValue;
                // summing up the words in the current biography 
                for (String eachWord : biographies) {
                    // Skip any words that do not occur in the training set 
                    if (words_T.contains(eachWord)) {
                        String currentWC = currentCategory + "_" + eachWord;
                        updateValue += (L_WC.get(currentWC) == null) ? tackleNull : L_WC.get(currentWC);
                    }
                }
                L_CB.put(currentCategory + "_" + currentPerson.getName(), updateValue);
            }
        }

        // Choosing the least value of L(C|B) and populate the Prediction 
        Prediction = helper.filterMin(L_CB);

        // Printing out the results 
        int rightNum = 0;
        int wrongNum = 0;
        for (int i = T_size; i < peoples.size(); i++) {
            people currentPerson = peoples.get(i);
            String name = currentPerson.getName();
            String category = currentPerson.getCategory();
            for (Map.Entry<String, Double> entry : Prediction.entrySet()) {
                if (name.equals(entry.getKey().substring(entry.getKey().indexOf("_") + 1))) {
                    String returnedCategory = entry.getKey().substring(0, entry.getKey().indexOf("_"));
                    if (category.equals(returnedCategory)) {
                        System.out.println(name + ".\tPrediction: " + returnedCategory + ".\tRight");
                        rightNum++;
                    }
                    else {
                        System.out.println(name + ".\tPrediction: " + returnedCategory + ".\tWrong");
                        wrongNum++;
                    }
                    helper.recoverProb(L_CB, name);
                    System.out.println();
                }
            }
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");
        System.out.println("Overall accuracy: " + formatter.format((double) rightNum / (rightNum + wrongNum)));
    }
}

