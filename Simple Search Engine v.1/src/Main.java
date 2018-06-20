
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        String Docs [] = {"dokument1.txt", "dokument2.txt", "dokument3.txt", "dokument4.txt", "dokument5.txt"};

        Map[] mapArray = new Map[Docs.length];
        int amountUnreadableDocs = 0;

        //Read all documents, create maparray with a map for each corresponding document
        //Each map has a word as the key and the term frequency (in that particular document) as the value
        for (int i = 0; i < Docs.length; i++)
        {
            mapArray[i] = listToMap(readFile(Docs[i]));

            //Count the unreadable docs to not count failed docs as existing ones when calculating IDF-value later
            if (mapArray[i].size() == 0)
            {
                amountUnreadableDocs++;
            }
        }

        //Put the documents into one map where a word is a key, the value is a list of the documents (in number) containing that word
        TreeMap<Object, List<Object>> wordMap = new TreeMap<Object, List<Object>>();
        wordMap = fillWordMap(wordMap, mapArray);
        

        //Asking for, and read a search word
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter a search word: ");
        String term = reader.next();

        //Change term to lower case
        term = term.toLowerCase();

        //Close scanner
        reader.close();

        //Taking the search term, checking which docs contain it via wordMap, extracts termfrequency from mapArray,
        // multiplies it by the IDF value, sorts the documents in order correlating to this, then returns the document names
        List<Object> searchResult = getSearchResult(term, wordMap, mapArray, Docs, amountUnreadableDocs);
        System.out.println(searchResult);
    }





    public static List<String> readFile(String fileName)
    {
        //This function reads the documents and return a list with each word as one element
        List<String> wordList = new ArrayList<String>();
        boolean docsReadSuccessfully = true;
        try
        {
            //Enables reader
            BufferedReader doc = new BufferedReader(new FileReader(new File(fileName)));

            //Create variables to extract each word
            boolean lastLine = false;
            String line;
            int indexWordStart;
            int indexWordEnd;
            int indexLastWordOfLine;
            List<String> wordListDoc = new ArrayList<String>();

            //Extracts all words and adds each one as an element to a list
            while (lastLine == false)
            {
                line = doc.readLine();
                if (line != null)
                {

                    indexWordStart = 0;
                    indexWordEnd = 0;
                    indexLastWordOfLine = line.lastIndexOf(" ");

                    //Looping through current line for each word
                    while (indexWordStart != indexLastWordOfLine + 1)
                    {
                        indexWordEnd = line.indexOf(" ", indexWordStart);

                        //Extract the next word, turn it into lower case and add it as the next element in the documents wordlist
                        wordListDoc.add(line.substring(indexWordStart, indexWordEnd).toLowerCase());
                        indexWordStart = indexWordEnd + 1;
                    }
                    wordListDoc.add(line.substring(indexLastWordOfLine +1));

                }
                else
                {
                    lastLine = true;
                    wordList = wordListDoc;
                }
            }
            doc.close();
        }
        catch (IOException e)
        {
            docsReadSuccessfully = false;
            System.out.println("The file named " + fileName + " could not be read");
        }
        return wordList;
    }
    public static TreeMap<String, Double> listToMap(List<String> list)
    {
        //This function takes a list of words and puts them into a map with the word as key
        // and the term frequency of that word as the value. It then returns the map.

        TreeMap<String, Double> map = new TreeMap<String, Double>();
        String word;
        double termFreq;
        double docLength = list.size();

        for (int i = 0; i < list.size(); i++)
        {
            word = list.get(i);
            if (!map.containsKey(word))
            {
                termFreq = 1/docLength;
                map.put(word,termFreq);
            }
            else
            {
                termFreq = (Double) map.get(word) + 1/docLength;
                map.put(word,termFreq);
            }
        }
        return map;
    }

    public static TreeMap<Object, List<Object>> fillWordMap(TreeMap<Object, List<Object>> wordMap, Map[] mapArray)
    {
        //This function put the "document-maps" into one map where a word is a key, the value is a list of the documents containing that word
        //The documents in the list are presented as the integer number with which they were entered into the list of documents at the start

        //The outer loop is looping through the maps for each documents that were added to "mapArray"
        for (int i = 0; i < mapArray.length; i++)
        {
            Set set = mapArray[i].keySet();
            Iterator iterator = set.iterator();

            //This next loop goes through each word (key) within the map determined by the outer loop and combines them into one map
            for (int j = 0; j < mapArray[i].size(); j++)
            {
                Object currentWord = iterator.next();
                List<Object> docNumbers = new ArrayList<Object>();

                //These if or else statements with a loop sets the correct document numbers as a list to be the value correlated with each key
                if (!wordMap.containsKey(currentWord))
                {
                    docNumbers.add(i + 1);
                    wordMap.put(currentWord, docNumbers);
                }
                else
                {
                    for (int a = 0; a < wordMap.get(currentWord).size(); a++)
                    {
                        docNumbers.add((wordMap.get(currentWord)).get(a));
                    }
                    docNumbers.add(i + 1);
                    wordMap.put(currentWord, docNumbers);
                }
            }
        }
        return wordMap;
    }

    public static List<Object> getSearchResult(String term, TreeMap<Object, List<Object>> wordMap, Map[] mapArray, String Docs[], int amountUnreadable )
    {
        //This function takes the search term, checks which docs contain it via wordMap, extracts the termfrequency of this word from mapArray,
        // multiplies it by the IDF value and then sorts the documents in order correlating to termfrequency*IDF,
        // then returns the document names in that order

        //Creates the list to be filled with document names of the matching documents in the correct order
        List<Object> searchResultDocs = new ArrayList<Object>();

        //Try and catch is used to account for possible searches without a match
        try
        {
            //Create a list of all document numbers that contains the term
            List<Object> docsWithTerm = wordMap.get(term);

            //Calculate the IDF value of the term
            double idf = Math.log10((double) (Docs.length-amountUnreadable) / docsWithTerm.size());

            //Adjusts the result as: [Document x, Document y,...]. Instead of [x,y,...]
            List<Object> searchResult = new ArrayList<Object>();

            //Adds the first document containing the search term
            searchResult.add(docsWithTerm.get(0));

            //Creating variables and iterator used to add the potential additional results and set them in TF-IDF order
            int nextDoc;
            Iterator iteratorDocsWithTerm = docsWithTerm.iterator();
            Object nextDocObj;

            //Move iterator to the first result document
            iteratorDocsWithTerm.next();

            //The loop goes through the list containing each number of documents with the search term
            for (int a = 1; a < docsWithTerm.size(); a++)
            {
                //Iterator to move through the added results
                Iterator iteratorResult = searchResult.iterator();

                //Create variables to enable comparison of TF-IDF values and placement of a document number in the correct location
                int index = 0;
                boolean valDone = false;
                nextDocObj = iteratorDocsWithTerm.next();
                nextDoc = (Integer) nextDocObj;

                //Loops through the list of added document numbers to compare the TF-IDF of the next document to add with the ones already added
                //Keeps going as long as the document has not been added and there are more already added documents with TF-IDF values to compare to
                while (valDone == false && iteratorResult.hasNext())
                {
                    iteratorResult.next();

                    //Calculate the TF-IDF value for the search term in the document to be added into the result list and compare it to the ones in the list
                    //If the new one has a larger value, add it before the one it is compared to, if it is lower, compare to the value of the next doc in the list
                    //or add at the end of the list if there are no more documents to compare to
                    Double tfIdfValNew = ((Double) mapArray[nextDoc - 1].get(term)) * idf;
                    Double tfIdfValCompare = ((Double) mapArray[(Integer) searchResult.get(index) - 1].get(term)) * idf;

                    if (tfIdfValNew > tfIdfValCompare)
                    {
                        searchResult.add(index, docsWithTerm.get(a));
                        valDone = true;
                    }
                    index = index + 1;
                }
                if (valDone == false)
                {
                    searchResult.add(docsWithTerm.get(a));
                }
            }

            //Add the document names (instead of the number in which it was entered into the list of filenames) to the final list that will be returned
            searchResultDocs.add("The following documents matches your search");
            for (int i = 0; i < searchResult.size(); i++)
            {
                searchResultDocs.add(Docs[(Integer) searchResult.get(i) - 1]);
            }
        }

        //Catch exception where no document contain the search term
        catch (NullPointerException a)
        {
            searchResultDocs.add("No document matches your search");
        }
        return searchResultDocs;
    }
}
