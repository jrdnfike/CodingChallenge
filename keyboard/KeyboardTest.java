package keyboard;

/**
 * A test class containing the provided examples
 * Can be modified as appropriate for more tests
 * @author Jordan Fike
 */
public class KeyboardTest {
    
    public static void main(String[] args)
    {
        AutocompleteProvider provider = new AutocompleteProvider();
        
        provider.train(
                "The third thing that I need to tell you is that this thing does not think thoroughly.");
        
        provider.train("");
        
        System.out.println(provider.getWords("thi").toString());
        
        System.out.println(provider.getWords("nee").toString());
        
        System.out.println(provider.getWords("th").toString());
    }
    
}
