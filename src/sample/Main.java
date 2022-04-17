package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lib.*;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;


import java.io.*;
import java.util.*;


public class Main extends Application {
    List<File> PdfFileList = null;
    File SeedFile = null;
    int informationIndex = 1;

    //---------------------------------------
    pdfStructure seedPdfStructure = null;
    int headLine = 0;
    int endLine = 0;
    int nowSelectPartIndex = 1;
    int nowPartIndex = 1;
    int totalPart = 1;
    int[] dividiStringIndexs = null;
    String[] strList = null;
    String[] divideStringList = null;
    HashMap<Integer,Integer> selectPartList = null;
    HashMap<Integer,String[]> tableAndDivideString = null;
    HashMap<Integer,String[]> pageIndexAndString = null;
    HashMap<Integer,int[]> typeTableIndexAndRealTableIndex = null;
    HashMap<Integer,ArrayList<Integer>> wordOfSelectIndex = null;
    HashMap<Integer,Integer> wordOfTotalNum = null;
    HashMap<Integer,String> divideStringMap = null;
    HashMap<Integer,Integer> typeHashMap = null;
    HashMap<Integer,HashMap<Integer,String>> partIndexAndLineHead = null;
    HashMap<Integer,Integer> tableReadFirstLine = null;
    ArrayList<Table> realTableList = null;



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //----------------------------------------------------------------------------------------------------------
        BorderPane initialPane = new BorderPane();
        HBox wordShowHBox = new HBox();
        VBox ButtonShow2VBox = new VBox(20);
        //----------------------------------------------------------------------------------------------------------
        Button Selected = new Button("确定为分割行");
        Button nextStage2 = new Button("下一阶段2");
        Button cancelSelectTheLine = new Button("取消该分割行");

        Button nextPart = new Button("下一分割部分");

        Button nextStage3 = new Button("下一阶段3");

        Button nextStage4 = new Button("下一阶段4");

        VBox ButtonShow1VBox = new VBox(20);
        VBox ButtonShow3VBox = new VBox(20);
        VBox ButtonShow4VBox = new VBox(20);
        VBox InformationVBox = new VBox();
        HBox StageHBox = new HBox();

        TableColumn firstColumn = new TableColumn("序号");
        firstColumn.setMinWidth(10); // 设置列的最小宽度
        // 设置该列取值对应的属性名称。此处价格列要展示Snack元素的price属性值
        firstColumn.setCellValueFactory(new PropertyValueFactory<>("Index"));
        firstColumn.setMaxWidth(30);
        TableColumn secondColumn = new TableColumn("行文本"); // 创建一个表格列
        secondColumn.setMinWidth(900);
        secondColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        // 把几个标题列一齐添加到表格视图
        TableView<Snack> tableView = new TableView<Snack>(); // 依据指定数据创建列表视图
        tableView.setPrefSize(800, 100);
        tableView.getColumns().addAll(firstColumn, secondColumn);
        Label selectLabel = new Label("选中项");
        selectLabel.setWrapText(true);

//        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Snack>() {
//            @Override
//            public void changed(ObservableValue<? extends Snack> arg0, Snack old_str, Snack new_str) {
//                // getSelectedIndex方法可获得选中项的序号，getSelectedItem方法可获得选中项的对象
//                String desc = String.format("第%d项:%s",
//                        tableView.getSelectionModel().getSelectedIndex()+1,
//                        tableView.getSelectionModel().getSelectedItem().getName().toString());
//                selectLabel.setText(desc); // 在标签上显示当前选中的文本项
//            }
//        });

        Label titileInformation = new Label("操作信息提示栏:");
        TextArea informationText = new TextArea();
        informationText.setEditable(false);
        Font font = new Font(15);
        informationText.setFont(font);
        informationText.setWrapText(true);

        Selected.setOnAction(actionEvent -> {
            int index = tableView.getSelectionModel().getSelectedIndex()+1;
            if(index!=0&&!divideStringMap.containsKey(index)){
                String divideString = tableView.getSelectionModel().getSelectedItem().getName();
                divideStringMap.put(index,divideString);
                informationText.appendText(String.format("%d:成功选择第%d行的文本作为分割字符串，该行文本为%s",informationIndex++,index,divideString)+"\n\r");
            }else if(index!=0) {
                informationText.appendText(String.format("%d:第%d的文本已添加，无需再次添加",informationIndex++,index)+"\n\r");
            }else{
                Alert nullFile = new Alert(Alert.AlertType.WARNING);
                nullFile.setHeaderText("空文件");
                nullFile.setContentText("您尚未选择PDF文件加载，表格为空");
                nullFile.showAndWait();
            }
        });

