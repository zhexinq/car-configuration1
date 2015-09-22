/**
 * @author zhexinq
 * File I/O class for parsing an input.txt to populate an automotive object
 */
package util;

import model.Automobile;
import java.io.*;
import exception.AutoException;
import exception.FixUtil;

public class FileIO {
	
	/* instance method for parsing file and store information into automotive object */
	public Automobile buildAutoObject(String filename) throws AutoException{
		BufferedReader input = null;
		String line = null;
		String[] lineValues = null;
		
		try {
			input = new BufferedReader(new FileReader(filename));
			line = input.readLine();
			if (line != null) {
				/* parse info for Auto model, OptionSets size, base price */
				int delimit = line.indexOf(':');
				/* throw exception if header malformed */
				if (delimit == -1)
					throw new AutoException(1);
				lineValues = line.substring(delimit+1).split(",");
				if (lineValues.length != 2)
					throw new AutoException(1);
				/* trim each value */
				for (int i = 0; i < lineValues.length; i++)
					lineValues[i] = lineValues[i].trim();
				/* missing base price handler */
				int basePrice;
				try {
					try {
						basePrice = Integer.parseInt(lineValues[2]);
					}
					catch (NumberFormatException e) {
						throw new AutoException(2);
					}
				}
				catch (AutoException e) {
					e.recordLog("log.txt", e);
					basePrice = Integer.parseInt(e.fix(e.getErrno()));
				}
				/* missing optionsets size handler */
				int optionsetsSize;
				try {
					try {
						optionsetsSize = Integer.parseInt(lineValues[1]);
					}
					catch (NumberFormatException e) {
						throw new AutoException(3);
					}
				}
				catch (AutoException e) {
					e.recordLog("log.txt", e);
					optionsetsSize = Integer.parseInt(e.fix(e.getErrno()));
				}
				/* create Auto object with extracted info */
				Automobile auto = new Automobile(lineValues[0], optionsetsSize, 
												 basePrice);
				int optionSetIndex = 0;
				/* parse info for Auto object OptionSets values */
				boolean eof = false;
				while (!eof) {
					line = input.readLine();
					if (line == null)
						eof = true;
					else {
						delimit = line.indexOf(':');
						/* get OptionSet name */
						String optionSetName = line.substring(0, delimit);
						/* get Option (name, price) */
						lineValues = line.substring(delimit+1).split(",");
						for (int i = 0; i < lineValues.length; i++)
							lineValues[i] = lineValues[i].trim();
						int N = lineValues.length/2;
						String[] optionNames = new String[N];
						int [] optionPrices = new int[N];
						for (int i = 0; i < 2*N; i+=2) {
							optionNames[i/2] = lineValues[i];
							optionPrices[i/2] = Integer.parseInt(lineValues[i+1]);
						}
						auto.updateOptionSet(optionSetIndex, optionSetName, 
								             optionNames, optionPrices);
						optionSetIndex++;
					}
				}
				return auto;
			}
		}
		catch (FileNotFoundException e) {
			throw new AutoException(0);
		}
		catch (IOException e) {
			System.out.println("input file cannot be read");
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					System.out.print("file cannot be close");
				}
			}
		}
		return null;
	}
	
	/* instance method for serializing an Auto instance */
	public String serialzeAutoObject(Automobile auto, String filename) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(auto);
			return filename;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/* instance method for deserializing an Auto instance */
	public Automobile deserializeAutoObject(String filename) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			Automobile auto = (Automobile)in.readObject();
			return auto;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static void main(String[] args) {
		try {
			throw new AutoException(1);
		}
		catch (AutoException e) {
			System.out.println("in catch block");
		}
		System.out.println("after catch block");
	}

}