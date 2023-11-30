package com.example.mushroom;

import com.itextpdf.layout.element.Cell;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

//import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.print.event.PrintJobAdapter;
import javax.swing.ImageIcon;

import javax.swing.JRootPane;


public class HelloApplication extends Application {
    Double bHeight = 0.0;
    String ff="";
    double cash = 0;
    double balance = 0;
    String Number = "";
    ArrayList<Items> items = new ArrayList<>();
    ObservableList<DaySales> daySales = FXCollections.observableArrayList();
    ObservableList<MonthSales> monthSales = FXCollections.observableArrayList();
    ObservableList<Items> SalesItems = FXCollections.observableArrayList();
    DataBaseConnection db = new DataBaseConnection();
    private void readItemsData() throws SQLException, ClassNotFoundException {

        try {
            Connection con = db.getConnection().connectDB();
            String sql = "SELECT * FROM items";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new Items(rs.getString(1),rs.getDouble(2)));

            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDaysData() throws SQLException, ClassNotFoundException {

        try {
            Connection con = db.getConnection().connectDB();
            String sql = "SELECT * FROM daySales";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                daySales.add(new DaySales(rs.getString(1),rs.getDouble(2),rs.getInt(3),rs.getDouble(4)));
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readMonthData() throws SQLException, ClassNotFoundException {

        try {
            Connection con = db.getConnection().connectDB();
            String sql = "SELECT * FROM MonthSales";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                monthSales.add(new MonthSales(rs.getDouble(1),rs.getString(2)));
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static double cm_to_pp(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }
    public PageFormat getPageFormat(PrinterJob pj) {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        double bodyHeight = bHeight;
        double headerHeight = 5.0;
        double footerHeight = 5.0;
        double width = cm_to_pp(8);
        double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
        paper.setSize(width, height);
        paper.setImageableArea(0, 10, width, height - cm_to_pp(1));

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        return pf;
    }
    private void AlertBox() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Input The Cash");
        textInput.getDialogPane().setContentText("The Cash :");
        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();
        if (input.getText() != null) {
            cash = Double.parseDouble(input.getText());
        }
    }
    public void printIn(){
        bHeight = Double.valueOf(SalesItems.size());

        // System.out.println(ItemsData.size());
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintable(), getPageFormat(pj));

     //   AlertBox();
        boolean dor = pj.printDialog();
        if (dor) {
            try {
                pj.print();

            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
       }
    }
    public class BillPrintable implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            Date currentDate = new Date();

            // Create a SimpleDateFormat object to format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

            // Format the current date and time as a string
            String formattedDate = dateFormat.format(currentDate);

            int r = SalesItems.size();
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/logon.png"));
           // icon.setImage(icon.getImage().getScaledInstance(1200, 1200, java.awt.Image.SCALE_DEFAULT));
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {

                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                try {
                    int y = 20;
                    int yShift = 10;
                    int headerRectHeight = 15;

                    int itemNameColumn = 10;
                    int quantityColumn = 85;
                    int priceColumn = 125;
                    int TotalpriceColumn = 150;

                    int tableX = itemNameColumn - 5; // Adjust the X-coordinate as needed
                    int tableY = y - yShift; // The Y-coordinate for the top of the table
                    int tableWidth = TotalpriceColumn + 50; // Adjust the width as needed
                    int tableHeight = y + yShift - tableY + 5; // Adjust the height as needed



                    JRootPane rootPane = new JRootPane();
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawImage(icon.getImage(), 50, 20, 90, 30, rootPane);
                    y += yShift + 30;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.drawString("        Mushroom Pizza        ", 12, y);
                    y += yShift;
                    g2d.drawString("   Birzeit -  Ramallah ", 12, y);
                    y += yShift;
                    g2d.drawString("   Address  Ramallah-Palestion ", 12, y);
                    y += yShift;
                    g2d.drawString("   https://www.facebook.com/pizzamushroom ", 12, y);
                    y += yShift;
                    g2d.drawString("     CONTACT  :   0595530092      ", 12, y);
                    y += yShift;
                    g2d.drawString("     CONTACT  :   0595530095      ", 12, y);
                    y += yShift;
                    g2d.drawString("   "+formattedDate+"       ", 12, y);
                    y += yShift;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    g2d.drawString("Name      Quantity   Price    Note   ", itemNameColumn, y);
                    y += yShift;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    for (int i = 0; i < SalesItems.size(); i++) {

                        double b = SalesItems.get(i).getPrice();
                        int v = SalesItems.get(i).getQuantity();
                      g2d.drawString(SalesItems.get(i).getName(), itemNameColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getQuantity()), quantityColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getPrice()), priceColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getNote()), TotalpriceColumn, y);
                        y += yShift;
                        g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                        y += yShift;


                    }
                    double sumAmout1 = 0;
                    for (Items o : SalesItems) {

                        sumAmout1 = o.getTotal() + sumAmout1;
                    }

                    balance = cash - sumAmout1;

                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                    g2d.drawString(" Total amount:     " + sumAmout1 + "   ", 10, y);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       THANK YOU COME AGAIN            ", 10, y);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("  SOFTWARE BY:Mohammad AbuThaher    ", 10, y);
                    y += yShift;
                    g2d.drawString("  CONTACT: mohammadmashhour24@gmail.com  ", 10, y);
                    y += yShift;

                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }

    }
    public class BillPrintableDikivery implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            Date currentDate = new Date();

            // Create a SimpleDateFormat object to format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

            // Format the current date and time as a string
            String formattedDate = dateFormat.format(currentDate);
            int r = SalesItems.size();
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/logon.png"));
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {

                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                try {
                    int y = 20;
                    int yShift = 10;
                    int headerRectHeight = 15;
                    int itemNameColumn = 10;
                    int quantityColumn = 85;
                    int priceColumn = 125;
                    int TotalpriceColumn = 150;

                    int tableX = itemNameColumn - 5; // Adjust the X-coordinate as needed
                    int tableY = y - yShift; // The Y-coordinate for the top of the table
                    int tableWidth = TotalpriceColumn + 50; // Adjust the width as needed
                    int tableHeight = y + yShift - tableY + 5; // Adjust the height as needed


                    JRootPane rootPane = new JRootPane();
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawImage(icon.getImage(), 50, 20, 90, 30, rootPane);
                    y += yShift + 30;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.drawString("         Mushroom Pizza       ", 12, y);
                    y += yShift;
                    g2d.drawString("   Birzeit - Ramallah ", 12, y);
                    y += yShift;
                    g2d.drawString("   Address  Ramallah-Palestion ", 12, y);
                    y += yShift;
                    g2d.drawString("             Delivery          ", 12, y);
                    y += yShift;
                    g2d.drawString("   "+formattedDate+"       ", 12, y);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));

                    g2d.drawString("         "+Number+"          ", 12, y);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    g2d.drawString("Name      Quantity   Price    Note   ", itemNameColumn, y);
                    y += yShift;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    for (int i = 0; i < SalesItems.size(); i++) {

                        double b = SalesItems.get(i).getPrice();
                        int v = SalesItems.get(i).getQuantity();
                        g2d.drawString(SalesItems.get(i).getName(), itemNameColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getQuantity()), quantityColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getPrice()), priceColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getNote()), TotalpriceColumn, y);
                        y += yShift;
                        g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                        y += yShift;


                    }
                    double sumAmout1 = 0;
                    for (Items o : SalesItems) {

                        sumAmout1 = o.getTotal() + sumAmout1;
                    }



                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                    g2d.drawString(" Total amount:     " + sumAmout1 + "   ", 10, y);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       THANK YOU COME AGAIN            ", 10, y);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("  SOFTWARE BY:Mohammad AbuThaher    ", 10, y);
                    y += yShift;
                    g2d.drawString("  CONTACT: mohammadmashhour24@gmail.com  ", 10, y);
                    y += yShift;
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }

    }
    private void AlertBoxDilivery() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Input The Number : ");
        textInput.getDialogPane().setContentText("The Number :");
        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();
        if (input.getText() != null) {
            Number =input.getText();
        }
    }
    public void printDilivery(){
        bHeight = Double.valueOf(SalesItems.size());

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintableDikivery(), getPageFormat(pj));

        AlertBoxDilivery();
        boolean dor = pj.printDialog();
        if (dor) {
            try {
                pj.print();

            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }

    }
    public class BillPrintableTakeAway implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            Date currentDate = new Date();

            // Create a SimpleDateFormat object to format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

            // Format the current date and time as a string
            String formattedDate = dateFormat.format(currentDate);
            int r = SalesItems.size();
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/logon.png"));
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {

                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                try {
                    int y = 20;
                    int yShift = 10;
                    int headerRectHeight = 15;
                    int itemNameColumn = 10;
                    int quantityColumn = 85;
                    int priceColumn = 125;
                    int TotalpriceColumn = 150;

                    int tableX = itemNameColumn - 5; // Adjust the X-coordinate as needed
                    int tableY = y - yShift; // The Y-coordinate for the top of the table
                    int tableWidth = TotalpriceColumn + 50; // Adjust the width as needed
                    int tableHeight = y + yShift - tableY + 5; // Adjust the height as needed


                    JRootPane rootPane = new JRootPane();
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawImage(icon.getImage(), 50, 20, 90, 30, rootPane);
                    y += yShift + 30;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.drawString("         Mushroom Pizaa       ", 12, y);
                    y += yShift;
                    g2d.drawString("   Address  Ramallah-Birzeit ", 12, y);
                    y += yShift;
                    g2d.drawString("   Address  Ramallah-Palestion ", 12, y);
                    y += yShift;
                    g2d.drawString("             Take Away       ", 12, y);
                    y += yShift;
                    g2d.drawString("   "+formattedDate+"       ", 12, y);
                    y += yShift;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    g2d.drawString("Name      Quantity   Price    Note   ", itemNameColumn, y);
                    y += yShift;
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += headerRectHeight;

                    for (int i = 0; i < SalesItems.size(); i++) {

                        double b = SalesItems.get(i).getPrice();
                        int v = SalesItems.get(i).getQuantity();
                        g2d.drawString(SalesItems.get(i).getName(), itemNameColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getQuantity()), quantityColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getPrice()), priceColumn, y);
                        g2d.drawString(String.valueOf(SalesItems.get(i).getNote()), TotalpriceColumn, y);
                        y += yShift;
                        g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                        y += yShift;


                    }
                    double sumAmout1 = 0;
                    for (Items o : SalesItems) {

                        sumAmout1 = o.getTotal() + sumAmout1;
                    }

                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                    g2d.drawString(" Total amount:     " + sumAmout1 + "   ", 10, y);
                    y += yShift;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2d.drawLine(tableX, y-5, tableX + tableWidth, y-5);
                    y += yShift;


                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       THANK YOU COME AGAIN            ", 10, y);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("  SOFTWARE BY:Mohammad AbuThaher    ", 10, y);
                    y += yShift;
                    g2d.drawString("  CONTACT: mohammadmashhour24@gmail.com  ", 10, y);
                    y += yShift;

                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }

    }
    public void printTakeAway(){
        bHeight = Double.valueOf(SalesItems.size());

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintableTakeAway(), getPageFormat(pj));

        boolean dor = pj.printDialog();
        if (dor) {
            try {
                pj.print();


            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }

    }
    static Cell getHeaderTextCell(String textValue) {

        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }
    static Cell getHeaderTextCellValue(String textValue) {

        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }
    static Cell getBillingandShippingCell(String textValue) {

        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
    }
    static Cell getCell10fLeft(String textValue, Boolean isBold) {
        Cell myCell = new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        return isBold ? myCell.setBold() : myCell;

    }
    Scene LoginPAGE , DaySalesPAGE ,MonthSalesPAGE , SalesPAGE ,MainPAGE , SiginUpPAGE;

    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        readItemsData();
        readDaysData();
        readMonthData();

        Pane LoginPane = new Pane();
        Image mh8 = new Image("loginBG.jpg");
        ImageView mah8 = new ImageView(mh8);
        mah8.setFitHeight(563);
        mah8.setFitWidth(900);

        TextField Text1 = new TextField();
        Text1.setPrefHeight(28);
        Text1.setPrefWidth(175);
        Text1.setLayoutX(573);
        Text1.setLayoutY(223);
        Text1.setPromptText("Enter a UserName");

        PasswordField pass = new PasswordField();
        pass.setPrefHeight(28);
        pass.setPrefWidth(175);
        pass.setLayoutX(573);
        pass.setLayoutY(281);
        pass.setPromptText("Enter a Password");

        Hyperlink signupLabel = new Hyperlink("Not Here? Sign Up");
        signupLabel.setLayoutX(600);
        signupLabel.setLayoutY(400);
        signupLabel.setFont(javafx.scene.text.Font.font("Barlow Condensed", 12));
        signupLabel.setTextFill(Color.WHITE);
        signupLabel.setOnAction(e -> {
            Text1.clear();
            pass.clear();
            stage.setScene(SiginUpPAGE);

        });

        Button But = new Button("Login", new ImageView("key.png"));
        But.setPrefHeight(34);
        But.setPrefWidth(162);
        But.setLayoutX(564);
        But.setLayoutY(357);
        But.setOnAction(e -> {
//        if(Text1.getText().equals("admin")&& pass.getText().equals("admin")){
//            stage.setScene(MainPAGE);
//        }
//        else{
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Wrong UserName or Password");
//            alert.setContentText("Please Enter a Valid UserName and Password");
//            alert.showAndWait();
//        }


            if(Text1.getText().isEmpty() || pass.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong UserName or Password");
            alert.setContentText("Please Fill All Fields");
            alert.showAndWait();
            return;
            }
            String user="";
            String passd="";
            int fg = 0;
            try {
                Connection con = db.getConnection().connectDB();
                String sql = "SELECT * FROM login WHERE username = '" + Text1.getText() + "' AND Password = '" + pass.getText() + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    fg = 1;
                    user = rs.getString("username");
                    passd = rs.getString("Password");
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if(fg==1 && user.equals(Text1.getText()) && passd.equals(pass.getText())){
                stage.setScene(MainPAGE);
                ff=Text1.getText();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wrong UserName or Password");
                alert.setContentText("Please Enter a Valid UserName and Password");
                alert.showAndWait();
                return;
            }


            Text1.clear();
            pass.clear();

        });


        LoginPane.getChildren().addAll(mah8, Text1, pass, But, signupLabel);

        LoginPAGE = new Scene(LoginPane, 900, 563);
        LoginPAGE.getStylesheets().add(getClass().getResource("login.css").toExternalForm());

//==================================== DaySalesPAGE ===============================================
        Pane DaySalesPane = new Pane();
        Image mh = new Image("mainPage.jpg");
        ImageView mah9 = new ImageView(mh);
        mah9.setFitHeight(600);
        mah9.setFitWidth(1150);


        TableView<DaySales> tableDays = new TableView<>();
        tableDays.setPrefHeight(315);
        tableDays.setPrefWidth(466);
        tableDays.setLayoutX(342);
        tableDays.setLayoutY(134);
        tableDays.setEditable(true);

        TableColumn<DaySales, String> NameItem = new TableColumn<>("Name");
        NameItem.setPrefWidth(116);

        NameItem.setMinWidth(10);
        NameItem.setResizable(false);
        NameItem.setCellValueFactory(new PropertyValueFactory<DaySales, String>("name"));


        TableColumn<DaySales, Double> Price = new TableColumn<>("Price");
        Price.setPrefWidth(116);
        Price.setMinWidth(10);
        Price.setResizable(false);
        Price.setCellValueFactory(new PropertyValueFactory<DaySales, Double>("price"));



        TableColumn<DaySales, Integer> Quantity = new TableColumn<>("Quantity");
        Quantity.setPrefWidth(116);
        Quantity.setMinWidth(10);
        Quantity.setResizable(false);
        Quantity.setCellValueFactory(new PropertyValueFactory<DaySales, Integer>("quantity"));

        TableColumn<DaySales, Double> Total = new TableColumn<>("Total");
        Total.setPrefWidth(116);
        Total.setMinWidth(10);
        Total.setResizable(false);
        Total.setCellValueFactory(new PropertyValueFactory<DaySales, Double>("total"));

        TextField TotalText = new TextField();
        TotalText.setLayoutX(965);
        TotalText.setLayoutY(539);

        tableDays.setItems(daySales);
        tableDays.getColumns().addAll(NameItem, Price, Quantity, Total);
        Button Back = new Button("Back");
        Back.setPrefHeight(43);
        Back.setPrefWidth(89);
        Back.setLayoutX(368);
        Back.setLayoutY(476);
        Back.setOnAction(e -> {
            stage.setScene(MainPAGE);
        });

        Button Delete = new Button("Delete");
        Delete.setPrefHeight(43);
        Delete.setPrefWidth(89);
        Delete.setLayoutX(477);
        Delete.setLayoutY(476);
        Delete.setOnAction(e -> {
            if (tableDays.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.NONE, "You Must Select a Row", ButtonType.OK);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }
                return;
            }

            DaySales selectedItem = tableDays.getSelectionModel().getSelectedItem();
            String name = selectedItem.getName();

            try {

                Connection con = db.getConnection().connectDB();
                String sql = "Delete from daysales WHERE name='" + name + "'";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                con.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }


            tableDays.getItems().remove(selectedItem);
            double sumPurchase = 0;
            for (DaySales daySale : daySales) {
                sumPurchase += daySale.getTotal();
            }
            TotalText.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));

        });
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        TextField test1 = new TextField();
        test1.setText(dtf.format(now));

        Button Sales = new Button("Sale");
        Sales.setPrefHeight(43);
        Sales.setPrefWidth(89);
        Sales.setLayoutX(586);
        Sales.setLayoutY(476);
        Sales.setOnAction(e->{
            if (daySales.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.NONE, "The Table is Empty", ButtonType.OK);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }
                return;
            }

            double sumPurchase = 0;
            for (DaySales daySale : daySales) {
                sumPurchase += daySale.getTotal();
            }
            monthSales.add(new MonthSales( sumPurchase,test1.getText()));
            try {

                Connection con = db.getConnection().connectDB();
                String sql = "INSERT INTO monthsales (total,Monthdate) VALUES ('" + sumPurchase + "','" + test1.getText() + "')";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                con.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {


            Calendar calendar = Calendar.getInstance();
            Date dateae = calendar.getTime();

            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dateae.getTime());
            LocalDate currentdate = LocalDate.now();

            String path = "C:\\mus\\" + currentdate + ".pdf";
            PdfWriter pdfWriter = new PdfWriter(path);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            float x = pdfDocument.getDefaultPageSize().getWidth() / 2;
            float y = pdfDocument.getDefaultPageSize().getHeight() / 2;

            Document document = new Document(pdfDocument);
            float threecol = 190f;
            float twocol = 285f;
            float twocol150 = 435f;
            float twoColumnWidth[] = { twocol150, twocol };
            float threeColumnWidth[] = { threecol, threecol, threecol};
            float fullWidth[] = { threecol * 3 };

            Paragraph onesp = new Paragraph("\n");
            Paragraph twosp = new Paragraph("\n\n");
            Table nestedtable = new Table(new float[] { twocol / 2, twocol / 2 });
            Table headerTable = new Table(twoColumnWidth);
            Border nb = new SolidBorder(com.itextpdf.kernel.color.Color.WHITE, 1, 0);

            headerTable.addCell(new Cell().add("Bills").setBold().setFontSize(20f).setBorder(nb)
                    .setTextAlignment(TextAlignment.LEFT).setMarginLeft(5));

            nestedtable.addCell(getHeaderTextCell("Day.:"));
            nestedtable.addCell(getHeaderTextCellValue(day));
            nestedtable.addCell(getHeaderTextCell("Bill Date:"));
            nestedtable.addCell(getHeaderTextCellValue("" + currentdate));

            headerTable.addCell(nestedtable.setBorder(nb)).setBorder(nb);
            document.add(headerTable);
            document.add(new Paragraph("\n"));
            Border gb = new SolidBorder(com.itextpdf.kernel.color.Color.GRAY, 2);
            Table tableDivider = new Table(fullWidth);
            document.add(tableDivider.setBorder(gb));
            document.add(onesp);
            Table twoColTable = new Table(twoColumnWidth);
            twoColTable.addCell(getBillingandShippingCell("Billing Information"));
            twoColTable.addCell(getBillingandShippingCell("Shipping Information"));
            document.add(twoColTable.setMarginBottom(12f));

            Table twoColTable2 = new Table(twoColumnWidth);
            twoColTable2.addCell(getCell10fLeft("Company", true));
            twoColTable2.addCell(getCell10fLeft("Name", true));
            twoColTable2.addCell(getCell10fLeft("Mushrrom", false));
            twoColTable2.addCell(getCell10fLeft(""+ff, false));
            document.add(twoColTable2);

            Table twoColTable3 = new Table(twoColumnWidth);
            twoColTable3.addCell(getCell10fLeft("Name", true));
            twoColTable3.addCell(getCell10fLeft("Address", true));
            twoColTable3.addCell(getCell10fLeft("" + ff, false));
            twoColTable3.addCell(getCell10fLeft("Ramallah-Birezeit", false));
            document.add(twoColTable3);
            float oneColoumnwidth[] = { twocol150 };
            Table oneColTable1 = new Table(oneColoumnwidth);
            oneColTable1.addCell(getCell10fLeft("Address", true));
            oneColTable1.addCell(getCell10fLeft("Ramallah-Birezeit", false));
            document.add(oneColTable1.setMarginBottom(10f));

            Table tableDivider2 = new Table(fullWidth);
            Border dgb = new DashedBorder(com.itextpdf.kernel.color.Color.GRAY, 0.5f);
            document.add(tableDivider2.setBorder(dgb));

            Paragraph producPara = new Paragraph("Products");

            document.add(producPara.setBold());
            Table threeColTable1 = new Table(threeColumnWidth);
            threeColTable1.setBackgroundColor(com.itextpdf.kernel.color.Color.BLACK, 0.7f);

            threeColTable1.addCell(new Cell().add("Description").setBold()
                    .setFontColor(com.itextpdf.kernel.color.Color.WHITE).setBorder(nb));
            threeColTable1.addCell(
                    new Cell().add("Quantity").setBold().setFontColor(com.itextpdf.kernel.color.Color.WHITE)
                            .setTextAlignment(TextAlignment.CENTER).setBorder(nb));
            threeColTable1
                    .addCell(new Cell().add("Price").setBold().setFontColor(com.itextpdf.kernel.color.Color.WHITE)
                            .setTextAlignment(TextAlignment.RIGHT).setBorder(nb))
                    .setMarginRight(15f);

                document.add(threeColTable1);
            Table threeColTable2 = new Table(threeColumnWidth);
            String FONT = "C:\\ARIALUNI.TTF";
            PdfFont F = PdfFontFactory.createFont( FONT, PdfEncodings.IDENTITY_H);


            LanguageProcessor al = new ArabicLigaturizer();

                float totalSum = 0;
                float totalProf = 0;
                for (int i = 0; i < daySales.size(); i++) {
                    double total = daySales.get(i).getQuantity() * daySales.get(i).getPrice();
                    totalSum += total;

                    // Split the item name into separate lines if it contains a new line character
                    String[] itemNameLines = daySales.get(i).getName().split("\n");

                    // Create a list of paragraphs for the item name
                    List<Paragraph> itemNameParagraphs = new ArrayList<>();
                    for (String line : itemNameLines) {
                        Paragraph itemNameParagraph = new Paragraph(al.process(line))
                                .setFont(F)
                                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
                                .setTextAlignment(TextAlignment.LEFT);
                        itemNameParagraphs.add(itemNameParagraph);
                    }

                    // Add the item name paragraphs to the table cell
                    for (int j = 0; j < itemNameParagraphs.size(); j++) {
                        threeColTable2.addCell(new Cell()
                                .add(itemNameParagraphs.get(j))
                                .setBorder(nb)
                                .setMarginLeft(10f));
                        // Only add quantity and total in the first line of the item name
                        if (j == 0) {
                            threeColTable2.addCell(new Cell()
                                    .add(String.valueOf(daySales.get(i).getQuantity()))
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setBorder(nb));
                            threeColTable2.addCell(new Cell()
                                    .add(String.valueOf(total))
                                    .setTextAlignment(TextAlignment.RIGHT)
                                    .setBorder(nb)
                                    .setMarginRight(15f));
                        } else {
                            // For subsequent lines, add empty cells
                            threeColTable2.addCell(new Cell().setBorder(nb));
                            threeColTable2.addCell(new Cell().setBorder(nb));
                        }
                    }
                }
            document.add(threeColTable2.setMarginBottom(20f));
            float onetwo[] = { threecol + 125f, threecol * 2 };
            Table threeColTable4 = new Table(onetwo);
            threeColTable4.addCell(new Cell().add("").setBorder(nb));
            threeColTable4.addCell(tableDivider2).setBorder(nb);
            document.add(threeColTable4);
            float threeColumnWidth23[] = { threecol, threecol, threecol, threecol, threecol, threecol };
            Table threeColTable3 = new Table(threeColumnWidth23);
            threeColTable3.addCell(new Cell().add("").setBorder(nb)).setMarginLeft(10f);
            threeColTable3.addCell(new Cell().add("Total").setTextAlignment(TextAlignment.CENTER).setBorder(nb));
            threeColTable3.addCell(
                            new Cell().add(String.valueOf(totalSum)).setTextAlignment(TextAlignment.RIGHT).setBorder(nb))
                    .setMarginRight(15f);
            threeColTable3.addCell(new Cell().add("").setBorder(nb)).setMarginLeft(10f);
            threeColTable3
                    .addCell(new Cell().add("Total Proft").setTextAlignment(TextAlignment.CENTER).setBorder(nb));
            threeColTable3.addCell(
                            new Cell().add(String.valueOf(totalProf)).setTextAlignment(TextAlignment.RIGHT).setBorder(nb))
                    .setMarginRight(12f);

            document.add(threeColTable3);
            document.add(tableDivider2);
            document.add(new Paragraph("\n"));
            document.add(tableDivider.setBorder(new SolidBorder(1)).setMarginBottom(15f));


            document.close();


            }catch (Exception e2){
                e2.printStackTrace();
            }
            try {
                // delete all rows from table daysales after sale with the database
                Connection con = db.getConnection().connectDB();
                String sql = "TRUNCATE TABLE daysales";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                con.close();


            }catch (Exception e2){
                e2.printStackTrace();

            }

            daySales.clear();


        });


        Label TotalLabel = new Label("Total: ");
        TotalLabel.setLayoutX(818);
        TotalLabel.setLayoutY(530);
        TotalLabel.setPrefHeight(43);
        TotalLabel.setPrefWidth(98);
        TotalLabel.setFont(javafx.scene.text.Font.font("Barlow Condensed", 19));




        DaySalesPane.getChildren().addAll(mah9, tableDays, Delete, Sales, Back, TotalLabel, TotalText);

        DaySalesPAGE = new Scene(DaySalesPane, 1150, 600);
        DaySalesPAGE.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