        cancelSelectTheLine.setOnAction(actionEvent -> {
            int index = tableView.getSelectionModel().getSelectedIndex()+1;
            System.out.println(index);
            if(index != 1 || index != strList.length){
                if(index!=0&&divideStringMap.containsKey(index)){
                    divideStringMap.remove(index);
                    informationText.appendText(String.format("%d:已取消选择第%d行的文本",informationIndex++,index)+"\n\r");
                }else if(index!=0) {
                    informationText.appendText(String.format("%d:未选择该行为分割行或已取消选择该行",informationIndex++)+"\n\r");
                }else{
                    Alert nullFile = new Alert(Alert.AlertType.WARNING);
                    nullFile.setHeaderText("空文件");
                    nullFile.setContentText("您尚未选择PDF文件加载，表格为空");
                    nullFile.showAndWait();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("首行和末行无法操作");
                alert.showAndWait();
            }
        });

        nextStage2.setOnAction(actionEvent -> {
            typeHashMap = new HashMap<>();
            selectPartList = new HashMap<>();
            nowPartIndex = 1;
            if(PdfFileList!=null&&PdfFileList.size()==1){
                informationText.appendText(String.format("%d:进入阶段2，请在进入到下一分割部分前，请确定每一个分割部分的类型（字段、长文本、表格、混合信息）",informationIndex++)+"\n\r");
                StageHBox.getChildren().set(1,ButtonShow2VBox);
                int[] indexs = new int[divideStringMap.size()];
                int index = 0;
                for(Map.Entry<Integer,String> keySet : divideStringMap.entrySet()){
                    indexs[index] = keySet.getKey();
                    index++;
                }
                Arrays.sort(indexs);
                dividiStringIndexs = indexs;
                if(divideStringMap.size()!=2){
                    List<Snack> snackList = new ArrayList<>();
                    for(int i = dividiStringIndexs[0] ; i < dividiStringIndexs[1]-1 ; i++){
                        snackList.add(new Snack(String.valueOf(i+1),strList[i]));
                    }
                    ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
                    tableView.setItems(obList);
                    tableView.refresh();
                    String str = "";
                    for(int i = 0 ; i < indexs.length ; i++){
                        str+= indexs[i];
                        if(i!=indexs.length-1) str +=",";
                    }
                    totalPart = indexs.length-1;
                    informationText.appendText(String.format("%d:您选择了第%s行作为分割行,全文被分割为%d部分",informationIndex++,str,totalPart)+"\n\r");
                    informationText.appendText(String.format("%d:当前为第1分割部分",informationIndex++)+"\n\r");
                    if(ButtonShow2VBox.getChildren().get(3)!=nextPart){
                        ButtonShow2VBox.getChildren().set(3,nextPart);
                    }
                }else{
                    List<Snack> snackList = new ArrayList<>();
                    for(int i = dividiStringIndexs[0] ; i < dividiStringIndexs[1]-1 ; i++){
                        snackList.add(new Snack(String.valueOf(i+1),strList[i]));
                    }
                    ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
                    tableView.setItems(obList);
                    tableView.refresh();
                    if(ButtonShow2VBox.getChildren().get(3)==nextPart){
                        ButtonShow2VBox.getChildren().set(3,nextStage3);
                    }
                    informationText.appendText(String.format("%d:未选择任何一行为分割行，当前为第1分割部分，全文只有一个部分",informationIndex++)+"\n\r");
                }
                divideStringList = new String[divideStringMap.size()];
                for(int i = 0 ; i < dividiStringIndexs.length ; i++){
                    String divideString = divideStringMap.get(dividiStringIndexs[i]);
                    divideStringList[i] = divideString;
                }
            }else{
                Alert nullFileOrMutiFiles = new Alert(Alert.AlertType.WARNING);
                nullFileOrMutiFiles.setContentText("您尚未选择PDF文件加载或您选择了多个文件处于批量解析状态"+"\n\r");
                nullFileOrMutiFiles.showAndWait();
            }
        });


        //----------------------------------------------------------------------------------------------------------
//        VBox ButtonShow2VBox = new VBox(20);
        Button selected = new Button("保存这个部分");
        Button cancelSelect = new Button("取消保存该部分");
//        Button nextPart = new Button("下一分割部分");
        Button lastPart = new Button("上一分割部分");
        Button lastStage1 = new Button("上一阶段1");
//        Button nextStage3 = new Button("下一阶段3");
        List<String> TypeList = Arrays.asList("字段", "长文本", "表格","混合信息");
        ObservableList<String> TypeListOBbList = FXCollections.observableArrayList(TypeList);
        ComboBox<String> comboBox = new ComboBox<String>(TypeListOBbList); // 依据指定数据创建下拉框
        //comboBox.setItems(obList); // 设置下拉框的数据来源
        comboBox.getSelectionModel().select(0); // 设置下拉框默认选中第1项
//        Font fontTypeList = Font.font("NSimSun", 16); // 创建一个字体对象
//        comboBox.setStyle(String.format("-fx-font: %f \"%s\";", font.getSize(), font.getFamily())); // 设置下拉框的字体


        lastStage1.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("返回上一阶段将不保留该阶段后的所有操作，是否继续？");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                List<Snack> snackList = new ArrayList<>();
                for(int i = 0 ; i < strList.length ; i++){
                    snackList.add(new Snack(String.valueOf(i+1),strList[i]));
                }
                ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
                tableView.setItems(obList);
                tableView.refresh();
                StageHBox.getChildren().set(1,ButtonShow1VBox);
                informationText.appendText(String.format("%d:返回阶段1",informationIndex++)+"\n\r");
            }
        });

        selected.setOnAction(actionEvent -> {
            if(!selectPartList.containsKey(nowPartIndex)){
                String type = comboBox.getSelectionModel().getSelectedItem();
                int typeIndex = 0;
                if(type.equals("字段")) typeIndex = 0;
                if(type.equals("长文本")) typeIndex = 1;
                if(type.equals("表格")) typeIndex = 2;
                if(type.equals("混合信息")) typeIndex = 3;
                if(typeIndex != 3){
                    selectPartList.put(nowPartIndex,typeIndex);
                    informationText.appendText(String.format("%d:第%d分割部分成功保存",informationIndex++,nowPartIndex)+"\n\r");
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("混合信息不需要保存");
                    alert.showAndWait();
                }
            }else{
                informationText.appendText(String.format("%d:第%d部分已经被保存，无需重复保存,如需更改类型，请取消保存后再保存",informationIndex++,nowPartIndex)+"\n\r");
            }
        });

        cancelSelect.setOnAction(actionEvent -> {
            if(selectPartList.containsKey(nowPartIndex)){
                selectPartList.remove(nowPartIndex);
                informationText.appendText(String.format("%d:第%d分割部分成功移除",informationIndex++,nowPartIndex)+"\n\r");
            }else{
                informationText.appendText(String.format("%d:第%d部分尚未被保存，无法取消",informationIndex++,nowPartIndex));
            }
        });

        nextPart.setOnAction(actionEvent -> {
            List<Snack> snackList = new ArrayList<>();
//            int bound = nowPartIndex < totalPart-1 ? dividiStringIndexs[nowPartIndex+1]-1 : strList.length-1;
            for(int i = dividiStringIndexs[nowPartIndex]; i < dividiStringIndexs[nowPartIndex+1]-1 ; i++){
                snackList.add(new Snack(String.valueOf(i+1),strList[i]));
            }
            ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
            String type = comboBox.getSelectionModel().getSelectedItem();
            int typeIndex = 0;
            if(type.equals("字段")) typeIndex = 0;
            if(type.equals("长文本")) typeIndex = 1;
            if(type.equals("表格")) typeIndex = 2;
            if(type.equals("混合信息")) typeIndex = 3;
            if(!typeHashMap.containsKey(nowPartIndex)){
                informationText.appendText(String.format("%d:第%d部分确定为%s类型",informationIndex++,nowPartIndex,type)+"\n\r");
                typeHashMap.put(nowPartIndex,typeIndex);
            }else if(typeHashMap.get(nowPartIndex) != typeIndex){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText(String.format("检测到该部分已更改为%s类型，是否确定更改？",type));
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get()==ButtonType.OK){
                    typeHashMap.replace(nowPartIndex,typeIndex);
                    informationText.appendText(String.format("%d:第%d部分已经修改为%s类型",informationIndex++,nowPartIndex,type)+"\n\r");
                }
            }
            if(nowPartIndex==1){
                ButtonShow2VBox.getChildren().set(4,lastPart);
            }
            if(nowPartIndex == totalPart-1){
                ButtonShow2VBox.getChildren().set(3,nextStage3);
            }
            nowPartIndex++;
            if(typeHashMap.containsKey(nowPartIndex)){
                comboBox.getSelectionModel().select(typeHashMap.get(nowPartIndex));
            }else{
                informationText.appendText(String.format("%d:该部分默认选择为字段类型",informationIndex++,nowPartIndex)+"\n\r");
                comboBox.getSelectionModel().select(0);
            }
            informationText.appendText(String.format("%d:进入第%d分割部分",informationIndex++,nowPartIndex)+"\n\r");
            tableView.setItems(obList);
            tableView.refresh();
        });

        lastPart.setOnAction(actionEvent -> {
            if(nowPartIndex==2){
                ButtonShow2VBox.getChildren().set(4,lastStage1);
            }
            if (nowPartIndex == typeHashMap.size()){
                ButtonShow2VBox.getChildren().set(3,nextPart);
            }
            int lastTypeIndex = typeHashMap.get(nowPartIndex-1);
            comboBox.getSelectionModel().select(lastTypeIndex);
            informationText.appendText(String.format("%d:进入第%d分割部分，该部分为%s类型",informationIndex++,nowPartIndex-1,comboBox.getSelectionModel().getSelectedItem())+"\n\r");
            List<Snack> snackList = new ArrayList<>();
//            int bound = nowPartIndex < totalPart-1 ? dividiStringIndexs[nowPartIndex]-1 : strList.length-1;
            for(int i = dividiStringIndexs[nowPartIndex-2]; i < dividiStringIndexs[nowPartIndex-1]-1 ; i++){
                snackList.add(new Snack(String.valueOf(i+1),strList[i]));
            }
            ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
            nowPartIndex--;
            tableView.setItems(obList);
            tableView.refresh();
        });

        nextStage3.setOnAction(actionEvent -> {
            if(selectPartList.size()==0){
                Alert noSelect = new Alert(Alert.AlertType.WARNING);
                noSelect.setContentText("您尚未保存任何一个部分！");
                noSelect.showAndWait();
            }else{
                String type = comboBox.getSelectionModel().getSelectedItem();
                int typeIndex = 0;
                if(type.equals("字段")) typeIndex = 0;
                if(type.equals("长文本")) typeIndex = 1;
                if(type.equals("表格")) typeIndex = 2;
                if(type.equals("混合信息")) typeIndex = 3;
                if(!typeHashMap.containsKey(nowPartIndex)){
                    typeHashMap.put(nowPartIndex,typeIndex);
                    informationText.appendText(String.format("%d:第%d部分被确定为%s类型",informationIndex++,nowPartIndex,type)+"\n\r");
                }else if(typeHashMap.get(nowPartIndex) != typeIndex){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText(String.format("检测到该部分已更改为%s类型，是否确定更改？",type));
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get()==ButtonType.OK){
                        typeHashMap.replace(nowPartIndex,typeIndex);
                        informationText.appendText(String.format("%d:第%d部分被更改为%s类型",informationIndex++,nowPartIndex,type)+"\n\r");
                    }
                }
                String s = "";
                for(int i = 1 ; i <= nowPartIndex ; i++){
                    String typeS = "第"+String.valueOf(i)+"分割部分"+":";
                    int typeI = typeHashMap.get(i);
                    String t = "";
                    if(typeI == 0) t = "字段";
                    if(typeI == 1) t = "长文本";
                    if(typeI == 2) t = "表格";
                    if(typeI == 3) t = "混合信息";
                    typeS = typeS+t;
                    s = s+typeS+" ";
                }
                informationText.appendText(String.format("%d:各个分割部分类型： %s",informationIndex++,s)+"\n\r");
                StageHBox.getChildren().set(1,ButtonShow3VBox);
                if(selectPartList.size() == 1){
                    ButtonShow3VBox.getChildren().set(2,nextStage4);
                }
                wordOfTotalNum = new HashMap<>();
                wordOfSelectIndex = new HashMap<>();
                partIndexAndLineHead = new HashMap<>();
                tableReadFirstLine = new HashMap<>();
                nowSelectPartIndex = 1;
                int[] selectIndexs = new int[selectPartList.size()];
                int index = 0;
                for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                    int selectIndex = set.getKey();
                    selectIndexs[index] = selectIndex;
                    index++;
                }
                Arrays.sort(selectIndexs);

                readMachine read = new readMachine();
                try {
                    tableAndDivideString = read.getTableAndDivideString(typeHashMap,divideStringList);
                    HashMap<Integer,int[]> tableIndexAndRealTablesIndex = read.readTableForExcute(pageIndexAndString,tableAndDivideString);
                    HashMap<Integer,Integer> typeIndexAndTableIndex = read.getTypeIndexAndTableIndex(typeHashMap);
                    typeTableIndexAndRealTableIndex = read.getTypeTableIndexAndRealTableIndex(tableIndexAndRealTablesIndex,typeIndexAndTableIndex);
                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("运行出现异常，请返回上一阶段，请重新确认文件中的表格是否已经全部划分清楚");
                    alert.showAndWait();
                }
                int partIndex = selectIndexs[0];
                int typeFisrtSelectPart = selectPartList.get(partIndex);
                String[] strings = Arrays.copyOfRange(strList,dividiStringIndexs[partIndex-1],dividiStringIndexs[partIndex]);
                List<Snack> wordSnack = new ArrayList<>();
                ObservableList<Snack> oblist = null;
                informationText.appendText(String.format("%d:进入阶段3，此阶段请命名各个选择部分在转换成EXCEL后的Head，字段类型部分请先选择要保存的字段再命名Head",informationIndex++)+"\n\r");
                informationText.appendText(String.format("%d:当前为第%d选择部分",informationIndex++,partIndex)+"\n\r");
                if(typeFisrtSelectPart == 0){
                    String[] wordList = read.readWordBySeperate(strings," ");
                    for(int i = 0 ; i < wordList.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),wordList[i]));
                    wordOfTotalNum.put(partIndex,wordList.length);
                    oblist = FXCollections.observableArrayList(wordSnack);
                }else if( typeFisrtSelectPart == 1 ){
                    for(int i = 0 ; i < strings.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),strings[i]));
                    oblist = FXCollections.observableArrayList(wordSnack);
                }else if (typeFisrtSelectPart == 2){
                    //tableMethod
                    try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("是否跳过表格首行？");
                    alert.setContentText("该部分为表格，表格按行读取，单元格从左往右读取。");
                    Optional<ButtonType> result = alert.showAndWait();
                    int startLine = 0;
                    int[] realTableIndexs = typeTableIndexAndRealTableIndex.get(partIndex);
                        for(int i = 0 ; i < realTableIndexs.length ; i++){
                            Table table = realTableList.get(realTableIndexs[i]);
                            int readFirst = 0;
                            if(i == 0){
                                readFirst = startLine;
                            }
                            String[][] tableExcuteString = read.readTable(table,readFirst);
                            for(int j = 0 ; j < tableExcuteString.length ; j++){
                                StringBuilder string = new StringBuilder();
                                for(int k = 0 ; k < tableExcuteString[0].length ; k++){
                                    string.append(tableExcuteString[j][k]+" ");
                                }
                                wordSnack.add(new Snack(String.valueOf(j+1),string.toString()));
                            }
                        }
                        if(result.get() == ButtonType.OK){
                            startLine = 1;
                        }
                        tableReadFirstLine.put(partIndex,startLine);
                        oblist = FXCollections.observableArrayList(wordSnack);
                    }catch (Exception e){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("运行出现异常，请返回上一阶段，请重新确认文件中的表格是否已经全部划分清楚");
                        alert.showAndWait();
                }
                }
