package source;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

import com.google.gson.JsonObject;


public class Main {
	
	private static LinkedList<Cluster> clusters, newClusters;			// câc cụm
	private static LinkedList<Article> articles;						// các bài báo
	private static HashMap<String, Integer> dicWords;					// lưu cố bài báo có xuất hiện từ i
	private static LinkedList<String> vectorDicWords;					// vector các từ mẫu
	private static int k;												// số cụm
	private static int n;												// số bài báo
	private static double[][] ranges;									// mảng khoảng cách của các phần từ ở mỗi cụm so với tâm của cụm đó
	
	static private void prepare() throws IOException{
		clusters = new LinkedList<Cluster>();
		newClusters = new LinkedList<Cluster>();
		articles = new LinkedList<Article>();
		dicWords = new HashMap<String, Integer>();
		vectorDicWords = new LinkedList<String>();
		
		System.out.print("Nhập số cum K: ");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		k = Integer.parseInt(bufferRead.readLine());
	}
	
	static public void getRangeOfCoordinateList() {
		int i, j;
		
		for (i = 0; i < k; i++) {
			for (j = 0; j < n; j++) {
				ranges[i][j] = clusters.get(i).getCenter().calculatingRange(articles.get(j));
			}
		}
	}
	
	static public int getMaxRange(int j) {
		int i, max;
		double maxRange;
		
		maxRange = ranges[0][j];
		max = 0;
		for (i = 0; i < k; i++)
			if (ranges[i][j] > maxRange) {
				max = i;
				maxRange = ranges[i][j];
			}
		return max;
	}
	
	static public void rebuildClusters() {
		int j, i;
		
		newClusters.clear();
		for (i = 0; i < k; i++)
			newClusters.add(new Cluster());
		
		for (j = 0; j < n; j++) {
			i = getMaxRange(j);
			newClusters.get(i).put(articles.get(j));
		}
	}
	
	/**
	 * @return true if have any change else return false
	 */
	static boolean checkChange() {
		int i;
		for (i = 0; i < k; i++)
			if (!clusters.get(i).compare(newClusters.get(i)))
				return true;
		return false;
	}
	
	static public void getVectorDicWord() throws IOException {
		FileReadWrite vectorFile = new FileReadWrite();
		vectorFile.createFile("vectorDicWord.txt");
		vectorFile.setBufferRead();
		String word;
		while ((word = vectorFile.bufferRead().readLine()) != null)
			vectorDicWords.add(word);
		vectorFile.closeRead();
	}
	
	static void readForder(){
		int i, p;
		LinkedList<String> DFi = new LinkedList<>();
		LinkedList<File> listFiles = new LinkedList<File>();
		FileReadWrite forderSource = new FileReadWrite();
		forderSource.createFile("tintuc");
		try {
			listFiles = forderSource.getAllFile(forderSource.getFile());
		} catch (IOException e) {
			System.out.println("ERROR at get all file: " + e.getMessage());
		}
		n = 0;
		
		/*for (p = 0; p < listCategory.length; p++) {
			FileReadWrite category = new FileReadWrite();
			category.createFile(listCategory[p]);
			try {
				listFiles = forderSource.getAllFile(category.getFile());
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}*/
			
			VietnameseMaxentTagger tagger = new VietnameseMaxentTagger();
			TaggerOptions.UNDERSCORE = true;
			String[] wordsList;
		
			System.out.println("Tổng số bài báo: " + listFiles.size());
			
			for (File file : listFiles) {
				if (file.getName().compareTo("0_LinkOfArticle.txt") == 0 || file.isDirectory()) continue;
				
				FileReadWrite articleFile = new FileReadWrite();
				articleFile.createFile(file);
				try {
					articleFile.setBufferRead();
					JsonObject jsonObject = articleFile.getContentFile();
					
					/**
					 * Lấy nội dung, thể lại các bài báo
					 */
					Article article = new Article(articleFile.getNameFile());
					article.setContent(jsonObject.get("Content").toString());
					article.processContent();
					article.setPath(file.getPath());
					article.setCategory(jsonObject.get("Category").toString());
					
					if (article.getContent().length() == 0) continue; 
					wordsList = tagger.tagText(article.getContent()).split(" ");
					
					/**
					 * duyệt các từ của các bài báo
					 */
					for (i = 0; i < wordsList.length; i++) 
						if (wordsList[i].length() > 0){
							Word word = new Word(wordsList[i]);
							//if (word.checkWordNecessary() && vectorDicWords.contains(word.getWord()))
							if (vectorDicWords.contains(word.getWord())) {
				
								article.putWord(word.getWord());
								
								if (!dicWords.containsKey(word.getWord())) {
									dicWords.put(word.getWord(), 1);
									DFi.add(word.getWord());
								}
								else if (!DFi.contains(word.getWord())) {
									int x = dicWords.get(word.getWord()) +1;
									dicWords.put(word.getWord(), x);
									DFi.add(word.getWord());
								}
							}
						}	
					
					DFi.clear();
					articles.add(article);
					articleFile.closeRead();
				} catch(Exception e) {
					System.out.println("ERROR at: " + "File " + n + ": " + file.getPath() + " Detail: " +  e.getMessage());
					System.exit(1);
				}
				n++;
				System.out.println("n: " + n);
			}
			
			System.out.println("\ttổng số từ: " + dicWords.size() + " / số bài báo: " + n);
		//}
	}
	
	public static void createVectorOfArticles() {
		
		for (Article article : articles) {
			article.createVector(vectorDicWords, dicWords, n);
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		prepare();							// chuẩn bị
		getVectorDicWord();					// lấy vector các từ mẫu
		readForder();						// đọc các file dữ liệu
		createVectorOfArticles();			// tạo vector cho mỗi bài báo
		
		ranges = new double[k][n];
		for (int i = 0; i < k; i++) {
			clusters.add(new Cluster());
			clusters.get(i).setCenter(articles.get(i));
		}
		System.out.println("\n");
		int times = 0;
		while (true) {
			times++;
			System.out.println("time: " + times);
			getRangeOfCoordinateList();							// tính các khoảng cách bài báo so với tâm
			rebuildClusters();									// xây dựng lại các cụm dựa trên khoảng cách vừa tính
			if (checkChange()) {								// nếu có thay đổi so với các cụm trước
				for (int i = 0; i < k; i++) {
					clusters.get(i).copy(newClusters.get(i));
					clusters.get(i).reCalculatingCenter(vectorDicWords.size());					// tính toán lại tâm của các cụm
				}
			} else break;
			if (times == 1000) break;							// giới hạn số lần lặp lại 200 lần
		}
		
		System.out.println("Clustering " + times + " times");
		for (int i = 0; i < k; i++) {
			System.out.println(i + ": " + clusters.get(i).getArticles().size());
			clusters.get(i).printOutputFile(i);
		}
		
		System.out.println("\nClustering: DONE!");
	}
}
