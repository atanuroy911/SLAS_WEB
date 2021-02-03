package com.slas.controller;

import com.slas.entity.LectureEntity;
import com.slas.entity.TeacherAuthEntity;
import com.slas.mapper.MainMapper;
import com.slas.repo.LectureSlas;
import com.slas.repo.RepoSlas;
import com.slas.util.MD5Util;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Controller
public class MainController implements ErrorController {

    private long card_no2 = 0;
    private String nopassmsg = null;
    @Autowired
    MainMapper mainMapper;

    @Autowired
    LectureSlas lectureSlas;

    @Autowired
    RepoSlas repoSlas;

    public String current_token;
//    String last_token="YYY";

    //登录到主界面
    @RequestMapping("/login")
    public ModelAndView Login(@RequestParam("card_no") long card_no, @RequestParam("pwd_no") String pwd_no, Model model) {
        TeacherAuthEntity teacherAuthEntity = mainMapper.GetUserByCard_no(card_no);
        if (teacherAuthEntity == null) {
            model.addAttribute("msg", "Login Details Wrong Try Again !!!");
            return new ModelAndView("login");
        } else if (teacherAuthEntity.getPwd_no().equals(MD5Util.inputPassToFormPass(pwd_no))) {
            //System.out.println(teacherAuthEntity.toString());
            card_no2 = card_no;
            model.addAttribute("msg", teacherAuthEntity.getName_full());
            return new ModelAndView("panel");
        } else if (!teacherAuthEntity.getPwd_no().equals(MD5Util.inputPassToFormPass(pwd_no))) {
            model.addAttribute("msg", "Login Details Wrong Try Again !!!");
            return new ModelAndView("login");
        } else {
            return new ModelAndView("error");
        }
    }


