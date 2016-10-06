import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Vector;

class AlgorithmKMP{
	private final String pattern;
	private String partOfInfinity;
	
	public AlgorithmKMP(String x){
		this.pattern = x;
	}
	private static int[] PrefFunc(String x){
		int[] res = new int[x.length()];
		int i = 0;
		int j = -1;
		res[0] = -1;
		while(i < x.length() - 1){
			while((j >= 0) && (x.charAt(i) != x.charAt(j)))
				j = res[j];
			i++;
			j++;
			if (x.charAt(i) == x.charAt(j))
				res[i] = res[j];
			else
				res[i] = j;
		}
		return res;
	}
	public int KMP(){
		String x = pattern,
			   s = partOfInfinity;
		int num = 0;			   
		if (x.length() > s.length()) 
			return num;
		int[] d = PrefFunc(x);
		int i = 0, j;
		while(i < s.length()){
			for (j = 0; i < s.length() && j < x.length(); i++, j++) 
				while((j >=0) && (x.charAt(j) != s.charAt(i)))
					j = d[j];
			if (j == x.length()){
				num = i - j + 1;
				break;
			}
		}	
		return num;
	}
	String getPartOfInfinity(){
		return this.partOfInfinity;
	}
	void setPartOfInfinity(String s){
		this.partOfInfinity = s;
	}
}
class Searcher{
	Searcher(String pattern){
		object = new AlgorithmKMP(pattern);
		infinity = BigInteger.ONE;			// возростающее число к бесконечности
		partString = "";					// суффикс строки части бесконечной строки
		createString();
		lengthPattern = pattern.length();
	}
	private String createString(){
		partInfinity = "";
		while(partInfinity.length() + partString.length() < lengthPartOfInfinity){
			partInfinity += infinity.toString();
			infinity = infinity.add(BigInteger.ONE);
		}
		
		if (partString.length() == 0){
			object.setPartOfInfinity(partInfinity);
			partString = partInfinity.substring(partInfinity.length() - lengthPattern);
		}	
		return partInfinity;
	}
	private void createString(String partString){
		partInfinity = partString + createString();
		partString = partInfinity.substring(partInfinity.length() - lengthPattern);
		object.setPartOfInfinity(partInfinity);
	}
	public BigInteger findPosition(){
		BigInteger posInInfinity = BigInteger.ZERO; // позиция в бесконечной последовательности цифр
		int posInPartOfInfinity = 0,		// позиция в части строки бесконечной последовательности цифр
			delta = 0;

		while((posInPartOfInfinity = object.KMP()) == 0){
			delta = partInfinity.length() - partString.length();
			posInInfinity = posInInfinity.add(BigInteger.valueOf(delta));
			createString(partString);
		}
		posInInfinity = posInInfinity.add(BigInteger.valueOf(posInPartOfInfinity));
		return posInInfinity;
	}
	String getPartOfInfinity(){
		return object.getPartOfInfinity();
	}
	
	private BigInteger infinity;
	private String partString;	  // суффикс строки части бесконечной строки
	private String partInfinity;  //часть бесконечной последовательности цифр, размером не менее 100 символов
	private final int lengthPartOfInfinity = 100;//150
	private final int lengthPattern;
	private AlgorithmKMP object;
}
public class Infinity{
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s;
		Searcher object;
		Vector<Searcher> v = new Vector<Searcher>();
		try {  
			while ((s = in.readLine()) != null && s.length() != 0){
				object = new Searcher(s);
				v.add(object);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		for (Iterator<Searcher> iterator = v.iterator(); iterator.hasNext();) {
			object = iterator.next();
			System.out.println(object.findPosition());
		} 
	}
}