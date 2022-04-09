package lib;

import technology.tabula.Table;

import java.util.ArrayList;

public class tabulaStructure {
    private int tableIndex = 0;
    private int tabulaReadType = 0;//若该结构为纯表格，则0默认按行读取，1按列读取
    private boolean isReadTabulaFirstLine = true;//是否读表格首行
    private boolean asHead = false;//是否将首行作为excel字段头
    private ArrayList<String> tabulaHeadName = null;

    public tabulaStructure(){

    }
    public tabulaStructure(int tableIndex){
        this.tableIndex = tableIndex;
    }
    public tabulaStructure(int tableIndex , boolean asHead){
        this.tableIndex = tableIndex;
        this.asHead = asHead;
    }

    public tabulaStructure(int tableIndex , ArrayList<String> tabulaHeadName , boolean isReadTabulaFirstLine){
        this.tableIndex = tableIndex;
        this.tabulaHeadName = tabulaHeadName;
        this.isReadTabulaFirstLine = isReadTabulaFirstLine;
    }


    public void setAsHead(boolean asHead) {
        this.asHead = asHead;
    }

    public void setReadTabulaFirstLine(boolean readTabulaFirstLine) {
        isReadTabulaFirstLine = readTabulaFirstLine;
    }

    public void setTabulaReadType(int tabulaReadType) {
        this.tabulaReadType = tabulaReadType;
    }

    public void setTabulaHeadName(ArrayList<String> headName) {
        this.tabulaHeadName = headName;
    }

    public int getTabulaReadType() {
        return tabulaReadType;
    }

    public ArrayList<String> getTabulaHeadName() {
        return tabulaHeadName;
    }

    public boolean getiIsReadTabulaFirstLine(){
        return isReadTabulaFirstLine;
    }

    public boolean getAsHead(){
        return asHead;
    }

    public int getTableIndex() {
        return tableIndex;
    }
}
