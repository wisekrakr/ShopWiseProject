package com.shopwise.admin.processes.export;

import com.shopwise.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<User>userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,"text/csv",".csv","_users");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Id","E-mail","Phone Number", "First Name", "Last Name", "Roles","Enabled","Created At"};
        String[] fieldMapping = {"id","email","phoneNumber","firstName","lastName","roles","enabled","createdAt"};

        csvWriter.writeHeader(csvHeader);

        for(User user: userList){
            csvWriter.write(user,fieldMapping);
        }

        csvWriter.close();
    }
}
