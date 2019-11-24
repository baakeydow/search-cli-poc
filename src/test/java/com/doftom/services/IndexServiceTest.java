package com.doftom.services;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.doftom.models.SearchEvent;
import com.doftom.search.SearchUtils;

public class IndexServiceTest {
  public static Map<String, TreeMap<String, TreeSet<Integer>>> eventIndex = new HashMap<String, TreeMap<String, TreeSet<Integer>>>();
  private File indexableDirectory;
  private int search_result;
  private float percent;

  @Before
  public void init() {
    search_result = 0;
    percent = 0;
  }

  @Test
  public void IndexServiceTestSearch() throws IOException {
    List<String> lines = Arrays.asList("2-3 xyz 2", "The rank score must be 100% if a file contains all the words",
        "rank the 100%", "The rank all score must be 100% if a file contains the words",
        "The rank all score must be 100% if a file contains the words 1", ">```mvn");
    lines.forEach((line) -> {
      ClassLoader cl = IndexServiceTest.class.getClassLoader();
      indexableDirectory = new File(cl.getResource("test-data").getPath());
      ArrayList<String> dir_content = SearchUtils.getDirContent(indexableDirectory);
      dir_content.forEach((file_in_folder) -> {
        assertIsTrue(file_in_folder, line);
      });
    });
  }

  private void assertIsTrue(String file_in_folder, String line) {
    try {
      Path filePath = Paths.get(indexableDirectory + "/" + file_in_folder);
      String file_content = Files.readString(filePath);
      SearchEvent current_event = new SearchEvent(file_content);
      IndexService.add(eventIndex, current_event);
      search_result = IndexService.search(eventIndex, line).size();
      percent = ((float) search_result / (float) SearchUtils.countWords(line)) * 100;
      System.out.println("[" + line + "]\033[0;32m Query Succes in \033[0m" + filePath.getFileName() + ": \033[0;34m" + percent
          + "%\033[0m [" + search_result + "]");
      IndexService.remove(eventIndex, current_event);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