//                nowPartIndex++;
                tableView.setItems(oblist);
                tableView.refresh();
            }
        });

        //----------------------------------------------------------------------------------------------------------
        VBox setHeadButtonVBox = new VBox(20);
        Button savaTheHead = new Button("保存");
        Button backToStage3 = new Button("返回");
        TableView<Snack> setHeadtableView = new TableView<Snack>(); // 依据指定数据创建列表视图
        setHeadtableView.setEditable(true);
        TableColumn setHeadFirstColumn = new TableColumn("行号");
        setHeadFirstColumn.setMinWidth(10); // 设置列的最小宽度
        // 设置该列取值对应的属性名称。此处价格列要展示Snack元素的price属性值
        setHeadFirstColumn.setCellValueFactory(new PropertyValueFactory<>("Index"));
        setHeadFirstColumn.setMaxWidth(30);
        TableColumn setHeadSecondColumn = new TableColumn("行文本"); // 创建一个表格列
        setHeadSecondColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        setHeadSecondColumn.setMinWidth(200);
        TableColumn setHeadThirdColumn = new TableColumn("Head"); // 创建一个表格列
        setHeadThirdColumn.setCellValueFactory(new PropertyValueFactory<>("Head"));
        setHeadThirdColumn.setMinWidth(200);
        setHeadThirdColumn.setEditable(true);
        setHeadThirdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        setHeadThirdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                String s = cellEditEvent.getNewValue().toString();
                Snack snack = setHeadtableView.getItems().get(cellEditEvent.getTablePosition().getRow());
                snack.setHead(s);
            }
        });
        // 把几个标题列一齐添加到表格视图
        setHeadtableView.setPrefSize(800, 100);
        setHeadtableView.getColumns().addAll(setHeadFirstColumn, setHeadSecondColumn,setHeadThirdColumn);



        //----------------------------------------------------------------------------------------------------------
