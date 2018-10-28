package assignment.file.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class DataFileParser {

  private String dataFileName;

  public String getDataFileName() {
    return dataFileName;
  }

  public void setDataFileName(String dataFileName) {
    this.dataFileName = dataFileName;
  }

  private BufferedReader fileReader;

  public BufferedReader getFileReader() {
    return fileReader;
  }

  public void setFileReader(BufferedReader fileReader) {
    this.fileReader = fileReader;
  }

  public DataFileParser(String filename) throws FileNotFoundException {
    this.dataFileName = filename;
    this.fileReader =
        new BufferedReader(
            new InputStreamReader(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(this.dataFileName)));
  }
}
