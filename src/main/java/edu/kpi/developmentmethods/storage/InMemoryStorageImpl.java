package edu.kpi.developmentmethods.storage;

import edu.kpi.developmentmethods.dto.Example;

import java.io.*;
import java.util.HashMap;

public class PersistentStorageImpl implements Storage {
    private static final String FILE_NAME = "storage.dat";
    private HashMap<String, Example> map;

    public PersistentStorageImpl() {
        this.map = loadFromFile();
    }

    @Override
    public void del(String key) {
        this.map.remove(key);
        saveToFile();
    }

    @Override
    public void put(String key, Example value) {
        this.map.put(key, value);
        saveToFile();
    }

    @Override
    public Example get(String key) {
        return this.map.get(key);
    }

   
    private void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    @SuppressWarnings("unchecked")
    private HashMap<String, Example> loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (HashMap<String, Example>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
