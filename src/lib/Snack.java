package lib;

import javafx.beans.property.SimpleStringProperty;

public class Snack {
    private SimpleStringProperty Index; // 序号
    private SimpleStringProperty name; // 快餐名称
    private SimpleStringProperty price; // 快餐价格

    public Snack(String xuhao, String name) {
        this.Index = new SimpleStringProperty(xuhao);
        this.name = new SimpleStringProperty(name);
    }

    public String getIndex() { // 获取序号
        return Index.get();
    }
    public void setIndex(String Index) { // 设置序号
        this.Index.set(Index);
    }
    public String getName() { // 获取快餐名称
        return name.get();
    }
    public void setName(String name) { // 设置快餐名称
        this.name.set(name);
    }
}
