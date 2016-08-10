package ke.co.narwassco.rest;

import java.util.Calendar;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import ke.co.narwassco.common.MapFileManager;
import ke.co.narwassco.common.ServletListener;
import ke.co.narwassco.pdf.PdfCommandExecutor;
import ke.co.narwassco.pdf.PdfSetting;

/**
 * MapPdf
 * @version 1.00
 * @author Igarashi
 */
@Path("/MapPdf")
public class MapPdf {
	private final Logger logger = Logger.getLogger(MapPdf.class);

	/**
	 * Map Serverから地図画像を取得し、PDFCreatorクラスを介して、PDFを作成する。
	 * @return PDF表示用のURL
	 */
	@Path("/OM")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> getReportMap(@QueryParam("bbox") String bbox) {

		logger.info("getReportMap start.");

		try{
			//String bbox = "35.8749959,-1.0834495,35.8754484,-1.0831842";
			String path = createMapPdf(ServletListener.MapPdfSetting,bbox);
			return new RestResult<String>(path);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Path("/A4")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> getA4Map(@QueryParam("bbox") String bbox) {

		logger.info("getA4Map start.");

		try{
			//String bbox = "35.8749959,-1.0834495,35.8754484,-1.0831842";
			//String path = createMapPdf(ServletListener.A4MapPdfSetting,bbox);
			String path = getMapUrl(ServletListener.A4MapPdfSetting,bbox);
			return new RestResult<String>(path);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	private String getMapUrl(PdfSetting pdfSetting,String bbox){
		MapFileManager mfmg = new MapFileManager(ServletListener.MapServerUrl, ServletListener.MapserverCommonPath, ServletListener.epsg);
		mfmg.setSize(pdfSetting.getMapWidth(), pdfSetting.getMapHeight());
		String url = mfmg.getUrl(pdfSetting.getMapfile(),pdfSetting.getLayers(),bbox);
		return url;
	}

	/**
	 * 地図PDFを作成する
	 * @param MapFileName マップファイル名
	 * @param Layers レイヤ名
	 * @return 出力後のPDFのURL
	 * @throws Exception
	 */
	private String createMapPdf(PdfSetting pdfSetting,String bbox) throws Exception{
		logger.info("createMapPdf start.");

		String url = getMapUrl(pdfSetting,bbox);

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("mapimage", url);

		String filename = "Map_" + Calendar.getInstance().getTime().getTime() + ".pdf";
		String filepath = ServletListener.downloadexportpath + "/" + filename;
		logger.debug(filepath);
		PdfCommandExecutor creator = new PdfCommandExecutor(filepath,pdfSetting,params);
		creator.create();

		return "." + ServletListener.downloadurlpath + "/" + filename;
	}
}
