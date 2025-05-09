package File_Reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ReaderBase<T> {

    protected String filename;

    public ReaderBase(String filename) {
        this.filename = filename;
    }

    public abstract T read();

    protected JsonNode readJsonFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(filename);
            byte[] jsonBytes = Files.readAllBytes(path);
            return mapper.readTree(jsonBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }
    }
}
