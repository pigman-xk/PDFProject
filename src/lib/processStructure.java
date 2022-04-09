package lib;

import java.util.ArrayList;

public class processStructure {
    String processString = null;
    String[] processStringByLine = null;
    String[] WordCollection = null;
    pdfStructure pdf = null;
    public processStructure(){
    }
    public processStructure(String processString, pdfStructure pdf , int index ){
        this.processString = processString;
        this.pdf = pdf;
    }

    public processStructure(String[] processStringByLine, pdfStructure pdf){
        this.processStringByLine = processStringByLine;
        this.pdf = pdf;
    }

    public void setDivideString(ArrayList<String> divideString){
        pdf.setDivideString(divideString);
        int partsNum = pdf.getDivideString().size()+1;
        pdf.setPartsNum(partsNum);
    }

    public void setPdfSelectSection(int[] selectSection){
        pdf.setSelectSection(selectSection);
    }

    public void setIndexInFatherStructure(int index){
        pdf.setIndexInFatherStructure(index);
    }

    public void setPartType(int partType){
        pdf.setPartType(partType);
    }

    public void setReadType(int readType){
        pdf.setReadMode(readType);
    }

    public String[] readByWord(){
        readMachine readMachine = new readMachine();
        WordCollection = readMachine.readWordBySeperate(processStringByLine," ");
        return WordCollection;
    }

    public void setWordNum(int wordNum){
        pdf.setWordNum(wordNum);
    }

    public void finishiSelectWord(int[] selectIndexs, int[] selectID , String[] WordHeadName){
        ArrayList<wordStructure> selectWord = new ArrayList<wordStructure>();
        for(int i = 0 ; i < selectIndexs.length ; i++){
            wordStructure newWord = new wordStructure(selectIndexs[i],selectID[i], WordHeadName[i]);
            selectWord.add(newWord);
        }
        pdf.setSelectWord(selectWord);
    }

    public void setTabulaNum(int tabulaNum){
        pdf.setTabulaNum(tabulaNum);
    }

    public void finishiSelectTabula(int[] selectIndexs, int[] selectID , String[] WordHeadName){
//        ArrayList<tabulaStructure> selectTabula = new ArrayList<tabulaStructure>();
//        for(int i = 0 ; i < selectIndexs.length ; i++){
//            ArrayList<String> tabulaHeadName = new ArrayList<>();
//            tabulaStructure newTabula = new tabulaStructure(selectIndexs[i],selectID[i]);
//            for(String s : WordHeadName) tabulaHeadName.add(s);
//            selectTabula.add(newTabula);
//        }
//        pdf.setSelectTS(selectTabula);
    }

    public void addSonStructure(pdfStructure pdf){
        this.pdf.addSonStruture(pdf);
        pdf.setFatherStructure(this.pdf);
    }

}
