/*
 * CIS 35B
 * Author: Wolfgang C. Strack
 * Compiler: Java 8 with Eclipse
 * 
 * This is the DeleteAuto interface.
 */
package adapter;

public interface DeleteAuto {
	public boolean deleteOptionSet(
			String autoMake,
			String autoModel,
			String optionSetName);
	public boolean deleteOption(
			String autoMake,
			String autoModel,
			String optionSetName,
			String optionName);
	public boolean deleteAuto(
			String autoMake,
			String autoModel);
}
