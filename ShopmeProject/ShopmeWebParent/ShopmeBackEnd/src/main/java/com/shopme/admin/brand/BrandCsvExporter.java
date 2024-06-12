package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Brand;


public class BrandCsvExporter extends AbstractExporter{

	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "brands_");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
						CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"Brand ID", "Brand Name", "Categories of Brand"};
		String[] fieldMapping = {"id", "name", "categories"};

		csvWriter.writeHeader(csvHeader);

		for(Brand brand: listBrands) {
			brand.setName(brand.getName());
			brand.setCategories(brand.getCategories());
			csvWriter.write(brand, fieldMapping);
		}

		csvWriter.close();
	}
}
