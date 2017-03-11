package keyboard;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * An implementation of basic keyboard auto-completion operations
 * 
 * @author Jordan Fike
 */
public class AutocompleteProvider 
{
    //Declare characters to ignore when training
    //Adjust as needed
    private final String CHARS_TO_IGNORE = ",.?!\n\t()";
    
    //Declare Candidate storage list
    //Will be sorted on insertion
    private ArrayList<Candidate> dictionary;
    
    /**
     * Instantiate a new provider by making an empty list
     */
    public AutocompleteProvider()
    {
        dictionary = new ArrayList<Candidate>();
    }
    
    /**
     * Provide a list of auto-completion candidates
     * with text beginning with the given fragment 
     * ranked in decreasing order of confidence
     * @param fragment The prefix to match for auto-completion
     * @return List of Candidates beginning with fragment sorted by confidence
     */
    public List<Candidate> getWords(String fragment)
    {
        //Handle empty case (though it should not be encountered...)
        if (dictionary.isEmpty())
        {
            return dictionary;
        }
        
        //Make separate list to store sorted results
        ArrayList<Candidate> results = new ArrayList<Candidate>();
        
        //Find the position of the first candidate alphabetically 
        //with the given prefix
        int dictionaryPosition = findDictionaryIndex(fragment);
        
        //The prefix is alphabetically after all stored candidates
        if (dictionaryPosition == dictionary.size())
        {
            return results;
        }
        
        //Get the first eligible Candidate
        Candidate currentCandidate = dictionary.get(dictionaryPosition);
        
        //The rank in terms of confidence of the current Candidate
        int resultsPosition = 0;
        
        boolean doneSearching = false;
        
        //Continue adding Candidate matches as results 
        //until we reach the end of the dictionary OR
        //we hit a non-match
        while (!doneSearching)
        {
            //Add the latest result to our output list
            results.add(resultsPosition, currentCandidate);
            
            //Advance alphabetically to the next stored Candidate
            dictionaryPosition++;
            
            //Ensure we do not go past the end of the list
            if (dictionaryPosition == dictionary.size())
            {
                break;
            }
            
            currentCandidate = dictionary.get(dictionaryPosition);
            
            //If our next Candidate does not have the given prefix, stop
            if (!currentCandidate.getWord().startsWith(fragment))
            {
                break;
            }
            
            //Sort the latest result within the results so far by confidence
            resultsPosition = findConfidenceResultIndex(currentCandidate, results);
        }
        
        //Output the results
        return results;
    }
    
    /**
     * A binary search for the placement of candidates within a
     * result list sorted by decreasing confidence
     * @param toPlace The Candidate to be placed as a result
     * @param currentResults The result list prior to insertion of toPlace
     * @return The position in the result list at which toPlace will be added
     */
    public int findConfidenceResultIndex(Candidate toPlace, 
            ArrayList<Candidate> currentResults)
    {
        //If no results or new element has greater confidence than any so far,
        //place as the first element
        if (currentResults.isEmpty() || 
                toPlace.getConfidence() > currentResults.get(0).getConfidence())
        {
            return 0;
        }
        
        //Find the middle of our list, adjusting for list length
        boolean evenRange = currentResults.size() % 2 == 0;
        int currentMiddle = 
                (evenRange ? currentResults.size() / 2 : 
                (currentResults.size() + 1) / 2);
        
        //Store the bounds of our confidence search
        int rangeStart, rangeEnd;
        
        //Compare the middle element to our new Candidate
        //If the new Candidate has lower confidence, shift our search to the end
        if (toPlace.getConfidence().compareTo(
                currentResults.get(currentMiddle - 1).getConfidence()) < 0)
        {
            rangeStart = currentMiddle;
            rangeEnd = currentResults.size();
        }
        
        //Otherwise shift the search to the upper half of confidence range
        else
        {
            rangeStart = 0;
            rangeEnd = currentMiddle;
        }
        
        //Adjust the middle of the range and range length properties
        evenRange = (rangeStart + rangeEnd) % 2 == 0;
        
        currentMiddle = 
            (evenRange ? (rangeStart + rangeEnd) / 2 : (rangeStart + rangeEnd + 1) / 2);
        
        
        boolean doneSearching = false;
        while (!doneSearching)
        {
            //If Candidate to be inserted has lower confidence than the middle,
            //increase our lower index bound
            if (toPlace.getConfidence().compareTo(
                currentResults.get(currentMiddle - 1).getConfidence()) < 0)
            {
                rangeStart = currentMiddle;
            }
            //If Candidate to be inserted has higher confidence than the middle,
            //decrease our upper index bound
            else
            {
                rangeEnd = currentMiddle;
            }
            
            //Adjust the middle of the range and range length properties
            evenRange = (rangeStart + rangeEnd) % 2 == 0;
        
            currentMiddle = 
                (evenRange ? (rangeStart + rangeEnd) / 2 : (rangeStart + rangeEnd + 1) / 2);
            
            //If the middle hits the edge of the range, we hit the desired index
            //At that point, the range size is 1
            doneSearching = !((currentMiddle > rangeStart) 
                    && (currentMiddle < rangeEnd));
        }
        
        return currentMiddle;
    }
    
