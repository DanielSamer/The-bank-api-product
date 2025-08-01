package com.mudson.The_bank_api_product.service.Impl; // Package declaration

import com.itextpdf.text.*; // iText library for PDF creation
import com.itextpdf.text.pdf.*; // iText library for PDF tables and advanced features
import com.mudson.The_bank_api_product.entity.Transaction; // Transaction entity
import com.mudson.The_bank_api_product.entity.User; // User entity
import com.mudson.The_bank_api_product.repository.TransactionRepository; // Repository for transactions
import com.mudson.The_bank_api_product.repository.UserRepository; // Repository for users
import lombok.extern.slf4j.Slf4j; // For logging
import org.springframework.stereotype.Component; // Marks this class as a Spring component

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for generating professional bank statements as PDF files.
 * The statement includes account info, transaction history, and a summary section.
 */
@Component
@Slf4j
public class BankStatementImp implements BankStatement {

    // Constants for file naming, bank info, and colors used in the PDF
    private static final String FILE_PREFIX = "BankStatement_"; // Prefix for PDF file name
    private static final String FILE_SUFFIX = ".pdf"; // Suffix for PDF file name
    private static final String BANK_NAME = "SUEZ CANAL BANK"; // Bank name
    private static final String BANK_LOCATION = "Head Office – Egypt"; // Bank location
    private static final String FOOTER_TEXT = "Thank you for banking with Suez Canal Bank. For inquiries, contact support@suezcanalbank.com."; // Footer message
    private static final String STATEMENT_TITLE = "Account Statement"; // Title for the statement
    private static final BaseColor SCB_BLUE = new BaseColor(0, 51, 102); // Main bank blue color
    private static final BaseColor SCB_LIGHT_BLUE = new BaseColor(77, 166, 255); // Light blue for headers
    private static final BaseColor SCB_BG = new BaseColor(244, 244, 244); // Background color
    private static final BaseColor TEXT_DARK = new BaseColor(51, 51, 51); // Dark text color
    private static final BaseColor ROW_ALT = new BaseColor(230, 240, 255); // Alternate row color

    private final TransactionRepository transactionRepository; // Used to fetch transactions from the database
    private final UserRepository userRepository; // Used to fetch user data from the database

    public BankStatementImp(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository; // Assign the transaction repository
        this.userRepository = userRepository; // Assign the user repository
    }

    /**
     * Generates a PDF bank statement for the given account number.
     * @param accountNumber the account number
     * @return list of transactions for the account
     */
    @Override
    public List<Transaction> generateStatement(String accountNumber) {
        try {
            User currentUser = userRepository.findByAccountNumber(accountNumber); // Fetch user by account number
            if (currentUser == null) {
                throw new IllegalArgumentException("Account not found: " + accountNumber); // Error if user not found
            }

            String customerName = currentUser.getFirstName() + " " + currentUser.getLastName(); // Get full name
            List<Transaction> transactionList = transactionRepository.findAll()
                    .stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber)) // Only transactions for this account
                    .collect(Collectors.toList());

            String FILE = FILE_PREFIX + accountNumber + FILE_SUFFIX; // File name for the PDF
            String statementDate = new SimpleDateFormat("dd MMM yyyy").format(new Date()); // Today's date

