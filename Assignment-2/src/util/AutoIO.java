/*
 * CIS 35B
 * Author: Wolfgang C. Strack
 * OS/Compiler: Windows 8/Java 8 with Eclipse
 * 
 * This class handles file IO when working with Automotive objects.
 */
package util;

import java.io.*;

public class AutoIO {
	/**
	 * The buildAutoObject method takes a filename, reads from that file,
	 * and instantiates a new Automotive based on the information in the file.
	 * If the instantiation is successful the Automotive object is returned,
	 * otherwise null is returned
	 * 
	 * File format of <filename> is assumed to be formatted as follows:
	 * 
	 * Automotive name // must be first line of file
	 * base price // must be second line of file
	 * OptionSet name1
	 * {
	 *     Option name1|price
	 *     Option name2|price
	 * }
	 * OptionSet name2
	 * {
	 *     Option name3|price
	 * }
	 * .
	 * .
	 * .
	 */
	public model.Automobile buildAutoObject(String filename) throws AutoException {
		try (FileReader file = new FileReader(filename);
				BufferedReader buffer = new BufferedReader(file)) {
			
			boolean eof = false;
			
			// read the first two lines, which must be the Automotive name and base price
			// and create a new Automotive based on this information
			model.Automobile newAuto = new 
					model.Automobile(buffer.readLine(), Double.parseDouble(buffer.readLine()));
			
			while (!eof) {
				String line = buffer.readLine();
				if (line == null)
					eof = true;
				else {
					String opsName = line; // save for adding Options to this OptionSet name
					newAuto.addOptionSet(opsName);
					
					buffer.readLine(); // next line after OptionSet name must be a "{"
					line = buffer.readLine();
					while (line.charAt(0) != '}') { // stop reading Options at the next "}"
						// substring is called to skip the \t before the Option
						// which is formatted as such: name|price
						String option[] = line.substring(1).split("\\|");
						newAuto.addOption(opsName, option[0], Float.parseFloat(option[1]));
						line = buffer.readLine();
					}
				}
			}
			
			return newAuto;
		} catch (IOException ioe) {
			throw new AutoException("IOException from file " + filename, 1);
		}
	}
	
	public void serializeAutoObject(String filename, model.Automobile auto) throws AutoException {
		try (FileOutputStream fos = new FileOutputStream(filename);
				ObjectOutputStream out = new ObjectOutputStream(fos)) {
			out.writeObject(auto);
		} catch (FileNotFoundException fnfe) {
			throw new AutoException(filename + " not found", 2);
		} catch (IOException ioe) {
			throw new AutoException("IOException in serialization file: " + filename, 3);
		}
	}
	
	public model.Automobile deserializeAutoObject(String filename) throws AutoException {
		model.Automobile result = null;
		
		try (FileInputStream fos = new FileInputStream(filename);
				ObjectInputStream in = new ObjectInputStream(fos)) {
			result = (model.Automobile) in.readObject();
		} catch (FileNotFoundException fnfe) {
			throw new AutoException(filename + " not found", 4);
		} catch (IOException ioe) {
			throw new AutoException("IOException in deserialization file: " + filename, 5);
		} catch (ClassNotFoundException cnfe) {
			throw new AutoException("Automobile class not found", 6);
		}
		
		return result;
	}
}
