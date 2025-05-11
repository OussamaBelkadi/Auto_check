package os.automation_check_file.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
@Slf4j
public class FileHelper {

    /**
     * Creates and writes data to a JSON file
     * @param data Object to write to file
     * @param filePath Path where file should be created
     * @throws java.io.IOException if file operations fail
     */
    public void writeJsonToFile(Object data, String filePath) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Create directory if it doesn't exist
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            // Write data to file
            objectMapper.writeValue(file, data);
            log.info("Successfully wrote data to file: {}", filePath);

        } catch (IOException e) {
            log.error("Error writing to file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * Creates and writes text content to a file
     * @param content Text content to write
     * @param filePath Path where file should be created
     * @throws IOException if file operations fail
     */
    public void writeTextToFile(String content, String filePath) throws IOException {
        try {
            // Create directory if it doesn't exist
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            // Write content using Files class
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
            log.info("Successfully wrote content to file: {}", filePath);

        } catch (IOException e) {
            log.error("Error writing to file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * Reads JSON file and converts it to specified type
     * @param filePath Path to JSON file
     * @param valueType Class type to convert JSON to
     * @return Object of specified type
     * @throws IOException if file operations fail
     */
    public <T> T readJsonFromFile(String filePath, Class<T> valueType) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), valueType);
        } catch (IOException e) {
            log.error("Error reading from file: {}", filePath, e);
            throw e;
        }
    }
}
