package com.doftom.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.doftom.search.SearchUtils;

public class App {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      throw new IllegalArgumentException("\033[0;31mRTFM !\n\n \033[0;32m No directory provided !\n\n\033[0m");
    }
    final File indexableDirectory = new File(args[0]);
    try (Scanner keyboard = new Scanner(System.in)) {
      while (true) {
        System.out.print("\033[0;33mSearch:$> \033[0m");
        final String line = keyboard.nextLine();
        if (line.isEmpty()) continue;
        if (line.equals(":exit")) System.exit(0);
        ArrayList<String> dir_content = SearchUtils.getDirContent(indexableDirectory);
        dir_content.forEach((file_in_folder) -> {
          Path filePath = Paths.get(indexableDirectory + "/" + file_in_folder);
          try {
            Files.walk(filePath)
            .filter(Files::isRegularFile)
            .forEach(s -> {
              SearchUtils.processQuery(s, line);
            });
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      }
    }
  }

}