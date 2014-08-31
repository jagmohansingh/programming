
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author Jagmohan
 */
public class Main {

    private List<String> input;
    private String primaryString;
    private String[] searchStrings;
    private String[] replaceStrings;
    private Map<Integer, Boolean> marked;

    public Main() {
        input = new ArrayList<String>();
    }

    private void readInput(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                while ((line = reader.readLine()) != null) {
                    input.add(line);
                }
                reader.close();
                fis.close();
            } catch (FileNotFoundException ex) {
                System.err.print("##### --> " + ex.getMessage());
            } catch (IOException ex) {
                System.err.print("##### --> " + ex.getMessage());
            }
        }
    }

    private void buildData(String line) {
        primaryString = null;
        searchStrings = null;
        replaceStrings = null;

        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            switch (i) {
                case 0:
                    primaryString = tokenizer.nextToken();
                    break;
                case 1:
                    String str = tokenizer.nextToken();
                    StringTokenizer st = new StringTokenizer(str, ",");
                    int size = st.countTokens() / 2;
                    searchStrings = new String[size];
                    replaceStrings = new String[size];
                    int j = 0;
                    while (st.hasMoreTokens()) {
                        if (j % 2 == 0) {
                            searchStrings[j / 2] = st.nextToken();
                        } else {
                            replaceStrings[j / 2] = st.nextToken();
                        }
                        j++;
                    }
                    break;
            }
            i++;
        }
    }

    private String processInput() {        
        StringBuilder output = new StringBuilder(primaryString);
        marked = new TreeMap<Integer, Boolean>();

        for (int i = 0; i < searchStrings.length; i++) {
            String search = searchStrings[i];
            String replace = replaceStrings[i];

            int foundAt = -1, from = 0;            
            while ((foundAt = output.indexOf(search, from)) != -1) {
                from = foundAt + replace.length();

                boolean canReplace = true;                
                for (int k = foundAt; k < foundAt + search.length(); k++) {
                    if (marked.containsKey(Integer.valueOf(k))) {
                        canReplace = false;
                        break;
                    }
                }

                if (canReplace) {
                    // replace string
                    output = output.delete(foundAt, foundAt + search.length());
                    output = output.insert(foundAt, replace);
                    // shift the marked indices
                    if (replace.length() > search.length()) {
                        shiftRight(foundAt, output.length(), replace.length() - search.length());
                    } else if (replace.length() < search.length()) {
                        shiftLeft(foundAt, output.length(), search.length() - replace.length());
                    }
                    // add new indices
                    for (int r = foundAt; r < from; r++) {
                        marked.put(Integer.valueOf(r), Boolean.TRUE);
                    }                    
                } else {                    
                    from = foundAt + 1;
                }                
            }
        }
        return output.toString();
    }

    private void shiftRight(int from, int length, int shift) {
        Map<Integer, Boolean> temp = new TreeMap<Integer, Boolean>();
        for (int j = from; j < length; j++) {
            Integer key = Integer.valueOf(j);
            if (marked.containsKey(key)) {
                marked.remove(key);
                temp.put(Integer.valueOf(j + shift), Boolean.TRUE);
            }
        }
        marked.putAll(temp);
    }

    private void shiftLeft(int from, int length, int shift) {
        Map<Integer, Boolean> temp = new TreeMap<Integer, Boolean>();
        for (int j = from; j < length; j++) {
            Integer key = Integer.valueOf(j);
            if (marked.containsKey(key)) {
                marked.remove(key);
                temp.put(Integer.valueOf(j - shift), Boolean.TRUE);
            }
        }
        marked.putAll(temp);
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            String fileName = args[0];

            // read input
            Main obj = new Main();
            obj.readInput(fileName);

            for (String line : obj.input) {
                obj.buildData(line);
                System.out.println(obj.processInput());
            }
        } else {
            System.err.print("##### --> No input!");
        }
    }
}
