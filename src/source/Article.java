package source;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Article {

	private String ID;
	private String path;
	private String category;
	private String content;							// ná»™i dung bÃ i bÃ¡o
	private HashMap<String, Integer> words;			// thá»‘ng kÃª cÃ¡c tá»« cáº§n thiáº¿t trong bÃ i bÃ¡o
	private double[] vector;						// vector biá»ƒu diá»…n bÃ i bÃ¡o
	
	public Article(String id) {
		this.ID = id;
		this.words = new HashMap<String, Integer>();
	}
	
	public String getID() {
		return this.ID;
	}
	public void setID(String id) {
		this.ID = id;
	}
	
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {	
		category = category.replaceAll("\"", "");
		category = category.replace(' ', '_');
		category = category.replaceAll("_>_", " ");
		category = category.replaceAll("_-_", "_");
		category = category.split(" ")[category.split(" ").length - 1].toLowerCase();
		category = Normalizer.normalize(category, Normalizer.Form.NFD).replace("đ", "d").replace("Đ", "D").replaceAll("[^\\p{ASCII}]", "");
		
		this.category = category;
	}
	
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void processContent(){
		this.content = this.content.replaceAll("\"", "");
		this.content = this.content.replaceAll("'", "\"");
		Document jsoup = Jsoup.parse(this.content);
		this.content = jsoup.text();
		
		String sepecialChats = "<>\\!~@#$%&()+*^\'“”\"";
		for (int i = 0; i < sepecialChats.length(); i++)
			this.content = this.content.replace(sepecialChats.charAt(i), '$');
		this.content = this.content.replaceAll("\\$", "");
		this.content = this.content.replaceAll(" [a-zA-Z] ", " ");
	}
	
	
	
	public HashMap<String, Integer> getWords() {
		return this.words;
	}
	
	public double[] getVector() {
		return this.vector;
	}
	public void setVector(double[] vector) {
		this.vector = vector;
	}
	
	public void putWord(String word) {
		if (this.words.containsKey(word))
			this.words.put(word, this.words.get(word) + 1);
		else
			this.words.put(word, 1);
	}
	
	public void createVector(LinkedList<String> vectorDicwords, HashMap<String, Integer> dicWords, int n) {
		this.vector = new double[vectorDicwords.size()];
		
		for (String word : vectorDicwords) {
			if (this.words.containsKey(word)) {
				DecimalFormat df = new DecimalFormat("0.000000");
				df.setRoundingMode(RoundingMode.UP);
				double d = Double.parseDouble(df.format(words.get(word) * Math.log(n / dicWords.get(word))));
				this.vector[vectorDicwords.indexOf(word)] = d;
			} else
				this.vector[vectorDicwords.indexOf(word)] = 0;
		}
		
		/*for (int i = 0; i < vectorDicwords.size(); i++)
			this.vector[i] = 0;
		
		for (String key : words.keySet()) {
			
			DecimalFormat df = new DecimalFormat("0.000");
			df.setRoundingMode(RoundingMode.UP);
			this.vector[vectorDicwords.indexOf(key)] = Double.parseDouble(df.format(words.get(key) * Math.log(n / dicWords.get(key))));
			
			//System.out.println(key + " " + words.get(key));
		}*/
	}
	/**
	 * @param x
	 * @return true if == else return false
	 */
	public boolean compare(Article x) {
		if (this.ID.compareTo(x.getID()) == 0)
			return true;
		return false;
	}
	
	public double calculatingRange(Article x){
		
		double XY = 0, X = 0, Y = 0;
		
		for (int i = 0; i < this.vector.length; i++) {
			XY += this.vector[i] * x.getVector()[i];
			X += this.vector[i] * this.vector[i];
			Y += x.getVector()[i] * x.getVector()[i];
		}
		
		X = Math.sqrt(X);
		Y = Math.sqrt(Y);
		
		if (X == 0 || Y == 0)
			return 0;
		
		DecimalFormat df = new DecimalFormat("0.00000");
		df.setRoundingMode(RoundingMode.UP);
		//System.out.println("XY: " + XY + ", X: " + X + ", Y: " + Y);
		double d = Double.parseDouble(df.format(XY / (X * Y)));
		
		return d;		
	}
	
	public void print() {
		
	}
	
}
