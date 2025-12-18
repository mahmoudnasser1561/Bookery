package code.demo.model;

import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private LocalDate publishDate;
    private String isbn;

    public Book() {}

    public Book(int id, String title, String author, String category, LocalDate publishDate, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishDate = publishDate;
        this.isbn = isbn;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
