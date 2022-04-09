package lib;

import java.util.ArrayList;

public class wordStructure {
    private int WordIndex = 0;
    private int WordID = 0;
    private String wordHeadName = "null";

    public wordStructure(){
    }
    public wordStructure(int index , int id , String Name){
        this.WordIndex = index;
        this.WordID = id;
        this.wordHeadName = Name;
    }

    public void setWordIndex(int wordIndex) {
        WordIndex = wordIndex;
    }

    public void setWordID(int wordID) {
        WordID = wordID;
    }

    public void setWordHeadName(String wordHeadName) {
        this.wordHeadName = wordHeadName;
    }

    public int getWordID() {
        return WordID;
    }

    public int getWordIndex() {
        return WordIndex;
    }

    public String getWordHeadName() {
        return wordHeadName;
    }
}
