package lib;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateExcel {
    XSSFWorkbook workbook = null;
    XSSFSheet sheet = null;
    ArrayList<String> headString = null;
    String excelPath = null;
    public CreateExcel(String excelPath){
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet();
        this.excelPath = excelPath;
    }

    public void setHead(ArrayList<String> sortKey, ArrayList<HashMap<Integer,ArrayList<String>>> headlist){
        headString = new ArrayList<>();
        for(int i = 0 ; i < sortKey.size() ; i++){
            headString.add(sortKey.get(i));
        }
        if(headlist.get(0).size()!=0){
            HashMap<Integer,ArrayList<String>> tableHead = headlist.get(0);
            for(int i = 0 ; i < headlist.size() ;i++){
                HashMap<Integer,ArrayList<String>> tableIndexAndHead = headlist.get(i);
                for(int j = 0 ; j < tableIndexAndHead.size() ; j++){
                    ArrayList<String> head = tableIndexAndHead.get(j);
                    if(head.size()>tableHead.get(j).size()){
                        tableHead.replace(j,head);
                    }
                }
            }
            for(int i = 0 ; i < tableHead.size() ;i++){
                ArrayList<String> head = tableHead.get(i);
                for(int j = 0 ; j < head.size() ; j++){
                    headString.add(head.get(j));
                }
            }
        }
        Row row = sheet.createRow(0);
        for(int i = 0 ; i < headString.size() ; i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(headString.get(i));
        }
    }

    public void writedataByRow(HashMap<String,String> headAndData){
        Row row = sheet.createRow(sheet.getLastRowNum()+1);
        for(int i = 0 ; i < headString.size() ; i++){
           String head = headString.get(i);
           Cell cell = row.createCell(i);
           if(headAndData.containsKey(head)){
               cell.setCellValue(headAndData.get(head));
           }
        }
    }

    public void create() throws IOException {
        FileOutputStream out = new FileOutputStream(excelPath);
        workbook.write(out);
        out.flush();
        out.close();
    }

    public void close() throws IOException {
        workbook.close();
    }
}
