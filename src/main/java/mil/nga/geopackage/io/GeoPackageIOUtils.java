package mil.nga.geopackage.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * Input / Output utility methods
 * 
 * @author osbornb
 */
public class GeoPackageIOUtils {

	/**
	 * Get the file extension
	 * 
	 * @param file
	 *            file
	 * @return extension
	 */
	public static String getFileExtension(File file) {

		String fileName = file.getName();
		String extension = null;

		int extensionIndex = fileName.lastIndexOf(".");
		if (extensionIndex > -1) {
			extension = fileName.substring(extensionIndex + 1);
		}

		return extension;
	}

	/**
	 * Get the file name with the extension removed
	 * 
	 * @param file
	 *            file
	 * @return file name
	 */
	public static String getFileNameWithoutExtension(File file) {

		String name = file.getName();

		int extensionIndex = name.lastIndexOf(".");
		if (extensionIndex > -1) {
			name = name.substring(0, extensionIndex);
		}

		return name;
	}

	/**
	 * Copy a file to a file location
	 * 
	 * @param copyFrom
	 * @param copyTo
	 * @throws IOException
	 */
	public static void copyFile(File copyFrom, File copyTo) throws IOException {

		InputStream from = new FileInputStream(copyFrom);
		OutputStream to = new FileOutputStream(copyTo);

		copyStream(from, to);
	}

	/**
	 * Copy an input stream to a file location
	 * 
	 * @param copyFrom
	 * @param copyTo
	 * @throws IOException
	 */
	public static void copyStream(InputStream copyFrom, File copyTo)
			throws IOException {
		copyStream(copyFrom, copyTo, null);
	}

	/**
	 * Copy an input stream to a file location
	 * 
	 * @param copyFrom
	 * @param copyTo
	 * @param progress
	 * @throws IOException
	 */
	public static void copyStream(InputStream copyFrom, File copyTo,
			GeoPackageProgress progress) throws IOException {

		OutputStream to = new FileOutputStream(copyTo);

		copyStream(copyFrom, to, progress);

		// Try to delete the file if progress was cancelled
		if (progress != null && !progress.isActive()
				&& progress.cleanupOnCancel()) {
			copyTo.delete();
		}
	}

	/**
	 * Get the file bytes
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static byte[] fileBytes(File file) throws IOException {

		FileInputStream fis = new FileInputStream(file);

		return streamBytes(fis);
	}

	/**
	 * Get the stream bytes
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public static byte[] streamBytes(InputStream stream) throws IOException {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		copyStream(stream, bytes);

		return bytes.toByteArray();
	}

	/**
	 * Copy an input stream to an output stream
	 * 
	 * @param copyFrom
	 * @param copyTo
	 * @throws IOException
	 */
	public static void copyStream(InputStream copyFrom, OutputStream copyTo)
			throws IOException {
		copyStream(copyFrom, copyTo, null);
	}

	/**
	 * Copy an input stream to an output stream
	 * 
	 * @param copyFrom
	 * @param copyTo
	 * @param progress
	 * @throws IOException
	 */
	public static void copyStream(InputStream copyFrom, OutputStream copyTo,
			GeoPackageProgress progress) throws IOException {

		byte[] buffer = new byte[1024];
		int length;
		while ((progress == null || progress.isActive())
				&& (length = copyFrom.read(buffer)) > 0) {
			copyTo.write(buffer, 0, length);
			if (progress != null) {
				progress.addProgress(length);
			}
		}

		copyTo.flush();
		copyTo.close();
		copyFrom.close();
	}

	/**
	 * Format the bytes into readable text
	 * 
	 * @param bytes
	 *            bytes
	 * @return bytes text
	 */
	public static String formatBytes(long bytes) {

		double value = bytes;
		String unit = "B";

		if (bytes >= 1024) {
			int exponent = (int) (Math.log(bytes) / Math.log(1024));
			exponent = Math.min(exponent, 4);
			switch (exponent) {
			case 1:
				unit = "KB";
				break;
			case 2:
				unit = "MB";
				break;
			case 3:
				unit = "GB";
				break;
			case 4:
				unit = "TB";
				break;
			}
			value = bytes / Math.pow(1024, exponent);
		}

		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(value) + " " + unit;
	}

}
