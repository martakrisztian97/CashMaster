package view;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Pénzrendező kalkulátor felülete.
 * @author Márta Krisztián
 * @since 2023-05-20
 */
public class CashMaster extends javax.swing.JFrame {
    
    private int AmountOfCoins; // érmék összege
    private int myMoneyOfWallet2; // a pénztárca 2.-ből a saját részem
    private int revolutBalance; // összegzett Revolut egyenleg
    private int balance; // egyenleg
    private RandomAccessFile raf;
    private List<String> balances; // .csv-ből beolvasott egyenlegek
    private DefaultTableModel dtm;

    /**
     * Creates new form CashMaster.
     */
    public CashMaster() {
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("D:/Saját programok/CashMaster/CashMaster/src/view/images/creditcard.png"));
        balances = new ArrayList<>();
        dtm = (DefaultTableModel)balanceHistoryTable.getModel();
        formattingTable();
    }
    
    /**
     * Beviteli mező beállítása, hogy csak 7 karaktert lehessen beírni.
     * @param jtextfield Beviteli mező.
     */
    public void only7CharacterInJTextfield(JTextField jtextfield) {
        if (jtextfield.getText().length() > 7) {
            String s = jtextfield.getText().substring(0, 7);
            jtextfield.setText(s);
        }
    }
    
    /**
     * Beviteli mező beállítása, hogy csak számokat lehessen beírni.
     * @param evt Gombesemény.
     */
    public void onlyNumbersInJTextfield(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9') && c != '\b' && c != (char) KeyEvent.VK_ENTER) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "Csak számokat adjon meg!", "Érvénytelen karakter!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Kiszámolja az érmék összegét.
     */
    public void calculateAmountOfCoins() {
        int amount5 = (int)(forint5Spinner.getValue())*5;
        int amount10 = (int)(forint10Spinner.getValue())*10;
        int amount20 = (int)(forint20Spinner.getValue())*20;
        int amount50 = (int)(forint50Spinner.getValue())*50;
        int amount100 = (int)(forint100Spinner.getValue())*100;
        int amount200 = (int)(forint200Spinner.getValue())*200;
        AmountOfCoins = amount5+amount10+amount20+amount50+amount100+amount200;
        coinsTextField.setText(AmountOfCoins+"");
        calculateBalance();
    }
    
    /**
     * Kiszámolja a pénztárca 2.-ből a saját részem.
     */
    public void calculateMyMoneyOfWallet2() {
        String allAmountWallet2Text = allAmountWallet2TextField.getText(); // beviteli mező szövege
        String reductionWallet2Text = reductionWallet2TextField.getText(); // beviteli mező szövege
        int allAmountWallet2Amount, reductionWallet2Amount; // beviteli mező szövegének megfelelő összeg
        
        if (allAmountWallet2Text.isEmpty()) allAmountWallet2Amount = 0;
        else allAmountWallet2Amount = Integer.parseInt(allAmountWallet2Text);
        
        if (reductionWallet2Text.isEmpty()) reductionWallet2Amount = 0;
        else reductionWallet2Amount = Integer.parseInt(reductionWallet2Text);
        
        myMoneyOfWallet2 = allAmountWallet2Amount-reductionWallet2Amount;
        wallet2TextField.setText(myMoneyOfWallet2+"");
        calculateBalance();
    }
    
    /**
     * Kiszámolja az összes Revolut egyenleget.
     */
    public void calculateRevolut() {
        String accountRevolutText = accountRevolutTextField.getText(); // beviteli mező szövege
        String cryptoRevolutText = cryptoRevolutTextField.getText(); // beviteli mező szövege
        int accountRevolutAmount, cryptoRevolutAmount; // beviteli mező szövegének megfelelő összeg
        
        
        if (accountRevolutText.isEmpty()) accountRevolutAmount = 0;
        else accountRevolutAmount = Integer.parseInt(accountRevolutText);
        
        if (cryptoRevolutText.isEmpty()) cryptoRevolutAmount = 0;
        else cryptoRevolutAmount = Integer.parseInt(cryptoRevolutText);
        
        revolutBalance = accountRevolutAmount+cryptoRevolutAmount;
        revolutTextField.setText(revolutBalance+"");
        calculateBalance();
    }
    
    /**
     * Kiszámolja az egyenleget.
     */
    public void calculateBalance() {
        String creditCardText, walletText, otherText, reductionText; // beviteli mezők szövege
        int creditCardAmount, walletAmount, otherAmount, reductionAmount; // beviteli mező szövegének megfelelő összeg
        creditCardText = creditCardTextField.getText();
        walletText = walletTextField.getText();
        otherText = otherTextField.getText();
        reductionText = reductionTextField.getText();
        
        if (creditCardText.isEmpty()) creditCardAmount = 0;
        else creditCardAmount = Integer.parseInt(creditCardText);
        
        if (walletText.isEmpty()) walletAmount = 0;
        else walletAmount = Integer.parseInt(walletText);
        
        if (otherText.isEmpty()) otherAmount = 0;
        else otherAmount = Integer.parseInt(otherText);
        
        if (reductionText.isEmpty()) reductionAmount = 0;
        else reductionAmount = Integer.parseInt(reductionText);
        
        balance = creditCardAmount+walletAmount+myMoneyOfWallet2+AmountOfCoins+revolutBalance+otherAmount-reductionAmount;
        balanceLabel.setText("EGYENLEG: "+String.format("%,d", balance)+" HUF");
    }
    
    /**
     * Szöveggé konvertálja az aktuális időt, yyyy-MM-dd HH:mm:ss formátumban.
     * @return Az aktuális idővel.
     */
    public String dateNow() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = dateFormat.format(date);
        return dateNow;
    }
    
    /**
     * Lementi az ablak képernyőképét .png formátumban.
     */
    public void saveBalanceToIMG() {
        BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.paint(img.getGraphics());
        String filename = dateNow().replace(":", "").replace(" ", "_");
        File outputFile = new File("D:/Saját programok/CashMaster/CashMaster/balances/"+filename+".png");
        try {
            ImageIO.write(img, "png", outputFile);
        } catch (IOException ex) {
            Logger.getLogger(CashMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Lementi a dátumot és az egyenleget egy .csv fájlba.
     */
    public void saveBalanceToCSV() {
        try {
            raf = new RandomAccessFile("D:/Saját programok/CashMaster/CashMaster/balance.csv", "rw");
            raf.seek(raf.length());
            raf.writeBytes(dateNow()+";"+String.format("%,d", balance)+"\r\n");
            raf.close();
        } catch (IOException e) {
            System.out.println("Hiba: " + e.getMessage());
        }
     }
    
    /**
     * Beolvassa az egyenleg előzményeket a .csv fájlból.
     */
    public void readBalancesFromCSVToTable() {
        String row;
        balances.removeAll(balances);
        try {
            raf = new RandomAccessFile("D:/Saját programok/CashMaster/CashMaster/balance.csv", "r");
            row = raf.readLine();
            while (row != null) {
                balances.add(row);
                row = raf.readLine();
            }
            raf.close();
        } catch (IOException e) {
            System.out.println("Hiba: " + e.getMessage());
        }
    }
    
    /**
     * Táblázat feltöltése idő szerinti csökkenő sorrendben.
     */
    public void fillTable() {
        // táblázat kiűrítése
        for (int i = dtm.getRowCount() - 1; i >= 0; i--) {
            dtm.removeRow(i);
        }
        
        // táblázat feltöltése
        Object[] row = new Object[2];
        for (int i = balances.size()-1; i >= 0; i--) {
            row[0] = balances.get(i).split(";")[0];
            row[1] = balances.get(i).split(";")[1]+" HUF";
            dtm.addRow(row);
        }
    }
    
    /**
     * Táblázat megjelenésének formázása.
     */
    public void formattingTable() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        balanceHistoryTable.getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
        balanceHistoryTable.getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
        balanceHistoryTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        balanceHistoryTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane = new javax.swing.JTabbedPane();
        calculatorPanel = new javax.swing.JPanel();
        logoLabel1 = new javax.swing.JLabel();
        mainItemsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        creditCardTextField = new javax.swing.JTextField();
        walletTextField = new javax.swing.JTextField();
        wallet2TextField = new javax.swing.JTextField();
        coinsTextField = new javax.swing.JTextField();
        revolutTextField = new javax.swing.JTextField();
        otherTextField = new javax.swing.JTextField();
        reductionTextField = new javax.swing.JTextField();
        clearMainItemsButton = new javax.swing.JButton();
        wallet2Panel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        allAmountWallet2TextField = new javax.swing.JTextField();
        reductionWallet2TextField = new javax.swing.JTextField();
        clearWallet2Button = new javax.swing.JButton();
        coinsPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        forint5Spinner = new javax.swing.JSpinner();
        forint10Spinner = new javax.swing.JSpinner();
        forint20Spinner = new javax.swing.JSpinner();
        forint50Spinner = new javax.swing.JSpinner();
        forint100Spinner = new javax.swing.JSpinner();
        forint200Spinner = new javax.swing.JSpinner();
        toNullCoinsButton = new javax.swing.JButton();
        balanceLabel = new javax.swing.JLabel();
        saveBalanceButton = new javax.swing.JButton();
        revolutPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        accountRevolutTextField = new javax.swing.JTextField();
        cryptoRevolutTextField = new javax.swing.JTextField();
        clearRevolutButton = new javax.swing.JButton();
        balanceHistoryPanel = new javax.swing.JPanel();
        logoLabel2 = new javax.swing.JLabel();
        balanceHistoryScrollPane = new javax.swing.JScrollPane();
        balanceHistoryTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CashMaster, a pénzrendező kalkulátor");
        setMaximumSize(new java.awt.Dimension(800, 950));
        setMinimumSize(new java.awt.Dimension(800, 950));
        setPreferredSize(new java.awt.Dimension(800, 950));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 950));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTabbedPane.setFocusable(false);
        jTabbedPane.setMaximumSize(new java.awt.Dimension(800, 900));
        jTabbedPane.setMinimumSize(new java.awt.Dimension(800, 900));
        jTabbedPane.setPreferredSize(new java.awt.Dimension(800, 900));

        calculatorPanel.setFocusable(false);
        calculatorPanel.setLayout(new java.awt.GridBagLayout());

        logoLabel1.setFont(new java.awt.Font("Magneto", 1, 36)); // NOI18N
        logoLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel1.setText("CashMaster");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        calculatorPanel.add(logoLabel1, gridBagConstraints);

        mainItemsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fő tételek", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16))); // NOI18N
        mainItemsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Bankkártya");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        mainItemsPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Pénztárca");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Pénztárca 2.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Érmék összege");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Revolut");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel5, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Egyéb");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel17, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("Csökkentés");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        mainItemsPanel.add(jLabel6, gridBagConstraints);

        creditCardTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        creditCardTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        creditCardTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        creditCardTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        creditCardTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        creditCardTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                creditCardTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                creditCardTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(creditCardTextField, gridBagConstraints);

        walletTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        walletTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        walletTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        walletTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        walletTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        walletTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                walletTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                walletTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(walletTextField, gridBagConstraints);

        wallet2TextField.setEditable(false);
        wallet2TextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        wallet2TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        wallet2TextField.setText("0");
        wallet2TextField.setFocusable(false);
        wallet2TextField.setMaximumSize(new java.awt.Dimension(150, 40));
        wallet2TextField.setMinimumSize(new java.awt.Dimension(150, 40));
        wallet2TextField.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(wallet2TextField, gridBagConstraints);

        coinsTextField.setEditable(false);
        coinsTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        coinsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        coinsTextField.setText("0");
        coinsTextField.setFocusable(false);
        coinsTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        coinsTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        coinsTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(coinsTextField, gridBagConstraints);

        revolutTextField.setEditable(false);
        revolutTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        revolutTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        revolutTextField.setText("0");
        revolutTextField.setFocusable(false);
        revolutTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        revolutTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        revolutTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(revolutTextField, gridBagConstraints);

        otherTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        otherTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        otherTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        otherTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        otherTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        otherTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                otherTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                otherTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        mainItemsPanel.add(otherTextField, gridBagConstraints);

        reductionTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        reductionTextField.setForeground(new java.awt.Color(255, 0, 0));
        reductionTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        reductionTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        reductionTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        reductionTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        reductionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                reductionTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reductionTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 25);
        mainItemsPanel.add(reductionTextField, gridBagConstraints);

        clearMainItemsButton.setBackground(new java.awt.Color(255, 82, 82));
        clearMainItemsButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        clearMainItemsButton.setForeground(new java.awt.Color(255, 255, 255));
        clearMainItemsButton.setText("TÖRÖL");
        clearMainItemsButton.setBorderPainted(false);
        clearMainItemsButton.setFocusable(false);
        clearMainItemsButton.setMaximumSize(new java.awt.Dimension(140, 40));
        clearMainItemsButton.setMinimumSize(new java.awt.Dimension(140, 40));
        clearMainItemsButton.setPreferredSize(new java.awt.Dimension(140, 40));
        clearMainItemsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMainItemsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        mainItemsPanel.add(clearMainItemsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 10);
        calculatorPanel.add(mainItemsPanel, gridBagConstraints);

        wallet2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pénztárca 2.", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16))); // NOI18N
        wallet2Panel.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Összesen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        wallet2Panel.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("Csökkentés");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        wallet2Panel.add(jLabel8, gridBagConstraints);

        allAmountWallet2TextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        allAmountWallet2TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        allAmountWallet2TextField.setMaximumSize(new java.awt.Dimension(150, 40));
        allAmountWallet2TextField.setMinimumSize(new java.awt.Dimension(150, 40));
        allAmountWallet2TextField.setPreferredSize(new java.awt.Dimension(150, 40));
        allAmountWallet2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                allAmountWallet2TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                allAmountWallet2TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        wallet2Panel.add(allAmountWallet2TextField, gridBagConstraints);

        reductionWallet2TextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        reductionWallet2TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        reductionWallet2TextField.setMaximumSize(new java.awt.Dimension(150, 40));
        reductionWallet2TextField.setMinimumSize(new java.awt.Dimension(150, 40));
        reductionWallet2TextField.setPreferredSize(new java.awt.Dimension(150, 40));
        reductionWallet2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                reductionWallet2TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reductionWallet2TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 25);
        wallet2Panel.add(reductionWallet2TextField, gridBagConstraints);

        clearWallet2Button.setBackground(new java.awt.Color(255, 82, 82));
        clearWallet2Button.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        clearWallet2Button.setForeground(new java.awt.Color(255, 255, 255));
        clearWallet2Button.setText("TÖRÖL");
        clearWallet2Button.setBorderPainted(false);
        clearWallet2Button.setFocusable(false);
        clearWallet2Button.setMaximumSize(new java.awt.Dimension(140, 40));
        clearWallet2Button.setMinimumSize(new java.awt.Dimension(140, 40));
        clearWallet2Button.setPreferredSize(new java.awt.Dimension(140, 40));
        clearWallet2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearWallet2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        wallet2Panel.add(clearWallet2Button, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        calculatorPanel.add(wallet2Panel, gridBagConstraints);

        coinsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Érmék", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16))); // NOI18N
        coinsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/5forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 0, 0);
        coinsPanel.add(jLabel9, gridBagConstraints);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/10forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        coinsPanel.add(jLabel10, gridBagConstraints);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/20forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        coinsPanel.add(jLabel11, gridBagConstraints);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/50forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        coinsPanel.add(jLabel12, gridBagConstraints);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/100forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        coinsPanel.add(jLabel13, gridBagConstraints);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/images/200forint.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        coinsPanel.add(jLabel14, gridBagConstraints);

        forint5Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint5Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint5Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint5Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint5Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint5Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint5SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(25, 10, 10, 25);
        coinsPanel.add(forint5Spinner, gridBagConstraints);

        forint10Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint10Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint10Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint10Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint10Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint10Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint10SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 25);
        coinsPanel.add(forint10Spinner, gridBagConstraints);

        forint20Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint20Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint20Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint20Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint20Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint20Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint20SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 25);
        coinsPanel.add(forint20Spinner, gridBagConstraints);

        forint50Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint50Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint50Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint50Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint50Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint50Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint50SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 25);
        coinsPanel.add(forint50Spinner, gridBagConstraints);

        forint100Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint100Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint100Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint100Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint100Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint100Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint100SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 25);
        coinsPanel.add(forint100Spinner, gridBagConstraints);

        forint200Spinner.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        forint200Spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        forint200Spinner.setMaximumSize(new java.awt.Dimension(80, 40));
        forint200Spinner.setMinimumSize(new java.awt.Dimension(80, 40));
        forint200Spinner.setPreferredSize(new java.awt.Dimension(80, 40));
        forint200Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forint200SpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 25);
        coinsPanel.add(forint200Spinner, gridBagConstraints);

        toNullCoinsButton.setBackground(new java.awt.Color(255, 82, 82));
        toNullCoinsButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        toNullCoinsButton.setForeground(new java.awt.Color(255, 255, 255));
        toNullCoinsButton.setText("LENULLÁZ");
        toNullCoinsButton.setBorderPainted(false);
        toNullCoinsButton.setFocusable(false);
        toNullCoinsButton.setMaximumSize(new java.awt.Dimension(140, 40));
        toNullCoinsButton.setMinimumSize(new java.awt.Dimension(140, 40));
        toNullCoinsButton.setPreferredSize(new java.awt.Dimension(140, 40));
        toNullCoinsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNullCoinsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        coinsPanel.add(toNullCoinsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 25);
        calculatorPanel.add(coinsPanel, gridBagConstraints);

        balanceLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        balanceLabel.setForeground(new java.awt.Color(0, 128, 0));
        balanceLabel.setText("EGYENLEG: 0 HUF");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        calculatorPanel.add(balanceLabel, gridBagConstraints);

        saveBalanceButton.setBackground(new java.awt.Color(0, 128, 0));
        saveBalanceButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        saveBalanceButton.setForeground(new java.awt.Color(255, 255, 255));
        saveBalanceButton.setText("EGYENLEG MENTÉSE");
        saveBalanceButton.setToolTipText("");
        saveBalanceButton.setBorderPainted(false);
        saveBalanceButton.setFocusable(false);
        saveBalanceButton.setMaximumSize(new java.awt.Dimension(200, 50));
        saveBalanceButton.setMinimumSize(new java.awt.Dimension(200, 50));
        saveBalanceButton.setPreferredSize(new java.awt.Dimension(200, 50));
        saveBalanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBalanceButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 25, 0);
        calculatorPanel.add(saveBalanceButton, gridBagConstraints);

        revolutPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Revolut", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16))); // NOI18N
        revolutPanel.setLayout(new java.awt.GridBagLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Számlák");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        revolutPanel.add(jLabel15, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Crypto");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        revolutPanel.add(jLabel16, gridBagConstraints);

        accountRevolutTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        accountRevolutTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        accountRevolutTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        accountRevolutTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        accountRevolutTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        accountRevolutTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                accountRevolutTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                accountRevolutTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 10, 25);
        revolutPanel.add(accountRevolutTextField, gridBagConstraints);

        cryptoRevolutTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cryptoRevolutTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cryptoRevolutTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        cryptoRevolutTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        cryptoRevolutTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        cryptoRevolutTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cryptoRevolutTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cryptoRevolutTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 25);
        revolutPanel.add(cryptoRevolutTextField, gridBagConstraints);

        clearRevolutButton.setBackground(new java.awt.Color(255, 82, 82));
        clearRevolutButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        clearRevolutButton.setForeground(new java.awt.Color(255, 255, 255));
        clearRevolutButton.setText("TÖRÖL");
        clearRevolutButton.setBorderPainted(false);
        clearRevolutButton.setFocusable(false);
        clearRevolutButton.setMaximumSize(new java.awt.Dimension(140, 40));
        clearRevolutButton.setMinimumSize(new java.awt.Dimension(140, 40));
        clearRevolutButton.setPreferredSize(new java.awt.Dimension(140, 40));
        clearRevolutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearRevolutButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        revolutPanel.add(clearRevolutButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        calculatorPanel.add(revolutPanel, gridBagConstraints);
        revolutPanel.getAccessibleContext().setAccessibleName("Revolut");

        jTabbedPane.addTab("Kalkulátor", calculatorPanel);

        balanceHistoryPanel.setFocusable(false);
        balanceHistoryPanel.setLayout(new java.awt.GridBagLayout());

        logoLabel2.setFont(new java.awt.Font("Magneto", 1, 36)); // NOI18N
        logoLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel2.setText("CashMaster");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(25, 25, 25, 25);
        balanceHistoryPanel.add(logoLabel2, gridBagConstraints);

        balanceHistoryScrollPane.setMaximumSize(new java.awt.Dimension(450, 700));
        balanceHistoryScrollPane.setMinimumSize(new java.awt.Dimension(450, 700));
        balanceHistoryScrollPane.setPreferredSize(new java.awt.Dimension(450, 700));

        balanceHistoryTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        balanceHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dátum", "Egyenleg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        balanceHistoryTable.setRowHeight(40);
        balanceHistoryScrollPane.setViewportView(balanceHistoryTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 25, 25);
        balanceHistoryPanel.add(balanceHistoryScrollPane, gridBagConstraints);

        jTabbedPane.addTab("Egyenleg előzmények", balanceHistoryPanel);

        getContentPane().add(jTabbedPane, new java.awt.GridBagConstraints());

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void forint5SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint5SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint5SpinnerStateChanged

    private void forint10SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint10SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint10SpinnerStateChanged

    private void forint20SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint20SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint20SpinnerStateChanged

    private void forint50SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint50SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint50SpinnerStateChanged

    private void forint100SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint100SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint100SpinnerStateChanged

    private void forint200SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_forint200SpinnerStateChanged
        calculateAmountOfCoins();
    }//GEN-LAST:event_forint200SpinnerStateChanged

    private void creditCardTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditCardTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_creditCardTextFieldKeyTyped

    private void walletTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_walletTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_walletTextFieldKeyTyped

    private void otherTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otherTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_otherTextFieldKeyTyped

    private void reductionTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reductionTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_reductionTextFieldKeyTyped

    private void allAmountWallet2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_allAmountWallet2TextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_allAmountWallet2TextFieldKeyTyped

    private void reductionWallet2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reductionWallet2TextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_reductionWallet2TextFieldKeyTyped

    private void creditCardTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditCardTextFieldKeyReleased
        only7CharacterInJTextfield(creditCardTextField);
        calculateBalance();
    }//GEN-LAST:event_creditCardTextFieldKeyReleased

    private void walletTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_walletTextFieldKeyReleased
        only7CharacterInJTextfield(walletTextField);
        calculateBalance();
    }//GEN-LAST:event_walletTextFieldKeyReleased

    private void otherTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otherTextFieldKeyReleased
        only7CharacterInJTextfield(otherTextField);
        calculateBalance();
    }//GEN-LAST:event_otherTextFieldKeyReleased

    private void reductionTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reductionTextFieldKeyReleased
        only7CharacterInJTextfield(reductionTextField);
        calculateBalance();
    }//GEN-LAST:event_reductionTextFieldKeyReleased

    private void allAmountWallet2TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_allAmountWallet2TextFieldKeyReleased
        only7CharacterInJTextfield(allAmountWallet2TextField);
        calculateMyMoneyOfWallet2();
    }//GEN-LAST:event_allAmountWallet2TextFieldKeyReleased

    private void reductionWallet2TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reductionWallet2TextFieldKeyReleased
        only7CharacterInJTextfield(reductionWallet2TextField);
        calculateMyMoneyOfWallet2();
    }//GEN-LAST:event_reductionWallet2TextFieldKeyReleased

    private void clearMainItemsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMainItemsButtonActionPerformed
        creditCardTextField.setText("");
        walletTextField.setText("");
        otherTextField.setText("");
        reductionTextField.setText("");
        creditCardTextField.requestFocus();
        calculateBalance();
    }//GEN-LAST:event_clearMainItemsButtonActionPerformed

    private void clearWallet2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearWallet2ButtonActionPerformed
        allAmountWallet2TextField.setText("");
        reductionWallet2TextField.setText("");
        allAmountWallet2TextField.requestFocus();
        calculateMyMoneyOfWallet2();
    }//GEN-LAST:event_clearWallet2ButtonActionPerformed

    private void toNullCoinsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toNullCoinsButtonActionPerformed
        forint5Spinner.setValue(0);
        forint10Spinner.setValue(0);
        forint20Spinner.setValue(0);
        forint50Spinner.setValue(0);
        forint100Spinner.setValue(0);
        forint200Spinner.setValue(0);
    }//GEN-LAST:event_toNullCoinsButtonActionPerformed

    private void saveBalanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBalanceButtonActionPerformed
        saveBalanceToIMG();
        saveBalanceToCSV();
        readBalancesFromCSVToTable();
        fillTable();
    }//GEN-LAST:event_saveBalanceButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        readBalancesFromCSVToTable();
        fillTable();
    }//GEN-LAST:event_formWindowOpened

    private void accountRevolutTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountRevolutTextFieldKeyReleased
        only7CharacterInJTextfield(accountRevolutTextField);
        calculateRevolut();
    }//GEN-LAST:event_accountRevolutTextFieldKeyReleased

    private void accountRevolutTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountRevolutTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_accountRevolutTextFieldKeyTyped

    private void cryptoRevolutTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cryptoRevolutTextFieldKeyReleased
        only7CharacterInJTextfield(cryptoRevolutTextField);
        calculateRevolut();
    }//GEN-LAST:event_cryptoRevolutTextFieldKeyReleased

    private void cryptoRevolutTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cryptoRevolutTextFieldKeyTyped
        onlyNumbersInJTextfield(evt);
    }//GEN-LAST:event_cryptoRevolutTextFieldKeyTyped

    private void clearRevolutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearRevolutButtonActionPerformed
        accountRevolutTextField.setText("");
        cryptoRevolutTextField.setText("");
        accountRevolutTextField.requestFocus();
        calculateRevolut();
    }//GEN-LAST:event_clearRevolutButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CashMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashMaster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CashMaster().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accountRevolutTextField;
    private javax.swing.JTextField allAmountWallet2TextField;
    private javax.swing.JPanel balanceHistoryPanel;
    private javax.swing.JScrollPane balanceHistoryScrollPane;
    private javax.swing.JTable balanceHistoryTable;
    private javax.swing.JLabel balanceLabel;
    private javax.swing.JPanel calculatorPanel;
    private javax.swing.JButton clearMainItemsButton;
    private javax.swing.JButton clearRevolutButton;
    private javax.swing.JButton clearWallet2Button;
    private javax.swing.JPanel coinsPanel;
    private javax.swing.JTextField coinsTextField;
    private javax.swing.JTextField creditCardTextField;
    private javax.swing.JTextField cryptoRevolutTextField;
    private javax.swing.JSpinner forint100Spinner;
    private javax.swing.JSpinner forint10Spinner;
    private javax.swing.JSpinner forint200Spinner;
    private javax.swing.JSpinner forint20Spinner;
    private javax.swing.JSpinner forint50Spinner;
    private javax.swing.JSpinner forint5Spinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel logoLabel1;
    private javax.swing.JLabel logoLabel2;
    private javax.swing.JPanel mainItemsPanel;
    private javax.swing.JTextField otherTextField;
    private javax.swing.JTextField reductionTextField;
    private javax.swing.JTextField reductionWallet2TextField;
    private javax.swing.JPanel revolutPanel;
    private javax.swing.JTextField revolutTextField;
    private javax.swing.JButton saveBalanceButton;
    private javax.swing.JButton toNullCoinsButton;
    private javax.swing.JPanel wallet2Panel;
    private javax.swing.JTextField wallet2TextField;
    private javax.swing.JTextField walletTextField;
    // End of variables declaration//GEN-END:variables
}
