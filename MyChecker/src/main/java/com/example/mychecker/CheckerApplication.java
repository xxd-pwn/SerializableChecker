package com.example.mychecker;

import com.example.mychecker.MySQl.MultiThreadTr;
import com.example.mychecker.MySQl.analysis;
import com.example.mychecker.MySQl.getTrSeqMap;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class CheckerApplication extends Application {
    /**
     * 前端可视化类，检查器运行入口
     *
     */
    @Override
    public void start(Stage stage) {

        //主页面
        AnchorPane root0 = new AnchorPane();

        //按钮组件，跳转至源代码
        Button link0 = new Button("点击获取源代码");
        link0.setLayoutX(525);
        link0.setLayoutY(710);
        link0.setFont(Font.font(null, FontWeight.BOLD, 15));
        link0.setTextFill(Color.BLUE);
        link0.setOnAction((e -> {
            getHostServices().showDocument("https://github.com/xxd-pwn/SerializableChecker");//源代码网址
        }));

        //标题label
        Text headline0 = new Text("数据库系统隔离异常检查器");
        headline0.setLayoutX(450);
        headline0.setLayoutY(100);
        headline0.setCache(true);
        headline0.setFill(Color.BLUE);
        headline0.setFont(Font.font(null, FontWeight.BOLD, 25));
        Reflection r0 = new Reflection();
        r0.setFraction(0.5);
        headline0.setEffect(r0);

        //待测试数据库选择
        Label tmp0 = new Label("请选择数据库");
        tmp0.setLayoutX(500);
        tmp0.setLayoutY(320);
        tmp0.setFont(Font.font(null, FontWeight.BOLD, 15));

        ChoiceBox dbms = new ChoiceBox();
        dbms.getItems().add("MySQL");
        dbms.getItems().add("Oracle");
        dbms.getItems().add("Redis");
        dbms.setLayoutX(615);
        dbms.setLayoutY(320);

        Button db = new Button("确定");
        db.setTextFill(Color.CADETBLUE);
        db.setFont(Font.font(null, FontWeight.BOLD, 15));
        db.setLayoutX(565);
        db.setLayoutY(375);
        root0.getChildren().addAll(db, dbms, headline0, link0, tmp0);

        //选择事件
        db.setOnAction(e0 ->{
            String dbname = (String)dbms.getValue();

            //以下为对MySQL的测试部分
            if(dbname.equals("MySQL")){
                Stage stage0 = new Stage();
                AnchorPane root = new AnchorPane();
                //按钮组件，跳转至源代码
                Button link = new Button("点击获取源代码");
                link.setLayoutX(525);
                link.setLayoutY(710);
                link.setFont(Font.font(null, FontWeight.BOLD, 15));
                link.setTextFill(Color.BLUE);
                link.setOnAction((e -> {
                    getHostServices().showDocument("https://github.com/xxd-pwn/SerializableChecker");//源代码网址
                }));

                //标题label
                Text headline = new Text("数据库系统隔离异常检查器（MySQL）");
                headline.setLayoutX(380);
                headline.setLayoutY(100);
                headline.setCache(true);
                headline.setFill(Color.BLUE);
                headline.setFont(Font.font(null, FontWeight.BOLD, 25));
                Reflection r = new Reflection();
                r.setFraction(0.5);
                headline.setEffect(r);

                //background --图片地址需要绝对路径，在其他地方运行请修改路径
//                BackgroundImage myBI= new BackgroundImage(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\2.png",1200,800,false,true),
//                        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                        BackgroundSize.DEFAULT);
//                root.setBackground(new Background(myBI));

                //设置输入框
//        Text MarkedWords = new Text("请在下面输入数据库隔离级别、Session数、事务数目与每个事务的操作数，以英文符号‘-’隔开");
//        MarkedWords.setLayoutX(300);
//        MarkedWords.setLayoutY(300);
//        MarkedWords.setFill(Color.CADETBLUE);
//        MarkedWords.setFont(Font.font(null, FontWeight.BOLD, 18));
                TextField parameter1 = new TextField();
                parameter1.setLayoutX(585);
                parameter1.setLayoutY(350);
                parameter1.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex1 = new Label("请输入session数目");
                ex1.setLayoutX(440);
                ex1.setLayoutY(355);
                ex1.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter2 = new TextField();
                parameter2.setLayoutX(585);
                parameter2.setLayoutY(380);
                parameter2.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex2 = new Label("请输入session中事务数目");
                ex2.setLayoutX(440);
                ex2.setLayoutY(385);
                ex2.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter3 = new TextField();
                parameter3.setLayoutX(585);
                parameter3.setLayoutY(410);
                parameter3.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex3 = new Label("请输入事务中操作的数目");
                ex3.setLayoutX(440);
                ex3.setLayoutY(415);
                ex3.setFont(Font.font(null, FontWeight.BOLD, 12));

                ChoiceBox isoLevelchoiceBox = new ChoiceBox();
                isoLevelchoiceBox.getItems().add("Serializable");
                isoLevelchoiceBox.getItems().add("Repeatable Read");
                isoLevelchoiceBox.getItems().add("Read Committed");
                isoLevelchoiceBox.getItems().add("Read Uncommitted");
                isoLevelchoiceBox.setLayoutX(585);
                isoLevelchoiceBox.setLayoutY(320);

                Label ex0 = new Label("请选择数据库隔离级别");
                ex0.setLayoutX(440);
                ex0.setLayoutY(325);
                ex0.setFont(Font.font(null, FontWeight.BOLD, 12));

                Button checkB = new Button("运行");
                checkB.setTextFill(Color.CADETBLUE);
                checkB.setFont(Font.font(null, FontWeight.BOLD, 15));
                checkB.setLayoutX(565);
                checkB.setLayoutY(475);

                //测试事件
                checkB.setOnAction(event -> {
                    String isolevel = (String) isoLevelchoiceBox.getValue();
                    String sessnum = parameter1.getText().trim();
                    String trnum = parameter2.getText().trim();
                    String opnum = parameter3.getText().trim();

                    Boolean Serializable;
                    String PrinfContent = "";
                    String Details = "";
                    String[] sesstrace;
                    List<ArrayList<Integer>> graph;
                    String[][] dependencyType;
                    Map depMap = new HashMap();
                    String minCircle;
                    try {
                        String trace = MultiThreadTr.RunAndGetTrace(Integer.parseInt(sessnum), Integer.valueOf(trnum), Integer.valueOf(opnum), isolevel);
                        getTrSeqMap gtm = new getTrSeqMap(trace, Integer.parseInt(sessnum), Integer.valueOf(trnum));
                        sesstrace = gtm.sessionTrace();
                        analysis a = new analysis(Integer.valueOf(sessnum) * Integer.valueOf(trnum));
                        a.check(trace);
                        Serializable = a.getSerializable();
                        PrinfContent = a.getResultPrinfln();
                        Details = a.getDetails();
                        graph = a.getGraph();
                        dependencyType = a.getDependencyType();
                        depMap = a.getDepMap();
                        minCircle = a.getMinCircle();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage1 = new Stage();
                    AnchorPane p1 = new AnchorPane();

                    //可序列化的情况
                    if(Serializable){
                        Text t1 = new Text("这批事务的执行是可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(300);
                        t1.setFill(Color.CADETBLUE);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));
                        p1.getChildren().addAll(t1);
                    }
                    //不可序列化的情况
                    else{
                        Text t1 = new Text("这批事务的执行是不可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(200);
                        t1.setFill(Color.RED);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));

                        Text t2 = new Text(PrinfContent);
                        t2.setLayoutX(135);
                        t2.setLayoutY(270);
                        t2.setFill(Color.BLACK);
                        t2.setFont(Font.font(null, FontWeight.BOLD, 15));

                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(590);
                        s1.setMax(800);
                        s1.setValue(0);
                        s1.setPrefWidth(800);
                        s1.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t1.setLayoutX(275 - new_val.doubleValue() * 3);
                            t2.setLayoutX(135 - new_val.doubleValue() * 3);
                        });

                        p1.getChildren().addAll(t1, t2, s1);
                    }

                    //返回数据库事务的依赖图
                    Button trSeqMap = new Button("点击获取依赖图");
                    trSeqMap.setLayoutX(340);
                    trSeqMap.setLayoutY(500);
                    trSeqMap.setFont(Font.font(null, FontWeight.BOLD, 15));
                    trSeqMap.setTextFill(Color.BLUE);
                    //按钮事件
                    Map finalDepMap = depMap;
                    trSeqMap.setOnAction((event3 -> {
                        Stage stage3 = new Stage();
                        AnchorPane p3 = new AnchorPane();

                        Label l0 = new Label(String.valueOf("Session"));
                        l0.setLayoutX(20);
                        l0.setLayoutY(5);
                        l0.setFont(Font.font(null, FontWeight.BOLD, 15));
                        p3.getChildren().addAll(l0);
                        int sessionNum = Integer.valueOf(sessnum);
                        for (int i = 0; i < sessionNum; i++) {
                            Label l1 = new Label(String.valueOf(i));
                            l1.setLayoutX(20);
                            l1.setLayoutY(70 + 100 * i);
                            l1.setFont(Font.font(null, FontWeight.BOLD, 15));
                            p3.getChildren().addAll(l1);
                        }

                        //建立节点
                        Map nodeIdx = new HashMap<>();
                        for(int i = 0; i < sesstrace.length; i++){
                            if(sesstrace[i] != null) {
                                String[] sessop = sesstrace[i].split("->");
                                for (int j = 0; j < sessop.length; j++) {
                                    double idx_x = 0, idx_y = 0;
                                    if (j == 0) {
                                        idx_x = 20 + 50;
                                        idx_y = 70 + 100 * i;
                                    } else {
                                        idx_x = (Double.parseDouble(String.valueOf(nodeIdx.get(sessop[j - 1])).split("-")[0])
                                                + sessop[j - 1].length() * 9.5 + 50);
                                        idx_y = 70 + 100 * i;
                                    }
                                    nodeIdx.put(sessop[j], idx_x + "-" + idx_y);//记录每个操作的坐标
                                    Label l2 = new Label(sessop[j]);
                                    l2.setLayoutX(idx_x);
                                    l2.setLayoutY(idx_y);
                                    l2.setFont(Font.font(null, FontWeight.BOLD, 15));
                                    l2.setStyle("-fx-border-color: blue");
                                    p3.getChildren().addAll(l2);
                                }
                            }
                        }
                        //建立依赖
                        Set circle = new HashSet<>();//存循环节点
                        if(!Serializable)
                        {
                            String[] circlenode = minCircle.split(" ");
                            for(int i = 0; i < circlenode.length; i++){
                                circle.add(circlenode[i]);
                            }
                        }
                        for(int i = 0; i < graph.size(); i++){
                            int tr1 = i;
                            for(int j = 0; j < graph.get(i).size(); j++){
                                int tr2 = graph.get(i).get(j);
                                String deptype = dependencyType[tr1][tr2];
                                String op = (String) finalDepMap.get(tr1 + "->" + tr2);
                                String op1 = op.split("->")[0];
                                String op2 = op.split("->")[1];
                                double idx_x1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[0]);
                                double idx_y1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[1]);
                                double idx_x2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[0]);
                                double idx_y2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[1]);
                                Line line = new Line(idx_x1, idx_y1, idx_x2, idx_y2 + 20);
                                if(!Serializable && circle.contains(String.valueOf(tr1)) && circle.contains(String.valueOf(tr2))){
                                    line.setStroke(Color.BLUE);
                                    line.setStrokeWidth(3);
                                }
                                Circle node = new Circle();
                                node.setCenterX(idx_x1);
                                node.setCenterY(idx_y1);
                                node.setRadius(3);
                                node.setFill(Color.GREEN);
                                Circle node2 = new Circle();
                                node2.setCenterX(idx_x2);
                                node2.setCenterY(idx_y2 + 20);
                                node2.setRadius(3);
                                node2.setFill(Color.RED);
                                Label dt = new Label(dependencyType[tr1][tr2]);
                                dt.setLayoutX((idx_x1 + idx_x2) / 2.0);
                                dt.setLayoutY((idx_y1 + idx_y2 + 20) / 2.0);
                                dt.setTextFill(Color.GRAY);
                                p3.getChildren().addAll(line, node, node2, dt);
                            }
                        }


                        //横向滚动条
                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(985);
                        s1.setMax(1000000);
                        s1.setValue(0);
                        s1.setPrefWidth(1000000);
                        s1.setMin(0);
                        //纵向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setOrientation(Orientation.VERTICAL);
                        s2.setLayoutX(1785);
                        s2.setMax(1000000);
                        s2.setValue(0);
                        s2.setPrefHeight(1000000);
                        s2.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutX(-new_val.doubleValue() * 0.999);
                            s2.setLayoutX(+new_val.doubleValue() * 0.999);
                        });
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutY(-new_val.doubleValue() * 0.999);
                            s1.setLayoutY(+new_val.doubleValue() * 0.999);
                        });
                        p3.getChildren().addAll(s1, s2);

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage3.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage3.setTitle("事务依赖关系图");
                        Scene scene3 = new Scene(p3, 1800, 1000);
                        stage3.setScene(scene3);
                        stage3.setResizable(false);
                        stage3.show();
                    }));

                    //返回具体测试细节
                    Button ForDetails = new Button("点击获取测试详情");
                    ForDetails.setLayoutX(332);
                    ForDetails.setLayoutY(540);
                    ForDetails.setFont(Font.font(null, FontWeight.BOLD, 15));
                    ForDetails.setTextFill(Color.BLUE);
                    String finalDetails = Details;
                    ForDetails.setOnAction((event2 -> {
                        Stage stage2 = new Stage();
                        AnchorPane p2 = new AnchorPane();

                        //编辑具体测试细节，包括完整的Trace，排完序的操作集，事务间所有依赖关系，所有极大连通图的结果
                        Text t3 = new Text(finalDetails);
                        t3.setLayoutY(15);
                        t3.setFill(Color.BLACK);
                        t3.setFont(Font.font(null, FontWeight.BOLD, 12));

                        //横向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setLayoutY(1290);
                        s2.setMax(1800);
                        s2.setValue(0);
                        s2.setPrefWidth(1800);
                        s2.setMin(0);
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutX(-(new_val.doubleValue()) * 20);
                        });

                        //纵向滚动条
                        ScrollBar s3 = new ScrollBar();
                        s3.setOrientation(Orientation.VERTICAL);
                        s3.setLayoutX(1785);
                        s3.setMax(1300);
                        s3.setValue(0);
                        s3.setPrefHeight(1300);
                        s3.setMin(0);
                        s3.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutY(-(new_val.doubleValue()) * 30);
                            if(t3.getLayoutY() == 0){
                                t3.setLayoutY(15);
                            }
                        });

                        p2.getChildren().addAll(t3, s2, s3);
                        //Over

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage2.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage2.setTitle("详情信息");
                        Scene scene2 = new Scene(p2, 1800, 1300);
                        stage2.setScene(scene2);
                        stage2.setResizable(false);
                        stage2.show();
                    }));

                    Text headline1 = new Text("结果");
                    headline1.setLayoutX(365);
                    headline1.setLayoutY(100);
                    headline1.setCache(true);
                    headline1.setFill(Color.BLUE);
                    headline1.setFont(Font.font(null, FontWeight.BOLD, 25));
                    p1.getChildren().addAll(headline1, ForDetails, trSeqMap);
                    //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                    stage1.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                    stage1.setTitle("测试结果");
                    Scene scene1 = new Scene(p1, 800, 600);
                    stage1.setScene(scene1);
                    stage1.setResizable(false);
                    stage1.show();
                });

                //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                stage0.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                root.getChildren().addAll(link, headline, parameter1, checkB, isoLevelchoiceBox, parameter2, parameter3, ex0, ex1, ex2, ex3);

                //stage设置
                Scene scene = new Scene(root, 1200, 800);
                stage0.setTitle("数据库系统隔离异常检查器（MySQL）");
                stage0.setScene(scene);
                stage0.setResizable(false);
                stage0.show();
            }

            //以下为对Oracle的测试部分
            else if(dbname.equals("Oracle")){
                Stage stage0 = new Stage();
                AnchorPane root = new AnchorPane();
                //按钮组件，跳转至源代码
                Button link = new Button("点击获取源代码");
                link.setLayoutX(525);
                link.setLayoutY(710);
                link.setFont(Font.font(null, FontWeight.BOLD, 15));
                link.setTextFill(Color.BLUE);
                link.setOnAction((e -> {
                    getHostServices().showDocument("https://github.com/xxd-pwn/SerializableChecker");//源代码网址
                }));

                //标题label
                Text headline = new Text("数据库系统隔离异常检查器（Oracle）");
                headline.setLayoutX(380);
                headline.setLayoutY(100);
                headline.setCache(true);
                headline.setFill(Color.BLUE);
                headline.setFont(Font.font(null, FontWeight.BOLD, 25));
                Reflection r = new Reflection();
                r.setFraction(0.5);
                headline.setEffect(r);

                //background --图片地址需要绝对路径，在其他地方运行请修改路径
//                BackgroundImage myBI= new BackgroundImage(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\2.png",1200,800,false,true),
//                        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                        BackgroundSize.DEFAULT);
//                root.setBackground(new Background(myBI));

                //设置输入框
//                Text MarkedWords = new Text("请在下面输入数据库隔离级别、Session数、事务数目与每个事务的操作数，以英文符号‘-’隔开");
//                MarkedWords.setLayoutX(300);
//                MarkedWords.setLayoutY(300);
//                MarkedWords.setFill(Color.CADETBLUE);
//                MarkedWords.setFont(Font.font(null, FontWeight.BOLD, 18));
                TextField parameter1 = new TextField();
                parameter1.setLayoutX(585);
                parameter1.setLayoutY(350);
                parameter1.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex1 = new Label("请输入session数目");
                ex1.setLayoutX(440);
                ex1.setLayoutY(355);
                ex1.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter2 = new TextField();
                parameter2.setLayoutX(585);
                parameter2.setLayoutY(380);
                parameter2.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex2 = new Label("请输入session中事务数目");
                ex2.setLayoutX(440);
                ex2.setLayoutY(385);
                ex2.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter3 = new TextField();
                parameter3.setLayoutX(585);
                parameter3.setLayoutY(410);
                parameter3.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex3 = new Label("请输入事务中操作的数目");
                ex3.setLayoutX(440);
                ex3.setLayoutY(415);
                ex3.setFont(Font.font(null, FontWeight.BOLD, 12));

                ChoiceBox isoLevelchoiceBox = new ChoiceBox();
                isoLevelchoiceBox.getItems().add("Serializable");
//                isoLevelchoiceBox.getItems().add("Repeatable Read");
                isoLevelchoiceBox.getItems().add("Read Committed");
//                isoLevelchoiceBox.getItems().add("Read Uncommitted");
                isoLevelchoiceBox.setLayoutX(585);
                isoLevelchoiceBox.setLayoutY(320);

                Label ex0 = new Label("请选择数据库隔离级别");
                ex0.setLayoutX(440);
                ex0.setLayoutY(325);
                ex0.setFont(Font.font(null, FontWeight.BOLD, 12));

                Button checkB = new Button("运行");
                checkB.setTextFill(Color.CADETBLUE);
                checkB.setFont(Font.font(null, FontWeight.BOLD, 15));
                checkB.setLayoutX(565);
                checkB.setLayoutY(475);

                //测试事件
                checkB.setOnAction(event -> {
//            System.out.println(parameter.getText().trim());
                    String isolevel = (String) isoLevelchoiceBox.getValue();
                    String sessnum = parameter1.getText().trim();
                    String trnum = parameter2.getText().trim();
                    String opnum = parameter3.getText().trim();

                    Boolean Serializable;
                    String PrinfContent = "";
                    String Details = "";
                    String[] sesstrace;
                    List<ArrayList<Integer>> graph;
                    String[][] dependencyType;
                    Map depMap = new HashMap();
                    String minCircle;
                    try {
                        String trace = com.example.mychecker.Oracle.MultiThreadTr.RunAndGetTrace(Integer.parseInt(sessnum), Integer.valueOf(trnum), Integer.valueOf(opnum), isolevel);
                        com.example.mychecker.Oracle.getTrSeqMap gtm = new com.example.mychecker.Oracle.getTrSeqMap(trace, Integer.parseInt(sessnum), Integer.valueOf(trnum));
                        sesstrace = gtm.sessionTrace();
                        com.example.mychecker.Oracle.analysis a = new com.example.mychecker.Oracle.analysis(Integer.valueOf(sessnum) * Integer.valueOf(trnum));
                        a.check(trace);
                        Serializable = a.getSerializable();
                        PrinfContent = a.getResultPrinfln();
                        Details = a.getDetails();
                        graph = a.getGraph();
                        dependencyType = a.getDependencyType();
                        depMap = a.getDepMap();
                        minCircle = a.getMinCircle();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage1 = new Stage();
                    AnchorPane p1 = new AnchorPane();

                    //可序列化的情况
                    if(Serializable){
                        Text t1 = new Text("这批事务的执行是可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(300);
                        t1.setFill(Color.CADETBLUE);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));
                        p1.getChildren().addAll(t1);
                    }
                    //不可序列化的情况
                    else{
                        Text t1 = new Text("这批事务的执行是不可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(200);
                        t1.setFill(Color.RED);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));

                        Text t2 = new Text(PrinfContent);
                        t2.setLayoutX(135);
                        t2.setLayoutY(270);
                        t2.setFill(Color.BLACK);
                        t2.setFont(Font.font(null, FontWeight.BOLD, 15));

                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(590);
                        s1.setMax(800);
                        s1.setValue(0);
                        s1.setPrefWidth(800);
                        s1.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t1.setLayoutX(275 - new_val.doubleValue() * 3);
                            t2.setLayoutX(135 - new_val.doubleValue() * 3);
                        });

                        p1.getChildren().addAll(t1, t2, s1);
                    }

                    //返回数据库事务的依赖图
                    Button trSeqMap = new Button("点击获取依赖图");
                    trSeqMap.setLayoutX(340);
                    trSeqMap.setLayoutY(500);
                    trSeqMap.setFont(Font.font(null, FontWeight.BOLD, 15));
                    trSeqMap.setTextFill(Color.BLUE);
                    //按钮事件
                    Map finalDepMap = depMap;
                    trSeqMap.setOnAction((event3 -> {
                        Stage stage3 = new Stage();
                        AnchorPane p3 = new AnchorPane();

                        Label l0 = new Label(String.valueOf("Session"));
                        l0.setLayoutX(20);
                        l0.setLayoutY(5);
                        l0.setFont(Font.font(null, FontWeight.BOLD, 15));
                        p3.getChildren().addAll(l0);
                        int sessionNum = Integer.valueOf(sessnum);
                        for (int i = 0; i < sessionNum; i++) {
                            Label l1 = new Label(String.valueOf(i));
                            l1.setLayoutX(20);
                            l1.setLayoutY(70 + 100 * i);
                            l1.setFont(Font.font(null, FontWeight.BOLD, 15));
                            p3.getChildren().addAll(l1);
                        }

                        //建立节点
                        Map nodeIdx = new HashMap<>();
                        for(int i = 0; i < sesstrace.length; i++){
                            if(sesstrace[i] != null) {
                                String[] sessop = sesstrace[i].split("->");
                                for (int j = 0; j < sessop.length; j++) {
                                    double idx_x = 0, idx_y = 0;
                                    if (j == 0) {
                                        idx_x = 20 + 50;
                                        idx_y = 70 + 100 * i;
                                    } else {
                                        idx_x = (Double.parseDouble(String.valueOf(nodeIdx.get(sessop[j - 1])).split("-")[0])
                                                + sessop[j - 1].length() * 9.5 + 50);
                                        idx_y = 70 + 100 * i;
                                    }
                                    nodeIdx.put(sessop[j], idx_x + "-" + idx_y);//记录每个操作的坐标
                                    Label l2 = new Label(sessop[j]);
                                    l2.setLayoutX(idx_x);
                                    l2.setLayoutY(idx_y);
                                    l2.setFont(Font.font(null, FontWeight.BOLD, 15));
                                    l2.setStyle("-fx-border-color: blue");
                                    p3.getChildren().addAll(l2);
                                }
                            }
                        }
                        //建立依赖
                        Set circle = new HashSet<>();//存循环节点
                        if(!Serializable)
                        {
                            String[] circlenode = minCircle.split(" ");
                            for(int i = 0; i < circlenode.length; i++){
                                circle.add(circlenode[i]);
                            }
                        }
                        for(int i = 0; i < graph.size(); i++){
                            int tr1 = i;
                            for(int j = 0; j < graph.get(i).size(); j++){
                                int tr2 = graph.get(i).get(j);
                                String deptype = dependencyType[tr1][tr2];
                                String op = (String) finalDepMap.get(tr1 + "->" + tr2);
                                String op1 = op.split("->")[0];
                                String op2 = op.split("->")[1];
                                double idx_x1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[0]);
                                double idx_y1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[1]);
                                double idx_x2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[0]);
                                double idx_y2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[1]);
                                Line line = new Line(idx_x1, idx_y1, idx_x2, idx_y2 + 20);
                                if(!Serializable && circle.contains(String.valueOf(tr1)) && circle.contains(String.valueOf(tr2))){
                                    line.setStroke(Color.BLUE);
                                    line.setStrokeWidth(3);
                                }
                                Circle node = new Circle();
                                node.setCenterX(idx_x1);
                                node.setCenterY(idx_y1);
                                node.setRadius(3);
                                node.setFill(Color.GREEN);
                                Circle node2 = new Circle();
                                node2.setCenterX(idx_x2);
                                node2.setCenterY(idx_y2 + 20);
                                node2.setRadius(3);
                                node2.setFill(Color.RED);
                                Label dt = new Label(dependencyType[tr1][tr2]);
                                dt.setLayoutX((idx_x1 + idx_x2) / 2.0);
                                dt.setLayoutY((idx_y1 + idx_y2 + 20) / 2.0);
                                dt.setTextFill(Color.GRAY);
                                p3.getChildren().addAll(line, node, node2, dt);
                            }
                        }


                        //横向滚动条
                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(985);
                        s1.setMax(1000000);
                        s1.setValue(0);
                        s1.setPrefWidth(1000000);
                        s1.setMin(0);
                        //纵向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setOrientation(Orientation.VERTICAL);
                        s2.setLayoutX(1785);
                        s2.setMax(1000000);
                        s2.setValue(0);
                        s2.setPrefHeight(1000000);
                        s2.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutX(-new_val.doubleValue() * 0.999);
                            s2.setLayoutX(+new_val.doubleValue() * 0.999);
                        });
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutY(-new_val.doubleValue() * 0.999);
                            s1.setLayoutY(+new_val.doubleValue() * 0.999);
                        });
                        p3.getChildren().addAll(s1, s2);

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage3.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage3.setTitle("事务依赖关系图");
                        Scene scene3 = new Scene(p3, 1800, 1000);
                        stage3.setScene(scene3);
                        stage3.setResizable(false);
                        stage3.show();
                    }));

                    //返回具体测试细节
                    Button ForDetails = new Button("点击获取测试详情");
                    ForDetails.setLayoutX(332);
                    ForDetails.setLayoutY(540);
                    ForDetails.setFont(Font.font(null, FontWeight.BOLD, 15));
                    ForDetails.setTextFill(Color.BLUE);
                    String finalDetails = Details;
                    ForDetails.setOnAction((event2 -> {
                        Stage stage2 = new Stage();
                        AnchorPane p2 = new AnchorPane();

                        //编辑具体测试细节，包括完整的Trace，排完序的操作集，事务间所有依赖关系，所有极大连通图的结果
                        Text t3 = new Text(finalDetails);
                        t3.setLayoutY(15);
                        t3.setFill(Color.BLACK);
                        t3.setFont(Font.font(null, FontWeight.BOLD, 12));

                        //横向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setLayoutY(1290);
                        s2.setMax(1800);
                        s2.setValue(0);
                        s2.setPrefWidth(1800);
                        s2.setMin(0);
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutX(-(new_val.doubleValue()) * 20);
                        });

                        //纵向滚动条
                        ScrollBar s3 = new ScrollBar();
                        s3.setOrientation(Orientation.VERTICAL);
                        s3.setLayoutX(1785);
                        s3.setMax(1300);
                        s3.setValue(0);
                        s3.setPrefHeight(1300);
                        s3.setMin(0);
                        s3.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutY(-(new_val.doubleValue()) * 30);
                            if(t3.getLayoutY() == 0){
                                t3.setLayoutY(15);
                            }
                        });

                        p2.getChildren().addAll(t3, s2, s3);
                        //Over

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage2.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage2.setTitle("详情信息");
                        Scene scene2 = new Scene(p2, 1800, 1300);
                        stage2.setScene(scene2);
                        stage2.setResizable(false);
                        stage2.show();
                    }));

                    Text headline1 = new Text("结果");
                    headline1.setLayoutX(365);
                    headline1.setLayoutY(100);
                    headline1.setCache(true);
                    headline1.setFill(Color.BLUE);
                    headline1.setFont(Font.font(null, FontWeight.BOLD, 25));
                    p1.getChildren().addAll(headline1, ForDetails, trSeqMap);
                    //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                    stage1.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                    stage1.setTitle("测试结果");
                    Scene scene1 = new Scene(p1, 800, 600);
                    stage1.setScene(scene1);
                    stage1.setResizable(false);
                    stage1.show();
                });

                //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                stage0.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                root.getChildren().addAll(link, headline, parameter1, checkB, isoLevelchoiceBox, parameter2, parameter3, ex0, ex1, ex2, ex3);

                //stage设置
                Scene scene = new Scene(root, 1200, 800);
                stage0.setTitle("数据库系统隔离异常检查器（Oracle）");
                stage0.setScene(scene);
                stage0.setResizable(false);
                stage0.show();
            }

            //以下为对Redis的测试部分
            else if(dbname.equals("Redis")){
                Stage stage0 = new Stage();
                AnchorPane root = new AnchorPane();
                //按钮组件，跳转至源代码
                Button link = new Button("点击获取源代码");
                link.setLayoutX(525);
                link.setLayoutY(710);
                link.setFont(Font.font(null, FontWeight.BOLD, 15));
                link.setTextFill(Color.BLUE);
                link.setOnAction((e -> {
                    getHostServices().showDocument("https://github.com/xxd-pwn/SerializableChecker");//源代码网址
                }));

                //标题label
                Text headline = new Text("数据库系统隔离异常检查器（Redis）");
                headline.setLayoutX(380);
                headline.setLayoutY(100);
                headline.setCache(true);
                headline.setFill(Color.BLUE);
                headline.setFont(Font.font(null, FontWeight.BOLD, 25));
                Reflection r = new Reflection();
                r.setFraction(0.5);
                headline.setEffect(r);

                //background --图片地址需要绝对路径，在其他地方运行请修改路径
//                BackgroundImage myBI= new BackgroundImage(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\2.png",1200,800,false,true),
//                        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                        BackgroundSize.DEFAULT);
//                root.setBackground(new Background(myBI));

                TextField parameter1 = new TextField();
                parameter1.setLayoutX(585);
                parameter1.setLayoutY(350);
                parameter1.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex1 = new Label("请输入session数目");
                ex1.setLayoutX(440);
                ex1.setLayoutY(355);
                ex1.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter2 = new TextField();
                parameter2.setLayoutX(585);
                parameter2.setLayoutY(380);
                parameter2.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex2 = new Label("请输入session中事务数目");
                ex2.setLayoutX(440);
                ex2.setLayoutY(385);
                ex2.setFont(Font.font(null, FontWeight.BOLD, 12));

                TextField parameter3 = new TextField();
                parameter3.setLayoutX(585);
                parameter3.setLayoutY(410);
                parameter3.setFont(Font.font(null, FontWeight.BOLD, 12));

                Label ex3 = new Label("请输入事务中操作的数目");
                ex3.setLayoutX(440);
                ex3.setLayoutY(415);
                ex3.setFont(Font.font(null, FontWeight.BOLD, 12));

                Button checkB = new Button("运行");
                checkB.setTextFill(Color.CADETBLUE);
                checkB.setFont(Font.font(null, FontWeight.BOLD, 15));
                checkB.setLayoutX(565);
                checkB.setLayoutY(475);

                //测试事件
                checkB.setOnAction(event -> {
//            System.out.println(parameter.getText().trim());
//            String isolevel = (String) isoLevelchoiceBox.getValue();
                    String sessnum = parameter1.getText().trim();
                    String trnum = parameter2.getText().trim();
                    String opnum = parameter3.getText().trim();

                    Boolean Serializable;
                    String PrinfContent = "";
                    String Details = "";
                    String[] sesstrace;
                    List<ArrayList<Integer>> graph;
                    String[][] dependencyType;
                    Map depMap = new HashMap();
                    String minCircle;
                    try {
                        String trace = com.example.mychecker.Redis.MultiThreadTr.RunAndGetTrace(Integer.parseInt(sessnum), Integer.valueOf(trnum), Integer.valueOf(opnum));
                        com.example.mychecker.Redis.getTrSeqMap gtm = new com.example.mychecker.Redis.getTrSeqMap(trace, Integer.parseInt(sessnum), Integer.valueOf(trnum));
                        sesstrace = gtm.sessionTrace();
                        com.example.mychecker.Redis.analysis a = new com.example.mychecker.Redis.analysis(Integer.valueOf(sessnum) * Integer.valueOf(trnum));
                        a.check(trace);
                        Serializable = a.getSerializable();
                        PrinfContent = a.getResultPrinfln();
                        Details = a.getDetails();
                        graph = a.getGraph();
                        dependencyType = a.getDependencyType();
                        depMap = a.getDepMap();
                        minCircle = a.getMinCircle();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage1 = new Stage();
                    AnchorPane p1 = new AnchorPane();

                    //可序列化的情况
                    if(Serializable){
                        Text t1 = new Text("这批事务的执行是可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(300);
                        t1.setFill(Color.CADETBLUE);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));
                        p1.getChildren().addAll(t1);
                    }
                    //不可序列化的情况
                    else{
                        Text t1 = new Text("这批事务的执行是不可序列化的!");
                        t1.setLayoutX(275);
                        t1.setLayoutY(200);
                        t1.setFill(Color.RED);
                        t1.setFont(Font.font(null, FontWeight.BOLD, 18));

                        Text t2 = new Text(PrinfContent);
                        t2.setLayoutX(135);
                        t2.setLayoutY(270);
                        t2.setFill(Color.BLACK);
                        t2.setFont(Font.font(null, FontWeight.BOLD, 15));

                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(590);
                        s1.setMax(800);
                        s1.setValue(0);
                        s1.setPrefWidth(800);
                        s1.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t1.setLayoutX(275 - new_val.doubleValue() * 3);
                            t2.setLayoutX(135 - new_val.doubleValue() * 3);
                        });

                        p1.getChildren().addAll(t1, t2, s1);
                    }

                    //返回数据库事务的依赖图
                    Button trSeqMap = new Button("点击获取依赖图");
                    trSeqMap.setLayoutX(340);
                    trSeqMap.setLayoutY(500);
                    trSeqMap.setFont(Font.font(null, FontWeight.BOLD, 15));
                    trSeqMap.setTextFill(Color.BLUE);
                    //按钮事件
                    Map finalDepMap = depMap;
                    trSeqMap.setOnAction((event3 -> {
                        Stage stage3 = new Stage();
                        AnchorPane p3 = new AnchorPane();

                        Label l0 = new Label(String.valueOf("Session"));
                        l0.setLayoutX(20);
                        l0.setLayoutY(5);
                        l0.setFont(Font.font(null, FontWeight.BOLD, 15));
                        p3.getChildren().addAll(l0);
                        int sessionNum = Integer.valueOf(sessnum);
                        for (int i = 0; i < sessionNum; i++) {
                            Label l1 = new Label(String.valueOf(i));
                            l1.setLayoutX(20);
                            l1.setLayoutY(70 + 100 * i);
                            l1.setFont(Font.font(null, FontWeight.BOLD, 15));
                            p3.getChildren().addAll(l1);
                        }

                        //建立节点
                        Map nodeIdx = new HashMap<>();
                        for(int i = 0; i < sesstrace.length; i++){
                            if(sesstrace[i] != null) {
                                String[] sessop = sesstrace[i].split("->");
                                for (int j = 0; j < sessop.length; j++) {
                                    double idx_x = 0, idx_y = 0;
                                    if (j == 0) {
                                        idx_x = 20 + 50;
                                        idx_y = 70 + 100 * i;
                                    } else {
                                        idx_x = (Double.parseDouble(String.valueOf(nodeIdx.get(sessop[j - 1])).split("-")[0])
                                                + sessop[j - 1].length() * 9.5 + 50);
                                        idx_y = 70 + 100 * i;
                                    }
                                    nodeIdx.put(sessop[j], idx_x + "-" + idx_y);//记录每个操作的坐标
                                    Label l2 = new Label(sessop[j]);
                                    l2.setLayoutX(idx_x);
                                    l2.setLayoutY(idx_y);
                                    l2.setFont(Font.font(null, FontWeight.BOLD, 15));
                                    l2.setStyle("-fx-border-color: blue");
                                    p3.getChildren().addAll(l2);
                                }
                            }
                        }
                        //建立依赖
                        Set circle = new HashSet<>();//存循环节点
                        if(!Serializable)
                        {
                            String[] circlenode = minCircle.split(" ");
                            for(int i = 0; i < circlenode.length; i++){
                                circle.add(circlenode[i]);
                            }
                        }
                        for(int i = 0; i < graph.size(); i++){
                            int tr1 = i;
                            for(int j = 0; j < graph.get(i).size(); j++){
                                int tr2 = graph.get(i).get(j);
                                String deptype = dependencyType[tr1][tr2];
                                String op = (String) finalDepMap.get(tr1 + "->" + tr2);
                                String op1 = op.split("->")[0];
                                String op2 = op.split("->")[1];
                                double idx_x1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[0]);
                                double idx_y1 = Double.parseDouble(String.valueOf(nodeIdx.get(op1)).split("-")[1]);
                                double idx_x2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[0]);
                                double idx_y2 = Double.parseDouble(String.valueOf(nodeIdx.get(op2)).split("-")[1]);
                                Line line = new Line(idx_x1, idx_y1, idx_x2, idx_y2 + 20);
                                if(!Serializable && circle.contains(String.valueOf(tr1)) && circle.contains(String.valueOf(tr2))){
                                    line.setStroke(Color.BLUE);
                                    line.setStrokeWidth(3);
                                }
                                Circle node = new Circle();
                                node.setCenterX(idx_x1);
                                node.setCenterY(idx_y1);
                                node.setRadius(3);
                                node.setFill(Color.GREEN);
                                Circle node2 = new Circle();
                                node2.setCenterX(idx_x2);
                                node2.setCenterY(idx_y2 + 20);
                                node2.setRadius(3);
                                node2.setFill(Color.RED);
                                Label dt = new Label(dependencyType[tr1][tr2]);
                                dt.setLayoutX((idx_x1 + idx_x2) / 2.0);
                                dt.setLayoutY((idx_y1 + idx_y2 + 20) / 2.0);
                                dt.setTextFill(Color.GRAY);
                                p3.getChildren().addAll(line, node, node2, dt);
                            }
                        }


                        //横向滚动条
                        ScrollBar s1 = new ScrollBar();
                        s1.setLayoutY(985);
                        s1.setMax(1000000);
                        s1.setValue(0);
                        s1.setPrefWidth(1000000);
                        s1.setMin(0);
                        //纵向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setOrientation(Orientation.VERTICAL);
                        s2.setLayoutX(1785);
                        s2.setMax(1000000);
                        s2.setValue(0);
                        s2.setPrefHeight(1000000);
                        s2.setMin(0);
                        s1.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutX(-new_val.doubleValue() * 0.999);
                            s2.setLayoutX(+new_val.doubleValue() * 0.999);
                        });
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            p3.setLayoutY(-new_val.doubleValue() * 0.999);
                            s1.setLayoutY(+new_val.doubleValue() * 0.999);
                        });
                        p3.getChildren().addAll(s1, s2);

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage3.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage3.setTitle("事务依赖关系图");
                        Scene scene3 = new Scene(p3, 1800, 1000);
                        stage3.setScene(scene3);
                        stage3.setResizable(false);
                        stage3.show();
                    }));

                    //返回具体测试细节
                    Button ForDetails = new Button("点击获取测试详情");
                    ForDetails.setLayoutX(332);
                    ForDetails.setLayoutY(540);
                    ForDetails.setFont(Font.font(null, FontWeight.BOLD, 15));
                    ForDetails.setTextFill(Color.BLUE);
                    String finalDetails = Details;
                    ForDetails.setOnAction((event2 -> {
                        Stage stage2 = new Stage();
                        AnchorPane p2 = new AnchorPane();

                        //编辑具体测试细节，包括完整的Trace，排完序的操作集，事务间所有依赖关系，所有极大连通图的结果
                        Text t3 = new Text(finalDetails);
                        t3.setLayoutY(15);
                        t3.setFill(Color.BLACK);
                        t3.setFont(Font.font(null, FontWeight.BOLD, 12));

                        //横向滚动条
                        ScrollBar s2 = new ScrollBar();
                        s2.setLayoutY(1290);
                        s2.setMax(1800);
                        s2.setValue(0);
                        s2.setPrefWidth(1800);
                        s2.setMin(0);
                        s2.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutX(-(new_val.doubleValue()) * 20);
                        });

                        //纵向滚动条
                        ScrollBar s3 = new ScrollBar();
                        s3.setOrientation(Orientation.VERTICAL);
                        s3.setLayoutX(1785);
                        s3.setMax(1300);
                        s3.setValue(0);
                        s3.setPrefHeight(1300);
                        s3.setMin(0);
                        s3.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            t3.setLayoutY(-(new_val.doubleValue()) * 30);
                            if(t3.getLayoutY() == 0){
                                t3.setLayoutY(15);
                            }
                        });

                        p2.getChildren().addAll(t3, s2, s3);
                        //Over

                        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                        stage2.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                        stage2.setTitle("详情信息");
                        Scene scene2 = new Scene(p2, 1800, 1300);
                        stage2.setScene(scene2);
                        stage2.setResizable(false);
                        stage2.show();
                    }));

                    Text headline1 = new Text("结果");
                    headline1.setLayoutX(365);
                    headline1.setLayoutY(100);
                    headline1.setCache(true);
                    headline1.setFill(Color.BLUE);
                    headline1.setFont(Font.font(null, FontWeight.BOLD, 25));
                    p1.getChildren().addAll(headline1, ForDetails, trSeqMap);
                    //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                    stage1.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                    stage1.setTitle("测试结果");
                    Scene scene1 = new Scene(p1, 800, 600);
                    stage1.setScene(scene1);
                    stage1.setResizable(false);
                    stage1.show();
                });

                //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//                stage0.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
                root.getChildren().addAll(link, headline, parameter1, checkB, parameter2, parameter3, ex1, ex2, ex3);

                //stage设置
                Scene scene = new Scene(root, 1200, 800);
                stage0.setTitle("数据库系统隔离异常检查器（Redis）");
                stage0.setScene(scene);
                stage0.setResizable(false);
                stage0.show();
            }

        });

        //background --图片地址需要绝对路径，在其他地方运行请修改路径
//        BackgroundImage myBI0= new BackgroundImage(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\2.png",1200,800,false,true),
//                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                BackgroundSize.DEFAULT);
//        root0.setBackground(new Background(myBI0));

        //设置图标 --图片地址需要绝对路径，在其他地方运行请修改路径
//        stage.getIcons().add(new Image("F:\\GraduationProject\\MyChecker\\src\\main\\resources\\img\\icon.png"));
        //stage设置
        Scene scene0 = new Scene(root0, 1200, 800);
        stage.setTitle("数据库系统隔离异常检查器");
        stage.setScene(scene0);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}