//        VBox ButtonShow3VBox = new VBox(20);
        Button selectTheWord = new Button("选择该字段");
        Button cancelSelectTheWord = new Button("取消选择该字段");
        Button setNameByMyself = new Button("命名该字段头");
        Button nextWordPart = new Button("下一个部分");
        Button lastWordPart = new Button("上一个部分");
        Button lastStage2 = new Button("上一阶段2");
//        Button nextStage4 = new Button("下一阶段4");

        selectTheWord.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex-1]);
            if(nowType == 2 || nowType == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("该选择部分不是字段类型，无需选择,请直接命名该部分的Head");
                alert.showAndWait();
            }else{
                int partIndex = selectIndexs[nowSelectPartIndex-1];
                int lineIndex = tableView.getSelectionModel().getSelectedIndex();
                if(wordOfSelectIndex.containsKey(partIndex)){
                    ArrayList<Integer> arr = wordOfSelectIndex.get(partIndex);
                    if(arr.contains(lineIndex)){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("已选择该字段，无需重复选择");
                        alert.showAndWait();
                    }else{
                        informationText.appendText(String.format("%d:选择%d行：%s",informationIndex++,lineIndex+1,tableView.getSelectionModel().getSelectedItem().getName())+"\n\r");
                        arr.add(lineIndex);
                    }
                }else{
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(lineIndex);
                    informationText.appendText(String.format("%d:选择%d行：%s",informationIndex++,lineIndex+1,tableView.getSelectionModel().getSelectedItem().getName())+"\n\r");
                    wordOfSelectIndex.put(partIndex,arr);
                }
            }
        });

        cancelSelectTheWord.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex-1]);
            if(nowType == 2 || nowType == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("该选择部分不是字段类型，无法操作");
                alert.showAndWait();
            }else{
                int partIndex = selectIndexs[nowSelectPartIndex-1];
                Integer lineIndex = tableView.getSelectionModel().getSelectedIndex();
                if(wordOfSelectIndex.containsKey(partIndex)){
                    ArrayList<Integer> arr = wordOfSelectIndex.get(partIndex);
                    if(arr.remove(lineIndex)){
                        informationText.appendText(String.format("%d:成功移除！",informationIndex++));
                        if(arr.size()==0) wordOfSelectIndex.remove(partIndex);
                    }else{
                        informationText.appendText(String.format("%d:该行未被选择，无法取消选择",informationIndex++));
                    }
                }else{
                    informationText.appendText(String.format("%d:该行未被选择，无法取消选择",informationIndex++));
                }
            }
        });

        setNameByMyself.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