//==================================== MonthSalesPAGE ===============================================
        Pane MonthSalesPane = new Pane();
        Image mh1 = new Image("mainPage.jpg");
        ImageView mah10 = new ImageView(mh1);
        mah10.setFitHeight(600);
        mah10.setFitWidth(1150);

        TableView<MonthSales> tableMonth = new TableView<>();
        tableMonth.setPrefHeight(311);
        tableMonth.setPrefWidth(518);
        tableMonth.setLayoutX(288);
        tableMonth.setLayoutY(136);
        tableMonth.setEditable(true);



        TableColumn<MonthSales, Double> monthTotal = new TableColumn<>("Total");
        monthTotal.setPrefWidth(260);
        monthTotal.setMinWidth(10);
        monthTotal.setResizable(false);
        monthTotal.setCellValueFactory(new PropertyValueFactory<MonthSales, Double>("total"));
        // edit the cell to be able to edit it
        monthTotal.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        monthTotal.setOnEditCommit(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setTotal(e.getNewValue());
        });


        TableColumn<MonthSales, String> monthDate = new TableColumn<>("Date");
        monthDate.setPrefWidth(260);
        monthDate.setMinWidth(10);
        monthDate.setResizable(false);
        monthDate.setCellValueFactory(new PropertyValueFactory<MonthSales, String>("date"));


        tableMonth.setItems(monthSales);
        tableMonth.getColumns().addAll(monthTotal, monthDate);

        Button Back1 = new Button("Back");
        Back1.setPrefHeight(43);
        Back1.setPrefWidth(89);
        Back1.setLayoutX(477);
        Back1.setLayoutY(476);
        Back1.setOnAction(e -> {
            stage.setScene(MainPAGE);
        });

        TextField TotalText1 = new TextField();
        TotalText1.setLayoutX(965);
        TotalText1.setLayoutY(539);

        Button calculate = new Button("Calculate");
        calculate.setPrefHeight(43);
        calculate.setPrefWidth(89);
        calculate.setLayoutX(330);
        calculate.setLayoutY(476);
        calculate.setOnAction(e -> {
            double total = 0;
            for (MonthSales monthSales : monthSales) {
                total += monthSales.getTotal();
            }
            TotalText1.setText(NumberFormat.getCurrencyInstance().format(total));
        });

        Button Print = new Button("Print");
        Print.setPrefHeight(43);
        Print.setPrefWidth(89);
        Print.setLayoutX(691);
        Print.setLayoutY(476);

        Label TotalLabel1 = new Label("Total: ");
        TotalLabel1.setLayoutX(818);
        TotalLabel1.setLayoutY(530);
        TotalLabel1.setPrefHeight(43);
        TotalLabel1.setPrefWidth(98);
        TotalLabel1.setFont(javafx.scene.text.Font.font("Barlow Condensed", 19));



        MonthSalesPane.getChildren().addAll(mah10, tableMonth,Back1 , TotalLabel1, TotalText1);

        MonthSalesPAGE = new Scene(MonthSalesPane, 1150, 600);
        MonthSalesPAGE.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//==================================== SalesPAGE ===============================================
        Pane SalesPane = new Pane();
        Image mh2 = new Image("mainPage.jpg");
        ImageView mah11 = new ImageView(mh2);
        mah11.setFitHeight(600);
        mah11.setFitWidth(1150);
        HBox hbox = new HBox();
        hbox.setPrefHeight(200);
        hbox.setPrefWidth(618);
        hbox.setLayoutX(523);
        hbox.setLayoutY(275);

        TextField TotalText2 = new TextField();
        TotalText2.setLayoutX(158);
        TotalText2.setLayoutY(393);
        TotalText2.setEditable(false);

        TableView<Items> tableISales = new TableView<>();
        tableISales.setPrefHeight(242);
        tableISales.setPrefWidth(402);
        tableISales.setLayoutX(14);
        tableISales.setLayoutY(137);
        tableISales.setEditable(true);

