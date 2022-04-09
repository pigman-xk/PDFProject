package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lib.LoadFile;
import lib.Snack;
import lib.readMachine;
import technology.tabula.Table;


import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main extends Application {
    List<File> PdfFileList = null;
    File SeedFile = null;
    int[] dividiStringIndexs = null;
    String[] strList = null;
    String[] divideStringList = null;
    int informationIndex = 1;
    int totalPart = 1;
    int nowPartIndex = 0;
    int nowSelectPartIndex = 1;
    HashMap<Integer,String[]> pageIndexAndString = null;
    HashMap<Integer,Integer> typeHashMap = null;
    HashMap<Integer,Integer> selectPartList = null;
    HashMap<Integer,String[]> tableAndDivideString = null;
    HashMap<Integer,int[]> typeTableIndexAndRealTableIndex = null;
    HashMap<Integer,Integer> wordOfTotalNum = null;
    HashMap<Integer,ArrayList<Integer>> wordOfSelectIndex = null;
    HashMap<Integer,String> divideStringMap = new HashMap<>();
    ArrayList<Table> realTableList = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //----------------------------------------------------------------------------------------------------------
        BorderPane initialPane = new BorderPane();
        HBox wordShowHBox = new HBox();
        VBox ButtonShow2VBox = new VBox(20);
//        HashMap<Integer,String> divideStringMap = new HashMap<>();
//        for(Map.Entry<Integer, String> set : divideStringMap.entrySet()){
//
//        }


        //----------------------------------------------------------------------------------------------------------
        Button Selected = new Button("确定为分割行");
        Button nextStage2 = new Button("下一阶段2");
        Button cancelSelectTheLine = new Button("取消该分割行");
        VBox ButtonShow1VBox = new VBox(20);
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

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Snack>() {
            @Override
            public void changed(ObservableValue<? extends Snack> arg0, Snack old_str, Snack new_str) {
                // getSelectedIndex方法可获得选中项的序号，getSelectedItem方法可获得选中项的对象
                String desc = String.format("第%d项:%s",
                        tableView.getSelectionModel().getSelectedIndex()+1,
                        tableView.getSelectionModel().getSelectedItem().getName().toString());
                selectLabel.setText(desc); // 在标签上显示当前选中的文本项
            }
        });

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
            if(index == 1 || index == strList.length){
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
            if(PdfFileList!=null&&PdfFileList.size()==1){
                StageHBox.getChildren().set(1,ButtonShow2VBox);
                if(divideStringMap.size()!=2){
                    int[] indexs = new int[divideStringMap.size()];
                    int index = 0;
                    for(Map.Entry<Integer,String> keySet : divideStringMap.entrySet()){
                        indexs[index] = keySet.getKey();
                        index++;
                    }
                    Arrays.sort(indexs);
                    dividiStringIndexs = indexs;
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
                    totalPart = indexs.length+1;
                    informationText.appendText(String.format("%d:您选择了第%s行作为分割行,全文被分割为%d部分",informationIndex++,str,totalPart)+"\n\r");
                    informationText.appendText(String.format("%d:当前为第1分割部分",informationIndex++));
                    if(ButtonShow2VBox.getChildren().get(3)!=nextPart){
                        ButtonShow2VBox.getChildren().add(3,nextPart);
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
                        ButtonShow2VBox.getChildren().remove(3);
                    }
                    divideStringList = new String[divideStringMap.size()];
                    for(int i = 0 ; i < dividiStringIndexs.length ; i++){
                        String divideString = divideStringMap.get(dividiStringIndexs[i]);
                        divideStringList[i] = divideString;
                    }
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
        Button nextPart = new Button("下一分割部分");
        Button lastPart = new Button("上一分割部分");
        Button lastStage1 = new Button("上一阶段1");
        Button nextStage3 = new Button("下一阶段3");
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
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("混合信息不需要保存");
                    alert.showAndWait();
                }
            }else{
                informationText.appendText(String.format("%d:第%d部分已经被保存，无需重复保存,如需更改类型，请取消保存后再保存",informationIndex++,nowPartIndex));
            }
        });

        cancelSelect.setOnAction(actionEvent -> {
            if(selectPartList.containsKey(nowPartIndex)){
                selectPartList.remove(nowPartIndex);
            }else{
                informationText.appendText(String.format("%d:第%d部分尚未被保存，无法取消",informationIndex++,nowPartIndex));
            }
        });

        nextPart.setOnAction(actionEvent -> {
            if(nowPartIndex==1){
                ButtonShow2VBox.getChildren().set(4,lastPart);
            }else if(nowPartIndex == totalPart-1){
                ButtonShow2VBox.getChildren().set(3,nextStage3);
            }
            List<Snack> snackList = new ArrayList<>();
            int bound = nowPartIndex < totalPart-1 ? dividiStringIndexs[nowPartIndex]-1 : strList.length-1;
            for(int i = dividiStringIndexs[nowPartIndex-1]; i < bound ; i++){
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
                typeHashMap.put(nowPartIndex,typeIndex);
            }else if(typeHashMap.get(nowPartIndex) != typeIndex){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText(String.format("检测到该部分已更改为%s类型，是否确定更改？",type));
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get()==ButtonType.OK){
                    typeHashMap.replace(nowPartIndex,typeIndex);
                }
            }
            nowPartIndex++;
            if(typeHashMap.containsKey(nowPartIndex)){
                comboBox.getSelectionModel().select(typeHashMap.get(nowPartIndex));
            }else{
                comboBox.getSelectionModel().select(0);
            }
            tableView.setItems(obList);
            tableView.refresh();
        });

        lastPart.setOnAction(actionEvent -> {
            if(nowPartIndex==2){
                ButtonShow2VBox.getChildren().set(4,lastStage1);
            }
            nowPartIndex--;
            int lastTypeIndex = typeHashMap.get(nowPartIndex);
            comboBox.getSelectionModel().select(lastTypeIndex);
            List<Snack> snackList = new ArrayList<>();
//            int bound = nowPartIndex < totalPart-1 ? dividiStringIndexs[nowPartIndex]-1 : strList.length-1;
            for(int i = dividiStringIndexs[nowPartIndex-1]; i < dividiStringIndexs[nowPartIndex]-1 ; i++){
                snackList.add(new Snack(String.valueOf(i+1),strList[i]));
            }
            ObservableList<Snack> obList = FXCollections.observableArrayList(snackList);
            tableView.setItems(obList);
            tableView.refresh();
        });

        nextStage3.setOnAction(actionEvent -> {
            if(selectPartList.size()==0){
                Alert noSelect = new Alert(Alert.AlertType.WARNING);
                noSelect.setContentText("您尚未保存任何一个部分！");
            }else{
                String type = comboBox.getSelectionModel().getSelectedItem();
                int typeIndex = 0;
                if(type.equals("字段")) typeIndex = 0;
                if(type.equals("长文本")) typeIndex = 1;
                if(type.equals("表格")) typeIndex = 2;
                if(type.equals("混合信息")) typeIndex = 3;
                if(!typeHashMap.containsKey(nowPartIndex)){
                    typeHashMap.put(nowPartIndex,typeIndex);
                }else if(typeHashMap.get(nowPartIndex) != typeIndex){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText(String.format("检测到该部分已更改为%s类型，是否确定更改？",type));
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get()==ButtonType.OK){
                        typeHashMap.replace(nowPartIndex,typeIndex);
                    }
                }
                StageHBox.getChildren().set(1,ButtonShow3VBox);
                wordOfTotalNum = new HashMap<>();
                wordOfSelectIndex = new HashMap<>();
                int[] selectIndexs = new int[selectPartList.size()];
                int index = 0;
                for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                    int selectIndex = set.getKey();
                    selectIndexs[index] = selectIndex;
                    index++;
                }
                Arrays.sort(selectIndexs);
                readMachine readMachine = new readMachine();
                tableAndDivideString = readMachine.getTableAndDivideString(typeHashMap,divideStringList);
                HashMap<Integer,int[]> tableIndexAndRealTablesIndex = readMachine.readTableForExcute(pageIndexAndString,tableAndDivideString);
                HashMap<Integer,Integer> typeIndexAndTableIndex = readMachine.getTypeIndexAndTableIndex(typeHashMap);
                typeTableIndexAndRealTableIndex = readMachine.getTypeTableIndexAndRealTableIndex(tableIndexAndRealTablesIndex,typeIndexAndTableIndex);
                int partIndex = selectIndexs[nowPartIndex];
                int typeFisrtSelectPart = selectPartList.get(partIndex);
                String[] strings = Arrays.copyOfRange(strList,dividiStringIndexs[partIndex-1],dividiStringIndexs[partIndex]);
                List<Snack> wordSnack = new ArrayList<>();
                ObservableList<Snack> oblist = null;
                if(typeFisrtSelectPart == 0){
                    String[] wordList = readMachine.readWordBySeperate(strings," ");
                    for(int i = 0 ; i < wordList.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),wordList[i]));
                    wordOfTotalNum.put(partIndex,wordList.length);
                    oblist = FXCollections.observableArrayList(wordSnack);
                }else if( typeFisrtSelectPart == 1 ){
                    for(int i = 0 ; i < strings.length ; i++) wordSnack.add(new Snack(String.valueOf(i+1),strings[i]));
                    oblist = FXCollections.observableArrayList(wordSnack);
                }else if (typeFisrtSelectPart == 2){
                    //tableMethod
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("是否跳过表格首行？");
                    alert.setContentText("该部分为表格，表格按行读取，单元格从左往右读取。");
                    Optional<ButtonType> result = alert.showAndWait();
                    int startLine = 0;
                    if(result.get() == ButtonType.OK){
                        startLine = 1;
                    }
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
                }
                tableView.setItems(oblist);
                tableView.refresh();
            }
        });

        //----------------------------------------------------------------------------------------------------------

        ScrollPane setHeadPane = new ScrollPane();
        














        //----------------------------------------------------------------------------------------------------------
        VBox ButtonShow3VBox = new VBox(20);
        Button selectTheWord = new Button("选择该字段");
        Button cancelSelectTheWord = new Button("取消选择该字段");
        Button setNameByMyself = new Button("命名该字段头");
        Button nextWordPart = new Button("下一个部分");
        Button lastStage2 = new Button("上一阶段2");
        Button nextStage4 = new Button("下一阶段4");

        selectTheWord.setOnAction(actionEvent -> {
            int[] selectIndexs = new int[selectPartList.size()];
            int index = 0;
            for(Map.Entry<Integer,Integer> set : selectPartList.entrySet()){
                int selectIndex = set.getKey();
                selectIndexs[index] = selectIndex;
                index++;
            }
            Arrays.sort(selectIndexs);
            int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex]);
            if(nowType == 2 || nowType == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("该选择部分不是字段类型，无需选择,请直接命名该部分的Head");
                alert.showAndWait();
            }else{
                int partIndex = selectIndexs[nowPartIndex];
                int lineIndex = tableView.getSelectionModel().getSelectedIndex();
                if(wordOfSelectIndex.containsKey(partIndex)){
                    ArrayList<Integer> arr = wordOfSelectIndex.get(partIndex);
                    if(arr.contains(lineIndex)){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("已选择该字段，无需重复选择");
                        alert.showAndWait();
                    }else{
                        arr.add(lineIndex);
                    }
                }else{
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(lineIndex);
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
            int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex]);
            if(nowType == 2 || nowType == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("该选择部分不是字段类型，无法操作");
                alert.showAndWait();
            }else{
                int partIndex = selectIndexs[nowPartIndex];
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
            int nowType = selectPartList.get(selectIndexs[nowSelectPartIndex]);
            if()
        });


        //----------------------------------------------------------------------------------------------------------

        VBox ButtonShow4VBox = new VBox(20);
        Button selectTheTable = new Button("选择该表格");
        Button excuteTable = new Button("解析当前表格");
        Button cancelSelectTheTable = new Button("取消选择该表格");
        Button nextTable = new Button("下一个表格");
        Button lastStage3 = new Button("上一阶段3");
        Button nextStage5 = new Button("下一阶段5");
        int tableIndex = -1;
        List<String> readTypeList = Arrays.asList("行读取","列读取");
        ObservableList<String> readobList = FXCollections.observableArrayList(readTypeList);
        ComboBox<String> readComboBox = new ComboBox<String>(readobList); // 依据指定数据创建下拉框
        //comboBox.setItems(obList); // 设置下拉框的数据来源
        readComboBox.getSelectionModel().select(0); // 设置下拉框默认选中第1项
        List<String> HeadNameList = Arrays.asList("表格首行设置为Head","跳过表头自定义Head");
        ObservableList<String> headNameobList = FXCollections.observableArrayList(HeadNameList);
        ComboBox<String> headNameComboBox = new ComboBox<String>(headNameobList); // 依据指定数据创建下拉框
        //comboBox.setItems(obList); // 设置下拉框的数据来源
        headNameComboBox.getSelectionModel().select(0); // 设置下拉框默认选中第1项

        //----------------------------------------------------------------------------------------------------------

        Button setName = new Button("自定义该字段Head");
