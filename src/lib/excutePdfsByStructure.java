package lib;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class excutePdfsByStructure {
    private pdfStructure pdf = null;
    private PDDocument pd = null;
    private String[] basicStringByLine = null;
    private ArrayList<String> wordCollection = new ArrayList<>();
    private ArrayList<String> wordHeadCollection = new ArrayList<>();
    private ArrayList<Table> tablesCollection = new ArrayList<>();

    public excutePdfsByStructure() {
    }

    public excutePdfsByStructure(pdfStructure pdf, String filePath) throws IOException {
        this.pdf = pdf;
        LoadFile loadFile = new LoadFile(filePath);
        loadFile.loadTables();
        basicStringByLine = loadFile.getBasicStringByLine(pdf.getFirstLine(),pdf.getEndLine());
        pd = loadFile.getPd();
        tablesCollection = loadFile.getTableList();
        this.excuteWord(this.pdf, 0, 0);
    }


//    /**
//     * tabula解码器(不考虑跨页表格)
//     **/
//    public void excuteTabula() {
////        ArrayList<tabulaStructure> selectTables = pdf.getSelectTS();
////        if(selectTables.size() == tablesCollection.size()){//若表格数目正确，则认为不存在跨页可能，对于比较特殊的情况：一个表格消失但由于有一个表格跨页导致该行判断true的情况暂时无法处理
////            int lineNum = 1;
////            for(tabulaStructure ts : selectTables){
////                int tableIndex = ts.getTableIndex();
//////                int tableId = ts.getTableID();
////                int starLine = 0;
////                ArrayList<String> tableHeadNames = ts.getTabulaHeadName();
////                Table nowTable = tablesCollection.get(tableIndex);
//////            if(tableId == tableIndex){//该表不是跨页表，或者该表是跨页表的前半部分
//////                lineNum = 1;
//////            }
////                if (!ts.getiIsReadTabulaFirstLine()) starLine = 1;
////                readMachine readMachine = new readMachine();
////                String[][] tableString = readMachine.readTable(nowTable,starLine);
////                for(int i = 0 ; i <tableString.length ; i++){
////                    for (int j = 0 ; j < tableString[i].length ; j++){
////                        String word = tableString[i][j];
////                        String wordHead = tableHeadNames.get(i)+lineNum;
////                        wordCollection.add(word);
////                        wordHeadCollection.add(wordHead);
////                        lineNum++;
////                    }
////                }
////            }
////        }else{
////
////        }
//    }

    public int getPages(String divideString) {
        int totalPages = pd.getNumberOfPages();
        ObjectExtractor oe = new ObjectExtractor(pd);
        BasicExtractionAlgorithm sea = new BasicExtractionAlgorithm();
        for (int pageIndex = 1; pageIndex < totalPages; pageIndex++) {
            Page page = oe.extract(pageIndex);
            List<Table> table = sea.extract(page);
            for (Table tables : table) {
                List<List<RectangularTextContainer>> rows = tables.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < cells.size(); j++) {
                        stringBuilder.append(cells.get(j).getText(false).trim());
                    }
                    if (stringBuilder.toString().equals(divideString)) {
                        return pageIndex;
                    }
                }
            }
        }
        return -1;
    }

    public ArrayList<Integer> getPages(String divideString1, String divideString2) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        int totalPages = pd.getNumberOfPages();
        ObjectExtractor oe = new ObjectExtractor(pd);
        BasicExtractionAlgorithm sea = new BasicExtractionAlgorithm();
        for (int pageIndex = 1; pageIndex < totalPages; pageIndex++) {
            Page page = oe.extract(pageIndex);
            List<Table> table = sea.extract(page);
            for (Table tables : table) {
                List<List<RectangularTextContainer>> rows = tables.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < cells.size(); j++) {
                        stringBuilder.append(cells.get(j).getText(false).trim());
                    }
                    if (stringBuilder.toString().equals(divideString1) || stringBuilder.toString().equals(divideString2)) {
                        arrayList.add(pageIndex);
                    }
                }
            }
        }
        return arrayList;
    }

    /*
     * 种子文件解码器
     * */
    public void excuteWord(pdfStructure pdf, int lineIndex, int tableIndex) {//lineIndex默认为0
        int nowSectionIndex = 1;//表示当前的SectionIndex
        int sonIndex = 1;//读到的第几个SonStructure
        ArrayList<String> divideString = pdf.getDivideString();
        int[] selectSection = pdf.getSelectSection();
        if (selectSection.length != 0) {
            for (int sectionIndex : selectSection) {
                if (sectionIndex == nowSectionIndex) {
                    pdfStructure SonPdfStructure = pdf.getSonStructures().get(sonIndex - 1);
                    boolean canRead = SonPdfStructure.getCanRead();
                    int endLine = lineIndex;//该section的尾行
                    for (int Index = lineIndex; Index < basicStringByLine.length; Index++) {
                        if (divideString.get(nowSectionIndex - 1).equals(basicStringByLine[Index])) {
                            nowSectionIndex = nowSectionIndex + 1;
                            endLine = Index;
                        }
                    }
                    if (canRead) {
                        int readMode = SonPdfStructure.getReadMode();
                        String[] processString = new String[endLine - lineIndex - 1];
                        processString = Arrays.copyOfRange(basicStringByLine, lineIndex + 1, endLine);
                        ArrayList<wordStructure> wordStructureArrayList = SonPdfStructure.getSelectWord();
                        if (readMode == 0) {
                            readMachine readMachine = new readMachine();
                            String[] resultString = readMachine.readWordBySeperate(processString, " ");
                            for (wordStructure w : wordStructureArrayList) {
                                int wordIndex = w.getWordIndex();
                                int wordId = w.getWordID();
                                String wordHeadName = w.getWordHeadName();
                                if (wordId == wordIndex) {
                                    wordCollection.add(resultString[wordIndex]);
                                    wordHeadCollection.add(wordHeadName);
                                } else {
                                    wordCollection.set(wordId, wordCollection.get(wordId) + wordCollection.get(wordIndex));
                                }
                            }
                        } else if (readMode == 1) {
                            StringBuilder stringBuilder = new StringBuilder();
                            String wordHeadName = wordStructureArrayList.get(0).getWordHeadName();
                            for (int i = lineIndex; i < endLine; i++) stringBuilder.append(basicStringByLine[i]);
                            wordCollection.add(stringBuilder.toString());
                            wordHeadCollection.add(wordHeadName);
                        } else if (readMode == 2) {
                            ArrayList<Integer> arrayList = this.getPages(divideString.get(lineIndex), divideString.get(endLine));
                            int starLinePage = arrayList.get(0);
                            int endLinePage = arrayList.get(arrayList.size() - 1);
                            if (starLinePage - endLinePage == 0) {
                                Table table = tablesCollection.get(tableIndex);
                                readMachine readMachine = new readMachine();
                                int starLine = 0;
                                int index = 1;
                                if (!SonPdfStructure.getTS().getiIsReadTabulaFirstLine()) starLine = 1;
                                String[][] tableString = readMachine.readTable(table, starLine);
                                ArrayList<String> HeadName = SonPdfStructure.getTS().getTabulaHeadName();
                                for (int i = starLine; i < tableString.length; i++) {
                                    for (int j = 0; j < tableString[0].length; j++) {
                                        wordCollection.add(tableString[i][j]);
                                        wordHeadCollection.add(HeadName.get(j) + index);
                                    }
                                    index++;
                                }
                            } else {//存在跨表
                                int tableNum = endLinePage - starLinePage + 1;//跨表的个数，若为2，则说明该表占table数组里的两个
                                List<Table> tables = new ArrayList<>();
                                for (int i = tableIndex; i < tableIndex + tableNum; i++) {
                                    tables.add(tablesCollection.get(tableIndex));
                                }
                                int index = 1;
                                readMachine readMachine = new readMachine();
                                for (int i = 0; i < tables.size(); i++) {
                                    int starLine = 0;
                                    if (!SonPdfStructure.getTS().getiIsReadTabulaFirstLine() && i == 0) starLine = 1;
                                    String[][] tableString = readMachine.readTable(tables.get(i), starLine);
                                    ArrayList<String> HeadName = SonPdfStructure.getTS().getTabulaHeadName();
                                    for (int j = starLine; j < tableString.length; j++) {
                                        for (int k = 0; k < tableString[0].length; k++) {
                                            wordCollection.add(tableString[j][k]);
                                            wordHeadCollection.add(HeadName.get(k) + index);
                                        }
                                        index++;
                                    }
                                }
                            }
                        }
                    } else if (SonPdfStructure.getSonStructures() != null) {
                        this.excuteWord(SonPdfStructure, lineIndex, tableIndex);
                    }
                    sonIndex++;
                    lineIndex = endLine;
                } else {
                    int partType = pdf.getSonStructures().get(sonIndex).getPartType();
                    for (int Index = lineIndex; Index < basicStringByLine.length; Index++) {
                        if (divideString.get(nowSectionIndex - 1).equals(basicStringByLine[Index])) {
                            if (partType == 2) {
                                ArrayList<Integer> arr = this.getPages(divideString.get(lineIndex), divideString.get(Index));
                                int starLinePage = arr.get(0);
                                int endLinePage = arr.get(arr.size() - 1);
                                if (starLinePage - endLinePage == 0) {
                                    tableIndex = tableIndex + 1;
                                } else {//存在跨表
                                    tableIndex = tableIndex + 1 + starLinePage - endLinePage;
                                }
                                nowSectionIndex = nowSectionIndex + 1;
                                lineIndex = Index;
                                break;
                            }
                        }
                        sonIndex++;
                    }
                }
            }
        }

    }
}