//        tableISales.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.F1) {
//                String selectedItem = String.valueOf(tableISales.getSelectionModel().getSelectedItem());
//                if (selectedItem != null) {
//                    SalesItems.add(new Items("توصيل",0,1,"") );
//                    double sumPurchase = 0;
//                    for (Items o : SalesItems ) {
//                        sumPurchase = o.getTotal() + sumPurchase;
//
//                    }
//                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
//                }
//            }
//        });









        TableColumn<Items, String> NameItem1 = new TableColumn<>("Name");
        NameItem1.setPrefWidth(80);
        NameItem1.setMinWidth(10);
        NameItem1.setResizable(false);
        NameItem1.setCellValueFactory(new PropertyValueFactory<Items, String>("name"));
        NameItem1.setCellFactory(TextFieldTableCell.forTableColumn());
        // Set an event handler to update the value when editing is committed
        NameItem1.setOnEditCommit(event -> {
            TablePosition<Items, String> position = event.getTablePosition();
            String newValue = event.getNewValue();
            int row = position.getRow();
            Items item = event.getTableView().getItems().get(row);
            item.setName(newValue);
            tableISales.refresh();
        });

        TableColumn<Items, Double> Price1 = new TableColumn<>("Price");
        Price1.setPrefWidth(74);
        Price1.setMinWidth(10);
        Price1.setResizable(false);
        Price1.setCellValueFactory(new PropertyValueFactory<Items, Double>("price"));
        Price1.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

// Set an event handler to update the value when editing is committed
        Price1.setOnEditCommit(event -> {
            TablePosition<Items, Double> position = event.getTablePosition();
            Double newValue = event.getNewValue();
            int row = position.getRow();
            Items item = event.getTableView().getItems().get(row);
            item.setPrice(newValue);
            tableISales.refresh();
            double total = 0;
            for (Items items : tableISales.getItems()) {
                total += items.getTotal();
            }
            TotalText2.setText(NumberFormat.getCurrencyInstance().format(total));
        });


        TableColumn<Items, Integer> Quantity1 = new TableColumn<>("Quantity");
        Quantity1.setPrefWidth(74);
        Quantity1.setMinWidth(10);
        Quantity1.setResizable(false);
        Quantity1.setCellValueFactory(new PropertyValueFactory<Items, Integer>("quantity"));

        Quantity1.setOnEditCommit(event -> {
            TablePosition<Items, Integer> pos = event.getTablePosition();
            Integer newQuantity = event.getNewValue();
            int row = pos.getRow();
            Items item = event.getTableView().getItems().get(row);
            item.setQuantity(newQuantity);
            tableISales.refresh();
            double total = 0;
            for (Items items : tableISales.getItems()) {
                total += items.getTotal();
            }
            TotalText2.setText(NumberFormat.getCurrencyInstance().format(total));

        });

        TableColumn<Items, Double> Total1 = new TableColumn<>("Total");
        Total1.setPrefWidth(74);
        Total1.setMinWidth(10);
        Total1.setResizable(false);
        Total1.setCellValueFactory(new PropertyValueFactory<Items, Double>("total"));

        TableColumn<Items, String> Note = new TableColumn<>("Note");
        Note.setPrefWidth(74);
        Note.setMinWidth(10);
        Note.setResizable(false);
        Note.setCellValueFactory(new PropertyValueFactory<Items, String>("note"));
        Note.setCellFactory(TextFieldTableCell.forTableColumn()); // Set cell factory for editing

        Note.setOnEditCommit(event -> {
            // Get the edited value from the event and update the underlying data model
            Items item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setNote(event.getNewValue());
        });

        tableISales.setItems(SalesItems);
        tableISales.getColumns().addAll(NameItem1, Price1, Quantity1, Total1, Note);

        Label TotalLabel2 = new Label("Total: ");
        TotalLabel2.setLayoutX(45);
        TotalLabel2.setLayoutY(397);
        TotalLabel2.setPrefHeight(43);
        TotalLabel2.setPrefWidth(98);
        TotalLabel2.setFont(javafx.scene.text.Font.font("Barlow Condensed", 19));



        ToggleGroup group = new ToggleGroup();
        RadioButton TakeAway = new RadioButton("Take Away");
        TakeAway.setToggleGroup(group);
        TakeAway.setLayoutX(59);
        TakeAway.setLayoutY(526);
        TakeAway.setTextFill(Color.WHITE);
        TakeAway.setUserData("Take Away"); // Set the userData property

        RadioButton Delivery = new RadioButton("Delivery");
        Delivery.setToggleGroup(group);
        Delivery.setLayoutX(165);
        Delivery.setLayoutY(526);
        Delivery.setTextFill(Color.WHITE);
        Delivery.setUserData("Delivery"); // Set the userData property


        RadioButton DineIn = new RadioButton("In");
        DineIn.setToggleGroup(group);
        DineIn.setLayoutX(256);
        DineIn.setLayoutY(526);
        DineIn.setTextFill(Color.WHITE);
        DineIn.setUserData("In"); // Set the userData property



        Button Delete1 = new Button("Delete");
        Delete1.setPrefHeight(61);
        Delete1.setPrefWidth(100);
        Delete1.setLayoutX(36);
        Delete1.setLayoutY(449);
        Delete1.setOnAction(e->{
            Items selectedItem = tableISales.getSelectionModel().getSelectedItem();
            tableISales.getItems().remove(selectedItem);
            double sumPurchase = 0;
            for (Items o : SalesItems ) {
                sumPurchase = o.getTotal() + sumPurchase;

            }
            TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
        });

        Button Clear = new Button("Clear");
        Clear.setPrefHeight(61);
        Clear.setPrefWidth(100);
        Clear.setLayoutX(165);
        Clear.setLayoutY(449);
        Clear.setOnAction(e->{
            tableISales.getItems().clear();
            TotalText2.setText("");
        });
