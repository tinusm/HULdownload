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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	public ResponseEntity<?> uploadFile() {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("IPM Model");
		DBconnection dbConnect = new DBconnection();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		conn = DBconnection.createDbConn();
		try {
			String sql = "select * from IPM_MODEL";
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();

			Row header = sheet.createRow(0);

			for (int i = 1, j = 0; i <= numberOfColumns; i++, j++) {
				header.createCell(j).setCellValue(metadata.getColumnName(i));
			}

			int l = 1;
			while (rs.next()) {
				XSSFRow row = sheet.createRow((short) l);
				for (int m = 0, n = 1; n <= numberOfColumns; m++) {
					row.createCell((short) m).setCellValue(rs.getString(n++));
				}

				l++;

			}

			FileOutputStream outputStream = new FileOutputStream(new File("HUL.xlsx"));
			workbook.write(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