//            if(wordOfSelectIndex.containsKey(selectIndexs[nowSelectPartIndex-1])&&wordOfSelectIndex.get(selectIndexs[nowSelectPartIndex-1]).size()!=0){
                int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex-1]);
                ObservableList<Snack> observableList = null;
                HashMap<Integer,String> lineAndHead = null;
                if(partIndexAndLineHead.containsKey(selectIndexs[nowSelectPartIndex-1])){
                    lineAndHead = partIndexAndLineHead.get(selectIndexs[nowSelectPartIndex-1]);
                }
                if(nowType == 0){
                    if(wordOfSelectIndex.containsKey(selectIndexs[nowSelectPartIndex-1])&&wordOfSelectIndex.get(selectIndexs[nowSelectPartIndex-1]).size()!=0){
                        ArrayList<Integer> arr = wordOfSelectIndex.get(selectIndexs[nowSelectPartIndex-1]);
                        HashMap<Integer,String> map = new HashMap<>();
                        for(int i = 0 ; i < arr.size() ; i++){
                            int lineIndex = arr.get(i);
                            map.put(lineIndex,tableView.getItems().get(lineIndex).getName());
                        }
                        List<Snack> list = new ArrayList<>();
                        for (Map.Entry<Integer,String> set: map.entrySet()){
                            String head = "";
                            if(lineAndHead!=null) head = lineAndHead.get(set.getKey()+1);
                            list.add(new Snack(String.valueOf(set.getKey()+1),set.getValue(),head));
                        }
                        observableList = FXCollections.observableArrayList(list);
                        setHeadtableView.setItems(observableList);
                        setHeadtableView.refresh();
                        StageHBox.getChildren().set(0,setHeadtableView);
                        StageHBox.getChildren().set(1,setHeadButtonVBox);
                        informationText.appendText(String.format("%d:请命名Head，填写表格Head行，填写完毕请按Enter确认，完成填写后请按保存",informationIndex++)+"\n\r");
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("您尚未选择字段，无法自定义字段头");
                        alert.showAndWait();
                    }
                }else if (nowType == 1){
                    int longTxtIndex = selectIndexs[nowSelectPartIndex-1];
                    int from = dividiStringIndexs[longTxtIndex-1];
                    int to = dividiStringIndexs[longTxtIndex];
                    String[] str = Arrays.copyOfRange(strList,from,to);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String s : str) stringBuilder.append(s);
                    String result = stringBuilder.toString();
                    List<Snack> list = new ArrayList<>();
                    String head = "";
                    if(lineAndHead!=null) head = lineAndHead.get(1);
                    list.add(new Snack(String.valueOf(1),result,head));
                    observableList = FXCollections.observableArrayList(list);
                    setHeadtableView.setItems(observableList);
                    setHeadtableView.refresh();
                    StageHBox.getChildren().set(0,setHeadtableView);
                    StageHBox.getChildren().set(1,setHeadButtonVBox);
                    informationText.appendText(String.format("%d:请命名Head，填写表格Head行，填写完毕请按Enter确认，完成填写后请按保存",informationIndex++)+"\n\r");
                }else if(nowType == 2){
                    List<Snack> list = new ArrayList<>();
                    int tableIndex = selectIndexs[nowSelectPartIndex-1];
                    int[] tableIndexs = typeTableIndexAndRealTableIndex.get(tableIndex);
                    Table table = realTableList.get(tableIndexs[0]);
                    List<List<RectangularTextContainer>> lines = table.getRows();
                    List<RectangularTextContainer> line1 = lines.get(0);
                    for(int i = 0 ; i < line1.size() ; i++) {
                        String head = "";
                        if(lineAndHead!=null) head = lineAndHead.get(i+1);
                        list.add(new Snack(String.valueOf(i+1),line1.get(i).getText(false),head));
                    }
                    observableList = FXCollections.observableArrayList(list);
                    setHeadtableView.setItems(observableList);
                    setHeadtableView.refresh();
                    StageHBox.getChildren().set(0,setHeadtableView);
                    StageHBox.getChildren().set(1,setHeadButtonVBox);
                    informationText.appendText(String.format("%d:请命名Head，填写表格Head行，填写完毕请按Enter确认，完成填写后请按保存",informationIndex++)+"\n\r");
                }
        });

        savaTheHead.setOnAction(actionEvent -> {
            HashMap<Integer,String> lineIndexAndHead = new HashMap<>();
            ObservableList<Snack> observableList = setHeadtableView.getItems();
            for(int i = 0 ; i < observableList.size() ; i++){
                Snack snack = observableList.get(i);
                lineIndexAndHead.put(Integer.parseInt(snack.getIndex()),snack.getHead());
            }
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            int partIndex = selectIndexs[nowSelectPartIndex-1];
            if(partIndexAndLineHead.containsKey(partIndex)){
                partIndexAndLineHead.replace(partIndex,lineIndexAndHead);
            }else {
                partIndexAndLineHead.put(partIndex,lineIndexAndHead);
            }
            informationText.appendText(String.format("%d:字段命名保存成功",informationIndex++)+"\n\r");
        });

        backToStage3.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("尚未保存Head，是否返回？");
            if(!partIndexAndLineHead.containsKey(selectIndexs[nowSelectPartIndex-1])){
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    StageHBox.getChildren().set(1,ButtonShow3VBox);
                    StageHBox.getChildren().set(0,tableView);
                }
            }else{
                StageHBox.getChildren().set(1,ButtonShow3VBox);
                StageHBox.getChildren().set(0,tableView);
            }
        });

        nextWordPart.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            boolean isNext = false;
            int nowType = typeHashMap.get(selectIndexs[nowSelectPartIndex-1]);
            if(partIndexAndLineHead.containsKey(selectIndexs[nowSelectPartIndex-1])){
                isNext = true;
            }
