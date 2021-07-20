package com.shopwise.admin.processes.export;

import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter{

    public void export(List<Category> categoriesList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,"text/csv",".csv","_categories");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Id","Category Name","Category Alias"};
        String[] fieldMapping = {"id","name","alias"};

        csvWriter.writeHeader(csvHeader);

        for(Category category: categoriesList){
            category.setName(category.getName().replace(">", " "));
            csvWriter.write(category,fieldMapping);
        }

        csvWriter.close();
    }
}
