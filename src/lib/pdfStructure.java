package lib;

import java.util.ArrayList;
/*
1.pdf结构为树结构，每个子结构只对应一个父结构，每个父结构可以有多个子结构
* \*/
public class pdfStructure {
    private int indexInFatherStructure = -1;
    private pdfStructure fatherStructure = null;//父结构
    private ArrayList<pdfStructure> sonStructures = new ArrayList<>();//子结构
    private int partsNum = 1;//分割数，默认为1，即不分割
    private ArrayList<String> divideString = new ArrayList<>(); // 文档从上到下的顺序 对应 index 从小到大顺序
    private int[] selectSection = null; //默认从文首开始，为part1，例如若有divideString则文首到第一个divideString就是part1
    private int partType = -1;//0：纯长文本，1：纯字段，2：纯表格，-1默认为混合信息
    private int tabulaNum = 0;//表格默认为0个
    private boolean canRead = false;
    private int firstLine = 0;//是否读页眉，读为0，不读为1
    private int endLine = 0;//是否读页尾，读为0，不读为1
    //可选部分
    private int readMode = -1;//0按字段读取；1按长文本读取;-1表示此结构尚未达到能读取的程度
    //字段相关可选
    private String divideMark = " ";//倘若已达到可读阶段且选择按字段，则设置分隔符,默认按“ ”(空格)进行分割读取
    private int WordNum = 0;//总共有多少个字段，包含空字段在内
    private ArrayList<wordStructure> selectWord = null;//选择的字段,只有readType为纯字段时才会初始化
    //表格相关可选
    private tabulaStructure ts = null;//选择的表格

    public pdfStructure(){
        sonStructures.add(this);
    }

    public pdfStructure(int partsNum,ArrayList<String> divideString){
        this.partsNum = partsNum;
        this.divideString = divideString;
    }

//    public void addSelectTS(tabulaStructure selectTS) {
//        this.selectTS.add(selectTS);
//    }

    public void addSonStruture(pdfStructure son){
        sonStructures.add(son);
    }

    public void setSelectWord(ArrayList<wordStructure> selectWord) {
        this.selectWord = selectWord;
    }

    public void setCanRead(boolean canRead){
        this.canRead = canRead;
    }

    public void setIndexInFatherStructure(int indexInFatherStructure) {
        this.indexInFatherStructure = indexInFatherStructure;
    }

    public void setSelectSection(int[] selectSection) {
        this.selectSection = selectSection;
    }

    public void setTabulaNum(int tabulaNum){
        this.tabulaNum = tabulaNum;
    }

    public void setSelectTS(tabulaStructure ts){
        this.ts = ts;
    }

    public void setWordNum(int wordNum){
        this.WordNum = wordNum;
    }

    public void setSonStructure(pdfStructure sonStructure){
        this.sonStructures = new ArrayList<>();
        sonStructures.add(sonStructure);
        sonStructure.setFatherStructure(this);
    }

    public void setFirstLine(int firstLine) {
        this.firstLine = firstLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public void setPartType(int partType) {
        this.partType = partType;
    }

    public void setFatherStructure(pdfStructure fatherStructure){
        this.fatherStructure = fatherStructure;
    }

    public void setReadMode(int readMode){//自定义分割符尚未实现
        this.readMode = readMode;
        if(readMode==1) selectWord = new ArrayList<>();
    }

    public void setDivideMark(String divideMark){
        this.divideMark = divideMark;
    }

    public void setDivideString(ArrayList<String> divideString) {
        this.divideString = divideString;
    }

    public void setPartsNum(int partsNum){
        this.partsNum = partsNum;
    }

    public int getWordNum() {
        return WordNum;
    }

    public ArrayList<pdfStructure> getSonStructures() {
        return sonStructures;
    }

    public pdfStructure getFatherStructure() {
        return fatherStructure;
    }

    public int getPartsNum() {
        return partsNum;
    }

    public int getReadMode() {
        return readMode;
    }

    public ArrayList<String> getDivideString() {
        return divideString;
    }

    public int getPartType() {
        return partType;
    }

    public String getDivideMark() {
        return divideMark;
    }

    public int getTabulaNum() {
        return tabulaNum;
    }

    public tabulaStructure getTS() {
        return ts;
    }

    public ArrayList<wordStructure> getSelectWord() {
        return selectWord;
    }


    public int getIndexInFatherStructure() {
        return indexInFatherStructure;
    }

    public int[] getSelectSection() {
        return selectSection;
    }

    public boolean getCanRead(){
        return canRead;
    }

    public int getFirstLine() {
        return firstLine;
    }

    public int getEndLine() {
        return endLine;
    }
}
