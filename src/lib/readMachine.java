package lib;

import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;

import java.util.*;

public class readMachine {
    public String[] readWordBySeperate(String[] string,String seperate){
        ArrayList<String> wordList = new ArrayList<>();
        for(String s : string){
            s = s.trim();
            String[] list = s.split(seperate);
            for(String word : list){
                wordList.add(word);
            }
        }
        String[] returnStrings = new String[wordList.size()];
        return wordList.toArray(returnStrings);
    }

    public String[][] readTable(Table table , int starLine){
        List<List<RectangularTextContainer>> rows = table.getRows();
        String[][] tabula = new String[rows.size()][];
        for(int i=starLine; i<rows.size(); i++) {
            int endCell = 0;
            List<RectangularTextContainer> cells = rows.get(i);
            String[] tabulacell = new String[cells.size()];
            if(cells.get(cells.size()-1).getText(false).equals("")&&cells.get(cells.size()-2).getText(false).equals("")){
                endCell = 2;
                tabulacell[0] = rows.get(i-1).get(0).getText(false);
                tabulacell[tabulacell.length-1] = rows.get(i-1).get(tabulacell.length-1).getText(false);
            }
            for(int j=0; j<cells.size()-endCell; j++) {
                tabulacell[j+endCell/2] = cells.get(j).getText(false);
            }
            tabula[i] = tabulacell;
        }
        return tabula;
    }

    //考虑到跨页表的读表操作,输入：整个文本字符串的哈希表，第几个表格的Index和每个表格对应的分割字符串的哈希表（包含所有表格）    输出：所有表格在提取表格Machine的Index数组，为真实机器序列0开始，而key为1开始的虚假序列，对应提取表格Machine的第一个表格。
    public HashMap<Integer,int[]> readTableForExcute(HashMap<Integer,String[]> pageIndexAndString,HashMap<Integer,String[]> tableAndDivideString){
        HashMap<Integer,int[]> tableIndexAndRealTablesIndex = new HashMap<>();
        int[] tableIndexs = new int[tableAndDivideString.size()];
        int k = 0;
        for(Map.Entry<Integer,String[]> set : tableAndDivideString.entrySet()){
            tableIndexs[k] = set.getKey();
        }
        Arrays.sort(tableIndexs);
        int tableIndex = 0;
        int totalTableNum = tableAndDivideString.size();
        for(int i = 0 ; i < totalTableNum ; i++){
            String[] divideStrings = tableAndDivideString.get(tableIndexs[i]);
            String startLine = divideStrings[0];
            String endLine = divideStrings[1];
            int startPage = this.getTheStringPage(pageIndexAndString,startLine);
            int endPage = this.getTheStringPage(pageIndexAndString,endLine);
            int tableNum = endPage-startPage+1;
            int[] tableIndexsList = new int[tableNum];
            for(int j = 0 ; j < tableNum ; j++){
                tableIndexsList[j] = tableIndex;
                tableIndex++;
            }
            tableIndexAndRealTablesIndex.put(i+1,tableIndexsList);
        }
        return tableIndexAndRealTablesIndex;
    }

    public int getTheStringPage(HashMap<Integer,String[]> pageIndexAndString,String string){
        for(Map.Entry<Integer,String[]> set : pageIndexAndString.entrySet()){
            String[] strings = set.getValue();
            for(String s : strings){
                if(s.equals(string)){
                    return set.getKey();
                }
            }
        }
        return -1;//代表在该txt中不存在该字符串
    }
    //输入：整个文件各个部分的typeHashMap  输出：输出各个Table在整个文章中
    public HashMap<Integer,Integer> getTypeIndexAndTableIndex(HashMap<Integer,Integer> typeHashMap){
        HashMap<Integer, Integer> typeIndexAndTableIndex = new HashMap<>();
        int[] indexs = new int[typeHashMap.size()];
        int k = 0;
        for(Map.Entry<Integer,Integer> set : typeHashMap.entrySet()){
            int index = set.getKey();
            indexs[k] = index;
        }
        Arrays.sort(indexs);
        int tableIndex = 1;
        for(int i = 0 ; i < indexs.length ; i++){
            int type = typeHashMap.get(indexs[i]);
            if(type == 2) {
                typeIndexAndTableIndex.put(indexs[i],tableIndex);
                tableIndex++;
            }
        }
        return typeIndexAndTableIndex;
    }

    public HashMap<Integer,String[]> getTableAndDivideString(HashMap<Integer,Integer> typeHashMap,String[] divideString){
        HashMap<Integer, String[]> tableAndDivideString = new HashMap<>();
        int[] indexs = new int[typeHashMap.size()];
        int k = 0;
        for(Map.Entry<Integer,Integer> set : typeHashMap.entrySet()){
            int index = set.getKey();
            indexs[k] = index;
        }
        Arrays.sort(indexs);
        for(int i = 0 ; i < indexs.length ; i++){
            int type = typeHashMap.get(indexs[i]);
            if(type == 2) {
                String[] strList = new String[2];
                strList[0] = divideString[indexs[i]-1];
                strList[1] = divideString[indexs[i]];
                tableAndDivideString.put(indexs[i],strList);
            }
        }
        return tableAndDivideString;
    }

    public HashMap<Integer,int[]> getTypeTableIndexAndRealTableIndex(HashMap<Integer,int[]> tableIndexAndRealTablesIndex,HashMap<Integer,Integer> typeIndexAndTableIndex){
        HashMap<Integer,int[]> typeTableIndexAndRealTableIndex = new HashMap<>();
        for(Map.Entry<Integer,Integer> set : typeIndexAndTableIndex.entrySet()){
            int typeIndex = set.getKey();
            int tableIndex = set.getValue();
            int[] RealTableIndex = tableIndexAndRealTablesIndex.get(tableIndex);
            typeTableIndexAndRealTableIndex.put(typeIndex,RealTableIndex);
        }
        return typeTableIndexAndRealTableIndex;
    }













}
