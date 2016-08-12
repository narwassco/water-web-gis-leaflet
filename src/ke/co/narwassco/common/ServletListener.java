package ke.co.narwassco.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ke.co.narwassco.pdf.PdfSetting;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * ServletListener
 * @author Jin Igarashi
 * @version 1.0
 */
public class ServletListener implements ServletContextListener {
	static public ServletContext sc;
	static public Logger logger = Logger.getLogger(ServletListener.class);

	static public String MapServerUrl = "";
	static public String MapserverCommonPath = "";
	static public String epsg = "";
	
	static public PdfSetting MapPdfSetting = null;

	static public String downloadurlpath = "";
	static public String downloadexportpath = "";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("contextDestroyed()");
	}

	@Override
	public void contextInitialized(ServletContextEvent contextevent) {
		try{
			System.out.println(this.getClass().getName()+"#contextInitialized()");

			sc = contextevent.getServletContext();
			String Propertiesfile = sc.getRealPath("/WEB-INF/narwassco.properties");
			String log4jPropertiesfile= sc.getRealPath("/WEB-INF/log4j.properties");

			// Log4J����
			PropertyConfigurator.configure(log4jPropertiesfile);

			// Propertiesfile�̓ǂݍ���
			logger.info("Propertiesfile:" + Propertiesfile);
			Properties conf = new Properties();
			FileInputStream fis = new FileInputStream(Propertiesfile);
			try{
				conf.load(fis);
			}finally{
				try{fis.close();}catch(Exception e){;}
			}

			MapServerUrl = conf.getProperty("mapserverurl");
			MapserverCommonPath = conf.getProperty("mapfilecommonpath");
			epsg = conf.getProperty("epsg");
			String pdfsetting = conf.getProperty("mappdfsetting");
			MapPdfSetting = new PdfSetting(pdfsetting,sc);
			
			downloadurlpath = conf.getProperty("downloadurlpath");
			downloadexportpath = sc.getRealPath(downloadurlpath);
			File downexportFile = new File(downloadexportpath);
			if (!downexportFile.exists()){
				downexportFile.mkdir();
			}
			
			logger.info("mapserverurl:" + MapServerUrl);
			logger.info("mapservercommonpath:" + MapserverCommonPath);
			logger.info("epsg:" + epsg);
			logger.info("pdfsetting:" + pdfsetting);
			logger.info("downloadexportpath:" + downloadexportpath);
		}catch(Throwable ex) {
			logger.error("contextInitialized()> failure.", ex);
			System.err.println("**** contextInitialized ERROR!!! **** ");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
