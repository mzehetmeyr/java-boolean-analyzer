module com.github.mzehetmeyr.javabooleananalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.mzehetmeyr.javabooleananalyzer to javafx.fxml;
    exports com.github.mzehetmeyr.javabooleananalyzer;
}