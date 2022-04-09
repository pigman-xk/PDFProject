package lib;

import technology.tabula.Table;

import java.io.IOException;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\Administrator.DESKTOP-5GNKJJ2\\Desktop\\100000644800.pdf";
        LoadFile pf = new LoadFile(filePath);
        pf.loadTables();
        ArrayList<Table> arr = pf.getTableList();
        Table table = arr.get(0);
        readMachine readMachine = new readMachine();
        String[][] strlist = readMachine.readTable(table,0);
        for(int i = 0 ; i < strlist.length ; i++){
            for(int j = 0 ; j < strlist[0].length ; j++){
                System.out.println(strlist[i][j]);
            }
        }
        //对于readtable的逻辑，空单元格得到”“字符串，而合并单元格只在第一个行返回
        //G6PD
        //chrX:153763476
        //NM_000402.3
        //c.482G>T
        //p.Gly161Val
        //Het
        //0.0006
        //PAT
        //G6PD 缺乏症[XLD]
        //chrX:153774276
        //NM_000402.3
        //c.185A>G
        //p.His62Arg
        //Het
        //0.002
        //PAT
//        int j = 2 ;
//        for(int i = j ; i < j+2 ; i++) System.out.println(i);
    }
}
