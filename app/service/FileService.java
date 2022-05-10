package service;

import com.google.inject.Inject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import context.MyExecutionContext;
import models.MyBlog;
import models.MyUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import repository.MyDatabase;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FileService {

    private final MyExecutionContext context;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);
    @Inject
    public FileService(MyExecutionContext context) {
        this.context = context;
        createAllDirectory();
    }

    public CompletionStage<Result> exportMyUserTableToCSV(){
        return CompletableFuture.supplyAsync(()->{
            String query = "SELECT * FROM MY_USER;";
            String fileName = "app/assets/CSV/UserList.pdf";
            try (Connection connection = MyDatabase.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)){
                ResultSet resultSet = statement.executeQuery();
                CSVWriter writer = new CSVWriter(new FileWriter(fileName));
                writer.writeAll(resultSet, true);
                writer.close();
                resultSet.close();
                return play.mvc.Results.ok("CSV File Created");
            } catch (SQLException | IOException e) {
                logger.warn(e.getMessage());
            }
            return play.mvc.Results.internalServerError("Something went wrong");
        }, context);
    }

    public CompletionStage<Result> exportMyBlogTableToCSV(){
        return CompletableFuture.supplyAsync(()->{
            String query = "SELECT * FROM MY_BLOGS;";
            String fileName = "app/assets/CSV/UserList.pdf";
            try (Connection connection = MyDatabase.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)){
                ResultSet resultSet = statement.executeQuery();
                CSVWriter writer = new CSVWriter(new FileWriter(fileName));
                writer.writeAll(resultSet, true);
                writer.close();
                resultSet.close();
                return play.mvc.Results.ok("CSV File Created");
            } catch (SQLException | IOException e) {
                logger.warn(e.getMessage());
            }
            return play.mvc.Results.internalServerError("Something went wrong");
        }, context);
    }

    public CompletionStage<Result> exportToExcel(List<MyUser> users){
        return CompletableFuture.supplyAsync(()->{
            try (XSSFWorkbook workbook = new XSSFWorkbook()){
                XSSFSheet sheet = workbook.createSheet("Users Detail");
                Map<String, Object[]> data = getHashmapFromUserList(users);
                Set<String> keySet = data.keySet();
                int rowCount = 0;
                for (String key : keySet) {
                    Row row = sheet.createRow(rowCount++);
                    Object [] objArr = data.get(key);
                    int cellCount = 0;
                    for (Object obj : objArr) {
                        Cell cell = row.createCell(cellCount++);
                        if(obj instanceof String)
                            cell.setCellValue((String)obj);
                        else if(obj instanceof Integer)
                            cell.setCellValue((Integer)obj);
                    }
                }
                FileOutputStream out = new FileOutputStream("app/assets/Excel/UserDetails.xslx");
                workbook.write(out);
                return play.mvc.Results.ok("Excel File created");
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
            return play.mvc.Results.internalServerError("Something went wrong");
        }, context);
    }

    public CompletionStage<Result> exportToPDF(List<MyBlog> blogs){
        return CompletableFuture.supplyAsync(()->{
            for (MyBlog blog: blogs) {
                try {
                    Document document = getDocumentWithAttributes();
                    String fileName = "app/assets/PDF/"+blog.getTitle()+".pdf";
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
                    document.open();
                    addTitle(document, blog.getTitle());
                    addAuthorName(document, blog.getAuthor().getUsername());
                    addCreationDate(document, blog.getTimestamp());
                    document.add(new Paragraph(""));
                    addImages(document, blog.getImagePathList());
                    addContent(document, blog.getContent());
                    document.close();
                    writer.close();
                    return play.mvc.Results.ok("PDF File created");
                } catch (DocumentException | IOException e) {
                    logger.warn(e.getMessage());
                }
            }
            return play.mvc.Results.internalServerError("Something went wrong");
        }, context);
    }


    private static void addImages(Document document, List<String> imagePaths) throws DocumentException, IOException {
        for (String imagePath : imagePaths){
            Image image = Image.getInstance(imagePath);
            image.scaleToFit(500, 500);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);
        }
    }

    private static void addContent(Document document, String content) throws DocumentException {
        Font contentFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
        for(String paragraph : content.split("\\R")){
            Paragraph contentParagraph = new Paragraph(paragraph, contentFont);
            contentParagraph.setFirstLineIndent(20);
            document.add(contentParagraph);
        }
    }

    private static void addCreationDate(Document document, String createdDate) throws DocumentException {
        Font dateFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.MAGENTA);
        Paragraph creationParagraph = new Paragraph(createdDate, dateFont);
        creationParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(creationParagraph);
    }

    static void addAuthorName(Document document, String authorName) throws DocumentException {
        Font authorNameFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.GREEN);
        Paragraph author = new Paragraph(authorName,authorNameFont);
        author.setAlignment(Element.ALIGN_RIGHT);
        document.add(author);
    }
    static void addTitle(Document document, String documentTitle) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, new BaseColor(255,0,0));
        Paragraph titleParagraph = new Paragraph(documentTitle, titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
    }
    static public Document getDocumentWithAttributes(){
        Document document = new Document();
        document.setPageSize(PageSize.A4);
        document.setMargins(40, 50, 50, 40);
        document.addAuthor("Santosh Mahato");
        document.addCreationDate();
        return document;
    }

    static public void createAllDirectory() {
        try {
            Files.createDirectories(Paths.get("app/assets/PDF/"));
            Files.createDirectories(Paths.get("app/assets/CSV/"));
            Files.createDirectories(Paths.get("app/assets/Excel/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object[]> getHashmapFromUserList(List<MyUser> users){
        Map<String, Object[]> data = new TreeMap<>();
        data.put("record", new Object[]{"UserId", "Username", "Email", "Password"});
        for(int i=0; i < users.size(); i++){
            MyUser user = users.get(i);
            data.put("record: "+(i+1), new Object[]{user.getId(), user.getUsername(), user.getEmail(), user.getPassword()});
        }
        return data;
    }
}