//            nowType!=0 || (wordOfSelectIndex.containsKey(selectIndexs[nowSelectPartIndex-1])&&wordOfSelectIndex.get(selectIndexs[nowSelectPartIndex-1]).size()!=0)
        if(isNext){
            readMachine readMachine = new readMachine();
            int nextType = selectPartList.get(selectIndexs[nowSelectPartIndex]);
            int partIndex = selectIndexs[nowSelectPartIndex];
            String[] strings = Arrays.copyOfRange(strList,dividiStringIndexs[partIndex-1],dividiStringIndexs[partIndex]-1);
            List<Snack> wordSnack = new ArrayList<>();
            ObservableList<Snack> oblist = null;
            if(nextType == 0){
                String[] wordList = readMachine.readWordBySeperate(strings," ");
                for(int i = 0 ; i < wordList.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),wordList[i]));
                wordOfTotalNum.put(partIndex,wordList.length);
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
            }else if( nextType == 1 ){
                for(int i = 0 ; i < strings.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),strings[i]));
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
            }else if (nextType == 2){
                //tableMethod
                int startLine = 0;
                int[] realTableIndexs = typeTableIndexAndRealTableIndex.get(partIndex);
                for(int i = 0 ; i < realTableIndexs.length ; i++){
                    Table table = realTableList.get(realTableIndexs[i]);
                    int readFirst = 0;
                    if(i == 0){
                        readFirst = startLine;
                    }
                    String[][] tableExcuteString = readMachine.readTable(table,readFirst);
                    for(int j = 0 ; j < tableExcuteString.length ; j++){
                        StringBuilder string = new StringBuilder();
                        for(int k = 0 ; k < tableExcuteString[0].length ; k++){
                            string.append(tableExcuteString[j][k]+" ");
                        }
                        wordSnack.add(new Snack(String.valueOf(j+1),string.toString()));
                    }
                }
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("是否跳过表格首行？");
                alert.setContentText("该部分为表格，表格按行读取，单元格从左往右读取。注意！若跳过首行，在批量解析时将也跳过首行");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    startLine = 1;
                }
                if(tableReadFirstLine.containsKey(selectIndexs[nowSelectPartIndex])){
                    tableReadFirstLine.replace(selectIndexs[nowSelectPartIndex],startLine);
                }else{
                    tableReadFirstLine.put(selectIndexs[nowSelectPartIndex],startLine);
                }

            }
            informationText.appendText(String.format("%d:当前为第%d选择部分",informationIndex++,nowSelectPartIndex)+"\n\r");
            if(nowSelectPartIndex==1){
                ButtonShow3VBox.getChildren().set(3,lastWordPart);
            }
            if(nowSelectPartIndex == selectPartList.size()-1){
                ButtonShow3VBox.getChildren().set(2,nextStage4);
            }
            nowSelectPartIndex++;
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("您尚未对该部分命名Head，无法跳转到下一部分");
            alert.showAndWait();
        }
        });

        lastWordPart.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            readMachine readMachine = new readMachine();
            int lastType = selectPartList.get(selectIndexs[nowSelectPartIndex-2]);
            int partIndex = selectIndexs[nowSelectPartIndex-2];
            String[] strings = Arrays.copyOfRange(strList,dividiStringIndexs[partIndex-1],dividiStringIndexs[partIndex]);
            List<Snack> wordSnack = new ArrayList<>();
            ObservableList<Snack> oblist = null;
            if(lastType == 0){
                String[] wordList = readMachine.readWordBySeperate(strings," ");
                for(int i = 0 ; i < wordList.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),wordList[i]));
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
            }else if( lastType == 1 ){
                for(int i = 0 ; i < strings.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),strings[i]));
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
            }else if (lastType == 2){
                //tableMethod
                int startLine = 0;
                int[] realTableIndexs = typeTableIndexAndRealTableIndex.get(partIndex);
                for(int i = 0 ; i < realTableIndexs.length ; i++){
                    Table table = realTableList.get(realTableIndexs[i]);
                    int readFirst = 0;
                    if(i == 0){
                        readFirst = startLine;
                    }
                    String[][] tableExcuteString = readMachine.readTable(table,readFirst);
                    for(int j = 0 ; j < tableExcuteString.length ; j++){
                        StringBuilder string = new StringBuilder();
                        for(int k = 0 ; k < tableExcuteString[0].length ; k++){
                            string.append(tableExcuteString[j][k]+" ");
                        }
                        wordSnack.add(new Snack(String.valueOf(j+1),string.toString()));
                    }
                }
                oblist = FXCollections.observableArrayList(wordSnack);
                tableView.setItems(oblist);
                tableView.refresh();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("是否跳过表格首行？");
                alert.setContentText("该部分为表格，表格按行读取，单元格从左往右读取。");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    startLine = 1;
                }
            }
            if(nowSelectPartIndex == 2){
                ButtonShow3VBox.getChildren().set(3,lastStage2);
            }
            if(nowSelectPartIndex == selectPartList.size()){
                ButtonShow3VBox.getChildren().set(2,nextWordPart);
            }
            informationText.appendText(String.format("%d:当前为第%d选择部分",informationIndex++,selectIndexs[nowSelectPartIndex-1])+"\n\r");
            nowSelectPartIndex--;
        });

        nextStage4.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            boolean isNext = false;
            int nowType = typeHashMap.get(selectIndexs[nowSelectPartIndex-1]);
            if(partIndexAndLineHead.containsKey(selectIndexs[nowSelectPartIndex-1])){
                isNext = true;
            }
            if(isNext) {
                informationText.appendText(String.format("%d:阶段4，此阶段可生成种子文件和excel预览文件",informationIndex++,nowSelectPartIndex)+"\n\r");
                StageHBox.getChildren().set(1, ButtonShow4VBox);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("您尚未对该部分命名Head，无法跳转到下一部分");
                alert.showAndWait();
            }
        });

        lastStage2.setOnAction(actionEvent -> {
//            nowPartIndex--;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("返回上一阶段将清除本阶段进行的一切操作，是否返回？");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                int lastTypeIndex = typeHashMap.get(nowPartIndex);
                comboBox.getSelectionModel().select(lastTypeIndex);
                List<Snack> snackList = new ArrayList<>();
//            int bound = nowPartIndex == 1 ? 0 : nowPartIndex;
                for(int i = dividiStringIndexs[nowPartIndex-1]; i < dividiStringIndexs[nowPartIndex]-1 ; i++){
                    snackList.add(new Snack(String.valueOf(i+1),strList[i]));
                }
                ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
                tableView.setItems(obList);
                informationText.appendText(String.format("%d:返回阶段2，当前为第%d选择部分",informationIndex++,nowSelectPartIndex)+"\n\r");
                StageHBox.getChildren().set(0,tableView);
                StageHBox.getChildren().set(1,ButtonShow2VBox);
            }
        });

        //----------------------------------------------------------------------------------------------------------
