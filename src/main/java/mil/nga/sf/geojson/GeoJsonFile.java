package mil.nga.sf.geojson;

public class GeoJsonFile {
	final private String name;
	final private byte[] bytes;
	public GeoJsonFile(String name, byte[] bytes){
		this.name = name;
		this.bytes = bytes;
	}
	public String getName() {
		return name;
	}
	public byte[] getBytes() {
		return bytes;
	}
}
