package com.unilever.unileverdownload.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.unilever.unileverdownload.controller.DBconnection;

@Controller
public class MainController {

	private static final String USERNAME = "admin_68a14adf7f56891156fda4dd8e09bdac08f4df53";
	private static final String PASSWORD = "xl,OrD5VIQ6Y!CRd";
	private static final String DOMAIN_ID = "3a512cebf44640acaefed32112e9ce8c";
	private static final String PROJECT_ID = "0ab4d206302d4cb38b0e3d1f9c8681bd";

	@Autowired
	private Environment env;

	/**
	 * Show the index page containing the form for uploading a file.
	 */
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	/**
	 * POST /uploadFile -> receive and locally save a file.
	 * 
	 * @param uploadfile
	 *            The uploaded file as Multipart file parameter in the HTTP
	 *            request. The RequestParam name must be the same of the
	 *            attribute "name" in the input tag with type file.
	 * 
	 * @return An http OK status in case of success, an http 4xx status in case
	 *         of errors.
	 */
	@RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
	@ResponseBody
	public void uploadFile(HttpServletRequest request,
            HttpServletResponse response) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("IPM Model");
		DBconnection dbConnect = new DBconnection();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		conn = DBconnection.createDbConn();
		try {
			//String sql = "select * from IPM_MODEL";
			String sql ="select SKU_NO, SKU_Name, Location, Location_Type, SKU_Location, SKU_Classification, Source, Category, Service_Level, Average_Weekly_Demand, SDFE_Per, SDFE,Lot_Sizes, OR_Delivery, Cycle_Time, Avg_Replen_Lead_Time, Lead_Time_Variability, SD_Variability, C_Factor_Sales, K_Factor_Sales, Model_Safety_Stock, Model_Safety_Stock_Weeks, Model_Safety_Stock_Days, Minstock_AftCapping_Weeks, Maxstock_Weeks, Minstock_AftCapping_CS, Maxstock_CS, CurrentSS_Weeks, Price, CurrentSS_Value, Proposed_IPMSS_Value, Min_Norms_Weeks, Max_Norms_Weeks, MinStock_Value, MaxStock_Value, Avg_Cycle_Stock, CurrentDate from IPM_MODEL";
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			Timestamp curentDate = null;
			Row header = sheet.createRow(0);

			for (int i = 1, j = 0; i <= numberOfColumns; i++, j++) {
				header.createCell(j).setCellValue(metadata.getColumnName(i));
			}

			int l = 1;
			while (rs.next()) {
				curentDate = rs.getTimestamp("CurrentDate");
				HSSFRow row = sheet.createRow((short) l);
				for (int m = 0, n = 1; n <= numberOfColumns; m++) {
					row.createCell((short) m).setCellValue(rs.getString(n++));
				}

				l++;

			}

			

			String S = new SimpleDateFormat("ddMMyyyyHHmmss").format(curentDate);

			/* Timestamp Changes - Start */
			String filename = "HUL_IPM_MODEL_" + S + ".xls";
			/* Timestamp Changes - End */
			response.setContentType("application/vnd.ms-excel");
           response.setHeader("Content-Disposition", "attachment; filename="+filename);
			FileOutputStream outputStream = new FileOutputStream(new File(filename));
			//workbook.write(outputStream);
			workbook.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		
	}

}
