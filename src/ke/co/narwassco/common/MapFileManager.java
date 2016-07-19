package ke.co.narwassco.common;

public class MapFileManager {

	private String mapservUrl;
	private String mapfiledir;
	private String epsg;
	private Integer width = 984;
	private Integer height = 540;
	private String format = "image/png";

	public void setSize(Integer width, Integer height){
		this.width = width;
		this.height = height;
	}

	public MapFileManager(String mapservurl, String mapfiledir, String epsg){
		this.mapservUrl = mapservurl;
		this.mapfiledir = mapfiledir;
		this.epsg = "EPSG:" + epsg;
	}

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