    /**
     * Trains the AutoCompletion provider with the given string passage
     * Adds all found words into our dictionary, increasing confidence
     * as appropriate
     * @param passage The passage from which words are to be recorded 
     */
    public void train(String passage)
    {
        String cleanedPassage = "";
        
        //Remove undesired characters (defined at the top of the class)
        for (int index = 0; index < passage.length(); index++)
        {
            if (!CHARS_TO_IGNORE.contains(passage.subSequence(index, index + 1)))
            {
                cleanedPassage = cleanedPassage + 
                        passage.substring(index, index + 1);
            }
        }
        
        //Ignore case when recording
        cleanedPassage = cleanedPassage.toLowerCase();
        
        //Use a Scanner to iterate over each word for recording in
        //our Candidate dictionary
        Scanner trainer = new Scanner(cleanedPassage);
        trainer.useDelimiter(" ");
        
        //Exit safely in zero valid word case
        if (!trainer.hasNext())
        {
            return;
        }
        
        //Initialize tracking variables
        String currentWord = trainer.next();
        Candidate currentCandidate = new Candidate(currentWord);
        
        //Iterate until we reach the last word
        while (trainer.hasNext())
        {
            //If we have already seen this word, add confidence to the
            //stored Candidate
            if (dictionary.contains(currentCandidate))
            {
                Candidate storedCandidate = 
                        dictionary.get(dictionary.indexOf(currentCandidate));
                storedCandidate.incrementConfidence();
            }
            
            //If this is a previously unencountered word
            else
            {
                //Make a new Candidate
                Candidate newCandidate = new Candidate(currentWord);
                
                //Add into appropriate index to keep dictionary
                //sorted alphabetically
                if (dictionary.size() > 0)
                {
                    dictionary.add(findDictionaryIndex(currentWord), newCandidate);
                }
                else
                {
                    dictionary.add(newCandidate);
                }
            }
            
            //Advance to the next word and reset Candidate for comparisons
            currentWord = trainer.next();
            currentCandidate = new Candidate(currentWord);
        }
        
        //If we have already seen the last word, add confidence to the
        //stored Candidate
        if (dictionary.contains(currentCandidate))
        {
            Candidate storedCandidate = 
                    dictionary.get(dictionary.indexOf(currentCandidate));
            storedCandidate.incrementConfidence();
        }

        //If the last word is a previously unencountered word
        else
        {
            //Make a new Candidate
            Candidate newCandidate = new Candidate(currentWord);

            //Add into appropriate index to keep dictionary
            //sorted alphabetically
            if (dictionary.size() > 0)
            {
                dictionary.add(findDictionaryIndex(currentWord), newCandidate);
            }
            else
            {
                dictionary.add(newCandidate);
            }
        }
        
        //Close our Scanner
        trainer.close();
    }
    
    /**
     * Finds the correct alphabetical placement of the given term
     * within the currently stored Candidate dictionary using a binary search
     * @param searchTerm The term to place
     * @return The index at which searchTerm can be inserted to
     * keep the dictionary in alphabetical order
     */
    public int findDictionaryIndex(String searchTerm)
    {
        //Check if our new term is before all stored Candidates
        if (searchTerm.compareTo(dictionary.get(0).getWord()) < 0)
        {
            return 0;
        }
        
        //Check if our new term is after all stored Candidates
        else if (searchTerm.compareTo(
                dictionary.get(dictionary.size() - 1).getWord()) > 0) 
        {
            return dictionary.size();
        }
        
        //Find the middle of the stored dictionary, adjusting for length
        boolean evenRange = dictionary.size() % 2 == 0;
        int currentMiddle = 
            (evenRange ? dictionary.size() / 2 : (dictionary.size() + 1) / 2);
        
        //Store the search boundaries
        int rangeStart, rangeEnd;
        
        //If our term comes before the middle, adjust our range to the
        //first half of the dictionary
        if (searchTerm.compareTo(dictionary.get(currentMiddle).getWord()) < 0)
        {
            rangeStart = 0;
            rangeEnd = currentMiddle;
        }
        //Otherwise, use the second half of the dictionary
        else
        {
            rangeStart = currentMiddle;
            rangeEnd = dictionary.size();
        }
        
        //Adjust the middle and range properties
        evenRange = (rangeStart + rangeEnd) % 2 == 0;
        currentMiddle = 
            (evenRange ? (rangeStart + rangeEnd) / 2 : (rangeStart + rangeEnd + 1) / 2);
        
        boolean doneSearching = false;
        while (!doneSearching)
        {
            //If the term is before the middle of our range, set the upper bound
            //earlier alphabetically
            if (searchTerm.compareTo(dictionary.get(currentMiddle).getWord()) < 0)
            {
                rangeEnd = currentMiddle;
            }
            //If the term comes after the middle, set the lower bound later
            else
            {
                rangeStart = currentMiddle;
            }
            
            //Re-calculate the middle and range properties
            evenRange = (rangeStart + rangeEnd) % 2 == 0;
        
            currentMiddle = 
                (evenRange ? (rangeStart + rangeEnd) / 2 : (rangeStart + rangeEnd + 1) / 2);
            
            //We are finished when the middle has hit a search boundary
            //In other words, done when our search range is 1
            doneSearching = !((currentMiddle > rangeStart) && (currentMiddle < rangeEnd));
        }
        
        return currentMiddle;
        
    }
}