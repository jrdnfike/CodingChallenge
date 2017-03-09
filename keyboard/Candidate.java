package keyboard;

/**
 * A class representing a candidate word for auto-completion
 * @author Jordan Fike
 */
public class Candidate implements Comparable
{
    //Instance fields
    private String word;
    private Integer confidence;
    
    /**
     * Constructor
     * Store the given text with initial confidence of 1
     * @param text The candidate word
     */
    public Candidate(String text)
    {
        if (!text.isEmpty())
        {
            word = text;
        }
        
        //Handle fringe case with expected value
        else
        {
            word = "EMPTYPARAM";
        }
        
        confidence = 1;
    }
    
    /**
     * Get the text representation of this Candidate
     * @return The text representation of this Candidate
     */
    public String getWord()
    {
        return word;
    }
    
    /**
     * Record another occurrence of this Candidate from training
     * passages.
     */
    public void incrementConfidence()
    {
        confidence++;
    }
    
    /**
     * Get the number of occurrences of this Candidate
     * in the current training regimen.
     * @return The confidence of this Candidate
     */
    public Integer getConfidence()
    {
        return confidence;
    }
    
    /**
     * Override the equals method for use in ArrayList operations
     * @param otherCandidate The Candidate to compare to this one
     * @return Whether the text of these two Candidates match
     */
    @Override
    public boolean equals(Object otherCandidate)
    {
        //Ensure correct typing
        if (otherCandidate instanceof Candidate)
        {
            return this.word.equals(((Candidate) otherCandidate).getWord());
        }
        
        else
        {
            return false;
        }
    }
    
    /**
     * Compare two candidates based on their text
     * @param otherCandidate The Candidate to compare with this one
     * @return The result of String.compareTo() on the text fields
     */
    @Override
    public int compareTo(Object otherCandidate)
    {
        //Ensure correct typing
        if (otherCandidate instanceof Candidate)
        {
            return this.word.compareTo(((Candidate) otherCandidate).getWord());
        }
        
        //Handle fringe case with expected value
        else
        {
            return Integer.MAX_VALUE;
        }
    }
    
    /**
     * Override the toString method for display purposes
     * @return The text of this Candidate 
     * followed by its confidence in parentheses
     */
    @Override
    public String toString()
    {
        return word + " (" + confidence + ")";
    }
}
