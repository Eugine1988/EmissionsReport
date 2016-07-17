package myapp;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EmmissionsReport extends Frame implements ActionListener, WindowListener, KeyListener{
    
    // <editor-fold defaultstate="collapsed" desc="Variables">
    
    //Declaring variables for drawing the grid and other objects in the frame/application
    private final int totalX = 30;
    private final int totalY = 30;
    private final ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(); //For holding data in RAM
    private TextField[][] grid;
    private Label lblMainHeading, lblAcceptable, lblConcerning, lblDangerous, lblLegend, lblAddress, lblDate, lblTime, lblRecordLevel, lblGasID1, lblGasID2;
    private Button btnClose, btnExport, btnObstructions, btnCO, btnNO2, btnSO2, btnOpen_Obstructions, btnOpen_CO, btnOpen_NO2, btnOpen_SO2;
    private TextField txtRecordLevel;
    
    //Declaring fonts to be used for different labels and text fields
    Font fntGrid = new Font("Serif", Font.PLAIN, 10);
    Font fntMainHeadingLabel = new Font("Arial Black", Font.BOLD, 27);
    Font fntSideBarLabel = new Font("Candara", Font.BOLD, 16);
    Font fntGasIDLabel = new Font("Serif", Font.BOLD, 14);
    Font fntDefaultLabel = new Font("Serif", Font.PLAIN, 13);
    
    //Declaring enum Gas-Type for destinguishing gas types in application (to use in methods)
    private enum gasType {SO2, NO2, CO, Obstructions};
    
    //Declaring object to use for opening new files
    private final JFileChooser fileChooser = new JFileChooser();
    private final FileNameExtensionFilter filterCSV = new FileNameExtensionFilter("CSV file", "csv");
    private final FileNameExtensionFilter filterRAF = new FileNameExtensionFilter("RAF file", "raf");
    private final File workingDirectory = new File(System.getProperty("user.dir"));
    private String fileName_Obstructions, fileName_CO, fileName_NO2, fileName_SO2;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Initialisation">
    
    public static void main(String[] args) {
        
        EmmissionsReport frame = new EmmissionsReport(); //Declares and instantiates frame object
        frame.run(); //Ecexutes the method called run()
        frame.setLocationRelativeTo(null); //Plces the frame in the center of the screen
    }
    
    private void run() {
        
        startLayout(); //Executes method called startLayout()
        setVisible(true); //Set's the frame/application visible
        this.addWindowListener(this); //Adds action listener to frame
    }
    
    private void startLayout() {
        
        SpringLayout layout = new SpringLayout(); //Instantiate SpringLayout
        setLayout(layout); //Apply the SpringLayout to the frame
        
        setTitle("Ian's Industrial Installation"); //Set title
        this.setSize(785,455); //Set the size of the frame
        
        locateGrid(layout); //Method locates the grid (30 by 30) and sets it to invisible
        locateButtons(layout); //Places all buttons on frame
        locateLabels(layout); //Places all labels o frame
        txtRecordLevel = locateATxtField(layout, txtRecordLevel, 7, "", 530, 45); //Places Rec.Level txt on frame
        this.setBackground(Color.lightGray); //Sets background to light grey
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Locating Methods">
    
    //Will place grid (30 by 30) in frame and make it invisible
    private void locateGrid(SpringLayout _layout) {
        
        grid = new TextField[totalX][totalY]; //Instantiate the grid
        
        for(int x = 0; x < totalX; x++) { //For each row
            for(int y = 0; y < totalY; y++) { //For each column
                
                grid[x][y] = new TextField(3); //Instantiate a text field
                grid[x][y].setFont(fntGrid); //Set the font of textfield
                grid[x][y].setVisible(false); //Make the text field invisible
                grid[x][y].setEditable(false); //Make the text uneditable
                
                add(grid[x][y]); //Add the text field to the frame
                
                //Put Layout settings to the textfield
                _layout.putConstraint(SpringLayout.NORTH, grid[x][y], 80 + x * 18, SpringLayout.NORTH, this);
                _layout.putConstraint(SpringLayout.WEST, grid[x][y], 180 + y * 37, SpringLayout.WEST, this);
            }
        }
    }
    
    //Will place all the buttons on the frame
    private void locateButtons(SpringLayout _layout){
        
        //All the statements below call the locateAButton() method to place a button on the screen
        
        btnClose = locateAButton(_layout, btnClose, "Close", 18, 383, 145, 25);
        btnExport = locateAButton(_layout, btnExport, "Export", 18, 355, 145, 25);
        
        btnObstructions = locateAButton(_layout, btnObstructions, "Obstructions", 18, 319, 106, 25);
        btnCO = locateAButton(_layout, btnCO, "Carbon Monoxide", 18, 291, 106, 25);
        btnNO2 = locateAButton(_layout, btnNO2, "Nitrogen Dioxide", 18, 263, 106, 25);
        btnSO2 = locateAButton(_layout, btnSO2, "Sulphur Dioxide", 18, 235, 106, 25);
        
        btnOpen_Obstructions = locateAButton(_layout, btnOpen_Obstructions, "Open", 126, 319, 37, 25);
        btnOpen_CO = locateAButton(_layout, btnOpen_CO, "Open", 126, 291, 37, 25);
        btnOpen_NO2 = locateAButton(_layout, btnOpen_NO2, "Open", 126, 263, 37, 25);
        btnOpen_SO2 = locateAButton(_layout, btnOpen_SO2, "Open", 126, 235, 37, 25);
    }
    
    //Contains all statements necessary to place one buton on the frame
    private Button locateAButton(SpringLayout _layout, Button _button, String  _buttonCaption, int x, int y, int w, int h) {
        
        _button = new Button(_buttonCaption); //Instantiate a button
        _button.addActionListener(this); //Add action listener
        
        add(_button); //Add a button to the frame
        
        //Put Layout settings to the button
        _layout.putConstraint(SpringLayout.WEST, _button, x, SpringLayout.WEST, this);
        _layout.putConstraint(SpringLayout.NORTH, _button, y, SpringLayout.NORTH, this);
        _button.setPreferredSize(new Dimension(w,h)); //Set the size of button
        
        return _button;
    }
    
    //Will place all the labels on the frame
    private void locateLabels(SpringLayout myLabelLayout){
        
        //All the statements below call the locateALabel() method to place a label on the screen
        
        lblMainHeading = locateALabel(lblMainHeading, "Ian's Industrial Installation", myLabelLayout, fntMainHeadingLabel, 28, 12, 350, 35, Color.lightGray, Color.WHITE);
        
        lblAcceptable = locateALabel(lblAcceptable, "Acceptable", myLabelLayout, fntSideBarLabel, 18, 200, 145, 28, Color.GREEN, Color.WHITE);
        lblConcerning = locateALabel(lblConcerning, "Concerning", myLabelLayout, fntSideBarLabel, 18, 167, 145, 28, Color.ORANGE, Color.WHITE);
        lblDangerous = locateALabel(lblDangerous, "Dangerous", myLabelLayout, fntSideBarLabel, 18, 134, 145, 28, Color.RED, Color.WHITE);
        
        lblLegend = locateALabel(lblLegend, "Legend", myLabelLayout, fntGasIDLabel, 62, 109);
        lblAddress = locateALabel(lblAddress, "Warehouse 7, Industry Pl.", myLabelLayout, fntDefaultLabel, 440, 15);
        lblDate = locateALabel(lblDate, "Date: " + "20/10/2007", myLabelLayout, fntDefaultLabel, 660, 15);
        lblTime = locateALabel(lblTime, "Time: " + "3:00 PM", myLabelLayout, fntDefaultLabel, 660, 45);
        lblRecordLevel = locateALabel(lblRecordLevel, "Recorded Level: ", myLabelLayout, fntDefaultLabel, 425, 45);
        
        lblGasID1 = locateALabel(lblGasID1, "NO2 Levels", myLabelLayout, fntGasIDLabel, 51, 58);
        lblGasID2 = locateALabel(lblGasID2, "(Nitrogen Dioxide)", myLabelLayout, fntGasIDLabel, 31, 78);
    }
    
    //Contains all statements necessary to place one label on the frame
    private Label locateALabel(Label myLabel, String  LabelCaption, SpringLayout myLabelLayout, Font _font, int x, int y){
        
        myLabel = new Label(LabelCaption); //Instantiate a label
        
        add(myLabel); //Add the label to the frame
        
        //Put Layout settings to the label
        myLabelLayout.putConstraint(SpringLayout.WEST, myLabel, x, SpringLayout.WEST, this);
        myLabelLayout.putConstraint(SpringLayout.NORTH, myLabel, y, SpringLayout.NORTH, this);
        myLabel.setFont(_font); //Set the font of the label
        
        return myLabel;
    }
    
    //This overloaded method for placing a label on form accepts additional arguments to set size of label and color of label & text
    private Label locateALabel(Label myLabel, String  LabelCaption, SpringLayout myLabelLayout, Font _font, int x, int y, int x_Size, int y_Size, Color bColor, Color txtColor){
        
        myLabel = new Label(LabelCaption); //Instantiate a label
        
        add(myLabel); //Add the label to the frame
        
        //Put Layout settings to the label
        myLabelLayout.putConstraint(SpringLayout.WEST, myLabel, x, SpringLayout.WEST, this);
        myLabelLayout.putConstraint(SpringLayout.NORTH, myLabel, y, SpringLayout.NORTH, this);
        myLabel.setFont(_font); //Set the font of the label
        
        myLabel.setBackground(bColor); //Set background color to the label
        myLabel.setForeground(txtColor); //Set text color of the label
        myLabel.setAlignment(Label.CENTER); //Set text alignment
        myLabel.setPreferredSize(new Dimension (x_Size, y_Size)); //Set the size of the label
        
        return myLabel;
    }
    
    //Contains all statements necessary to place one text field on the frame
    private TextField locateATxtField(SpringLayout _layout, TextField _txtField, int _width, String  _txtFieldCaption, int x, int y){
        
        _txtField = new TextField(_width); //Instantiate a text field
        _txtField.setText(_txtFieldCaption); //Set the text in the text field
        _txtField.setEditable(false); //Make the text uneditable
        
        add(_txtField); //Add the text field to the frame
        
        //Put Layout settings to the text field
        _layout.putConstraint(SpringLayout.WEST, _txtField, x, SpringLayout.WEST, this);
        _layout.putConstraint(SpringLayout.NORTH, _txtField, y, SpringLayout.NORTH, this);
        
        return _txtField;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Methods">
    
    //Determines if filename given has .raf extension,
    //and calls other methods to load the file
    private void openFileByType(String _fileName, gasType _gasType){
        
        //If the FileChooser "Cancel" option was choosen
        //then the _fileName will be null, in which case do nothing (return)
        if(_fileName == null)
            return;
        
        //If filename contains .raf extension, then read the file using readRAF() method
        //then pass the returned List<List<String>> to updateGrid() method
        if (getFileExtension(_fileName).equals("raf"))
            updateGrid(readRAF("RandomAccessFile.raf"), _gasType);
        
        //Else read file by using readFile() method and pass data into updateGrid() method
        else
            updateGrid(readFile(_fileName), _gasType);
    }
    
    //Opens the file chooser and returns the name of chosen file as string
    private String openFileChooser(String _fileName){
        
        //String fileName;
        fileChooser.setFileFilter(filterRAF); //Set filter for file chooser
        fileChooser.setFileFilter(filterCSV); //Set another filter for file chooser
        fileChooser.setCurrentDirectory(workingDirectory); //Set directory to open file chooser in
        
        int response = fileChooser.showOpenDialog(EmmissionsReport.this); //Open the file chooser box
        
        //If user clicks open button, the chosen file will open,
        //if user clicks cancel button or closes filechooser
        //then this method will return the same string it took as parameter
        if (response == JFileChooser.APPROVE_OPTION)
            _fileName = fileChooser.getSelectedFile().toString();
        
        return _fileName;
    }
    
    //Accepts data as parameter and updates grid based on the data
    private void updateGrid(ArrayList<ArrayList<String>> _data, gasType _gasType){
        
        //For loop updates entire 30 by 30 grid
        for(int x = 0; x < totalX; x++) { //For every row
            for(int y = 0; y < totalY; y++) { //For every column
                
                //Code below inputs the values from RAM to the grid
                if (x < _data.size() && y < _data.get(0).size()) //Updates up to size of _data
                {
                    //Set the cell to visible
                    grid[x][y].setVisible(true);
                    
                    //Store the value in String variable, so that it can be used for mouse listener
                    String valueStr = _data.get(x).get(y);
                    
                    //Convert value to int, so it can be used in a method to get color for the cell
                    int value = Integer.parseInt(valueStr);
                    
                    //Set the color of cell and text color in the cell to a color derrived from method
                    //If value in cell is higher then 0, then color depends on the tada type (CO, SO2, etc)
                    if (value > 0) {
                        Color cellColor = getCellColor(_gasType, value);
                        grid[x][y].setBackground(cellColor);
                        grid[x][y].setForeground(cellColor);
                        
                    //If the value is zero, then don't waste time by running methods, just make the cell white
                    } else {
                        grid[x][y].setBackground(Color.WHITE);
                        grid[x][y].setForeground(Color.WHITE);
                    }
                    
                    //Input value to the cell
                    grid[x][y].setText(_data.get(x).get(y));
                    
                    //Adds Mouse-Listener to each cell
                    grid[x][y].addMouseListener(new MouseAdapter() {
                        
                        @Override public void mouseReleased(MouseEvent e) {
                            //When mouse released, sets txtRecordLevel to value of cell
                            txtRecordLevel.setText(valueStr);
                        }
                        //Functions below have to be implemented/present
                        @Override public void mouseClicked(MouseEvent e) {}
                        @Override public void mousePressed(MouseEvent e) {}
                        @Override public void mouseEntered(MouseEvent e) {}
                        @Override public void mouseExited(MouseEvent e) {}
                    });
                    
                } else{ //Updates the rest of grid
                    
                    //Make the text-field invisible
                    grid[x][y].setVisible(false);
                }
            }
        }
        
        //After the grid has been updated, change the screen size
        int xSize = 210 + data.get(0).size() * 37; //Calculate & store width based on data
        int ySize = 105 + data.size() * 19; //Calculate & store height based on data
        
        if (xSize > 785 && ySize > 455) //If both x & y calculated size is larger then minimum frame size
            this.setSize(xSize, ySize); //Reset both x & y size to calculated size
        else if (xSize > 785) //If only calculated x size is larger then min width
            this.setSize(xSize, 460); //Change only the width of frame
        else if(ySize > 460) //If only calculated y size is larger then min height
            this.setSize(785, ySize); //Change only the height of frame
        else //If both calculated numbers are less then minimum frame size
            this.setSize(785, 455); //Set frame to minimum size
        
        this.setLocationRelativeTo(null); //Set location of frame to center of screen
    }
    
    //Takes file name (String) as parameter, reads the csv file and returns data
    private ArrayList<ArrayList<String>> readFile(String _fileName) {
        
        //If the data object contains values, clear the object before updating
        if (data.size() > 0)
            data.clear();
        
        //Surround by try catch, because it's going to try read from outside application
        try{
            //Instantiate BufferReader and pass in file name
            BufferedReader br = new BufferedReader(new FileReader(_fileName));
            
            //First 3 lines in file should contain name of organisation, address, date & time
            lblMainHeading.setText(br.readLine()); //Read first line & set label text
            lblAddress.setText(br.readLine()); //Read 2nd line & set label text
            //3rd Line contains Date & Time seperated by comma (,)
            String[] dateAndTime = br.readLine().split(","); //Read 3rd line & record in String[] variable
            lblDate.setText(dateAndTime[0]); //Set label to date derived from file
            lblTime.setText(dateAndTime[1]); //Set label to time derived from file
            
            Boolean check = true; //To be used in while loop
            String line; //To be used for reading a line from file
            
            //Read the rest of lines that contain grid data
            while(check){ //While true
                
                //In the If condition store the line while checking if next line exists
                if(( line = br.readLine() ) != null){ 
                    
                    String[] temp = line.split(","); //Seperate values in line by commas, store in array
                    data.add(new ArrayList<String>(Arrays.asList(temp))); //Convert & add array to list of data
                    
                //If the next line read gave result if null
                } else{
                    check = false; //Set condition to false to stop the loop
                }
            }
        //Catch exceptions from reading a file
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        return data;
    }
    
    //Takes file name (String) as parameter, reads the raf file and returns data
    private ArrayList<ArrayList<String>> readRAF(String _fileName){
        
        //If the data object contains values, clear the object before updating
        if (data.size() > 0)
            data.clear();
        
        //Surround by try catch, because it's going to try read from outside application
        try {
            //Instantiate RandomAccessFile, pass in file name and set as read only
            RandomAccessFile file = new RandomAccessFile(_fileName, "r");
            file.seek(0); //Set the pointer to beginning of file
            
            // <editor-fold defaultstate="collapsed" desc="Reading first 3 lines">
            
            //The first 3 lines are 'CompanyName', 'Address' and 'Date,Time'
            
            lblMainHeading.setText(file.readUTF()); //Read the first data from RAF
            //This for loop function will place the seek/pointer to next data/variable
            for (int i = 0; i < 20 - lblMainHeading.getText().length(); i++)
                file.readByte();
            
            lblAddress.setText(file.readUTF()); //Read the next data from RAF
            //This for loop function will place the seek/pointer to next data/variable
            for (int i = 0; i < 20 - lblAddress.getText().length(); i++)
                file.readByte();
            
            String text[] = file.readUTF().split(","); //Read next data & split string by comma, store in text[]
            lblDate.setText(text[0]); lblTime.setText(text[1]); //Read the next data from RAF
            //This for loop function will place the seek/pointer to next data/variable
            for (int i = 0; i < 20 - (text[0].length() + 1 + text[1].length()); i++)
                file.readByte();
            
            // </editor-fold>
            
            //Next data/variable in RAF file should be int of # of rows, followed by int #columns
            int numbOfRows = file.readInt(); //Read next int variable from RAF
            int numbOfColumns = file.readInt(); //Read next int variable from RAF
            
            //For loop will itirate through numbOfRows * numbOfColumns
            for (int x = 0; x < numbOfRows; x++) {
                
                data.add(new ArrayList<String>()); //Add new List to data
                
                for (int y = 0; y < numbOfColumns; y++) {
                    
                    data.get(x).add(file.readUTF()); //Read next String value from file
                    
                    //This for loop function will place the seek/pointer to next data/variable
                    for (int i = 0; i < 4 - data.get(x).get(y).length(); i++)
                        file.readByte();
                }
            }
            file.close(); //Close the RandomAccessFile
        
        //Catch any exceptionis from reading RAF
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        
        return data;
    }
    
    //Writes RAF file. First 3 lines are 'comp name', 'address', 'date,time'
    //then int 'number of rows', int 'number of columns', then data from grid
    private void writeRAF(){
        
        //Surround by TryCatch, because it's going to write to file outside application
        try {
            //Instantiate RandomAccessFile, pass in file name and set as write
            RandomAccessFile file = new RandomAccessFile("RandomAccessFile.raf", "rw");;
            file.seek(0); //Set the pointer to beginning of file
            
            // <editor-fold defaultstate="collapsed" desc="Writing first 3 lines to RAF">
            
            //The first 3 lines are 'CompanyName', 'Address' and 'Date & Time'
            
            file.writeUTF(lblMainHeading.getText()); //Write the first data to RAF
            //This for loop function will fill in some space with bytes
            for (int i = 0; i < 20 - lblMainHeading.getText().length(); i++)
                file.write(20);
            
            file.writeUTF(lblAddress.getText()); //Write the next data to RAF
            //This for loop function will fill in some space with bytes
            for (int i = 0; i < 20 - lblAddress.getText().length(); i++)
                file.write(20);
            
            //Get and concatinate into a string text from lblDate and lblTime
            String text = lblDate.getText() + "," + lblTime.getText();
            file.writeUTF(text); //Write the next data to RAF
            //This for loop function will fill in some space with bytes
            for (int i = 0; i < 20 - text.length(); i++)
                file.write(20);
            
            
            // </editor-fold>
            
            file.writeInt(data.size()); //Write int # of rows in data to RAF
            file.writeInt(data.get(0).size()); //Write int # of columns in data to RAF
            
            //For loop will go through every value in data
            for(int x = 0; x < data.size(); x++) { //For each row
                for(int y = 0; y < data.get(0).size(); y++) { //For each column
                    
                    //Write value as String to RAF
                    file.writeUTF(data.get(x).get(y));
                    //This for loop function will fill in some space with bytes
                    for (int i = 0; i < 4 - data.get(x).get(y).length(); i++)
                        file.writeByte(4);
                }
            }
            
            //file.writeUTF(GetDataAsString());
            file.close();
            
        //Catch any exceptions
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
    
    //Writes DAT file. First 3 lines are 'comp name', 'address', 'date,time'
    //then int 'number of rows', int 'number of columns', then data from grid
    private void writeDAT(){
        
        //Create a name for the file
        String fileName = "DAT_File.dat";
        
        //Print to file: (x (row#),y (column#),Value#)
        try{
            //Instantiate new stream and pass in the fileName (will create new file)
            PrintWriter outStream = new PrintWriter(fileName);
            
            //Write the first 3 lines (comp name \n address \n date,time) to DAT file
            outStream.write(lblMainHeading.getText() + "\n" +
                            lblAddress.getText() + "\n" +
                            lblDate.getText() + "," + lblTime.getText() + "\n");
            
            //Loop through all values in data
            for(int x = 0; x < data.size(); x++) { //For each row
                for(int y = 0; y < data.get(0).size(); y++) { //For each column
                    
                    //Print to file new line (\n), except at very first loop iteration
                    if(x > 0 || y > 0)
                        outStream.print("\n");
                    
                    //Print to file: x(coordinate),y(coordinate),value
                    outStream.print((x + 1) + "," + (y + 1) + "," + data.get(x).get(y));
                }
            }
            //Close the stream
            outStream.close();
            
        //Catch any exceptions
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
    
    //Writes RPT file. First 3 lines are 'comp name', 'address', 'date,time'
    //then int 'number of rows', int 'number of columns', then data from grid
    private void writeRPT(){
        
        //Create a name for the file
        String fileName = "RPT_File.rpt";
        
        //Print to file: Value#,Amount#,Value2#,Amount#...
        try{
            //Instantiate new stream and pass in the fileName (will create new file)
            PrintWriter outStream = new PrintWriter(fileName);
            
            //Write the first 3 lines (comp name \n address \n date,time) to RPT file
            outStream.write(lblMainHeading.getText() + "\n" +
                            lblAddress.getText() + "\n" +
                            lblDate.getText() + "," + lblTime.getText() + "\n");
            
            //When printing data to file:
            //print in format: Value#,Amount#,Value2#,Amount#...
            
            //For each row in data
            for(int x = 0; x < data.size(); x++) {
                
                //When on new line of data, print new line (\n) to file
                if(x > 0)
                    outStream.print("\n");
                
                //For keeping count of how many similar values there are on each row
                int count = 1;
                
                //Go through each value (y)
                for(int y = 0; y < data.get(0).size(); y++) {
                    
                    //Executes when not the first value in the row
                    if(y > 0){
                        
                        //If curent value does not equal previous y value
                        if (data.get(x).get(y).equals(data.get(x).get(y - 1)))
                        {
                            //Do nothing except add 1 to count
                            count++;
                            
                            //If current value does equal previous y value
                        } else{
                            //Print this statement (e.g. Value#,Amount#,Value2#,Amount#...)
                            outStream.print("," + count + "," + data.get(x).get(y));
                            //Reset the counter back to 1
                            count = 1;
                        }
                        
                        //If y is at the end of the line
                        if(y == data.get(0).size() - 1)
                            //Only print the count number
                            outStream.print("," + count);
                        
                    //This will execute only at beginning of each row
                    } else{
                        //Print the first number of the row
                        outStream.print(data.get(x).get(y));
                    }
                }
            }
            //Close the stream
            outStream.close();
            
        //Catch any errors
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
    
    //Takes gas type and value as parameter and returns color for a cell
    private Color getCellColor(gasType _gasType, int _value){
        
        //Switch on the _gasType parameter given to this method
        //If match found, then call getCellColorHelperMethod to get and return the color,
        //pass in the minimum acceptible, concerning and dangerous levels as int parameters
        switch(_gasType){
            case SO2:
                return getCellColorHelperMethod(_value, 2, 10, 30);
            case NO2:
                return getCellColorHelperMethod(_value, 2, 10, 30);
            case CO:
                return getCellColorHelperMethod(_value, 1, 8, 25);
            case Obstructions:
                return getCellColorHelperMethod(_value, 1, 2, 3);
            default:
                return Color.BLACK;
        }
    }
    
    //Takes value, minimum acceptible, concerning and dangerous levels as int parameters
    //and returns a color based on the parameters
    //This method shortens the repeatable code in the getColor() method
    private Color getCellColorHelperMethod(int _value, int _acceptLevelMin, int _concernLevelMin, int _DangerLevelMin){
        
        //If value is between acceptable minimum level and concerning minimum level (or equal to acceptable)
        if (_value >= _acceptLevelMin && _value < _concernLevelMin) {
            return Color.GREEN; //Return green color
            
        //Else if value is between concerning minimum level and dangerous minimum level (or equal to concerning)
        } else if (_value >= _concernLevelMin && _value < _DangerLevelMin) {
            return Color.YELLOW; //Return yellow color
            
        //Else if value is more than or equal to dangerous level
        } else if (_value >= _DangerLevelMin) {
            return Color.RED; //Return red color
            
        //If value is below acceptable vallue
        } else {
            return Color.WHITE; //Return white
        }
    }
    
    //Takes file name as String and returns the file extension as String (e.g. raf)
    private String getFileExtension(String _filePath){
        
        int dot = _filePath.lastIndexOf("."); //Get the index of where dot (.) is
        return _filePath.substring(dot + 1); //Get and return the file extension
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Events">
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // <editor-fold defaultstate="collapsed" desc="Load-Gas buttons">
        
        //Obstructions button
        if(e.getSource() == btnObstructions) {
            
            //If the fileName doesn't contain a file name
            if (fileName_Obstructions == null)
                //Call method to open file chooser and store returned String to fileName
                fileName_Obstructions = openFileChooser(fileName_Obstructions);
            
            //Call method to open file
            openFileByType(fileName_Obstructions, gasType.Obstructions);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("Obstructions"); lblGasID2.setText("");
        }
        
        //CarbonMonoxide button
        if(e.getSource() == btnCO) {
            
            //If the fileName doesn't contain a file name
            if (fileName_CO == null)
                //Call method to open file chooser and store returned String to fileName
                fileName_CO = openFileChooser(fileName_CO);
            
            //Call method to open file
            openFileByType(fileName_CO, gasType.CO);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("CO Levels"); lblGasID2.setText("(Carbon Monoxide)");
        }
        
        //NitrogenDioxide button
        if(e.getSource() == btnNO2) {
            
            //If the fileName doesn't contain a file name
            if (fileName_NO2 == null)
                //Call method to open file chooser and store returned String to fileName
                fileName_NO2 = openFileChooser(fileName_NO2);
            
            //Call method to open file
            openFileByType(fileName_NO2, gasType.NO2);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("NO2 Levels"); lblGasID2.setText("(Nitrogen Dioxide)");
        }
        
        //SulphurDioxide button
        if(e.getSource() == btnSO2) {
            
            //If the fileName doesn't contain a file name
            if (fileName_SO2 == null)
                //Call method to open file chooser and store returned String to fileName
                fileName_SO2 = openFileChooser(fileName_SO2);
            
            //Call method to open file
            openFileByType(fileName_SO2, gasType.SO2);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("SO2 Levels"); lblGasID2.setText("(Sulphur Dioxide)");
        }
        
        //OpenObstructions button
        if(e.getSource() == btnOpen_Obstructions) {
            
            //Call method to open file chooser and store returned String to fileName
            fileName_Obstructions = openFileChooser(fileName_Obstructions);
            
            //Call method to open file
            openFileByType(fileName_Obstructions, gasType.Obstructions);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("Obstructions"); lblGasID2.setText("");
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Open-File buttons">
        
        //OpenCarbonMonoxide button
        if(e.getSource() == btnOpen_CO) {
            
            //Call method to open file chooser and store returned String to fileName
            fileName_CO = openFileChooser(fileName_CO);
            
            //Call method to open file
            openFileByType(fileName_CO, gasType.CO);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("CO Levels"); lblGasID2.setText("(Carbon Monoxide)");
        }
        
        //OpenNitrogenDioxide button
        if(e.getSource() == btnOpen_NO2) {
            
            //Call method to open file chooser and store returned String to fileName
            fileName_NO2 = openFileChooser(fileName_NO2);
            
            //Call method to open file
            openFileByType(fileName_NO2, gasType.NO2);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("NO2 Levels"); lblGasID2.setText("(Nitrogen Dioxide)");
        }
        
        //OpenSulphurDioxide button
        if(e.getSource() == btnOpen_SO2) {
            
            //Call method to open file chooser and store returned String to fileName
            fileName_SO2 = openFileChooser(fileName_SO2);
            
            //Call method to open file
            openFileByType(fileName_SO2, gasType.SO2);
            
            //Change the labels that display/notify the gas type
            lblGasID1.setText("SO2 Levels"); lblGasID2.setText("(Sulphur Dioxide)");
        }
        
        // </editor-fold>
        
        //Export button
        if(e.getSource() == btnExport) {
            
            writeRAF(); //Write currently displayed data to RAF file
            writeDAT(); //Write currently displayed data to DAT file
            writeRPT(); //Write currently displayed data to RPT file
        }
        
        //Close button
        if(e.getSource() == btnClose) {
            System.exit(0);
        }
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Events not used / have to be implemented">
    
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
    
    // </editor-fold>
    
    // </editor-fold>
}