//        VBox ButtonShow4VBox = new VBox();
        Button generateTheSeed = new Button("生成种子文件");
        Button generateTheExcel = new Button("生成EXCEL预览文件");
        Button lastStage3 = new Button("上一阶段");

        lastStage3.setOnAction(actionEvent -> {
            informationText.appendText(String.format("%d:返回阶段3",informationIndex++,nowSelectPartIndex)+"\n\r");
            StageHBox.getChildren().set(1,ButtonShow3VBox);
        });

        generateTheSeed.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser=new DirectoryChooser();
            File file = directoryChooser.showDialog(primaryStage);
            String path = file.getPath();
            path = path.replaceAll("\\\\","\\\\\\\\");
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setContentText("请编辑种子文件名，请勿使用特别符号！");
            Optional<String> fileName = textInputDialog.showAndWait();
            path = path+"\\"+fileName.get()+".txt";
            //totaopart、divideString、typeHashMap、firstLine、endLine、tabulaNum、
            ArrayList<String> divideString = new ArrayList<>();
            for(String s : divideStringList) divideString.add(s);
            seedPdfStructure = new pdfStructure(totalPart,divideString);
            int[] selectSection = new int[selectPartList.size()];
            int k = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                selectSection[k] = set.getKey();
                k++;
            }
            Arrays.sort(selectSection);
            int typeIndex = 0;
            int[] typeIndexs = new int[typeHashMap.size()];
            for(Map.Entry<Integer,Integer> set : typeHashMap.entrySet()){
                typeIndexs[typeIndex] = set.getKey();
                typeIndex++;
            }
            Arrays.sort(typeIndexs);
            for(int i : typeIndexs){
                int type = typeHashMap.get(i);
                pdfStructure sonPdfStructure = new pdfStructure();
                sonPdfStructure.setIndexInFatherStructure(i);
                sonPdfStructure.setPartType(type);
                if(Arrays.binarySearch(selectSection,i)>=0){
                    ArrayList<String> sonDivideStringList = new ArrayList<>();
                    sonDivideStringList.add(divideStringList[i-1]);
                    sonDivideStringList.add(divideStringList[i]);
                    sonPdfStructure.setDivideString(sonDivideStringList);
                    HashMap<Integer,String> LineAndHead = partIndexAndLineHead.get(i);
                    if(type == 0){
                        ArrayList<wordStructure> selectWord = new ArrayList<>();
                        for(Map.Entry<Integer,String> set : LineAndHead.entrySet()){
                            wordStructure wordStructure = new wordStructure();
                            wordStructure.setWordIndex(set.getKey());
                            wordStructure.setWordHeadName(set.getValue());
                            selectWord.add(wordStructure);
                        }
                        sonPdfStructure.setSelectWord(selectWord);
                    }else if(type == 1){
                        sonPdfStructure.setLongTxtHead(LineAndHead.get(1));
                    }else if( type == 2){
                        tabulaStructure tabulaStructure = new tabulaStructure(i);
                        ArrayList<String> tabulaHeadList = new ArrayList<>();
                        for(int index = 0 ; index < LineAndHead.size() ; index++){
                            String h = LineAndHead.get(index+1);
                            tabulaHeadList.add(h);
                        }
                        tabulaStructure.setTabulaHeadName(tabulaHeadList);
                        boolean readFirst = true;
                        if(tableReadFirstLine.get(i)==1){
                            readFirst = false;
                        }
                        tabulaStructure.setReadTabulaFirstLine(readFirst);
                        sonPdfStructure.setSelectTS(tabulaStructure);
                    }
                }
                sonPdfStructure.setFatherStructure(seedPdfStructure);
                seedPdfStructure.addSonStruture(sonPdfStructure);
            }
            seedPdfStructure.setSelectSection(selectSection);
            seedPdfStructure.setTabulaNum(tableAndDivideString.size());
            seedPdfStructure.setFirstLine(headLine);
            seedPdfStructure.setEndLine(endLine);
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
                objectOutputStream.writeObject(seedPdfStructure);
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        generateTheExcel.setOnAction(actionEvent -> {
            try {
                String path = PdfFileList.get(0).getPath();
                path = path.replaceAll("\\\\","\\\\\\\\");
                excutePdfsByStructure excutePdfsByStructure = new excutePdfsByStructure(seedPdfStructure,path);
                HashMap<String,String> headAndWord = excutePdfsByStructure.getHeadAndWord();
                ArrayList<String> sortKey = excutePdfsByStructure.getSortedKey();
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File file = directoryChooser.showDialog(primaryStage);
                String excelPath = file.getPath();
                excelPath = excelPath.replaceAll("\\\\","\\\\\\\\");
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setContentText("请编辑Excel文件名，请勿使用特别符号！");
                Optional<String> fileName = textInputDialog.showAndWait();
                excelPath = excelPath+"\\"+fileName.get()+".xlsx";
                CreateExcel createExcel = new CreateExcel(excelPath);
                ArrayList<HashMap<Integer,ArrayList<String>>> headlist = new ArrayList<>();
                headlist.add(excutePdfsByStructure.getTableIndexAndHead());
                createExcel.setHead(sortKey,headlist);
                createExcel.writedataByRow(headAndWord);
                createExcel.create();
                createExcel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //----------------------------------------------------------------------------------------------------------

//        VBox ButtonShow4VBox = new VBox(20);
//        Button selectTheTable = new Button("选择该表格");
//        Button excuteTable = new Button("解析当前表格");
//        Button cancelSelectTheTable = new Button("取消选择该表格");
//        Button nextTable = new Button("下一个表格");
//        Button lastStage3 = new Button("上一阶段3");
//        Button nextStage5 = new Button("下一阶段5");
//        int tableIndex = -1;
//        List<String> readTypeList = Arrays.asList("行读取","列读取");
//        ObservableList<String> readobList = FXCollections.observableArrayList(readTypeList);
//        ComboBox<String> readComboBox = new ComboBox<String>(readobList); // 依据指定数据创建下拉框
//        //comboBox.setItems(obList); // 设置下拉框的数据来源
//        readComboBox.getSelectionModel().select(0); // 设置下拉框默认选中第1项
//        List<String> HeadNameList = Arrays.asList("表格首行设置为Head","跳过表头自定义Head");
//        ObservableList<String> headNameobList = FXCollections.observableArrayList(HeadNameList);
//        ComboBox<String> headNameComboBox = new ComboBox<String>(headNameobList); // 依据指定数据创建下拉框
//        //comboBox.setItems(obList); // 设置下拉框的数据来源
//        headNameComboBox.getSelectionModel().select(0); // 设置下拉框默认选中第1项

        //----------------------------------------------------------------------------------------------------------

//        Button setName = new Button("自定义该字段Head");
////        TextInputDialog setHeadTextInputDialog = new TextInputDialog();
////        setHeadTextInputDialog.setTitle("自定义Head");
////        setHeadTextInputDialog.setHeaderText("请设置该|字段|的Head");
////        setHeadTextInputDialog.setContentText("请输入表头名（不输入默认为无）：");
//        Button generatePredictExcel = new Button("生成预览表格文件");
//        Button generateSeedFile = new Button("生成种子文件");

        //----------------------------------------------------------------------------------------------------------

        VBox.setVgrow(informationText,Priority.ALWAYS);
        InformationVBox.getChildren().addAll(titileInformation,informationText);
        ButtonShow1VBox.getChildren().addAll(Selected,cancelSelectTheLine,nextStage2);
        ButtonShow1VBox.setAlignment(Pos.CENTER);
        ButtonShow2VBox.getChildren().addAll(comboBox,selected,cancelSelect,nextPart,lastStage1);
        ButtonShow2VBox.setAlignment(Pos.CENTER);
        ButtonShow3VBox.getChildren().addAll(selectTheWord,cancelSelectTheWord,nextWordPart,lastStage2,setNameByMyself);
        ButtonShow3VBox.setAlignment(Pos.CENTER);
        ButtonShow4VBox.getChildren().addAll(generateTheSeed,generateTheExcel,lastStage3);
        ButtonShow4VBox.setAlignment(Pos.CENTER);
        setHeadButtonVBox.getChildren().addAll(savaTheHead,backToStage3);
        setHeadButtonVBox.setAlignment(Pos.CENTER);
//        ButtonShow4VBox.getChildren().addAll(excuteTable,selectTheTable,cancelSelectTheTable,nextTable,lastStage3);
//        ButtonShow4VBox.setAlignment(Pos.CENTER);
        StageHBox.getChildren().addAll(tableView,ButtonShow1VBox);
        wordShowHBox.getChildren().addAll(StageHBox,InformationVBox);
        HBox.setHgrow(StageHBox,Priority.ALWAYS);
        HBox.setHgrow(tableView,Priority.ALWAYS);
        HBox.setHgrow(setHeadtableView,Priority.ALWAYS);
        //文件菜单相关----------------------------------------------------------------------------------------------------------
        Menu menu = new Menu("浏览文件");
        MenuItem PDFitem = new MenuItem("选择一个或多个PDF文件....");
        MenuItem SeedItem = new MenuItem("选择种子文件....");
        MenuItem excute = new MenuItem("批量解析");
        menu.getItems().addAll(PDFitem,SeedItem,excute);
        //创建文件选择器
        FileChooser PDFfileChooser = new FileChooser();
        PDFfileChooser.setTitle("选择PDF文件....");
        PDFfileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.pdf"));
        //在菜单项上添加动作

        PDFitem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                List<File> fileList = PDFfileChooser.showOpenMultipleDialog(primaryStage);
                //打开对话框
//                PdfFileList = PDFfileChooser.showOpenMultipleDialog(primaryStage);
                try {
                    LoadFile loadFile = null;
                    if(fileList!=null&&fileList.size()==1){
                        PdfFileList = fileList;
                        informationText.clear();
                        informationIndex = 1;
                        informationText.appendText(String.format("%d:选择了%s",informationIndex++,PdfFileList.get(0).getName())+"\n\r");
                        TextInputDialog HeadtextInputDialog = new TextInputDialog();
                        HeadtextInputDialog.setTitle("是否读页眉");
                        HeadtextInputDialog.setHeaderText("警告！若PDF文件存在页眉会影响解析的准确性，且去除页眉时请确保pdf文件中每页的页眉都具有相同行数(图片可忽略)");
                        HeadtextInputDialog.setContentText("请输入需跳过页眉的行数（不输入默认无页眉）：");
                        Optional<String> Headresult = HeadtextInputDialog.showAndWait();
                        TextInputDialog FoottextInputDialog = new TextInputDialog();
                        FoottextInputDialog.setTitle("是否读页尾");
                        FoottextInputDialog.setHeaderText("警告！若PDF文件存在页尾会影响解析的准确性，且去除页眉时请确保pdf文件中每页的页尾都具有相同行数(图片可忽略)");
                        FoottextInputDialog.setContentText("请输入需跳过页尾的行数（不输入默认无页尾）：");
                        Optional<String> Footresult = FoottextInputDialog.showAndWait();
                        headLine = 0;
                        if(!Headresult.isEmpty()&&!Headresult.get().equals("")){
                            headLine = Integer.parseInt(Headresult.get());
                        }
                        endLine = 0;
                        if(!Footresult.isEmpty()&&!Headresult.get().equals("")){
                            endLine = Integer.parseInt(Footresult.get());
                        }
                        try{
                            loadFile = new LoadFile(PdfFileList.get(0));
                        }catch (IOException e){
                            System.out.println("缺少字体mstmc.ttf");
                        }
                        strList = loadFile.getBasicStringByLine(headLine,endLine);
                        pageIndexAndString = loadFile.getBasicStringByLineForTable(headLine,endLine);
                        loadFile.loadTables();
                        realTableList = loadFile.getTableList();
                        loadFile.closePDFDocument();
                        List<Snack> snackList = new ArrayList<>();
                        for(int i = 0 ; i < strList.length ; i++){
                            snackList.add(new Snack(String.valueOf(i+1),strList[i]));
                        }
                        ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
                        divideStringMap =  new HashMap<>();
                        totalPart = 1;
                        nowPartIndex = 1;
                        nowSelectPartIndex = 1;
                        tableView.setItems(obList);
                        tableView.refresh();
                        StageHBox.getChildren().set(1,ButtonShow1VBox);
                        divideStringMap.put(1,strList[0]);
                        divideStringMap.put(strList.length, strList[strList.length-1]);
                    }else if (fileList!=null&&fileList.size()>1){
                        PdfFileList = fileList;
                        informationText.clear();
                        informationIndex = 1;
                        String s = "";
                        for(int i = 0 ; i < PdfFileList.size(); i++){
                            s = s + PdfFileList.get(i).getName();
                            if(i != PdfFileList.size()-1) s += "、";
                        }
                        informationText.appendText(String.format("%d:选择了%s",informationIndex++,s)+"\n\r");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});

        FileChooser SeedfileChooser = new FileChooser();
        SeedfileChooser.setTitle("选择种子txt文件....");
        SeedfileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.txt"));

        SeedItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //打开对话框
                File file = SeedfileChooser.showOpenDialog(primaryStage);
                if(file!=null){
                    SeedFile = file;
                    informationText.appendText(String.format("%d:选择了%s",informationIndex++,SeedFile.getName())+"\n\r");
                }
            }});

        excute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(PdfFileList!=null){
                    if(SeedFile!=null){
                        informationText.appendText(String.format("%d:开始解析，请稍后！",informationIndex++)+"\n\r");
                        DirectoryChooser directoryChooser=new DirectoryChooser();
                        File file = directoryChooser.showDialog(primaryStage);
                        String excelPath = file.getPath();
                        excelPath = excelPath.replaceAll("\\\\","\\\\\\\\");
                        TextInputDialog textInputDialog = new TextInputDialog();
                        textInputDialog.setContentText("请编辑Excel文件名，请勿使用特别符号！");
                        Optional<String> fileName = textInputDialog.showAndWait();
                        excelPath = excelPath+"\\"+fileName.get()+".xlsx";
                        CreateExcel createExcel = new CreateExcel(excelPath);
                        Thread excuteThread = new Thread(() -> {
                            try{
                                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SeedFile)));
                                pdfStructure seedPdfStructure = (pdfStructure)objectInputStream.readObject();
                                ArrayList<HashMap<String,String>> data = new ArrayList<>();
                                ArrayList<HashMap<Integer,ArrayList<String>>> headlist = new ArrayList<>();
                                ArrayList<String> sortKey = new ArrayList<>();
                                for(int i = 0 ; i < PdfFileList.size() ; i++){
                                        String path = PdfFileList.get(i).getPath();
                                        path = path.replaceAll("\\\\","\\\\\\\\");
                                        excutePdfsByStructure excutePdfsByStructure = new excutePdfsByStructure(seedPdfStructure,path);
                                        data.add(excutePdfsByStructure.getHeadAndWord());
                                        headlist.add(excutePdfsByStructure.getTableIndexAndHead());
                                        sortKey = excutePdfsByStructure.getSortedKey();
                                    }
                                createExcel.setHead(sortKey,headlist);
                                for(int i = 0; i < data.size() ; i++){
                                        createExcel.writedataByRow(data.get(i));
                                    }
                                    createExcel.create();
                                    createExcel.close();
                                    informationText.appendText(String.format("%d:解析完毕！",informationIndex++)+"\n\r");
                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
                            });
                            excuteThread.start();
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("尚未选择用于解析的种子文件");
                        alert.showAndWait();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("尚未选择要解析的PDF文件");
                    alert.showAndWait();
                }
            }
        });
        //----------------------------------------------------------------------------------------------------------

        initialPane.setCenter(wordShowHBox);
        //创建菜单栏并向其中添加菜单。
        MenuBar menuBar = new MenuBar(menu);
        Group group = new Group(menuBar);
        initialPane.setTop(group);
        Scene scene = new Scene(initialPane, 1600, 800, Color.BEIGE);
        //----------------------------------------------------------------------------------------------------------
        primaryStage.setTitle("基于电子病案PDF格式转换软件");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
