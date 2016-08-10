package ke.co.narwassco.common;

/**
 * Managing to create URL for Mapserver
 * @author Jin Igarashi
 * @version 1.0
 */
public class MapFileManager {

	private String mapservUrl;
	private String mapfiledir;
	private String epsg;
	private Integer width = 984;
	private Integer height = 540;
	private String format = "image/png";

	/**
	 * Set mapsize
	 * @param width
	 * @param height
	 */
	public void setSize(Integer width, Integer height){
		this.width = width;
		this.height = height;
	}

	/**
	 * Do settings for WMS
	 * @param mapservurl URL for mapserver
	 * @param mapfiledir directory path of mapfile
	 * @param epsg EPSG code
	 */
	public MapFileManager(String mapservurl, String mapfiledir, String epsg){
		this.mapservUrl = mapservurl;
		this.mapfiledir = mapfiledir;
		this.epsg = "EPSG:" + epsg;
	}

	/**
	 * get URL for mapserver
	 * @param mapfile mapfile name
	 * @param layers layer name, separated by comma
	 * @param bbox min x, min y, max x, max y
	 * @return
	 */
	public String getUrl(String mapfile, String layers, String bbox){

		String url = this.mapservUrl
				+ "map=" + this.mapfiledir + mapfile
				+ "&SERVICE=WMS&VERSION=1.1.1"
				+ "&REQUEST=GetMap"
				+ "&STYLES="
				+ "&Layers=" + layers
				+ "&SRS=" + this.epsg
				+ "&BBOX=" + bbox
				+ "&WIDTH=" + this.width.toString()
				+ "&HEIGHT=" + this.height.toString()
				+ "&FORMAT=" + this.format;

		return url;
	}

}
