package design.patterns.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


public class ConfigReader {

	public static String getConfigClassName() throws Exception {

		URL url = ConfigReader.class.getResource("ioc/DaoClass.txt");
		File file = new File(url.getPath());
		FileReader fileReader = new FileReader(file);
		StringBuffer stringBuffer = new StringBuffer();
		int numCharsRead;
		char[] charArray = new char[1024];
		while ((numCharsRead = fileReader.read(charArray)) > 0) {
			stringBuffer.append(charArray, 0, numCharsRead);
		}
		fileReader.close();
//        System.out.println("Contents of file:");
//        System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}

	// config reader should read config.properties now
//	public static String getConfigProperty(String key) throws Exception {
//		// Read the config.properties file and return the value for the given key
//		// no, how can i register config file location dynamically ?
//
//		URL url = ConfigReader.class.getResource("config.properties");
//		File file = new File(url.getPath());
//		FileReader fileReader = new FileReader(file);
//		Properties properties = new Properties();
//		properties.load(fileReader);
//		fileReader.close();
//		return properties.getProperty(key);
//	}

	public static String getConfigProperty(String key) throws Exception {
		Properties props = new Properties();

		try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
		    if (input == null) {
		        throw new FileNotFoundException("Config file not found in classpath: config.properties");
		    }
		    props.load(input);
		}

		return props.getProperty(key);
	}

}