    //登录到视图界面
    @RequestMapping("/view")
    public ModelAndView toView(Model model) {
        if(card_no2!=0) {
            List<LectureEntity> list;
            List<LectureEntity> list2 = new ArrayList<>();
            list = lectureSlas.findAll();
            List<String> list3 = new ArrayList<>();
            Iterator<LectureEntity> iterator = list.iterator();
            while (iterator.hasNext()) {
                LectureEntity lectureEntity = iterator.next();
                if (lectureEntity.getCard_no() == card_no2) {
                    list2.add(lectureEntity);
//                    System.out.println(lectureEntity);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                        Statement stmt = con.createStatement();
                        ResultSet rs;
                        String sql = "SELECT COUNT(*) from " + lectureEntity.getLecture_id();
                        stmt.executeQuery(sql);
                        rs = stmt.getResultSet();
                        while (rs.next()) {
                            String value = rs.getString("COUNT(*)");
                            System.out.println(value);
                            list3.add(value);
                        }
                        con.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
//            Iterator<String> iterator1 = list3.iterator();
//            while(iterator1.hasNext()){
//                System.out.println(iterator.next());
//            }

            model.addAttribute("studentcount", list3);
            model.addAttribute("lectures", list2);
//        model.addAttribute("lecturecnt", count);
            return new ModelAndView("view");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }


    //插入课程到数据库
    @RequestMapping("/insert2")
    public ModelAndView insert(@RequestParam("lecture_id") String lecture_id,
                               @RequestParam("lecture_name") String lecture_name, Model model) {
        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setLecture_id(lecture_id);
        lectureEntity.setLecture_name(lecture_name);
        lectureEntity.setCard_no(card_no2);
        lectureSlas.save(lectureEntity);
        boolean okay = true;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
            Statement stmt = con.createStatement();
            String sql = "CREATE TABLE " + lecture_id + "( `id` INT NOT NULL AUTO_INCREMENT , `student_id` BIGINT NOT NULL , `student_name` VARCHAR(50) NOT NULL , PRIMARY KEY (`id`))";
            stmt.execute(sql);
            con.close();
        } catch (Exception e) {
            model.addAttribute("insert_msg", "Lecture With Same ID already Exists");
            System.out.println(e);
            okay = false;
        }
        if (okay == true) {
            return new ModelAndView("redirect:view");
        } else {
            return new ModelAndView("insert");

        }

    }

    @RequestMapping("/pwdchange")
    public ModelAndView ChangePassword(@RequestParam("currentPassword") String curpwd, @RequestParam("newPassword") String newpwd, Model model) {
        if(card_no2!=0) {
            TeacherAuthEntity teacherAuthEntity = mainMapper.GetUserByCard_no(card_no2);
            if (teacherAuthEntity.getPwd_no().equals(MD5Util.inputPassToFormPass(curpwd))) {
                teacherAuthEntity.setPwd_no(MD5Util.inputPassToFormPass(newpwd));
                repoSlas.save(teacherAuthEntity);
                System.out.println(teacherAuthEntity.toString());
                model.addAttribute("changepwdmsg", "Password Changed successfully");
                return new ModelAndView("panel");
            } else {

                model.addAttribute("changepwdmsg", "Current Password incorrect,Password Change Failed!");
                return new ModelAndView("panel");
            }
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }

    //删除课程
    @RequestMapping("/delete/{id}")
    public ModelAndView deleteLecture(@PathVariable("id") int id, Model model) {
        if(card_no2!=0) {
            LectureEntity lectureEntity = mainMapper.GetLectureById(id);
            String lid = lectureEntity.getLecture_id();
//        System.out.println();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement stmt = con.createStatement();
                String sql = "DROP table " + lid;
                stmt.execute(sql);
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            lectureSlas.deleteById(id);
            return new ModelAndView("redirect:/view");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }

    @RequestMapping("/index")
    public ModelAndView toIndex() {
        return new ModelAndView("index");
    }

    @RequestMapping("/signup")
    public ModelAndView toSignup() {
        return new ModelAndView("signup");
    }

    @RequestMapping("/userLogin")
    public ModelAndView uaa(Model model) {
        model.addAttribute("nopassmsg", nopassmsg);
        return new ModelAndView("login");
    }


    @RequestMapping("/about")
    public ModelAndView toAbout() {
        return new ModelAndView("about");
    }

    @RequestMapping("/contact")
    public ModelAndView toContact() {
        return new ModelAndView("contact");
    }


    @RequestMapping("/changepwd")
    public ModelAndView toPwd_change() {
        return new ModelAndView("pwd_change");
    }

    //跳转到插入界面
    @RequestMapping("/insert")
    public ModelAndView toInsert(Model model) {
        if(card_no2!=0) {
            return new ModelAndView("insert");
        }
        else {
            model.addAttribute("nopassmsg", "Login First to access that page!!");
            return new ModelAndView("redirect:userLogin");
        }
    }

    @RequestMapping("/panel")
    public ModelAndView toPanel(Model model) {
        if(card_no2!=0) {
            return new ModelAndView("panel");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }

    @RequestMapping(value = "/show",
            params = {"date", "name", "teacher", "period"}, method = RequestMethod.GET)
    public ModelAndView toShow(@RequestParam("date") String date,
                               @RequestParam("name") String name,
                               @RequestParam("teacher") String teacher,
                               @RequestParam("period") Integer period,
                               Model model) throws IOException {
        if(card_no2!=0) {
            String ip = null;

            try {

                ip = InetAddress.getLocalHost().getHostAddress();

            } catch (UnknownHostException e) {

                e.printStackTrace();

            }
            if(ip.startsWith("127")){
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));

                ip = in.readLine(); //you get the IP as a String
            }
//            else if(ip.startsWith("169")){
//                ip="localhost";
//            }
            //ip = InetAddress.getLocalHost().getHostName();
            String const_ip = "http://" + ip + ":8080" + "/qrcode?text=";
            model.addAttribute("name", name);
            model.addAttribute("info", const_ip + ip + ":8080");
            model.addAttribute("info2", const_ip + date + "," + name + "," + teacher + "," + period + "," + token());
            return new ModelAndView("show");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }


    @RequestMapping(value = "/qr_gen", params = "lecture_id", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView QR_gen(@RequestParam("lecture_id") String lecture_id, Model model) {
        if(card_no2!=0) {
            String lid = lecture_id;
            String date = findDate();
            model.addAttribute("lid", lid);
            model.addAttribute("date", date);
            return new ModelAndView("qr_gen");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }

    }

    @RequestMapping(value = "/post", params = "data", method = RequestMethod.POST)
    public void recv_data(@RequestParam("data") String data) {
        String[] vars = data.split(",");
        String date = vars[0];
        String lid = vars[1];
        String atdata = vars[2] + " " + Integer.parseInt(vars[3]);
        String token_recv = vars[4];
        long sid = Long.parseLong(vars[5]);
        ResultSet rs;
        String sql;
        if (token_recv.equals(current_token)) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement stmt = con.createStatement();
                sql = "select * from " + lid;
                rs = stmt.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int rowCount = metaData.getColumnCount();

                boolean isMyColumnPresent = false;
                String myColumnName = vars[0];
                for (int i = 1; i <= rowCount; i++) {
                    if (myColumnName.equals(metaData.getColumnName(i))) {
                        isMyColumnPresent = true;
                        System.out.println("column present");
                    }
                }

                if (!isMyColumnPresent) {
                    String sql1 = "ALTER TABLE " + lid + " ADD COLUMN `" + date + "` VARCHAR(20) DEFAULT 0";
                    stmt.executeUpdate(sql1);
//                    System.out.println(sql1);

                }
                String sqlp = "select * from " + lid + " where student_id = " + sid;
                ResultSet rs1 = stmt.executeQuery(sqlp);
                System.out.println(sqlp);
                rs1.last();
                int metaData1 = rs1.getRow();
                System.out.println(metaData1);
                if (metaData1 == 1) {
                    String sql2 = "UPDATE " + lid + " SET `" + date + "` = '" + atdata + "' WHERE student_id = " + sid;
//                String sql = "INSERT into test (id, data) VALUES(1, '" + token_recv + "')" ;
                    stmt.execute(sql2);
//                    System.out.println(sql2);
                } else {
                    String sql2 = "INSERT into " + lid + " (student_id, student_name, `" + date + "`) VALUES  ("
                            + sid + ", 'guest', " + ", '" + atdata + "')";
//                    insert into GX100012 ( student_id, student_name, phn_no, `20190101`) VALUES (234455, 'guest' ,99776, 'asdr 1');
//                String sql = "INSERT into test (id, data) VALUES(1, '" + token_recv + "')" ;
                    stmt.execute(sql2);
                }
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Token Mismatch!!");
//            System.out.println(current_token);
//            System.out.println(date);
//            System.out.println(lid);
//            System.out.println(atdata);
//            System.out.println(token_recv);
//            System.out.println(sid);
        }
    }



    //跳转到编辑界面，并且带上查到的讲座
    @RequestMapping(value = "/editLect", params = {"id", "lecture_id", "lecture_name"}, method = RequestMethod.POST)
    public ModelAndView editLecture(@RequestParam("id") Integer id,
                                    @RequestParam("lecture_id") String lid,
                                    @RequestParam("lecture_name") String lname, Model model){
        if(card_no2!=0) {
            LectureEntity lectureEntity = mainMapper.GetLectureById(id);
            String oldid = lectureEntity.getLecture_id();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement stmt = con.createStatement();
                String sql = "UPDATE lecture_list SET lecture_id= '" + lid + "', lecture_name = '" + lname + "' WHERE id= " + id;
                stmt.execute(sql);
                String sql2 = "ALTER TABLE " + oldid + " RENAME TO " + lid;
                stmt.execute(sql2);
                con.close();
                model.addAttribute("editmsg", "Edit Successful!!");
            } catch (Exception e) {
                model.addAttribute("editmsg", "Edit Unsuccessful !! System Error !! See Exception!!");
                System.out.println(e);
            }


            model.addAttribute("oldname", lname);
            model.addAttribute("oldid", lid);
            model.addAttribute("id", id);
            return new ModelAndView("edit");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }

    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView toEdit(@RequestParam("id") Integer id, Model model){
        if(card_no2!=0) {
            LectureEntity lectureEntity = mainMapper.GetLectureById(id);
            String oldid = lectureEntity.getLecture_id();
            String oldname = lectureEntity.getLecture_name();
            model.addAttribute("oldname", oldname);
            model.addAttribute("oldid", oldid);
            model.addAttribute("id", id);
            return new ModelAndView("edit");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }



    public String token(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        current_token = saltStr;
        return saltStr;

    }


    @RequestMapping(value = "/st_import", method = RequestMethod.POST, params = "id")
    public ModelAndView toImport(@RequestParam("id") String id, Model model){
        if(card_no2!=0) {
            String value = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement stmt = con.createStatement();
                ResultSet rs;
                String sql = "SELECT COUNT(*) from " + id;
                stmt.executeQuery(sql);
                rs = stmt.getResultSet();
                while (rs.next()) {
                    value = rs.getString("COUNT(*)");
                    //System.out.println(value);
                }
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            model.addAttribute("value", value);
            model.addAttribute("id", id);
            return new ModelAndView("st_import");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping("/import")
    public ModelAndView mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile,
                                             @RequestParam("id") String id, Model model) throws IOException {
        if(card_no2!=0) {
            System.out.println(id);
//        List<Test> tempStudentList = new ArrayList<Test>();
            Workbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            Iterator<Row> rowIterator = sheet.rowIterator();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement stmt = con.createStatement();
                String sql0 = "TRUNCATE TABLE " + id;
                stmt.execute(sql0);
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Now let's iterate over the columns of the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    long cellValue = Long.parseLong(dataFormatter.formatCellValue(cell));
                    Cell cell1 = cellIterator.next();
                    String cellValue1 = dataFormatter.formatCellValue(cell1);

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                        Statement stmt = con.createStatement();
                        String sql = "insert into " + id + " (student_id, student_name) VALUES (" + cellValue + ", '" + cellValue1 + "')";
                        stmt.execute(sql);
                        con.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    System.out.print(cellValue + "\t" + cellValue1);
                }
                System.out.println();
            }
            return new ModelAndView("redirect:view");
        }
        else {
            nopassmsg = "Please login to see the pages inside";
            return new ModelAndView("redirect:userLogin");
        }

    }

    @RequestMapping(value = "texport", params = "id", method = RequestMethod.GET)
    public void export(HttpServletResponse response, @RequestParam("id") String id){
            String date = findDate();
            String headerValue = "attachment; filename=DB_" + date + "_" + id + ".xls";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", headerValue);
            System.out.println(id);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr", "root", "991211");
                Statement st = con.createStatement();
                String sql = "Select * from " + id;
                ResultSet rs = st.executeQuery(sql);

                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet sheet = wb.createSheet("Excel Sheet");
                HSSFRow rowhead = sheet.createRow((short) 0);
                rowhead.createCell((short) 0).setCellValue("Student ID");
                rowhead.createCell((short) 1).setCellValue("Student Name");
                rowhead.createCell((short) 2).setCellValue(date);

                int index = 1;
                while (rs.next()) {
                    HSSFRow row = sheet.createRow((short) index);
                    row.createCell((short) 0).setCellValue(rs.getString("student_id"));
                    row.createCell((short) 1).setCellValue(rs.getString("student_name"));
                    row.createCell((short) 2).setCellValue(rs.getString(date));
                    index++;
                }
//            FileOutputStream fileOut = new FileOutputStream();
                //System.out.println("here");
                wb.write(response.getOutputStream()); // Write workbook to response.
                wb.close();

//            wb.write(fileOut);
//            fileOut.close();
                System.out.println("Data is saved in excel file.");
                rs.close();
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }

    }

    @RequestMapping(value = "aexport", params = "id", method = RequestMethod.GET)
    public void aexport(HttpServletResponse response, @RequestParam("id") String id){
        String headerValue="attachment; filename=DB_All_" + id + ".xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", headerValue);
        List<String> headerValues= new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://120.79.140.98:3306/class_qr","root","991211");

            Statement statement = con.createStatement();
            XSSFSheet spreadsheet = workbook.createSheet("AllRecord");
            ResultSet resultSet = statement.executeQuery("Select * from " + id);


            XSSFRow row = spreadsheet.createRow(0);
            XSSFCell cell;
            int cc=resultSet.getMetaData().getColumnCount();
            for(int i=1;i<=cc;i++)
            {
                String headerVal=resultSet.getMetaData().getColumnName(i);
                headerValues.add(headerVal);
                cell = row.createCell(i-1);
                cell.setCellValue(resultSet.getMetaData().getColumnName(i));
            }
            System.out.println(headerValues);

            int i = 1;
            while (resultSet.next())
            {

                XSSFRow row1 = spreadsheet.createRow((short) i);
                for(int p=0;p<headerValues.size();p++)
                {
                    row1.createCell((short) p).setCellValue(resultSet.getString(headerValues.get(p)));
                }
                i++;
            }
            workbook.write(response.getOutputStream()); // Write workbook to response.
            workbook.close();
            System.out.println("File written successfully");

        }catch(Exception e){}
    }


    @RequestMapping("/logout")
    public ModelAndView logoutUser(){
        card_no2 = 0;
        nopassmsg = null;
        return new ModelAndView("redirect:/userLogin");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request) {
    public String handleError() {

        // display generic error
        return "error";
    }

    public String findDate(){
        LocalDate localDate = LocalDate.now();
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        return formattedDate;
    }


}
