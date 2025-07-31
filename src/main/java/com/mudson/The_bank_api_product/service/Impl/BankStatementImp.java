package com.mudson.The_bank_api_product.service.Impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mudson.The_bank_api_product.entity.Transaction;
import com.mudson.The_bank_api_product.entity.User;
import com.mudson.The_bank_api_product.repository.TransactionRepository;
import com.mudson.The_bank_api_product.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    private static final String FILE_PREFIX = "BankStatement_";
    private static final String FILE_SUFFIX = ".pdf";
    private static final String BANK_NAME = "SUEZ CANAL BANK";
    private static final String BANK_LOCATION = "Head Office – Egypt";
    private static final String FOOTER_TEXT = "Thank you for banking with Suez Canal Bank. For inquiries, contact support@suezcanalbank.com.";
    private static final String STATEMENT_TITLE = "Account Statement";
    private static final BaseColor SCB_BLUE = new BaseColor(0, 51, 102);          // #003366
    private static final BaseColor SCB_LIGHT_BLUE = new BaseColor(77, 166, 255);  // #4DA6FF
    private static final BaseColor SCB_BG = new BaseColor(244, 244, 244);         // #F4F4F4
    private static final BaseColor TEXT_DARK = new BaseColor(51, 51, 51);         // #333333
    private static final BaseColor ROW_ALT = new BaseColor(230, 240, 255);        // Light blue for alternating rows

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public BankStatementImp(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generates a PDF bank statement for the given account number.
     * @param accountNumber the account number
     * @return list of transactions for the account
     */
    @Override
    public List<Transaction> generateStatement(String accountNumber) {
        try {
            User currentUser = userRepository.findByAccountNumber(accountNumber);
            if (currentUser == null) {
                throw new IllegalArgumentException("Account not found: " + accountNumber);
            }

            String customerName = currentUser.getFirstName() + " " + currentUser.getLastName();
            List<Transaction> transactionList = transactionRepository.findAll()
                    .stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .collect(Collectors.toList());

            String FILE = FILE_PREFIX + accountNumber + FILE_SUFFIX;
            String statementDate = new SimpleDateFormat("dd MMM yyyy").format(new Date());

            try (OutputStream outputStream = new FileOutputStream(FILE)) {
                Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                PdfWriter.getInstance(document, outputStream);
                document.open();

                // --- Logo Placeholder ---
//                Paragraph logo = new Paragraph("[BANK LOGO]", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, SCB_BLUE));
//                logo.setAlignment(Element.ALIGN_CENTER);
//                document.add(logo);
//                document.add(Chunk.NEWLINE);
                try {
                    String logoPath = "C:\\Daniel\\Internships\\Suez Canal Bank Summer Internship 2025\\The-bank-api-product\\suez.jpg"; // Adjust path as needed
                    Image logo = Image.getInstance(logoPath);

                    // Optional: scale and align the image
                    logo.scaleToFit(100, 100); // width, height
                    logo.setAlignment(Element.ALIGN_LEFT);

                    document.add(logo);
                    document.add(Chunk.NEWLINE); // Adds a blank line after the logo
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // --- Bank Header ---
                PdfPTable bankInfo = new PdfPTable(1);




                document.add(bankInfo);
                document.add(Chunk.NEWLINE);

                // --- Statement Title and Date ---
                Paragraph statementTitle = new Paragraph(STATEMENT_TITLE, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SCB_BLUE));
                statementTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(statementTitle);
                Paragraph datePara = new Paragraph("Statement Date: " + statementDate, new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_DARK));
                datePara.setAlignment(Element.ALIGN_CENTER);
                document.add(datePara);
                document.add(Chunk.NEWLINE);

                // --- Account Info Table ---
                PdfPTable accountInfo = new PdfPTable(2);
                accountInfo.setWidthPercentage(100);
                accountInfo.setSpacingBefore(10f);
                accountInfo.setSpacingAfter(10f);
                accountInfo.addCell(createLabelCell("Account Holder:", true));
                accountInfo.addCell(createValueCell(customerName));
                accountInfo.addCell(createLabelCell("Account Number:", true));
                accountInfo.addCell(createValueCell(accountNumber));
                accountInfo.addCell(createLabelCell("Email:", true));
                accountInfo.addCell(createValueCell(currentUser.getEmail()));
                document.add(accountInfo);

                // --- Summary Section ---
                BigDecimal totalCredit = transactionList.stream()
                        .filter(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType()))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalDebit = transactionList.stream()
                        .filter(t -> "DEBIT".equalsIgnoreCase(t.getTransactionType()))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal closingBalance = currentUser.getAccountBalance();

                PdfPTable summaryTable = new PdfPTable(2);
                summaryTable.setWidthPercentage(60);
                summaryTable.setSpacingBefore(10f);
                summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                summaryTable.addCell(createLabelCell("Total Credits:", true));
                summaryTable.addCell(createValueCell(totalCredit.toString()));
                summaryTable.addCell(createLabelCell("Total Debits:", true));
                summaryTable.addCell(createValueCell(totalDebit.toString()));
                summaryTable.addCell(createLabelCell("Closing Balance:", true));
                summaryTable.addCell(createValueCell(closingBalance.toString()));
                document.add(summaryTable);
                document.add(Chunk.NEWLINE);

                // --- Transactions Table ---
                PdfPTable txnTable = new PdfPTable(3);
                txnTable.setWidthPercentage(100);
                txnTable.setSpacingBefore(10f);
                txnTable.setWidths(new float[]{2, 2, 2});
                txnTable.addCell(createHeaderCell("Type", SCB_LIGHT_BLUE));
                txnTable.addCell(createHeaderCell("Amount", SCB_LIGHT_BLUE));
                txnTable.addCell(createHeaderCell("Status", SCB_LIGHT_BLUE));

                boolean alternate = false;
                for (Transaction txn : transactionList) {
                    BaseColor bg = alternate ? ROW_ALT : BaseColor.WHITE;

                    String type = txn.getTransactionType() != null ? txn.getTransactionType() : "N/A";
                    String amount = txn.getAmount() != null ? txn.getAmount().toString() : "0.00";
                    String status = txn.getStatus() != null ? txn.getStatus() : "-";
                
                    txnTable.addCell(createBodyCell(type, bg));
                    txnTable.addCell(createBodyCell(amount, bg));
                    txnTable.addCell(createBodyCell(status, bg));
                    alternate = !alternate;
                }
                document.add(txnTable);

                // --- Footer ---
                document.add(Chunk.NEWLINE);
                Paragraph thanks = new Paragraph(FOOTER_TEXT, new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, TEXT_DARK));
                thanks.setSpacingBefore(30f);
                thanks.setAlignment(Element.ALIGN_CENTER);
                document.add(thanks);

                document.close();
                log.info("PDF generated successfully: {}", FILE);
            }
            return transactionList;
        } catch (Exception e) {
            log.error("Failed to generate PDF statement: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF statement: " + e.getMessage(), e);
        }
    }

    // --- Styling Helpers ---
    private PdfPCell createLabelCell(String text, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, bold ? Font.BOLD : Font.NORMAL, TEXT_DARK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6f);
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_DARK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6f);
        return cell;
    }

    private PdfPCell createHeaderCell(String text, BaseColor bgColor) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, TEXT_DARK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10f);
        return cell;
    }

    private PdfPCell createBodyCell(String text, BaseColor bgColor) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_DARK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        return cell;
    }
}
