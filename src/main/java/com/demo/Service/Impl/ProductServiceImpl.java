package com.demo.Service.Impl;

import com.demo.Entity.Product;
import com.demo.Service.ProductService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductServiceImpl implements ProductService {


    private static List<Product> productList = new ArrayList<>();
    private static int lastId = 100; // starting from the highest ID already used

    static {
        // it will be store the product value during the class loading.
    }

    @Override
    public List<Product> getAllProduct() {
        return productList;
    }

    @Override
    public Product saveProduct(Product product) {
        product.setId(++lastId);
        productList.add(product);
        return product;
    }

    @Override
    public ByteArrayInputStream generatePdf() {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add text to Pdf file
            Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, Color.RED);
            Paragraph para = new Paragraph("Product Record", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            // create table
            PdfPTable table = new PdfPTable(6);

            // Add PDF Table Heading
            Stream.of("Product Id", "Product Category", "Product Name", "Product Quantity", "Product Price", "Total Price").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(Color.orange);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorder(2);
                header.setPhrase(new Phrase(headerTitle, headerFont));
                table.addCell(header);
            });


            for (Product prod : productList) {

                // Add PDF Table first cell
                PdfPCell idCell = new PdfPCell(new Phrase(prod.getId().toString()));
                idCell.setPaddingLeft(4);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // Add PDF Table second cell
                PdfPCell categoryCell = new PdfPCell(new Phrase(prod.getCategory()));
                categoryCell.setPaddingLeft(4);
                categoryCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                categoryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(categoryCell);

                // Add PDF Table third cell
                PdfPCell nameCell = new PdfPCell(new Phrase(prod.getName()));
                nameCell.setPaddingLeft(4);
                nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(nameCell);

                // Add PDF Table fourth cell
                PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(prod.getQuantity())));
                quantityCell.setPaddingLeft(4);
                quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(quantityCell);

                // Add PDF Table fifth cell
                PdfPCell priceCell = new PdfPCell(new Phrase(String.valueOf(prod.getPrice())));
                priceCell.setPaddingLeft(4);
                priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(priceCell);

                // Add PDF Table sixth cell
                double totalPrice = prod.getQuantity() * prod.getPrice();
                PdfPCell TotalPriceCell = new PdfPCell(new Phrase(String.valueOf(totalPrice)));
                TotalPriceCell.setPaddingLeft(4);
                TotalPriceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                TotalPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(TotalPriceCell);
            }

            // add table in document
            document.add(table);

            // close the document
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // return to the byte Array
        return new ByteArrayInputStream(out.toByteArray());
    }

}