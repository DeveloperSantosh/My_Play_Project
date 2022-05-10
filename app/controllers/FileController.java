package controllers;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import repository.BlogRepository;
import repository.UserRepository;
import service.FileService;

import java.util.concurrent.CompletionStage;

public class FileController extends Controller {
    public final FileService fileService;

    @Inject
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    public CompletionStage<Result> getUserCsvFile(){
        return fileService.exportMyUserTableToCSV();
    }

    public CompletionStage<Result> getUserExcelFile(){
        return fileService.exportToExcel(UserRepository.getInstance().findAllUsers());
    }

    public CompletionStage<Result> getBlogPdfFile(){
        return fileService.exportToPDF(BlogRepository.getInstance().findAllBlogs());
    }
}
