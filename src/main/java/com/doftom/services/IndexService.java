package com.doftom.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.doftom.models.SearchEvent;

public class IndexService {

  private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
  private static Lock wLock = rwLock.writeLock();
  private static Lock rLock = rwLock.readLock();

  private static StringTokenizer tokenize(String stringToTokenize) {
    return new StringTokenizer(stringToTokenize);
  }

  private static List<String> get(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, String word) {
    List<String> objectIds = new ArrayList<String>();
    if (objectIndex.get(word) != null) {
      objectIds = objectIndex.get(word).entrySet().stream().map(e -> {
        return e.getKey();
      }).collect(Collectors.toList());
    }
    return objectIds;
  }

  public static void add(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, SearchEvent eventData) {
    String text = eventData.getSearchData();
    if (text != null) {
      StringTokenizer tokenizer = tokenize(text);
      int offset = 0;
      int nbTokens = tokenizer.countTokens();
      if (nbTokens > 1) {
        while (tokenizer.hasMoreTokens()) {
          add(objectIndex, tokenizer.nextToken(), eventData.getId(), ++offset);
        }
      } else {
        add(objectIndex, text, eventData.getId(), ++offset);
      }
    } else {
      throw new NullPointerException("eventData is null");
    }
  }

  private static void add(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, String word, String objectId, Integer offset) {
    wLock.tryLock();
    rLock.tryLock();
    try {
      TreeMap<String, TreeSet<Integer>> objects = objectIndex.get(word);
      if (objects == null) {
        objects = new TreeMap<String, TreeSet<Integer>>();
      }
      TreeSet<Integer> positions = objects.get(objectId);
      if (positions == null) {
        positions = new TreeSet<Integer>();
      }
      positions.add(offset);
      objects.put(objectId, positions);
      objectIndex.put(word, objects);
    } finally {
      wLock.unlock();
      rLock.unlock();
    }
  }

  public static void remove(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, SearchEvent eventData) {
    String text = eventData.getSearchData();
    if (text != null) {
      StringTokenizer tokenizer = tokenize(text);
      int offset = 0;
      while (tokenizer.hasMoreTokens()) {
        remove(objectIndex, tokenizer.nextToken(), eventData.getId(), ++offset);
      }
    } else {
      throw new NullPointerException("null");
    }
  }

  private static void remove(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, String word, String objectId, Integer offset) {
    wLock.tryLock();
    rLock.tryLock();
    try {
      TreeMap<String, TreeSet<Integer>> objects = objectIndex.get(word);
      if (objects != null) {
        TreeSet<Integer> positions = objects.get(objectId);
        if (positions != null) {
          positions.remove(offset);
          objects.remove(objectId, positions);
          if (objects.isEmpty()) {
            objectIndex.remove(word);
          } else {
            objectIndex.put(word, objects);
          }
        }
      }
    } finally {
      wLock.unlock();
      rLock.unlock();
    }
  }

  public static List<String> search(Map<String, TreeMap<String, TreeSet<Integer>>> objectIndex, String query) {
    rLock.lock();
    List<String> results = new ArrayList<String>();
    try {
      StringTokenizer tokenizer = tokenize(query);
      while (tokenizer.hasMoreTokens()) {
        List<String> tokenObjectIds = get(objectIndex, tokenizer.nextToken());
        if (!tokenObjectIds.isEmpty()) {
          results.add(tokenObjectIds.toString());
        }
      }
      return results;
    } finally {
      rLock.unlock();
    }
  }
}