//        tableISales.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.F2) {
//                String selectedItem = String.valueOf(tableISales.getSelectionModel().getSelectedItem());
//                if (selectedItem != null) {
//                    if (group.getSelectedToggle() != null){
//
//                        if (group.getSelectedToggle().getUserData().equals("Take Away")) {
//                            printTakeAway();
//                        } else if (group.getSelectedToggle().getUserData().equals("Delivery")) {
//                            printDilivery();
//                            Number="";
//                        } else if (group.getSelectedToggle().getUserData().equals("In")) {
//                            printIn();
//                            cash=0;
//
//                        }
//                    }else {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Error");
//                        alert.setHeaderText("Please Select the type of the order");
//                        alert.showAndWait();
//                        return;
//                    }
//                }
//            }
//        });


        Button pay = new Button("Pay");
        pay.setPrefHeight(61);
        pay.setPrefWidth(100);
        pay.setLayoutX(295);
        pay.setLayoutY(449);
        pay.setOnAction(e-> {
            if (group.getSelectedToggle() != null){

                if (group.getSelectedToggle().getUserData().equals("Take Away")) {
                        printTakeAway();
                } else if (group.getSelectedToggle().getUserData().equals("Delivery")) {
                    printDilivery();
                    Number="";
                } else if (group.getSelectedToggle().getUserData().equals("In")) {
                    printIn();
                    cash=0;

                }
        }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please Select the type of the order");
                alert.showAndWait();
                return;
            }


            try {
              for (int i =0 ; i<SalesItems.size() ;i++) {

                  daySales.add(new DaySales(SalesItems.get(i).getName(), SalesItems.get(i).getPrice(), SalesItems.get(i).getQuantity(), SalesItems.get(i).getTotal()));

                  Connection con = db.getConnection().connectDB();
                  String sql1 = "insert into daysales(name,price,quantity,total) values('" + SalesItems.get(i).getName() + "','"
                          + SalesItems.get(i).getPrice() + "','" + SalesItems.get(i).getQuantity() + "','"
                          + SalesItems.get(i).getTotal() + "')";
                  Statement stmt = con.createStatement();
                  stmt.executeUpdate(sql1);
                  con.close();
              }

            } catch (Exception m) {
                m.printStackTrace();
            }


        SalesItems.clear();
            group.getSelectedToggle().setSelected(false);



        });

        Button bake = new Button("Back");
        bake.setPrefHeight(61);
        bake.setPrefWidth(100);
        bake.setLayoutX(1022);
        bake.setLayoutY(526);
        bake.setOnAction(e->{
            stage.setScene(MainPAGE);
        });

        Button logoutButton = new Button("LOGOUT", new ImageView("logout.png"));
        logoutButton.setPrefSize(150, 33);
        logoutButton.setLayoutX(894);
        logoutButton.setLayoutY(51);
        logoutButton.setContentDisplay(ContentDisplay.LEFT);
        logoutButton.setOnAction(e->{
            stage.setScene(LoginPAGE);
        });

        Button Pizza = new Button("البيتزا");
        Pizza.setPrefHeight(50);
        Pizza.setPrefWidth(100);
        Pizza.setLayoutX(1039);
        Pizza.setLayoutY(143);
        Pizza.setOnAction(e -> {
        hbox.getChildren().clear();
        Pane PizzaPane = new Pane();

        Button Mushroom = new Button(" بيتزا مشروم");
        Mushroom.setPrefHeight(50);
        Mushroom.setPrefWidth(100);
        Mushroom.setLayoutX(504);
        Mushroom.setLayoutY(14);
            Mushroom.setOnAction(r->{
                Stage MushroomStage = new Stage();
                Pane MushroomPane = new Pane();
                String n = null;
                Button MushroomSmall = new Button("S");
                MushroomSmall.setPrefHeight(50);
                MushroomSmall.setPrefWidth(100);
                MushroomSmall.setLayoutX(420);
                MushroomSmall.setLayoutY(27);
                MushroomSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items("ماشروم"+" "+MushroomSmall.getText(), 30, 1, ""));
                    double sumPurchase = 0;

                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;


                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomStage.close();
                });

                Button MushroomMedium = new Button("M");
                MushroomMedium.setPrefHeight(50);
                MushroomMedium.setPrefWidth(100);
                MushroomMedium.setLayoutX(261);
                MushroomMedium.setLayoutY(27);
                MushroomMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items("ماشروم"+" "+MushroomMedium.getText(), 55, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomStage.close();
                    MushroomStage.close();
                });

                Button MushroomLarge = new Button("L");
                MushroomLarge.setPrefHeight(50);
                MushroomLarge.setPrefWidth(100);
                MushroomLarge.setLayoutX(83);
                MushroomLarge.setLayoutY(27);
                MushroomLarge.setOnAction(t->{
                    tableISales.getItems().add(new Items("ماشروم"+" "+MushroomLarge.getText(), 65, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    MushroomStage.close();
                });

                MushroomPane.getChildren().addAll(MushroomSmall,MushroomMedium,MushroomLarge,Done);

                Scene MushroomScene = new Scene(MushroomPane, 600, 400);
                MushroomScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                MushroomStage.setScene(MushroomScene);
                MushroomStage.show();


            });



        Button veggie = new Button("بيتزا خضار");
        veggie.setPrefHeight(50);
        veggie.setPrefWidth(100);
        veggie.setLayoutX(394);
        veggie.setLayoutY(14);
        veggie.setOnAction(r->{
            Stage veggieStage = new Stage();
            Pane veggiePane = new Pane();
            Button veggieSmall = new Button("S");
            veggieSmall.setPrefHeight(50);
            veggieSmall.setPrefWidth(100);
            veggieSmall.setLayoutX(420);
            veggieSmall.setLayoutY(27);
            veggieSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("خضار"+" "+veggieSmall.getText(), 28, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                veggieStage.close();
            });

            Button veggieMedium = new Button("M");
            veggieMedium.setPrefHeight(50);
            veggieMedium.setPrefWidth(100);
            veggieMedium.setLayoutX(261);
            veggieMedium.setLayoutY(27);
            veggieMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("خضار"+" "+veggieMedium.getText(), 45, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                veggieStage.close();
            });

            Button veggieLarge = new Button("L");
            veggieLarge.setPrefHeight(50);
            veggieLarge.setPrefWidth(100);
            veggieLarge.setLayoutX(83);
            veggieLarge.setLayoutY(27);
            veggieLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("خضار"+" "+veggieLarge.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                veggieStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                veggieStage.close();
            });

            veggiePane.getChildren().addAll(veggieSmall,veggieMedium,veggieLarge,Done);

            Scene veggieScene = new Scene(veggiePane, 600, 400);
            veggieScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            veggieStage.setScene(veggieScene);
            veggieStage.show();
        });

        Button Salami = new Button("بيتزا سلامي");
        Salami.setPrefHeight(50);
        Salami.setPrefWidth(100);
        Salami.setLayoutX(284);
        Salami.setLayoutY(14);
        Salami.setOnAction(r->{
            // TODO Auto-generated method stub
            Stage SalamiStage = new Stage();
            Pane SalamiPane = new Pane();
            Button SalamiSmall = new Button("S");
            SalamiSmall.setPrefHeight(50);
            SalamiSmall.setPrefWidth(100);
            SalamiSmall.setLayoutX(420);
            SalamiSmall.setLayoutY(27);
            SalamiSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("سلامي"+" "+SalamiSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                SalamiStage.close();
            });

            Button SalamiMedium = new Button("M");
            SalamiMedium.setPrefHeight(50);
            SalamiMedium.setPrefWidth(100);
            SalamiMedium.setLayoutX(261);
            SalamiMedium.setLayoutY(27);
            SalamiMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("سلامي"+" "+SalamiMedium.getText(), 50, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                SalamiStage.close();
            });

            Button SalamiLarge = new Button("L");
            SalamiLarge.setPrefHeight(50);
            SalamiLarge.setPrefWidth(100);
            SalamiLarge.setLayoutX(83);
            SalamiLarge.setLayoutY(27);
            SalamiLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("سلامي"+" "+SalamiLarge.getText(), 60, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                SalamiStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                SalamiStage.close();
            });

            SalamiPane.getChildren().addAll(SalamiSmall,SalamiMedium,SalamiLarge,Done);

            Scene SalamiScene = new Scene(SalamiPane, 600, 400);
            SalamiScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            SalamiStage.setScene(SalamiScene);
            SalamiStage.show();



        });

        Button margarita = new Button("بيتزا مارجريتا");
        margarita.setPrefHeight(50);
        margarita.setPrefWidth(100);
        margarita.setLayoutX(174);
        margarita.setLayoutY(14);
        margarita.setOnAction(r->{
            // TODO Auto-generated method stub
            Stage margaritaStage = new Stage();
            Pane margaritaPane = new Pane();
            Button margaritaSmall = new Button("S");
            margaritaSmall.setPrefHeight(50);
            margaritaSmall.setPrefWidth(100);
            margaritaSmall.setLayoutX(420);
            margaritaSmall.setLayoutY(27);
            margaritaSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("مارجريتا"+" "+margaritaSmall.getText(), 28, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                margaritaStage.close();
            });

            Button margaritaMedium = new Button("M");
            margaritaMedium.setPrefHeight(50);
            margaritaMedium.setPrefWidth(100);
            margaritaMedium.setLayoutX(261);
            margaritaMedium.setLayoutY(27);
            margaritaMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("مارجريتا"+" "+margaritaMedium.getText(), 45, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                margaritaStage.close();
            });

            Button margaritaLarge = new Button("L");
            margaritaLarge.setPrefHeight(50);
            margaritaLarge.setPrefWidth(100);
            margaritaLarge.setLayoutX(83);
            margaritaLarge.setLayoutY(27);
            margaritaLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("مارجريتا"+" "+margaritaLarge.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                margaritaStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                margaritaStage.close();
            });

            margaritaPane.getChildren().addAll(margaritaSmall,margaritaMedium,margaritaLarge,Done);

            Scene margaritaScene = new Scene(margaritaPane, 600, 400);
            margaritaScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            margaritaStage.setScene(margaritaScene);
            margaritaStage.show();

        });

        Button pabaroni = new Button("بيتزا بيبروني");
        pabaroni.setPrefHeight(50);
        pabaroni.setPrefWidth(100);
        pabaroni.setLayoutX(64);
        pabaroni.setLayoutY(14);
        pabaroni.setOnAction(r->{
            Stage pabaroniStage = new Stage();
            Pane pabaroniPane = new Pane();
            Button pabaroniSmall = new Button("S");
            pabaroniSmall.setPrefHeight(50);
            pabaroniSmall.setPrefWidth(100);
            pabaroniSmall.setLayoutX(420);
            pabaroniSmall.setLayoutY(27);
            pabaroniSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيبروني"+" "+pabaroniSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                pabaroniStage.close();
            });

            Button pabaroniMedium = new Button("M");
            pabaroniMedium.setPrefHeight(50);
            pabaroniMedium.setPrefWidth(100);
            pabaroniMedium.setLayoutX(261);
            pabaroniMedium.setLayoutY(27);
            pabaroniMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيبروني"+" "+pabaroniMedium.getText(), 50, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                pabaroniStage.close();
            });

            Button pabaroniLarge = new Button("L");
            pabaroniLarge.setPrefHeight(50);
            pabaroniLarge.setPrefWidth(100);
            pabaroniLarge.setLayoutX(83);
            pabaroniLarge.setLayoutY(27);
            pabaroniLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيبروني"+" "+pabaroniLarge.getText(), 60, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                pabaroniStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                pabaroniStage.close();
            });

            pabaroniPane.getChildren().addAll(pabaroniSmall,pabaroniMedium,pabaroniLarge,Done);

            Scene pabaroniScene = new Scene(pabaroniPane, 600, 400);
            pabaroniScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            pabaroniStage.setScene(pabaroniScene);
            pabaroniStage.show();





        });

        Button barbeque = new Button("بيتزا باربيكيو");
        barbeque.setPrefHeight(50);
        barbeque.setPrefWidth(100);
        barbeque.setLayoutX(504);
        barbeque.setLayoutY(75);
        barbeque.setOnAction(r->{
            Stage barbequeStage = new Stage();
            Pane barbequePane = new Pane();
            Button barbequeSmall = new Button("S");
            barbequeSmall.setPrefHeight(50);
            barbequeSmall.setPrefWidth(100);
            barbequeSmall.setLayoutX(420);
            barbequeSmall.setLayoutY(27);
            barbequeSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("باربيكيو"+" "+barbequeSmall.getText(), 32, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                barbequeStage.close();
            });

            Button barbequeMedium = new Button("M");
            barbequeMedium.setPrefHeight(50);
            barbequeMedium.setPrefWidth(100);
            barbequeMedium.setLayoutX(261);
            barbequeMedium.setLayoutY(27);
            barbequeMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("باربيكيو"+" "+barbequeMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                barbequeStage.close();
            });

            Button barbequeLarge = new Button("L");
            barbequeLarge.setPrefHeight(50);
            barbequeLarge.setPrefWidth(100);
            barbequeLarge.setLayoutX(83);
            barbequeLarge.setLayoutY(27);
            barbequeLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("باربيكيو"+" "+barbequeLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                barbequeStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                barbequeStage.close();
            });

            barbequePane.getChildren().addAll(barbequeSmall,barbequeMedium,barbequeLarge,Done);

            Scene barbequeScene = new Scene(barbequePane, 600, 400);
            barbequeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            barbequeStage.setScene(barbequeScene);
            barbequeStage.show();

        });

        Button meat = new Button("بيتزا اللحمة");
        meat.setPrefHeight(50);
        meat.setPrefWidth(100);
        meat.setLayoutX(394);
        meat.setLayoutY(75);
        meat.setOnAction(r->{
            Stage meatStage = new Stage();
            Pane meatPane = new Pane();
            Button meatSmall = new Button("S");
            meatSmall.setPrefHeight(50);
            meatSmall.setPrefWidth(100);
            meatSmall.setLayoutX(420);
            meatSmall.setLayoutY(27);
            meatSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا اللحمة"+" "+meatSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                meatStage.close();
            });

            Button meatMedium = new Button("M");
            meatMedium.setPrefHeight(50);
            meatMedium.setPrefWidth(100);
            meatMedium.setLayoutX(261);
            meatMedium.setLayoutY(27);
            meatMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا اللحمة"+" "+meatMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                meatStage.close();
            });

            Button meatLarge = new Button("L");
            meatLarge.setPrefHeight(50);
            meatLarge.setPrefWidth(100);
            meatLarge.setLayoutX(83);
            meatLarge.setLayoutY(27);
            meatLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا اللحمة"+" "+meatLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                meatStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                meatStage.close();
            });

            meatPane.getChildren().addAll(meatSmall,meatMedium,meatLarge,Done);

            Scene meatScene = new Scene(meatPane, 600, 400);
            meatScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            meatStage.setScene(meatScene);
            meatStage.show();


        });

        Button Anshofi = new Button("بيتزا انشوفي");
        Anshofi.setPrefHeight(50);
        Anshofi.setPrefWidth(100);
        Anshofi.setLayoutX(284);
        Anshofi.setLayoutY(75);
        Anshofi.setOnAction(r->{
            Stage AnshofiStage = new Stage();
            Pane AnshofiPane = new Pane();
            Button AnshofiSmall = new Button("S");
            AnshofiSmall.setPrefHeight(50);
            AnshofiSmall.setPrefWidth(100);
            AnshofiSmall.setLayoutX(420);
            AnshofiSmall.setLayoutY(27);
            AnshofiSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا انشوفي"+"\n"+AnshofiSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                AnshofiStage.close();
            });

            Button AnshofiMedium = new Button("M");
            AnshofiMedium.setPrefHeight(50);
            AnshofiMedium.setPrefWidth(100);
            AnshofiMedium.setLayoutX(261);
            AnshofiMedium.setLayoutY(27);
            AnshofiMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا انشوفي"+"\n"+AnshofiMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                AnshofiStage.close();
            });

            Button AnshofiLarge = new Button("L");
            AnshofiLarge.setPrefHeight(50);
            AnshofiLarge.setPrefWidth(100);
            AnshofiLarge.setLayoutX(83);
            AnshofiLarge.setLayoutY(27);
            AnshofiLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا انشوفي"+"\n"+AnshofiLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                AnshofiStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                AnshofiStage.close();
            });

            AnshofiPane.getChildren().addAll(AnshofiSmall,AnshofiMedium,AnshofiLarge,Done);

            Scene AnshofiScene = new Scene(AnshofiPane, 600, 400);
            AnshofiScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            AnshofiStage.setScene(AnshofiScene);
            AnshofiStage.show();


        });


        Button gamberiandBasha = new Button("بيتزا الجمبري"+"\n"+"بالباشميل");
        gamberiandBasha.setPrefHeight(50);
        gamberiandBasha.setPrefWidth(100);
        gamberiandBasha.setLayoutX(174);
        gamberiandBasha.setLayoutY(75);
        gamberiandBasha.setOnAction(r->{

            Stage gamberiandBashaStage = new Stage();
            Pane gamberiandBashaPane = new Pane();
            Button gamberiandBashaSmall = new Button("S");
            gamberiandBashaSmall.setPrefHeight(50);
            gamberiandBashaSmall.setPrefWidth(100);
            gamberiandBashaSmall.setLayoutX(420);
            gamberiandBashaSmall.setLayoutY(27);
            gamberiandBashaSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الجمبري"+"\n"+"بالباشميل"+" "+gamberiandBashaSmall.getText(), 32, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                gamberiandBashaStage.close();
            });

            Button gamberiandBashaMedium = new Button("M");
            gamberiandBashaMedium.setPrefHeight(50);
            gamberiandBashaMedium.setPrefWidth(100);
            gamberiandBashaMedium.setLayoutX(261);
            gamberiandBashaMedium.setLayoutY(27);
            gamberiandBashaMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الجمبري"+"\n"+"بالباشميل"+" "+gamberiandBashaMedium.getText(), 57, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                gamberiandBashaStage.close();
            });

            Button gamberiandBashaLarge = new Button("L");
            gamberiandBashaLarge.setPrefHeight(50);
            gamberiandBashaLarge.setPrefWidth(100);
            gamberiandBashaLarge.setLayoutX(83);
            gamberiandBashaLarge.setLayoutY(27);
            gamberiandBashaLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الجمبري"+"\n"+"بالباشميل"+" "+gamberiandBashaLarge.getText(), 67, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                gamberiandBashaStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                gamberiandBashaStage.close();

            });

            gamberiandBashaPane.getChildren().addAll(gamberiandBashaSmall,gamberiandBashaMedium,gamberiandBashaLarge,Done);

            Scene gamberiandBashaScene = new Scene(gamberiandBashaPane, 600, 400);

            gamberiandBashaScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            gamberiandBashaStage.setScene(gamberiandBashaScene);
            gamberiandBashaStage.show();

        });


        Button ananas = new Button("بيتزا الاناناس");
        ananas.setPrefHeight(50);
        ananas.setPrefWidth(100);
        ananas.setLayoutX(64);
        ananas.setLayoutY(75);

        ananas.setOnAction(r->{

            Stage ananasStage = new Stage();
            Pane ananasPane = new Pane();
            Button ananasSmall = new Button("S");
            ananasSmall.setPrefHeight(50);
            ananasSmall.setPrefWidth(100);
            ananasSmall.setLayoutX(420);
            ananasSmall.setLayoutY(27);
            ananasSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الاناناس"+"\n"+ananasSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasMedium = new Button("M");
            ananasMedium.setPrefHeight(50);
            ananasMedium.setPrefWidth(100);
            ananasMedium.setLayoutX(261);
            ananasMedium.setLayoutY(27);
            ananasMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الاناناس"+"\n"+ananasMedium.getText(), 45, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasLarge = new Button("L");
            ananasLarge.setPrefHeight(50);
            ananasLarge.setPrefWidth(100);
            ananasLarge.setLayoutX(83);
            ananasLarge.setLayoutY(27);
            ananasLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الاناناس"+"\n"+ananasLarge.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                ananasStage.close();
            });

            ananasPane.getChildren().addAll(ananasSmall,ananasMedium,ananasLarge,Done);

            Scene ananasScene = new Scene(ananasPane, 600, 400);

            ananasScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            ananasStage.setScene(ananasScene);
            ananasStage.show();


            });

        Button fuorSeason = new Button("بيتزا الفصول"+"\n "+"الاربعة");
        fuorSeason.setPrefHeight(50);
        fuorSeason.setPrefWidth(100);
        fuorSeason.setLayoutX(504);
        fuorSeason.setLayoutY(136);
        fuorSeason.setOnAction(r->{

            Stage ananasStage = new Stage();
            Pane ananasPane = new Pane();
            Button ananasSmall = new Button("S");
            ananasSmall.setPrefHeight(50);
            ananasSmall.setPrefWidth(100);
            ananasSmall.setLayoutX(420);
            ananasSmall.setLayoutY(27);
            ananasSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الفصول"+"\n "+"الاربعة"+ananasSmall.getText(), 32, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasMedium = new Button("M");
            ananasMedium.setPrefHeight(50);
            ananasMedium.setPrefWidth(100);
            ananasMedium.setLayoutX(261);
            ananasMedium.setLayoutY(27);
            ananasMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الفصول"+"\n "+"الاربعة"+ananasMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasLarge = new Button("L");
            ananasLarge.setPrefHeight(50);
            ananasLarge.setPrefWidth(100);
            ananasLarge.setLayoutX(83);
            ananasLarge.setLayoutY(27);
            ananasLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا الفصول"+"\n "+"الاربعة"+ananasLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                ananasStage.close();
            });

            ananasPane.getChildren().addAll(ananasSmall,ananasMedium,ananasLarge,Done);

            Scene ananasScene = new Scene(ananasPane, 600, 400);

            ananasScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            ananasStage.setScene(ananasScene);
            ananasStage.show();


        });


        Button Tuna = new Button("بيتزا التونة");
        Tuna.setPrefHeight(50);
        Tuna.setPrefWidth(100);
        Tuna.setLayoutX(394);
        Tuna.setLayoutY(136);
        Tuna.setOnAction(r->{

            Stage ananasStage = new Stage();
            Pane ananasPane = new Pane();
            Button ananasSmall = new Button("S");
            ananasSmall.setPrefHeight(50);
            ananasSmall.setPrefWidth(100);
            ananasSmall.setLayoutX(420);
            ananasSmall.setLayoutY(27);
            ananasSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا التونة"+" "+ananasSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasMedium = new Button("M");
            ananasMedium.setPrefHeight(50);
            ananasMedium.setPrefWidth(100);
            ananasMedium.setLayoutX(261);
            ananasMedium.setLayoutY(27);
            ananasMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا التونة"+" "+ananasMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasLarge = new Button("L");
            ananasLarge.setPrefHeight(50);
            ananasLarge.setPrefWidth(100);
            ananasLarge.setLayoutX(83);
            ananasLarge.setLayoutY(27);
            ananasLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا التونة"+" "+ananasLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                ananasStage.close();
            });

            ananasPane.getChildren().addAll(ananasSmall,ananasMedium,ananasLarge,Done);

            Scene ananasScene = new Scene(ananasPane, 600, 400);

            ananasScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            ananasStage.setScene(ananasScene);
            ananasStage.show();


        });

        Button macs = new Button("بيتزا مكسيكي");
        macs.setPrefHeight(50);
        macs.setPrefWidth(100);
        macs.setLayoutX(284);
        macs.setLayoutY(136);
        macs.setOnAction(r->{

            Stage ananasStage = new Stage();
            Pane ananasPane = new Pane();
            Button ananasSmall = new Button("S");
            ananasSmall.setPrefHeight(50);
            ananasSmall.setPrefWidth(100);
            ananasSmall.setLayoutX(420);
            ananasSmall.setLayoutY(27);
            ananasSmall.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا مكسيكي"+"\n"+ananasSmall.getText(), 30, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasMedium = new Button("M");
            ananasMedium.setPrefHeight(50);
            ananasMedium.setPrefWidth(100);
            ananasMedium.setLayoutX(261);
            ananasMedium.setLayoutY(27);
            ananasMedium.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا مكسيكي"+"\n"+ananasMedium.getText(), 55, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button ananasLarge = new Button("L");
            ananasLarge.setPrefHeight(50);
            ananasLarge.setPrefWidth(100);
            ananasLarge.setLayoutX(83);
            ananasLarge.setLayoutY(27);
            ananasLarge.setOnAction(t->{
                tableISales.getItems().add(new Items("بيتزا مكسيكي"+"\n"+ananasLarge.getText(), 65, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                ananasStage.close();
            });

            Button Done = new Button("Done");
            Done.setPrefHeight(50);
            Done.setPrefWidth(100);
            Done.setLayoutX(267);
            Done.setLayoutY(305);
            Done.setOnAction(t->{
                ananasStage.close();
            });

            ananasPane.getChildren().addAll(ananasSmall,ananasMedium,ananasLarge,Done);

            Scene ananasScene = new Scene(ananasPane, 600, 400);

            ananasScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            ananasStage.setScene(ananasScene);
            ananasStage.show();


        });

        PizzaPane.getChildren().addAll(Mushroom,veggie,Salami,margarita,pabaroni,barbeque,meat,Anshofi,gamberiandBasha,ananas,fuorSeason,Tuna,macs);
            hbox.getChildren().add(PizzaPane);


        });


        Button Burger = new Button("السندوتشات");
        Burger.setPrefHeight(50);
        Burger.setPrefWidth(100);
        Burger.setLayoutX(931);
        Burger.setLayoutY(143);
        Burger.setOnAction(e->{
            hbox.getChildren().clear();
            Pane BurgerPane = new Pane();

            Button Mushroom = new Button("مشروم برجر");
            Mushroom.setPrefHeight(50);
            Mushroom.setPrefWidth(100);
            Mushroom.setLayoutX(504);
            Mushroom.setLayoutY(14);
            Mushroom.setOnAction(r->{
                Stage MushroomStage = new Stage();
                Pane MushroomPane = new Pane();
                Button MushroomSmall = new Button("وجبة");
                MushroomSmall.setPrefHeight(50);
                MushroomSmall.setPrefWidth(100);
                MushroomSmall.setLayoutX(391);
                MushroomSmall.setLayoutY(27);

                MushroomSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items("مشروم برجر"+"\n"+MushroomSmall.getText(), 36, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomStage.close();
                });

                Button MushroomMedium = new Button("ساندوتش");
                MushroomMedium.setPrefHeight(50);
                MushroomMedium.setPrefWidth(100);
                MushroomMedium.setLayoutX(100);
                MushroomMedium.setLayoutY(27);
                MushroomMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items("مشروم برجر"+"\n"+MushroomMedium.getText(), 32, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    MushroomStage.close();
                });

                MushroomPane.getChildren().addAll(MushroomSmall,MushroomMedium,Done);

                Scene MushroomScene = new Scene(MushroomPane, 600, 400);

                MushroomScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                MushroomStage.setScene(MushroomScene);
                MushroomStage.show();


            });

            Button Meat = new Button("برجر لحمة ");
            Meat.setPrefHeight(50);
            Meat.setPrefWidth(100);
            Meat.setLayoutX(394);
            Meat.setLayoutY(14);
            Meat.setOnAction(r-> {
                        Stage MeatStage = new Stage();
                        Pane MeatPane = new Pane();
                        Button MeatSmall = new Button("وجبة");
                        MeatSmall.setPrefHeight(50);
                        MeatSmall.setPrefWidth(100);
                        MeatSmall.setLayoutX(391);
                        MeatSmall.setLayoutY(27);

                        MeatSmall.setOnAction(t -> {
                            tableISales.getItems().add(new Items("برجر لحمة" + "\n" + MeatSmall.getText(), 32, 1, ""));
                            double sumPurchase = 0;
                            for (Items o : SalesItems ) {
                                sumPurchase = o.getTotal() + sumPurchase;

                            }
                            TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                            MeatStage.close();
                        });

                        Button MeatMedium = new Button("ساندوتش");
                        MeatMedium.setPrefHeight(50);
                        MeatMedium.setPrefWidth(100);
                        MeatMedium.setLayoutX(100);
                        MeatMedium.setLayoutY(27);
                        MeatMedium.setOnAction(t -> {
                            tableISales.getItems().add(new Items("برجر لحمة" + "\n" + MeatMedium.getText(), 28, 1, ""));
                            double sumPurchase = 0;
                            for (Items o : SalesItems ) {
                                sumPurchase = o.getTotal() + sumPurchase;

                            }
                            TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                            MeatStage.close();
                        });

                        Button Done = new Button("Done");
                        Done.setPrefHeight(50);
                        Done.setPrefWidth(100);
                        Done.setLayoutX(267);
                        Done.setLayoutY(305);
                        Done.setOnAction(t -> {
                            MeatStage.close();
                        });

                        MeatPane.getChildren().addAll(MeatSmall, MeatMedium, Done);

                        Scene MeatScene = new Scene(MeatPane, 600, 400);

                        MeatScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                        MeatStage.setScene(MeatScene);
                        MeatStage.show();
                    });

            Button ChickenStake = new Button("برجر"+"\n"+"ستيك دجاج");
            ChickenStake.setPrefHeight(50);
            ChickenStake.setPrefWidth(100);
            ChickenStake.setLayoutX(284);
            ChickenStake.setLayoutY(14);
            ChickenStake.setOnAction(r->{
                Stage ChickenStakeStage = new Stage();
                Pane ChickenStakePane = new Pane();
                Button ChickenStakeSmall = new Button("وجبة");
                ChickenStakeSmall.setPrefHeight(50);
                ChickenStakeSmall.setPrefWidth(100);
                ChickenStakeSmall.setLayoutX(391);
                ChickenStakeSmall.setLayoutY(27);

                ChickenStakeSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ستيك دجاج"+"\n"+ChickenStakeSmall.getText(), 25, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ChickenStakeStage.close();
                });

                Button ChickenStakeMedium = new Button("ساندوتش");
                ChickenStakeMedium.setPrefHeight(50);
                ChickenStakeMedium.setPrefWidth(100);
                ChickenStakeMedium.setLayoutX(100);
                ChickenStakeMedium.setLayoutY(27);
                ChickenStakeMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ستيك دجاج"+"\n"+ChickenStakeMedium.getText(), 18, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ChickenStakeStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    ChickenStakeStage.close();
                });

                ChickenStakePane.getChildren().addAll(ChickenStakeSmall,ChickenStakeMedium,Done);

                Scene ChickenStakeScene = new Scene(ChickenStakePane, 600, 400);

                ChickenStakeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                ChickenStakeStage.setScene(ChickenStakeScene);
                ChickenStakeStage.show();

            });



            Button ChickenKresto = new Button("برجر"+"\n"+"كريسبي");
            ChickenKresto.setPrefHeight(50);
            ChickenKresto.setPrefWidth(100);
            ChickenKresto.setLayoutX(174);
            ChickenKresto.setLayoutY(14);
            ChickenKresto.setOnAction(r->{
                Stage ChickenKrestoStage = new Stage();
                Pane ChickenKrestoPane = new Pane();
                Button ChickenKrestoSmall = new Button("وجبة");
                ChickenKrestoSmall.setPrefHeight(50);
                ChickenKrestoSmall.setPrefWidth(100);
                ChickenKrestoSmall.setLayoutX(391);
                ChickenKrestoSmall.setLayoutY(27);

                ChickenKrestoSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" برجر كريسبي"+"\n"+ChickenKrestoSmall.getText(), 25, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ChickenKrestoStage.close();
                });

                Button ChickenKrestoMedium = new Button("ساندوتش");
                ChickenKrestoMedium.setPrefHeight(50);
                ChickenKrestoMedium.setPrefWidth(100);
                ChickenKrestoMedium.setLayoutX(100);
                ChickenKrestoMedium.setLayoutY(27);
                ChickenKrestoMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" برجر كريسبي"+"\n"+ChickenKrestoMedium.getText(), 18, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ChickenKrestoStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    ChickenKrestoStage.close();
                });

                ChickenKrestoPane.getChildren().addAll(ChickenKrestoSmall,ChickenKrestoMedium,Done);

                Scene ChickenKrestoScene = new Scene(ChickenKrestoPane, 600, 400);

                ChickenKrestoScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                ChickenKrestoStage.setScene(ChickenKrestoScene);
                ChickenKrestoStage.show();


            });

            Button MsahadSandwich = new Button("مسحب"+ "\n" +"ساندويش");
            MsahadSandwich.setPrefHeight(50);
            MsahadSandwich.setPrefWidth(100);
            MsahadSandwich.setLayoutX(64);
            MsahadSandwich.setLayoutY(14);
            MsahadSandwich.setOnAction(r->{
                Stage MsahadSandwichStage = new Stage();
                Pane MsahadSandwichPane = new Pane();
                Button MsahadSandwichSmall = new Button("وجبة");
                MsahadSandwichSmall.setPrefHeight(50);
                MsahadSandwichSmall.setPrefWidth(100);
                MsahadSandwichSmall.setLayoutX(391);
                MsahadSandwichSmall.setLayoutY(27);

                MsahadSandwichSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" مسحب"+" "+MsahadSandwichSmall.getText(), 25, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MsahadSandwichStage.close();
                });

                Button MsahadSandwichMedium = new Button("ساندوتش");
                MsahadSandwichMedium.setPrefHeight(50);
                MsahadSandwichMedium.setPrefWidth(100);
                MsahadSandwichMedium.setLayoutX(100);
                MsahadSandwichMedium.setLayoutY(27);
                MsahadSandwichMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" مسحب"+"\n"+MsahadSandwichMedium.getText(), 18, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MsahadSandwichStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    MsahadSandwichStage.close();
                });

                MsahadSandwichPane.getChildren().addAll(MsahadSandwichSmall,MsahadSandwichMedium,Done);

                Scene MsahadSandwichScene = new Scene(MsahadSandwichPane, 600, 400);

                MsahadSandwichScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                MsahadSandwichStage.setScene(MsahadSandwichScene);
                MsahadSandwichStage.show();
            });

            Button vahitaSandwich = new Button("فاهيتا"+ "\n" +"ساندويش");
            vahitaSandwich.setPrefHeight(50);
            vahitaSandwich.setPrefWidth(100);
            vahitaSandwich.setLayoutX(504);
            vahitaSandwich.setLayoutY(75);
            vahitaSandwich.setOnAction(r->{

                Stage vahitaSandwichStage = new Stage();
                Pane vahitaSandwichPane = new Pane();
                Button vahitaSandwichSmall = new Button("وجبة");
                vahitaSandwichSmall.setPrefHeight(50);
                vahitaSandwichSmall.setPrefWidth(100);
                vahitaSandwichSmall.setLayoutX(391);
                vahitaSandwichSmall.setLayoutY(27);

                vahitaSandwichSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" فاهيتا"+" "+vahitaSandwichSmall.getText(), 25, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    vahitaSandwichStage.close();
                });

                Button vahitaSandwichMedium = new Button("ساندوتش");
                vahitaSandwichMedium.setPrefHeight(50);
                vahitaSandwichMedium.setPrefWidth(100);
                vahitaSandwichMedium.setLayoutX(100);
                vahitaSandwichMedium.setLayoutY(27);
                vahitaSandwichMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" فاهيتا"+"\n"+vahitaSandwichMedium.getText(), 20, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    vahitaSandwichStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    vahitaSandwichStage.close();
                });

                vahitaSandwichPane.getChildren().addAll(vahitaSandwichSmall,vahitaSandwichMedium,Done);

                Scene vahitaSandwichScene = new Scene(vahitaSandwichPane, 600, 400);

                vahitaSandwichScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                vahitaSandwichStage.setScene(vahitaSandwichScene);
                vahitaSandwichStage.show();
            });


            Button MushroomSandwich = new Button("مشروم"+ "\n" +"ساندويش");
            MushroomSandwich.setPrefHeight(50);
            MushroomSandwich.setPrefWidth(100);
            MushroomSandwich.setLayoutX(394);
            MushroomSandwich.setLayoutY(75);

            MushroomSandwich.setOnAction(r->{
                Stage MushroomSandwichStage = new Stage();
                Pane MushroomSandwichPane = new Pane();
                Button MushroomSandwichSmall = new Button("وجبة");
                MushroomSandwichSmall.setPrefHeight(50);
                MushroomSandwichSmall.setPrefWidth(100);
                MushroomSandwichSmall.setLayoutX(391);
                MushroomSandwichSmall.setLayoutY(27);

                MushroomSandwichSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" مشروم"+" "+MushroomSandwichSmall.getText(), 30, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomSandwichStage.close();
                });

                Button MushroomSandwichMedium = new Button("ساندوتش");
                MushroomSandwichMedium.setPrefHeight(50);
                MushroomSandwichMedium.setPrefWidth(100);
                MushroomSandwichMedium.setLayoutX(100);
                MushroomSandwichMedium.setLayoutY(27);
                MushroomSandwichMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" مشروم"+"\n"+MushroomSandwichMedium.getText(), 25, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    MushroomSandwichStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    MushroomSandwichStage.close();
                });

                MushroomSandwichPane.getChildren().addAll(MushroomSandwichSmall,MushroomSandwichMedium,Done);

                Scene MushroomSandwichScene = new Scene(MushroomSandwichPane, 600, 400);

                MushroomSandwichScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                MushroomSandwichStage.setScene(MushroomSandwichScene);
                MushroomSandwichStage.show();
            });


            BurgerPane.getChildren().addAll(Mushroom,Meat,ChickenStake,ChickenKresto,MsahadSandwich,vahitaSandwich,MushroomSandwich);
            hbox.getChildren().add(BurgerPane);

        });


        Button Potato = new Button("البطاطا");
        Potato.setPrefHeight(50);
        Potato.setPrefWidth(100);
        Potato.setLayoutX(819);
        Potato.setLayoutY(143);
        Potato.setOnAction(e->{
            hbox.getChildren().clear();
            Pane PotatoPane = new Pane();

            Button PotatoNormal = new Button("بطاطا عادية");
            PotatoNormal.setPrefHeight(50);
            PotatoNormal.setPrefWidth(100);
            PotatoNormal.setLayoutX(504);
            PotatoNormal.setLayoutY(14);
            PotatoNormal.setOnAction(r->{
                Stage PotatoNormalStage = new Stage();
                Pane PotatoNormalPane = new Pane();
                Button PotatoNormalSmall = new Button("S");
                PotatoNormalSmall.setPrefHeight(50);
                PotatoNormalSmall.setPrefWidth(100);
                PotatoNormalSmall.setLayoutX(391);
                PotatoNormalSmall.setLayoutY(27);

                PotatoNormalSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" بطاطا"+" "+PotatoNormalSmall.getText(), 7, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoNormalStage.close();
                });

                    Button PotatoNormalMedium = new Button("L");
                PotatoNormalMedium.setPrefHeight(50);
                PotatoNormalMedium.setPrefWidth(100);
                PotatoNormalMedium.setLayoutX(100);
                PotatoNormalMedium.setLayoutY(27);
                PotatoNormalMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" بطاطا"+" "+PotatoNormalMedium.getText(), 15, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoNormalStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    PotatoNormalStage.close();
                });

                PotatoNormalPane.getChildren().addAll(PotatoNormalSmall,PotatoNormalMedium,Done);

                Scene PotatoNormalScene = new Scene(PotatoNormalPane, 600, 400);

                PotatoNormalScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                PotatoNormalStage.setScene(PotatoNormalScene);
                PotatoNormalStage.show();

            });

            Button PotatoWedgs = new Button("بطاطا ويدجز");
            PotatoWedgs.setPrefHeight(50);
            PotatoWedgs.setPrefWidth(100);
            PotatoWedgs.setLayoutX(394);
            PotatoWedgs.setLayoutY(14);
            PotatoWedgs.setOnAction(r->{
                Stage PotatoWedgsStage = new Stage();
                Pane PotatoWedgsPane = new Pane();
                Button PotatoWedgsSmall = new Button("S");
                PotatoWedgsSmall.setPrefHeight(50);
                PotatoWedgsSmall.setPrefWidth(100);
                PotatoWedgsSmall.setLayoutX(391);
                PotatoWedgsSmall.setLayoutY(27);
                PotatoWedgsSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ويدجز"+" "+PotatoWedgsSmall.getText(), 10, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoWedgsStage.close();
                });

                Button PotatoWedgsMedium = new Button("L");
                PotatoWedgsMedium.setPrefHeight(50);
                PotatoWedgsMedium.setPrefWidth(100);
                PotatoWedgsMedium.setLayoutX(100);
                PotatoWedgsMedium.setLayoutY(27);
                PotatoWedgsMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ويدجز"+" "+PotatoWedgsMedium.getText(), 18, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoWedgsStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    PotatoWedgsStage.close();
                });

                PotatoWedgsPane.getChildren().addAll(PotatoWedgsSmall,PotatoWedgsMedium,Done);

                Scene PotatoWedgsScene = new Scene(PotatoWedgsPane, 600, 400);

                PotatoWedgsScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                PotatoWedgsStage.setScene(PotatoWedgsScene);
                PotatoWedgsStage.show();
            });

            Button PotatoCarly = new Button("بطاطا كارلي");
            PotatoCarly.setPrefHeight(50);
            PotatoCarly.setPrefWidth(100);
            PotatoCarly.setLayoutX(284);
            PotatoCarly.setLayoutY(14);

            PotatoCarly.setOnAction(r->{
                Stage PotatoCarlyStage = new Stage();
                Pane PotatoCarlyPane = new Pane();
                Button PotatoCarlySmall = new Button("S");
                PotatoCarlySmall.setPrefHeight(50);
                PotatoCarlySmall.setPrefWidth(100);
                PotatoCarlySmall.setLayoutX(391);
                PotatoCarlySmall.setLayoutY(27);
                PotatoCarlySmall.setOnAction(t->{
                    tableISales.getItems().add(new Items(" كارلي"+" "+PotatoCarlySmall.getText(), 12, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoCarlyStage.close();
                });

                Button PotatoCarlyMedium = new Button("L");
                PotatoCarlyMedium.setPrefHeight(50);
                PotatoCarlyMedium.setPrefWidth(100);
                PotatoCarlyMedium.setLayoutX(100);
                PotatoCarlyMedium.setLayoutY(27);
                PotatoCarlyMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" كارلي"+" "+PotatoCarlyMedium.getText(), 18, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    PotatoCarlyStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    PotatoCarlyStage.close();
                });

                PotatoCarlyPane.getChildren().addAll(PotatoCarlySmall,PotatoCarlyMedium,Done);

                Scene PotatoCarlyScene = new Scene(PotatoCarlyPane, 600, 400);

                PotatoCarlyScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                PotatoCarlyStage.setScene(PotatoCarlyScene);
                PotatoCarlyStage.show();

            });


            PotatoPane.getChildren().addAll(PotatoNormal,PotatoWedgs,PotatoCarly);
            hbox.getChildren().add(PotatoPane);


        });

        Button appetizers = new Button("المقبلات");
        appetizers.setPrefHeight(50);
        appetizers.setPrefWidth(100);
        appetizers.setLayoutX(709);
        appetizers.setLayoutY(143);
        appetizers.setOnAction(e->{
            hbox.getChildren().clear();
            Pane appetizersPane = new Pane();

            Button ChickenFinder = new Button("اصابع دجاج");
            ChickenFinder.setPrefHeight(50);
            ChickenFinder.setPrefWidth(100);
            ChickenFinder.setLayoutX(504);
            ChickenFinder.setLayoutY(14);
            ChickenFinder.setOnAction(r->{
                tableISales.getItems().add(new Items(" اصابع دجاج", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });


            Button OnionRings = new Button("حلقات البصل");
            OnionRings.setPrefHeight(50);
            OnionRings.setPrefWidth(100);
            OnionRings.setLayoutX(394);
            OnionRings.setLayoutY(14);
            OnionRings.setOnAction(r->{
                tableISales.getItems().add(new Items(" حلقات البصل", 12, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button garlicbread = new Button("خبز بالثوم");
            garlicbread.setPrefHeight(50);
            garlicbread.setPrefWidth(100);
            garlicbread.setLayoutX(284);
            garlicbread.setLayoutY(14);
            garlicbread.setOnAction(r->{
                tableISales.getItems().add(new Items(" خبز بالثوم", 10, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button garlicbreadCheese = new Button("خبز بالثوم"+ "\n" +"والجبنة");
            garlicbreadCheese.setPrefHeight(50);
            garlicbreadCheese.setPrefWidth(100);
            garlicbreadCheese.setLayoutX(174);
            garlicbreadCheese.setLayoutY(14);
            garlicbreadCheese.setOnAction(r->{
                tableISales.getItems().add(new Items("خبز بالثوم"+ "\n" +"والجبنة", 15, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button MozzarellaFingers = new Button("اصابع"+ "\n" +"الموزريلا");
            MozzarellaFingers.setPrefHeight(50);
            MozzarellaFingers.setPrefWidth(100);
            MozzarellaFingers.setLayoutX(64);
            MozzarellaFingers.setLayoutY(14);
            MozzarellaFingers.setOnAction(r->{
                tableISales.getItems().add(new Items("اصابع"+ "\n" +"الموزريلا", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button ballCheese = new Button("كرات الجبنة");
            ballCheese.setPrefHeight(50);
            ballCheese.setPrefWidth(100);
            ballCheese.setLayoutX(504);
            ballCheese.setLayoutY(75);
            ballCheese.setOnAction(r->{
                tableISales.getItems().add(new Items(" كرات الجبنة", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button ballPotato = new Button("كرات البطاطا");
            ballPotato.setPrefHeight(50);
            ballPotato.setPrefWidth(100);
            ballPotato.setLayoutX(394);
            ballPotato.setLayoutY(75);
            ballPotato.setOnAction(r->{
                tableISales.getItems().add(new Items(" كرات البطاطا", 12, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            appetizersPane.getChildren().addAll(ChickenFinder,OnionRings,garlicbread,garlicbreadCheese,MozzarellaFingers,ballCheese,ballPotato);
            hbox.getChildren().add(appetizersPane);


        });

        Button Wings = new Button("الاجنحة");
        Wings.setPrefHeight(50);
        Wings.setPrefWidth(100);
        Wings.setLayoutX(599);
        Wings.setLayoutY(143);

        Wings.setOnAction(e->{
            hbox.getChildren().clear();
            Pane WingsPane = new Pane();

            Button Wingshot = new Button("اجنحة حار");
            Wingshot.setPrefHeight(50);
            Wingshot.setPrefWidth(100);
            Wingshot.setLayoutX(504);
            Wingshot.setLayoutY(14);
            Wingshot.setOnAction(r->{
                tableISales.getItems().add(new Items(" اجنحة حار", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button WingsBBQ = new Button("اجنحة باربكيو");
            WingsBBQ.setPrefHeight(50);
            WingsBBQ.setPrefWidth(100);
            WingsBBQ.setLayoutX(394);
            WingsBBQ.setLayoutY(14);
            WingsBBQ.setOnAction(r->{
                tableISales.getItems().add(new Items(" اجنحة باربكيو", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button WingsSweetChilli = new Button("اجنحة سويت"+ "\n" +"شيلي");
            WingsSweetChilli.setPrefHeight(50);
            WingsSweetChilli.setPrefWidth(100);
            WingsSweetChilli.setLayoutX(284);
            WingsSweetChilli.setLayoutY(14);
            WingsSweetChilli.setOnAction(r->{
                tableISales.getItems().add(new Items("اجنحة سويت"+ "\n" +"شيلي", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button WingsHoney = new Button("اجنحة عسل"+ "\n" +"وخردل");
            WingsHoney.setPrefHeight(50);
            WingsHoney.setPrefWidth(100);
            WingsHoney.setLayoutX(174);
            WingsHoney.setLayoutY(14);
            WingsHoney.setOnAction(r->{
                tableISales.getItems().add(new Items("اجنحة عسل"+ "\n" +"وخردل", 22, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            WingsPane.getChildren().addAll(Wingshot,WingsBBQ,WingsSweetChilli,WingsHoney);
            hbox.getChildren().add(WingsPane);


        });

        Button Salads = new Button("السلطات");
        Salads.setPrefHeight(50);
        Salads.setPrefWidth(100);
        Salads.setLayoutX(489);
        Salads.setLayoutY(143);

        Salads.setOnAction(e->{
            hbox.getChildren().clear();
            Pane SaladsPane = new Pane();

            Button CeaserSalad = new Button("سلطة سيزر");
            CeaserSalad.setPrefHeight(50);
            CeaserSalad.setPrefWidth(100);
            CeaserSalad.setLayoutX(504);
            CeaserSalad.setLayoutY(14);
            CeaserSalad.setOnAction(r->{
                tableISales.getItems().add(new Items(" سلطة سيزر", 22, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button GreekSalad = new Button("سلطة يونانية");
            GreekSalad.setPrefHeight(50);
            GreekSalad.setPrefWidth(100);
            GreekSalad.setLayoutX(394);
            GreekSalad.setLayoutY(14);
            GreekSalad.setOnAction(r->{
                tableISales.getItems().add(new Items(" سلطة يونانية", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button TunaSalad = new Button("سلطة"+ "\n" +"مكسيكية");
            TunaSalad.setPrefHeight(50);
            TunaSalad.setPrefWidth(100);
            TunaSalad.setLayoutX(284);
            TunaSalad.setLayoutY(14);
            TunaSalad.setOnAction(r->{
                tableISales.getItems().add(new Items("سلطة"+ "\n" +"مكسيكية", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button jarjerSalad = new Button("سلطة جرجير");
            jarjerSalad.setPrefHeight(50);
            jarjerSalad.setPrefWidth(100);
            jarjerSalad.setLayoutX(174);
            jarjerSalad.setLayoutY(14);
            jarjerSalad.setOnAction(r->{
                tableISales.getItems().add(new Items(" سلطة جرجير", 18, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            SaladsPane.getChildren().addAll(CeaserSalad,GreekSalad,TunaSalad,jarjerSalad);
            hbox.getChildren().add(SaladsPane);


        });

        Button ColdDrinks = new Button("المشروبات"+"\n"+"الباردة");
        ColdDrinks.setPrefHeight(50);
        ColdDrinks.setPrefWidth(100);
        ColdDrinks.setLayoutX(1039);
        ColdDrinks.setLayoutY(208);
        ColdDrinks.setOnAction(e->{
            hbox.getChildren().clear();
            Pane ColdDrinksPane = new Pane();

            Button Cola = new Button("كولا");
            Cola.setPrefHeight(50);
            Cola.setPrefWidth(100);
            Cola.setLayoutX(504);
            Cola.setLayoutY(14);
            Cola.setOnAction(r->{
                Stage ColaStage = new Stage();

                Pane ColaPane = new Pane();
                ColaPane.setPrefHeight(400);
                ColaPane.setPrefWidth(600);

                Button small = new Button("S");
                small.setPrefHeight(50);
                small.setPrefWidth(100);
                small.setLayoutX(391);
                small.setLayoutY(27);

                small.setOnAction(t->{
                    tableISales.getItems().add(new Items(" كولا صغير", 3, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ColaStage.close();
                });


                Button medium = new Button("L");
                medium.setPrefHeight(50);
                medium.setPrefWidth(100);
                medium.setLayoutX(100);
                medium.setLayoutY(27);

                medium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" كولا كبير", 5, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    ColaStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    ColaStage.close();
                });

                ColaPane.getChildren().addAll(small,medium,Done);
                Scene ColaScene = new Scene(ColaPane);
                ColaScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                ColaStage.setScene(ColaScene);
                ColaStage.show();
            });

            Button xl = new Button("XL");
            xl.setPrefHeight(50);
            xl.setPrefWidth(100);
            xl.setLayoutX(394);
            xl.setLayoutY(14);
            xl.setOnAction(r->{
                tableISales.getItems().add(new Items(" XL", 7, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button water = new Button("ماء");
            water.setPrefHeight(50);
            water.setPrefWidth(100);
            water.setLayoutX(284);
            water.setLayoutY(14);
            water.setOnAction(r->{
               Stage waterStage = new Stage();

                Pane waterPane = new Pane();
                waterPane.setPrefHeight(400);
                waterPane.setPrefWidth(600);

                Button small = new Button("S");
                small.setPrefHeight(50);
                small.setPrefWidth(100);
                small.setLayoutX(391);
                small.setLayoutY(27);

                small.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ماء صغير", 3, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    waterStage.close();
                });

                Button medium = new Button("L");
                medium.setPrefHeight(50);
                medium.setPrefWidth(100);
                medium.setLayoutX(100);
                medium.setLayoutY(27);

                medium.setOnAction(t->{
                    tableISales.getItems().add(new Items(" ماء كبير", 5, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    waterStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    waterStage.close();
                });

                waterPane.getChildren().addAll(small,medium,Done);
                Scene waterScene = new Scene(waterPane);
                waterStage.setScene(waterScene);
                waterScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                waterStage.show();


            });


            Button juice = new Button("عصير");
            juice.setPrefHeight(50);
            juice.setPrefWidth(100);
            juice.setLayoutX(174);
            juice.setLayoutY(14);
            juice.setOnAction(r->{
                tableISales.getItems().add(new Items(" عصير", 3, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button labnUp = new Button("لبن اب");
            labnUp.setPrefHeight(50);
            labnUp.setPrefWidth(100);
            labnUp.setLayoutX(64);
            labnUp.setLayoutY(14);
            labnUp.setOnAction(r->{
                tableISales.getItems().add(new Items(" لبن اب", 3, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            ColdDrinksPane.getChildren().addAll(Cola,xl,water,juice,labnUp);
            hbox.getChildren().add(ColdDrinksPane);
        });


        Button croissant = new Button("الكرواسون");
        croissant.setPrefHeight(50);
        croissant.setPrefWidth(100);
        croissant.setLayoutX(931);
        croissant.setLayoutY(208);
        croissant.setOnAction(e->{

        });

        Button plugins = new Button("الاضافات");
        plugins.setPrefHeight(50);
        plugins.setPrefWidth(100);
        plugins.setLayoutX(819);
        plugins.setLayoutY(208);
        plugins.setOnAction(e->{
           hbox.getChildren().clear();
            Pane pluginsPane = new Pane();


           Button ChederCheas = new Button("جبنة تشدر");
            ChederCheas.setPrefHeight(50);
            ChederCheas.setPrefWidth(100);
            ChederCheas.setLayoutX(504);
            ChederCheas.setLayoutY(14);
            ChederCheas.setOnAction(r->{
                tableISales.getItems().add(new Items(" جبنة تشدر", 5, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button Meat = new Button("لحمة 150"+"\n"+"جرام");
            Meat.setPrefHeight(50);
            Meat.setPrefWidth(100);
            Meat.setLayoutX(394);
            Meat.setLayoutY(14);
            Meat.setOnAction(r->{
                tableISales.getItems().add(new Items("لحمة 150"+"\n"+"جرام", 15, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button Mushroom = new Button("تبديل بطاطا");
            Mushroom.setPrefHeight(50);
            Mushroom.setPrefWidth(100);
            Mushroom.setLayoutX(284);
            Mushroom.setLayoutY(14);
            Mushroom.setOnAction(r->{
                tableISales.getItems().add(new Items(" تبديل بطاطا", 5, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });

            Button Cheese = new Button("جبنة على"+"\n"+"البطاطا");
            Cheese.setPrefHeight(50);
            Cheese.setPrefWidth(100);
            Cheese.setLayoutX(174);
            Cheese.setLayoutY(14);
            Cheese.setOnAction(r->{
                tableISales.getItems().add(new Items("جبة على"+"\n"+"البطاطا", 5, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });




            Button stuffedsides = new Button("اطراف بالجبنة");
            stuffedsides.setPrefHeight(50);
            stuffedsides.setPrefWidth(100);
            stuffedsides.setLayoutX(64);
            stuffedsides.setLayoutY(14);
            stuffedsides.setOnAction(r->{
                Stage stuffedsidesStage = new Stage();
                Pane stuffedsidesPane = new Pane();
                stuffedsidesPane.setPrefHeight(600);
                stuffedsidesPane.setPrefWidth(400);


                Button MushroomSmall = new Button("S");
                MushroomSmall.setPrefHeight(50);
                MushroomSmall.setPrefWidth(100);
                MushroomSmall.setLayoutX(420);
                MushroomSmall.setLayoutY(27);
                MushroomSmall.setOnAction(t->{
                    tableISales.getItems().add(new Items("اطراف محشوة"+"\n"+" بالجبنة"+MushroomSmall.getText(), 10, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    stuffedsidesStage.close();
                });

                Button MushroomMedium = new Button("M");
                MushroomMedium.setPrefHeight(50);
                MushroomMedium.setPrefWidth(100);
                MushroomMedium.setLayoutX(261);
                MushroomMedium.setLayoutY(27);
                MushroomMedium.setOnAction(t->{
                    tableISales.getItems().add(new Items("اطراف محشوة"+"\n"+" بالجبنة"+MushroomMedium.getText(), 15, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    stuffedsidesStage.close();
                });

                Button MushroomLarge = new Button("L");
                MushroomLarge.setPrefHeight(50);
                MushroomLarge.setPrefWidth(100);
                MushroomLarge.setLayoutX(83);
                MushroomLarge.setLayoutY(27);
                MushroomLarge.setOnAction(t->{
                    tableISales.getItems().add(new Items("اطراف محشوة"+"\n"+" بالجبنة"+MushroomLarge.getText(), 20, 1, ""));
                    double sumPurchase = 0;
                    for (Items o : SalesItems ) {
                        sumPurchase = o.getTotal() + sumPurchase;

                    }
                    TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
                    stuffedsidesStage.close();
                });

                Button Done = new Button("Done");
                Done.setPrefHeight(50);
                Done.setPrefWidth(100);
                Done.setLayoutX(267);
                Done.setLayoutY(305);
                Done.setOnAction(t->{
                    stuffedsidesStage.close();
                });

                stuffedsidesPane.getChildren().addAll(MushroomSmall,MushroomMedium,MushroomLarge,Done);

                Scene MushroomScene = new Scene(stuffedsidesPane, 600, 400);
                MushroomScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                stuffedsidesStage.setScene(MushroomScene);
                stuffedsidesStage.show();




            });

            Button Checking = new Button("اضافة دجاج");
            Checking.setPrefHeight(50);
            Checking.setPrefWidth(100);
            Checking.setLayoutX(504);
            Checking.setLayoutY(75);
            Checking.setOnAction(r->{
                tableISales.getItems().add(new Items("اضافة دجاج", 10, 1, ""));
                double sumPurchase = 0;
                for (Items o : SalesItems ) {
                    sumPurchase = o.getTotal() + sumPurchase;

                }
                TotalText2.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
            });





            pluginsPane.getChildren().addAll(ChederCheas,Meat,Mushroom,Cheese,stuffedsides ,Checking);
            hbox.getChildren().add(pluginsPane);


        });


        SalesPane.getChildren().addAll(mah11, tableISales, TotalLabel2, TotalText2, Delete1, Clear, pay, TakeAway, Delivery, DineIn, Pizza
                , Burger, Potato, appetizers, Wings, Salads, ColdDrinks, croissant, plugins, hbox , bake,logoutButton);

        SalesPAGE = new Scene(SalesPane, 1150, 600);
        SalesPAGE.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        SalesPAGE.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F1) {
                String selectedItem = String.valueOf(tableISales.getSelectionModel().getSelectedItem());
                if (selectedItem != null) {
                    SalesItems.add(new Items("توصيل", 0, 1, ""));
                }
            }
        });

        SalesPAGE.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F3) {
                String selectedItem = String.valueOf(tableISales.getSelectionModel().getSelectedItem());
                if (selectedItem != null) {
                    if (group.getSelectedToggle() != null){

                        if (group.getSelectedToggle().getUserData().equals("Take Away")) {
                            printTakeAway();
                        } else if (group.getSelectedToggle().getUserData().equals("Delivery")) {
                            printDilivery();
                            Number="";
                        } else if (group.getSelectedToggle().getUserData().equals("In")) {
                            printIn();
                            cash=0;

                        }
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Please Select the type of the order");
                        alert.showAndWait();
                        return;
                    }
                }
            }
        });


//====================================== MainPAGE ======================================


        Pane MainPane = new Pane();
        Image image = new Image("mainPage.jpg");
        ImageView mah12 = new ImageView(image);
        mah12.setFitHeight(600);
        mah12.setFitWidth(1150);

        Button MainSales = new Button("الصفحة الرئيسية");
        MainSales.setPrefHeight(104);
        MainSales.setPrefWidth(220);
        MainSales.setLayoutX(465);
        MainSales.setLayoutY(152);
        MainSales.setOnAction(e->{
            stage.setScene(SalesPAGE);
        });


        Button ff = new Button("المبيعات اليومية");
        ff.setPrefHeight(104);
        ff.setPrefWidth(220);
        ff.setLayoutX(465);
        ff.setLayoutY(269);
        ff.setOnAction(e->{
            stage.setScene(DaySalesPAGE);
            double sumPurchase = 0;
            for (DaySales o : daySales ) {
                sumPurchase = o.getTotal() + sumPurchase;
            }
            TotalText.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));

        });


        Button r = new Button("المبيعات الشهرية");
        r.setPrefHeight(104);
        r.setPrefWidth(220);
        r.setLayoutX(465);
        r.setLayoutY(386);

        r.setOnAction(e->{
            stage.setScene(MonthSalesPAGE);
            double sumPurchase = 0;
            for (MonthSales o : monthSales ) {
                sumPurchase = o.getTotal() + sumPurchase;

            }
            TotalText1.setText(NumberFormat.getCurrencyInstance().format(sumPurchase));
        });




        MainPane.getChildren().addAll(mah12, ff, r, MainSales);
        MainPAGE = new Scene(MainPane, 1150, 600);
        MainPAGE.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

//==============================SignupPAGE=====================================
        Image Regpageimage = new Image("signup.jpg");
        ImageView RegBackImage = new ImageView(Regpageimage);
        RegBackImage.setFitHeight(563);
        RegBackImage.setFitWidth(900);


        TextField userName = new TextField();
        userName.setPrefSize(175, 28);
        userName.setLayoutX(563);
        userName.setLayoutY(198);
        userName.setPromptText("Enter a UserName");


        PasswordField password = new PasswordField();
        password.setPrefSize(175, 28);
        password.setLayoutX(563);
        password.setLayoutY(255);
        password.setPromptText("Enter a Password");

        PasswordField choiceBox = new PasswordField();
        choiceBox.setLayoutX(563);
        choiceBox.setLayoutY(310);
        choiceBox.setPrefSize(175, 28);
        choiceBox.setPromptText("Enter The Secret Code");
        choiceBox.setStyle("-fx-background-color: transparent");


        Hyperlink backLoginLabel = new Hyperlink("Back to Login !");
        backLoginLabel.setLayoutX(594);
        backLoginLabel.setLayoutY(418);
        backLoginLabel.setFont(javafx.scene.text.Font.font("Barlow Condensed", 12));
        backLoginLabel.setTextFill(Color.WHITE);
        backLoginLabel.setOnAction(e -> {
            userName.clear();
            password.clear();

            stage.setScene(LoginPAGE);
        });

        Button Register = new Button("Sign Up", new ImageView("key.png"));
        Register.setPrefSize(162, 34);
        Register.setLayoutX(556);
        Register.setLayoutY(371);
        Register.setContentDisplay(ContentDisplay.LEFT);
        Register.setOnAction(e->{
            int flag= 0;
            if(userName.getText().isEmpty() || password.getText().isEmpty() || choiceBox.getText().isEmpty()){
                //Alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please Enter All The Information");
                alert.showAndWait();
                return;
            }

            try {

                Connection con = db.getConnection().connectDB();
                String sql = "Select * from login where username = '" + userName.getText() + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (!rs.next()) {
                    flag = 1;
                    System.out.println("Username is available");
                }
                con.close();

            } catch (Exception e2) {
                e2.getMessage();
            }
            if(flag == 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Username is already taken");
                alert.showAndWait();
                return;
            }else {
                if (choiceBox.getText().equals("nimer")) {
                    try {
                        Connection con = db.getConnection().connectDB();
                        String sql = "INSERT INTO login (username, password) VALUES ('" + userName.getText() + "', '" + password.getText() + "')";
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(sql);
                        con.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Success");
                        alert.setContentText("You have successfully registered");
                        alert.showAndWait();
                        userName.clear();
                        password.clear();
                        choiceBox.clear();
                    } catch (Exception e1) {
                        e1.getMessage();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Wrong Secret Code");
                    alert.showAndWait();
                    return;
                }
            }

        });


        Pane RegEPane = new Pane();
        RegEPane.getChildren().addAll(RegBackImage, userName, password, choiceBox, Register, backLoginLabel);
        SiginUpPAGE = new Scene(RegEPane, 900, 563);
        SiginUpPAGE.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
//==============================Fake Days Sales =====================================



        stage.setTitle("Mushroom");
        stage.setScene(LoginPAGE);
        // add image to the stage
        stage.getIcons().add(new Image("logon.png"));
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}