package lib;

import technology.tabula.Table;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws IOException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream("C:\\Users\\Administrator.DESKTOP-5GNKJJ2\\Desktop\\seed.txt")));
            pdfStructure pdf = (pdfStructure)objectInputStream.readObject();
            ArrayList<pdfStructure> sonPdfStructure = pdf.getSonStructures();
            for(int i = 0 ; i < sonPdfStructure.size() ; i++){
                System.out.println(sonPdfStructure.get(i).getPartType()+"|");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
