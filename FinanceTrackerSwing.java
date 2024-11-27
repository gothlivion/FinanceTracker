import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class FinanceTrackerSwing {
    static class Transaction {
        String type;
        double amount;
        String description;
        String date;

        Transaction(String type, double amount, String description, String date) {
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.date = date;
        }

        @Override
        public String toString() {
            return type + ";" + amount + ";" + description + ";" + date;
        }
    }

    private static final String DATA_FILE = "finance_data.csv";
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        loadTransactions();

        JFrame frame = new JFrame("Finance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Table
        String[] columnNames = {"Type", "Summe", "Beschreibung", "Datum"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JButton addIncomeButton = new JButton("Einnahme hinzufügen");
        JButton addExpenseButton = new JButton("Ausgabe hinzufügen");
        JButton deleteButton = new JButton("Transaktion löschen");
        JButton analyzeButton = new JButton("Finanzen Analyze");
        JButton exportButton = new JButton("Export to CSV");

        // Panel
        JPanel panel = new JPanel();
        panel.add(addIncomeButton);
        panel.add(addExpenseButton);
        panel.add(deleteButton);
        panel.add(analyzeButton);
        panel.add(exportButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        // Action Listeners
        addIncomeButton.addActionListener(e -> addTransaction("Eingang"));
        addExpenseButton.addActionListener(e -> addTransaction("Ausgabe"));
        deleteButton.addActionListener(e -> deleteTransaction(table.getSelectedRow()));
        analyzeButton.addActionListener(e -> analyzeFinances());
        exportButton.addActionListener(e -> exportToCSV());

        frame.setVisible(true);
    }

    private static void addTransaction(String type) {
        JTextField amountField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField dateField = new JTextField(); // Beispiel-Format für Datum
        Object[] message = {
                "Summe:", amountField,
                "Beschreibung:", descriptionField,
                "Datum (dd-MM-yyyy):", dateField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add " + type, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                String date = dateField.getText();
                transactions.add(new Transaction(type, amount, description, date));
                exportToCSV(); // Transaktion sofort speichern
                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Falsche Eingabe", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteTransaction(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < transactions.size()) {
            transactions.remove(rowIndex);
            exportToCSV(); // CSV-Datei nach Löschen aktualisieren
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie eine Transaktion zum löschen aus.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void analyzeFinances() {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.type.equals("Eingang")) {
                totalIncome += t.amount;
            } else if (t.type.equals("Ausgabe")) {
                totalExpense += t.amount;
            }
        }

        String message = String.format("Einkommen insgesamt: %.2f\nAusgaben insgesamt: %.2f\nSaldo: %.2f",
                totalIncome, totalExpense, totalIncome - totalExpense);
        JOptionPane.showMessageDialog(null, message, "Financzen Analyze", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write("Type,Summe,Beschreibung,Datum"); // Header schreiben
            writer.newLine();
            for (Transaction t : transactions) {
                writer.write(t.type + "," + t.amount + "," + t.description + "," + t.date);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "Data exported to " + DATA_FILE, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadTransactions() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved data found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine(); // Überspringe den Header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    transactions.add(new Transaction(parts[0], Double.parseDouble(parts[1]), parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{t.type, t.amount, t.description, t.date});
        }
    }
}



