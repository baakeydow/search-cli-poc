package com.doftom.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.doftom.models.SearchEvent;
import com.doftom.services.IndexService;

public class SearchUtils {
  public static float percent = 0;
  public static int search_result = 0;
  public static Map<String, TreeMap<String, TreeSet<Integer>>> eventIndex = new HashMap<String, TreeMap<String, TreeSet<Integer>>>();

  public static ArrayList<String> getDirContent(File folder) {
    ArrayList<String> logList = new ArrayList<String>();
    for (final File fileEntry : folder.listFiles()) {
      logList.add(fileEntry.getName());
    }
    return logList;
  }
   public static int countWords(String str) {
      if (str == null || str.isEmpty()) {
        return 0;
      }
      StringTokenizer tokens = new StringTokenizer(str);
      return tokens.countTokens();
   }
   public static void processQuery(Path filePath, String query) {
    try {
      if (filePath.toFile().exists()
          && !filePath.toFile().isDirectory()
          && filePath.toFile().canRead()
          && Files.isReadable(filePath)) {
        String file_content = Files.readString(filePath);
        SearchEvent current_event = new SearchEvent(file_content);
        IndexService.add(eventIndex, current_event);
        search_result = IndexService.search(eventIndex, query).size();
        if (search_result > 0) {
          percent = ((float) search_result / (float) countWords(query)) * 100;
          System.out.println("\033[0;32m Query Succes in \033[0m" + filePath + ": \033[0;34m" + percent + "%\033[0m [" + search_result + "]");
        } else {
          System.out.println("[" + filePath + "\033[0;31m No matches found \033[0m");
        }
        IndexService.remove(eventIndex, current_event);
      }
    } catch (IOException e) {}
  }
}