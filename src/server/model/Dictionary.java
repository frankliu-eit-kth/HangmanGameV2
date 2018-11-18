package server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
/**
 * a new class added in this version
 * used to store the word read from file to reduce repetitive reading
 * @author m1339
 *
 */
public class Dictionary {
	
	public static ArrayList<String> dictionary=new ArrayList<String>();
	public static boolean initialized=false;
	/**
	 * invoked in GameController
	 * when creating a new game controller, will run this method in a thread pool
	 * if already initialized then will immediately return
	 */
	public static void readWords() {
		if(initialized)return;
		
		String wordFile="words.txt";
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(wordFile))) {
                String s;
                while ((s = br.readLine()) != null) {
                    dictionary.add(s);
                }
                initialized=true;
            }
           
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	public static String getRandomWord() {
		Random r = new Random();
		return dictionary.get(r.nextInt(dictionary.size() ) );
	}

}
