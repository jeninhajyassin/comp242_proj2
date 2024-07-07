package com.example.jenproj2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloApplication extends Application {

    private static final List<String> START_TAGS = Arrays.asList("<242>", "<equations>", "<files>", "<equation>", "<file>");
    private static final List<String> END_TAGS = Arrays.asList("</242>", "</equations>", "</files>", "</equation>", "</file>");

    private final Stack<String> filesHistory = new Stack<>();
    private String currentFile;


    @Override
    public void start(Stage stage) {

        BorderPane mainPagePane = new BorderPane();
        Scene mainPage = new Scene(mainPagePane, 900, 600);
        mainPage.getStylesheets().add("style.css");
        mainPagePane.setPadding(new Insets(10, 10, 10, 10));


        Button back = new Button("Back");
        Button loadButton = new Button("Load");

        Label fileName = new Label("File Name");
        fileName.setAlignment(Pos.CENTER);

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(10);
        topBar.getChildren().addAll(back,fileName, loadButton);
        mainPagePane.setTop(topBar);


        VBox elements = new VBox();
        elements.setSpacing(10);
        elements.setPadding(new Insets(10, 10, 10, 10));

        Label equation = new Label("Equation");
        equation.setAlignment(Pos.CENTER);
        TextArea equations = new TextArea();

        Label filesList = new Label("Files List");
        filesList.setAlignment(Pos.CENTER);

        //clickable list of files
        ListView<String> files = new ListView<>();
        files.setPrefHeight(200);
        files.setMinHeight(200);
        files.setMaxHeight(200);





        elements.getChildren().addAll(equation, equations, filesList, files);

        mainPagePane.setCenter(elements);

        try {

            loadButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    loadFile(file, files, equations, fileName);
                }

            });

            back.setOnAction(e -> {
                if (!filesHistory.isEmpty()) {
                    String file = filesHistory.pop();
                    currentFile = null;
                    loadFile(new File(file), files, equations, fileName);
                }
            });

            files.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selectedFile = files.getSelectionModel().getSelectedItem();
                    if (selectedFile != null) {
                        File file = new File(selectedFile);
                        if (isValid(file)) {
                            loadFile(file, files, equations, fileName);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Invalid file");
                            alert.setContentText("The file you selected is not a valid file");
                            alert.showAndWait();
                        }
                    }
                }
            });

        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("an error occurred");
            alert.setContentText("an error occurred while loading the file");
            alert.showAndWait();
        }




        stage.setTitle("Project 2");
        stage.setScene(mainPage);
        stage.show();
    }


    // method to load the file contents and add current file to the stack
    public void loadFile(File file, ListView<String> files, TextArea equations,Label fileName){
        if (isValid(file)) {

            fileName.setText(file.getName());
            if (currentFile != null) {
                filesHistory.push(currentFile);
            }

            currentFile = file.getAbsolutePath();
            files.getItems().clear();
            equations.clear();
            loadAndEvaluateEquations(file, equations);
            loadFiles(file, files);

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid File");
            alert.setContentText("The file you selected is not a valid 242 file");
            alert.showAndWait();
        }

    }



    //=============================== methods for validating file ===============================
    private boolean isValid(File file) {
        try {
            //get all file in a string
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String fileString = stringBuilder.toString();

            String[] tags = getTags(fileString);
            if (tags.length == 0 || !tags[0].equals("<242>")) {
                return false;
            }
            //check if the tags are valid
            return isBalanced(tags);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static String[] getTags(String file) {
        //regular expression to match tags.
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(file);

        // Find all the tags in the file.
        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags.toArray(new String[0]);
    }
    public boolean isBalanced(String[] tags) {
        Stack<String> stack = new Stack<>();
        for (String tag : tags) {
            if (isStartTag(tag)) {
                stack.push(tag);
            } else if (isEndTag(tag)) {
                if (stack.isEmpty()) {
                    return false;
                }
                String startTag = stack.pop();
                if (!isMatchingPair(startTag, tag)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
    private boolean isMatchingPair(String startTag, String tag) {
        return startTag.equalsIgnoreCase(tag.replace("/", ""));
    }
    private static boolean isStartTag(String tag) {
        return START_TAGS.contains(tag);
    }
    private static boolean isEndTag(String tag) {
        return END_TAGS.contains(tag);
    }
    //===========================================================================================


    //=============================== methods for equations ============================
    private void loadAndEvaluateEquations(File file, TextArea equations) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<equation>")) {
                    String equation = line.substring(line.indexOf("<equation>") + 10, line.indexOf("</equation>")).trim();
                    String postfix = toPostFix(equation);
                    if (postfix.charAt(0) == 'i' || postfix.charAt(0) == 'U') {
                        equations.appendText(equation + " => " + postfix + "\n");
                    } else {
                        equations.appendText(equation + " => " + postfix + " => "+ evaluate(postfix) + "\n");
                    }
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid File");
            alert.setContentText("The file you selected is not a valid 242 file");
            alert.showAndWait();
        }
    }
    private String toPostFix(String equation) {

        if (equation == null || equation.isEmpty()) {
            return "invalid equation";
        }
        try {
            if (!validEquation(equation)) {
                return "invalid equation";
            }
        }catch (Exception e){
            return "Unbalanced Parenthesis";
        }

        String[] tokens = equation.split("\\s+");

        Stack<String> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        for (String s : tokens) {
            if (isNumber(s)) {
                postfix.append(" ").append(s).append(" ");
            } else if (s.equals("(")) {
                stack.push(s);
            } else if (s.equals(")")) {
                while (!stack.isEmpty() && !Objects.equals(stack.peek(), "(")) {
                    postfix.append(" ").append(stack.pop()).append(" ");
                }
                if (!stack.isEmpty() && !Objects.equals(stack.peek(), "(")) {
                    return "invalid equation";
                } else {
                    stack.pop();
                }
            } else {
                while (!stack.isEmpty() && precedence(s) <= precedence(stack.peek())) {
                    postfix.append(" ").append(stack.pop()).append(" ");
                }
                stack.push(s);
            }
        }
        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(" ");
        }
        return postfix.toString();
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int precedence(String c) {
        return switch (c) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }
    public boolean validEquation(String equation) throws Exception {
        equation = equation.replaceAll("\\s+", "");
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < equation.length(); i++) {
            char c = equation.charAt(i);
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    throw new Exception("Unbalanced parenthesis");
                }
                stack.pop();
            } else if (isOperator(c)) {
                if (i == 0 || i == equation.length() - 1) {
                    // Operators cannot appear at the start or end of the equation.
                    return false;
                }
                char prevChar = equation.charAt(i - 1);
                char nextChar = equation.charAt(i + 1);
                if (isOperator(prevChar) || isOperator(nextChar)) {
                    // Operators cannot be adjacent to other operators.
                    return false;
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new Exception("Unbalanced parenthesis");
        }

        return stack.isEmpty();
    }
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    public double evaluate(String equation) {
        Stack<Double> stack = new Stack<>();

        String[] tokens = equation.split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double a = stack.pop();
                double b = stack.pop();
                switch (token) {
                    case "+" -> stack.push(b + a);
                    case "-" -> stack.push(b - a);
                    case "*" -> stack.push(b * a);
                    case "/" -> stack.push(b / a);
                }
            }
        }
        return stack.pop();
    }

    //===================================================================================


    //=============================== methods for files ================================
    private void loadFiles(File file, ListView<String> files ) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<file>")) {
                    String fileName = line.substring(line.indexOf("<file>") + 6, line.indexOf("</file>")).trim();
                    files.getItems().add(fileName);
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid File");
            alert.setContentText("The file you selected is not a valid 242 file");
            alert.showAndWait();
        }

    }




    public static void main(String[] args) {
        launch();
    }
}