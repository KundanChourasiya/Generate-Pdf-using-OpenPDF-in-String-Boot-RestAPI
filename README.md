# Generate-Pdf-using-OpenPDF-in-String-Boot-RestAPI

> [!NOTE]
> ### In this Api we generate PDF using openPDF in spring boot RestApi.

## Tech Stack
- Java-17
- Spring Boot-3x
- lombok
- PostMan
- Swagger UI

## Modules
* Product Module

## Documentation
Swagger UI Documentation - http://localhost:8080/swagger-ui/

## API Root Endpoint
```
https://localhost:8080/
http://localhost:8080/swagger-ui/
user this data for checking purpose.
```
## Step To Be Followed
> 1. Create Rest Api will return to Product Details.
>    
>    **Project Documentation**
>    - **Entity** - Product (class)
>    - **Payload** - ApiResponceDto (class)
>    - **Service** - ProductService (interface), ProductServiceImpl (class)
>    - **Controller** - ProductController (Class)
>      
> 2. Add OpenPDF is a Java library in pom.xml file.
> 3. Create a generate pdf method in service class.

## Important Dependency to be used
``` 
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
 </dependency>

 <dependency>
     <groupId>org.projectlombok</groupId>
     <artifactId>lombok</artifactId>
     <optional>true</optional>
 </dependency>

<!-- https://mvnrepository.com/artifact/com.github.librepdf/openpdf -->
	<dependency>
		<groupId>com.github.librepdf</groupId>
		<artifactId>openpdf</artifactId>
		<version>2.0.3</version>
	</dependency>

<dependency>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
	<version>2.3.0</version>
</dependency>
```

## Create Product class in Entity Package.
```
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private Integer id;
    private String category;
    private String name;
    private Integer quantity;
    private double price;

}
```

## Create ProductService interface and ProductServiceImpl class in Service package.

### *ProductService*
```
public interface ProductService {

    // GET: Retrieve all Product
    public List<Product> getAllProduct();

    // POST: Save a new Product
    public Product saveProduct(Product product);

    // GET: Generate a product list Report
    public ByteArrayInputStream generatePdf();
}
```

### *ProductServiceImpl*
```
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
```

##  Create ApiResponse inside the Payload Package.
### *ApiResponseDto* 
```
@Setter
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;
    public ApiResponse(boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
```

### *Create ProductController class inside the Controller Package.* 

```
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET: Retrieve all Product
    // URL: http://localhost:8080/api/v1/product
    @GetMapping("/product")
    public ResponseEntity<ApiResponse<?>> getAllProducts() {
        List<Product> allProduct = productService.getAllProduct();
        if (allProduct.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(false, "Product List Empty!!!", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<Product>> response = new ApiResponse<>(true, "Product List Found!!!", allProduct);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // POST: Save a new Product
    // URL: http://localhost:8080/api/v1/product
    @PostMapping(value = "/product")
    public ResponseEntity<ApiResponse<?>> saveProduct(@RequestBody Product product) {
        try {
            Product saveProduct = productService.saveProduct(product);
            ApiResponse<Product> response = new ApiResponse<>(true, "Product saved successfully!!!", saveProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Product> response = new ApiResponse<>(false, "Product Not Saved!!!", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // GET: Generate a product list Report
    // URL: http://localhost:8080/api/v1/product/pdf-report
    @GetMapping(value = "/product/pdf-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> productReport() throws IOException {
        ByteArrayInputStream generatedPdf = productService.generatePdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=Product_List.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(generatedPdf));
    }

}

```

### Following pictures will help to understand flow of API

### *Swagger*

![image](https://github.com/user-attachments/assets/14cd6950-c879-45e5-8f4f-b2cbbcc5a402)

### *Postman Test Cases*

Url - http://localhost:8080/api/v1/product
![image](https://github.com/user-attachments/assets/47b46eeb-18bb-4af8-9a22-bed63f1a94c0)


Url - http://localhost:8080/api/v1/product
![image](https://github.com/user-attachments/assets/a03ced8f-2b6d-4c06-ace8-c79a4d507af3)

Url - http://localhost:8080/api/v1/product/pdf-report
![image](https://github.com/user-attachments/assets/2e396dc6-4712-469d-b165-bc0f71a97122)

**Note**: copy and paste the browser search bar(http://localhost:8080/api/v1/product/pdf-report).
![image](https://github.com/user-attachments/assets/9b458abf-63a6-4a51-8c8e-bd2fb435c174)