            try (OutputStream outputStream = new FileOutputStream(FILE)) { // Open file output stream
                Document document = new Document(PageSize.A4, 50, 50, 50, 50); // Create A4 PDF document with margins
                PdfWriter.getInstance(document, outputStream); // Attach writer to document
                document.open(); // Open document for writing

                // --- Logo Placeholder ---
                try {
                    String logoPath = "C:\\Daniel\\Internships\\Suez Canal Bank Summer Internship 2025\\The-bank-api-product\\suez.jpg"; // Path to logo image
                    Image logo = Image.getInstance(logoPath); // Load logo image
                    logo.scaleToFit(100, 100); // Resize logo
                    logo.setAlignment(Element.ALIGN_LEFT); // Align logo to left
                    document.add(logo); // Add logo to document
                    document.add(Chunk.NEWLINE); // Add space after logo
                } catch (Exception e) {
                    e.printStackTrace(); // Print error if logo fails
                }

                // --- Bank Header ---
                PdfPTable bankInfo = new PdfPTable(1); // Placeholder for bank info (currently empty)
                document.add(bankInfo); // Add bank info table
                document.add(Chunk.NEWLINE); // Add space

                // --- Statement Title and Date ---
                Paragraph statementTitle = new Paragraph(STATEMENT_TITLE, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SCB_BLUE)); // Title
                statementTitle.setAlignment(Element.ALIGN_CENTER); // Center title
                document.add(statementTitle); // Add title
                Paragraph datePara = new Paragraph("Statement Date: " + statementDate, new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_DARK)); // Date
                datePara.setAlignment(Element.ALIGN_CENTER); // Center date
                document.add(datePara); // Add date
                document.add(Chunk.NEWLINE); // Add space

                // --- Account Info Table ---
                PdfPTable accountInfo = new PdfPTable(2); // Table with 2 columns
                accountInfo.setWidthPercentage(100); // Full width
                accountInfo.setSpacingBefore(10f); // Space before
                accountInfo.setSpacingAfter(10f); // Space after
                accountInfo.addCell(createLabelCell("Account Holder:", true)); // Label cell
                accountInfo.addCell(createValueCell(customerName)); // Value cell
                accountInfo.addCell(createLabelCell("Account Number:", true)); // Label cell
                accountInfo.addCell(createValueCell(accountNumber)); // Value cell
                accountInfo.addCell(createLabelCell("Email:", true)); // Label cell
                accountInfo.addCell(createValueCell(currentUser.getEmail())); // Value cell
                document.add(accountInfo); // Add account info table

                // --- Summary Section ---
                BigDecimal totalCredit = transactionList.stream()
                        .filter(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType())) // Only credits
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum credits
                BigDecimal totalDebit = transactionList.stream()
                        .filter(t -> "DEBIT".equalsIgnoreCase(t.getTransactionType())) // Only debits
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum debits
                BigDecimal closingBalance = currentUser.getAccountBalance(); // Current balance

                PdfPTable summaryTable = new PdfPTable(2); // Table for summary
                summaryTable.setWidthPercentage(100); // Full width
                summaryTable.setSpacingBefore(10f); // Space before
                summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT); // Align left
                summaryTable.addCell(createLabelCell("Total Credits:", true)); // Label
                summaryTable.addCell(createValueCell(totalCredit.toString())); // Value
                summaryTable.addCell(createLabelCell("Total Debits:", true)); // Label
                summaryTable.addCell(createValueCell(totalDebit.toString())); // Value
                summaryTable.addCell(createLabelCell("Closing Balance:", true)); // Label
                summaryTable.addCell(createValueCell(closingBalance.toString())); // Value
                document.add(summaryTable); // Add summary table
                document.add(Chunk.NEWLINE); // Add space

                // --- Transactions Table ---
                PdfPTable txnTable = new PdfPTable(3); // Table with 3 columns
                txnTable.setWidthPercentage(100); // Full width
                txnTable.setSpacingBefore(10f); // Space before
                txnTable.setWidths(new float[]{2, 2, 2}); // Set column widths
                txnTable.addCell(createHeaderCell("Type", SCB_LIGHT_BLUE)); // Header cell
                txnTable.addCell(createHeaderCell("Amount", SCB_LIGHT_BLUE)); // Header cell
                txnTable.addCell(createHeaderCell("Status", SCB_LIGHT_BLUE)); // Header cell

                boolean alternate = false; // Used for alternating row colors
                for (Transaction txn : transactionList) { // For each transaction
                    BaseColor bg = alternate ? ROW_ALT : BaseColor.WHITE; // Alternate row color
                    String type = txn.getTransactionType() != null ? txn.getTransactionType() : "N/A"; // Transaction type
                    String amount = txn.getAmount() != null ? txn.getAmount().toString() : "0.00"; // Amount
                    String status = txn.getStatus() != null ? txn.getStatus() : "-"; // Status
                    txnTable.addCell(createBodyCell(type, bg)); // Add type cell
                    txnTable.addCell(createBodyCell(amount, bg)); // Add amount cell
                    txnTable.addCell(createBodyCell(status, bg)); // Add status cell
                    alternate = !alternate; // Switch color for next row
                }
                document.add(txnTable); // Add transactions table

                // --- Footer ---
                document.add(Chunk.NEWLINE); // Add space
                Paragraph thanks = new Paragraph(FOOTER_TEXT, new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, TEXT_DARK)); // Footer text
                thanks.setSpacingBefore(30f); // Space before footer
                thanks.setAlignment(Element.ALIGN_CENTER); // Center footer
                document.add(thanks); // Add footer

                document.close(); // Close the PDF document
                log.info("PDF generated successfully: {}", FILE); // Log success
            }
            return transactionList; // Return the list of transactions
        } catch (Exception e) {
            log.error("Failed to generate PDF statement: {}", e.getMessage(), e); // Log error
            throw new RuntimeException("Failed to generate PDF statement: " + e.getMessage(), e); // Throw error
        }
    }

    // --- Styling Helpers ---
    // These methods help create styled table cells for the PDF
    private PdfPCell createLabelCell(String text, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, bold ? Font.BOLD : Font.NORMAL, TEXT_DARK); // Set font style
        PdfPCell cell = new PdfPCell(new Phrase(text, font)); // Create cell with text
        cell.setBorder(Rectangle.NO_BORDER); // No border
        cell.setPadding(6f); // Padding
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_DARK); // Set font style
        PdfPCell cell = new PdfPCell(new Phrase(text, font)); // Create cell with text
        cell.setBorder(Rectangle.NO_BORDER); // No border
        cell.setPadding(6f); // Padding
        return cell;
    }

    private PdfPCell createHeaderCell(String text, BaseColor bgColor) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, TEXT_DARK); // Bold font for header
        PdfPCell cell = new PdfPCell(new Phrase(text, font)); // Create cell with text
        cell.setBackgroundColor(bgColor); // Set background color
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center text
        cell.setPadding(10f); // Padding
        return cell;
    }

    private PdfPCell createBodyCell(String text, BaseColor bgColor) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_DARK); // Normal font
        PdfPCell cell = new PdfPCell(new Phrase(text, font)); // Create cell with text
        cell.setBackgroundColor(bgColor); // Set background color
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center text
        cell.setPadding(8f); // Padding
        return cell;
    }
}
