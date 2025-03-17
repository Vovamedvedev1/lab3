package org.example;
import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();
        List<BookReader> bookreaders = null;
        try (Reader reader = Files.newBufferedReader(Paths.get("books.json"))) {
            TypeToken<List<BookReader>> token = new TypeToken<List<BookReader>>() {};
            bookreaders = gson.fromJson(reader, token.getType());
        } catch (IOException e) {
            System.out.println(e.toString());
            return;
        }
        System.out.println("-----------------------Example 1---------------------------------------");
        bookreaders.forEach(bookreader -> System.out.println(bookreader.getName() + " " + bookreader.getSurname()));
        System.out.println("Count bookreaders: " + bookreaders.size());
        System.out.println("-----------------------Example 2---------------------------------------");
        Set<Book>selectedBooks = bookreaders.stream().flatMap(bookreader -> bookreader.getFavoriteBooks().stream()).collect(Collectors.toSet());
        selectedBooks.forEach(book -> System.out.println(book.toString()));
        System.out.println("Count unique books: " + selectedBooks.size());
        System.out.println("-----------------------Example 3---------------------------------------");
        selectedBooks.stream().sorted(Comparator.comparingInt(Book::getPublishingYear)).forEach(book -> System.out.println(book.toString()));
        System.out.println("-----------------------Example 4---------------------------------------");
        Set<BookReader>readersJaneAusten = bookreaders.stream().filter(bookReader -> bookReader.getFavoriteBooks().stream().anyMatch(book -> book.getAuthor().equals("Jane Austen"))).collect(Collectors.toSet());
        System.out.println("Readers who have a book by Jane Austen in their favorites:");
        if (readersJaneAusten.isEmpty())
            System.out.print("No one likes to read books by Jane Austen");
        else
            readersJaneAusten.forEach(readerJaneAusten -> System.out.println(readerJaneAusten.getName() + " " + readerJaneAusten.getSurname()));
        System.out.println("-----------------------Example 5---------------------------------------");
        int maxCountFavoriteBook = bookreaders.stream().mapToInt(bookreader -> bookreader.getFavoriteBooks().size()).max().orElse(0);
        System.out.println("The maximum number of books added to favorites: " + maxCountFavoriteBook);
        System.out.println("-----------------------Example 6---------------------------------------");
        double bookMeanCount = bookreaders.stream().mapToInt(bookreader -> bookreader.getFavoriteBooks().size()).average().orElse(0);
        System.out.println(bookMeanCount);
        Map<String, List<BookReader>> smsGroups = bookreaders.stream()
                .filter(BookReader::isSubscribed)
                .map(bookReader -> {
                    bookReader.appendMessage(new SmsMessage(bookReader.getPhone(), SmsMessage.getMessageForAnyCategory(bookReader.getFavoriteBooks().size(), bookMeanCount)));
                    return bookReader;
                }).collect(Collectors.groupingBy(bookReader -> {
                    return bookReader.getMessages().isEmpty() ? "no_message" : bookReader.getMessages();
                }));
        smsGroups.forEach((category, bookReaderList) -> {
            System.out.println("Category: " + category);
            bookReaderList.forEach(bookReader -> System.out.println("Name: " + bookReader.getName() + ", Surname: " + bookReader.getSurname() +  ", Phone: " + bookReader.getPhone() +  ", Messages: " + bookReader.getMessages()));
        });
    }
}