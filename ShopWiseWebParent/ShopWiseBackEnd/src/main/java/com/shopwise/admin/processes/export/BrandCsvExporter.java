package com.shopwise.admin.processes.export;

import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter{

    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,"text/csv",".csv","_brands");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Id","Brand Name"};
        String[] fieldMapping = {"id","name"};

        csvWriter.writeHeader(csvHeader);

        for(Brand brand: brandList){
            csvWriter.write(brand,fieldMapping);
        }

        csvWriter.close();
    }
}
