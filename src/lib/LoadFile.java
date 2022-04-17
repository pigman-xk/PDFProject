package lib;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadFile {
    private String basicString = null;
    private File pdfFile = null;
    private PDDocument pd = null;
    private ArrayList<Table> tableList = null;
    public LoadFile(String filePath) throws IOException {
        pdfFile = new File(filePath);
        pd = PDDocument.load(pdfFile);
        processBasicString();
    }
    public LoadFile(File file) throws IOException {
        pdfFile = file;
        pd = PDDocument.load(pdfFile);
        processBasicString();
    }

    public void closePDFDocument() throws IOException {
        pd.close();
    }

    private void processBasicString() throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);
//        textStripper.setPageStart("首行");
////        textStripper.setArticleStart("首行");
//
////        textStripper.setParagraphStart("首行");
//        textStripper.setPageEnd("尾行");
        basicString = textStripper.getText(pd);
    }
    public String[] getBasicStringByLine(int fitstLine,int endLine){//返回一个按行读取的字符串列表
//        String[] readStringByLine = basicString.split("\r\n");
//        for(int i = 0; i < readStringByLine.length ; i++){
//            String s = readStringByLine[i].trim();
//            readStringByLine[i] = s;
//        }
        ArrayList<String> readStringByLine = new ArrayList<>();
        readStringByLine.add("STARTLINE");
        int totalPages = pd.getNumberOfPages();
        ObjectExtractor oe = new ObjectExtractor(pd);
        BasicExtractionAlgorithm sea = new BasicExtractionAlgorithm();
        for(int pageIndex = 1 ; pageIndex < totalPages+1 ; pageIndex++){
            Page page = oe.extract(pageIndex);
            List<Table> table = sea.extract(page);
            for(Table tables: table) {
                List<List<RectangularTextContainer>> rows = tables.getRows();
                for(int i=0+fitstLine; i<rows.size()-endLine; i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int j=0; j<cells.size(); j++) {
                        stringBuilder.append(cells.get(j).getText()+" ");
                    }
                    readStringByLine.add(stringBuilder.toString());
                }
            }
        }
        readStringByLine.add("ENDLINE");
        String[] strings = new String[readStringByLine.size()];
        return readStringByLine.toArray(strings);
    }
    public HashMap<Integer,String[]> getBasicStringByLineForTable(int fitstLine,int endLine){//返回一个按行读取的字符串列表
        HashMap<Integer,String[]> pageIndexAndString = new HashMap<>();
        int totalPages = pd.getNumberOfPages();
        ObjectExtractor oe = new ObjectExtractor(pd);
        BasicExtractionAlgorithm sea = new BasicExtractionAlgorithm();
        for(int pageIndex = 1 ; pageIndex < totalPages+1 ; pageIndex++){
            Page page = oe.extract(pageIndex);
            List<Table> table = sea.extract(page);
            ArrayList<String> readStringByLine = new ArrayList<>();
            if(pageIndex==1) readStringByLine.add("STARTLINE");
            for(Table tables: table) {
                List<List<RectangularTextContainer>> rows = tables.getRows();
                for(int i=0+fitstLine; i<rows.size()-endLine; i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int j=0; j<cells.size(); j++) {
                        stringBuilder.append(cells.get(j).getText()+" ");
                    }
                    readStringByLine.add(stringBuilder.toString());
                }
            }
            if(pageIndex==totalPages) readStringByLine.add("ENDLINE");
            String[] strings = new String[readStringByLine.size()];
            pageIndexAndString.put(pageIndex,readStringByLine.toArray(strings));
        }
        return pageIndexAndString;
    }


    public String getBasicString(){
        return basicString;
    }

    public void loadTables(){
        tableList = new ArrayList<>();
        int totalPages = pd.getNumberOfPages();
        ObjectExtractor oe = new ObjectExtractor(pd);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        for(int i = 1 ; i <= totalPages ; i++){
            Page page = oe.extract(i);
            List<Table> tables = sea.extract(page);
            if(tables.size()>0){
                for(Table j : tables) tableList.add(j);
            }
        }
    }
    public ArrayList<Table> getTableList(){
        return tableList;
    }

    public PDDocument getPd() {
        return pd;
    }
}