//        TextInputDialog setHeadTextInputDialog = new TextInputDialog();
//        setHeadTextInputDialog.setTitle("自定义Head");
//        setHeadTextInputDialog.setHeaderText("请设置该|字段|的Head");
//        setHeadTextInputDialog.setContentText("请输入表头名（不输入默认为无）：");
        Button generatePredictExcel = new Button("生成预览表格文件");
        Button generateSeedFile = new Button("生成种子文件");

        //----------------------------------------------------------------------------------------------------------

        VBox.setVgrow(informationText,Priority.ALWAYS);
        InformationVBox.getChildren().addAll(titileInformation,informationText);
        ButtonShow1VBox.getChildren().addAll(Selected,cancelSelectTheLine,nextStage2);
        ButtonShow1VBox.setAlignment(Pos.CENTER);
        ButtonShow2VBox.getChildren().addAll(comboBox,selected,cancelSelect,nextPart,lastStage1);
        ButtonShow2VBox.setAlignment(Pos.CENTER);
        ButtonShow3VBox.getChildren().addAll(selectTheWord,cancelSelectTheWord,nextWordPart,lastStage2);
        ButtonShow3VBox.setAlignment(Pos.CENTER);
        ButtonShow4VBox.getChildren().addAll(excuteTable,selectTheTable,cancelSelectTheTable,nextTable,lastStage3);
        ButtonShow4VBox.setAlignment(Pos.CENTER);
        StageHBox.getChildren().addAll(tableView,ButtonShow1VBox);
        wordShowHBox.getChildren().addAll(StageHBox,InformationVBox);
        HBox.setHgrow(StageHBox,Priority.ALWAYS);
        HBox.setHgrow(tableView,Priority.ALWAYS);
        //文件菜单相关----------------------------------------------------------------------------------------------------------
        Menu menu = new Menu("浏览文件");
        MenuItem PDFitem = new MenuItem("选择一个或多个PDF文件....");
        MenuItem SeedItem = new MenuItem("选择种子文件....");
        menu.getItems().addAll(PDFitem,SeedItem);
        //创建文件选择器
        FileChooser PDFfileChooser = new FileChooser();
        PDFfileChooser.setTitle("选择PDF文件....");
        PDFfileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.pdf"));
        //在菜单项上添加动作

        PDFitem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //打开对话框
                PdfFileList = PDFfileChooser.showOpenMultipleDialog(primaryStage);
                try {
                    LoadFile loadFile = null;
                    if(PdfFileList!=null&&PdfFileList.size()==1){
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
                        int headLine = 0;
                        if(!Headresult.isEmpty()&&!Headresult.get().equals("")){
                            headLine = Integer.parseInt(Headresult.get());
                        }
                        int endLine = 0;
                        if(!Footresult.isEmpty()&&!Headresult.get().equals("")){
                            endLine = Integer.parseInt(Footresult.get());
                        }
                        loadFile = new LoadFile(PdfFileList.get(0));
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
                        selectPartList = new HashMap<>();
                        tableView.setItems(obList);
                        tableView.refresh();
                        StageHBox.getChildren().set(1,ButtonShow1VBox);
                        divideStringMap.clear();
                        divideStringMap.put(1,strList[0]);
                        divideStringMap.put(strList.length, strList[strList.length-1]);
                    }
                } catch (IOException e) {
                    System.out.println("选择了0项文件");
                }
            }});

        FileChooser SeedfileChooser = new FileChooser();
        SeedfileChooser.setTitle("选择种子txt文件....");
        SeedfileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.txt"));

        SeedItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //打开对话框
                SeedFile = SeedfileChooser.showOpenDialog(primaryStage);
            }});
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
