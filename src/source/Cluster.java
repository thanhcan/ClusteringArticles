package source;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class Cluster {
	
	private LinkedList<Article> articles;
	private Article center;
	
	public Cluster() {
		// TODO Auto-generated constructor stub
		this.articles = new LinkedList<Article>();
	}

	public void put(Article x) {
		this.articles.add(x);
	}
	public void push(Article x) {
		this.articles.push(x);
	}
	
	public int size() {
		return this.articles.size();
	}
	
	public LinkedList<Article> getArticles() {
		return this.articles;
	}
	
	public void setCenter(Article center) {
		this.center = center;
	}
	public Article getCenter() {
		return this.center;
	}
	public void reCalculatingCenter(int nVector){
		double[] vector = new double[nVector];
		for (Article article : this.articles) {
			for (int i = 0; i < nVector; i++)
				vector[i] += article.getVector()[i];
		}
		for (int i = 0; i < nVector; i++) {
			vector[i] = vector[i] / nVector;
		}
		this.center.setVector(vector);
	}
	
	/**
	 * @param cluster
	 * @return true if == else return false
	 */
	public boolean compare(Cluster cluster) {
		if (this.articles.size() != cluster.getArticles().size())
			return false;
		for (int i = 0; i < this.articles.size(); i++)
			if (articles.get(i).compare(cluster.getArticles().get(i)))
				return false;
		return true;
	}
	
	public void copy(Cluster cluster) {
		this.articles.clear();
		this.articles.addAll(cluster.getArticles());
	}

	public void printOutputFile(int stt) throws IOException{		
		String path, name, x1, x2;
		for (Article article : this.articles) {
			
			x1 = "\\\\" + article.getID();
			x2 = "/" + article.getID();
			path = article.getPath().replaceAll(x1, "");
			path = article.getPath().replaceAll(x2, "");
			
			FileReadWrite articleFile = new FileReadWrite();
			articleFile.createFile(article.getID(), path);
			
			//System.out.println(article.getCategory());
			path = "output/" + stt;
			name = article.getCategory() + "_" + article.getID();
			FileReadWrite output = new FileReadWrite();
			output.createFile(name, path);
			
			if (output.getFile().exists())
				output.getFile().delete();
			Files.copy(articleFile.getFile().toPath(), output.getFile().toPath());
		}
	}
}
